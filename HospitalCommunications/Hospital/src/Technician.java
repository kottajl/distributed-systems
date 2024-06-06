package src;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Technician extends Personel {

    private static void sendResponse (Channel channel, String orderingDoctorKey, String message) {
        String key = "results." + orderingDoctorKey;

        try {
            channel.basicPublish(EXCHANGE_RESULTS_NAME, key, null, message.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main (String[] argv) throws Exception {

        // Declare connection and channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);


        // Declare exchanges
        channel.exchangeDeclare(EXCHANGE_EXAMINATIONS_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_RESULTS_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_ADMIN_BROADCAST_NAME, BuiltinExchangeType.FANOUT);


        // Create and bind broadcast queue
        String broadcastQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(broadcastQueueName, EXCHANGE_ADMIN_BROADCAST_NAME, "");


        // User input (qualifications + examination time)
        String[] qualifications = new String[2];
        int examinationTime;

        if (argv.length == 3) {
            qualifications[0] = argv[0];
            qualifications[1] = argv[1];
            examinationTime = Integer.parseInt(argv[2]);
        }
        else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Pass the qualifications [knee/hip/elbow]: ");
            String input = scanner.nextLine();
            qualifications = input.split(" ");
            if (qualifications.length != 2) {
                System.err.println("Wrong format of qualifications!");
                System.exit(-1);
            }
            System.out.print("Pass the examination time (in seconds): ");
            examinationTime = scanner.nextInt();
            scanner.close();
        }


        // Create and bind queues for injuries
        for (String qualification: qualifications) {
            if (!Injuries.checkCorrectness(qualification)) {
                System.err.println("Incorrect arguments passed!");
                System.exit(-1);
            }

            String queueName = qualification + "_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_EXAMINATIONS_NAME, qualification + ".examination");
        }


        // Create consumer for admin's broadcasts
        Consumer admin_consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Message from admin: " + message);
            }
        };


        // Create order's consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);

                String[] order = message.split(" ");
                String doctorKey = order[0];
                String injury = order[1];
                String patientName = order[2];

                System.out.print("Working on " + injury + " examination for " + patientName + "... ");

                try {
                    Thread.sleep(1000L * examinationTime);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("done.");
                String response = patientName + " " + injury + " " + "done";
                Technician.sendResponse(channel, doctorKey, response);

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        // Start listening
        System.out.println("Waiting for orders...");
        channel.basicConsume(broadcastQueueName, true, admin_consumer);
        for (String qualification: qualifications) {
            String queueName = qualification + "_queue";
            channel.basicConsume(queueName, false, consumer);
        }

    }

}
