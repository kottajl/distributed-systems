package src;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Administrator extends Personel {

    private static void broadcastMessage (Channel channel, String message) {
        String key = "admin.message";

        try {
            channel.basicPublish(EXCHANGE_ADMIN_BROADCAST_NAME, key, null, message.getBytes(StandardCharsets.UTF_8));
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


        // Declare exchanges
        channel.exchangeDeclare(EXCHANGE_EXAMINATIONS_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_RESULTS_NAME, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(EXCHANGE_ADMIN_BROADCAST_NAME, BuiltinExchangeType.FANOUT);


        // Create and bind admin queues
        String queueName = "admin_info_queue";
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, EXCHANGE_EXAMINATIONS_NAME, "#");
        channel.queueBind(queueName, EXCHANGE_RESULTS_NAME, "#");


        // All messages consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(message);
            }
        };
        try {
            channel.basicConsume(queueName, true, consumer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


        // Parse input
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            switch (input) {

                case "exit":
                case "quit":
                    System.exit(0);

                case "":
                    break;

                default:
                    broadcastMessage(channel, input);
                    break;

            }
        }

    }

}

