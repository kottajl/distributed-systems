package src;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Doctor extends Personel {

    public static String getDoctorKey (int doctorId) {
        return "doctor" + doctorId;
    }

    private static void orderExamination (Channel channel, int doctorId, String injury, String patientName) {
        String key = injury + ".examination";
        String message = Doctor.getDoctorKey(doctorId) + " " + injury + " " + patientName;

        try {
            channel.basicPublish(EXCHANGE_EXAMINATIONS_NAME, key, null, message.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void secretary (Channel channel, int doctorId) {
        String queueName0, queueName1;


        // Binding admin messages queue
        try {
            queueName0 = channel.queueDeclare().getQueue();
            channel.queueBind(queueName0, EXCHANGE_ADMIN_BROADCAST_NAME, "");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Binding results queue
        try {
            queueName1 = Doctor.getDoctorKey(doctorId) + "_results_queue";
            channel.queueDeclare(queueName1, true, false, false, null);
            channel.queueBind(queueName1, EXCHANGE_RESULTS_NAME, "results." + Doctor.getDoctorKey(doctorId));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Consumer for admin's broadcasts
        Consumer admin_consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Message from admin: " + message);
            }
        };


        // Technicians responses consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(message);

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };


        // Start listening
        try {
            channel.basicConsume(queueName0, true, admin_consumer);
            channel.basicConsume(queueName1, false, consumer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main (String[] argv) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Id: ");
        final int myId = Integer.parseInt(scanner.nextLine());
        System.out.println("I am " + Doctor.getDoctorKey(myId));


        // Declare connection and channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();


        // Declare exchanges
        channel.exchangeDeclare(EXCHANGE_EXAMINATIONS_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_RESULTS_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_ADMIN_BROADCAST_NAME, BuiltinExchangeType.FANOUT);


        // All operations needed to listening
        Doctor.secretary(channel, myId);


        // Parsing input
        boolean exit = false;
        while (true) {
            System.out.print("> ");
            String[] input = scanner.nextLine().split(" ");

            if (input.length == 0)
                continue;

            switch (input[0]) {

                case "quit":
                case "exit":
                    exit = true;
                    break;

                case "knee":
                case "hip":
                case "elbow":
                    if (input.length != 2)
                        System.out.println("???");
                    else
                        Doctor.orderExamination(channel, myId, input[0], input[1]);
                    break;

                case "":
                    break;

                default:
                    System.out.println("???");
                    break;

            }

            if (exit)
                break;
        }

        channel.close();
        connection.close();
    }

}
