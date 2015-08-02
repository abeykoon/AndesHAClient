import messageConsumers.AndesHAQueueConsumer;
import messageProducers.AndesHAQueueSender;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.NamingException;

public class AndesHAClient {

    private static class RunnableQueueSender implements Runnable {

        private AndesHAQueueSender sender;
        private int numberOfMessages;

        public RunnableQueueSender(String queueName, Integer acknowledgementType, Integer numberOfMessages) throws
                JMSException, NamingException {
            this.sender = new AndesHAQueueSender(queueName, acknowledgementType);
            this.numberOfMessages = numberOfMessages;
        }
        @Override
        public void run() {
            for(int i = 0 ; i < numberOfMessages; i++) {
                String message = "message ID = " + i;
                try {
                    sender.publish(message);
                    System.out.println("[" + i + "]  sent message: " + message);
                } catch (JMSException e) {
                    System.out.println("Error while sending message. Trying again");
                    try {
                        sender.publish(message);
                        System.out.println("[" + i + "]  sent message: " + message);
                    } catch (JMSException e1) {
                        System.out.println("Error while sending message. Giving up...");
                    }
                }
            }
        }
    }

    private static class RunnableQueueReceiver implements Runnable {

        private AndesHAQueueConsumer receiver;
        private int numberOfMessages;

        public RunnableQueueReceiver(String queueName, Integer acknowledgementType, Integer numberOfMessages) throws
                JMSException, NamingException {
            this.receiver = new AndesHAQueueConsumer(queueName, acknowledgementType);
            this.numberOfMessages = numberOfMessages;
        }

        @Override
        public void run() {
            int messageCounter = 0;

            while (messageCounter < numberOfMessages) {
                try {
                    Message jmsMessage = receiver.consume();
                    TextMessage textMessage = (TextMessage) jmsMessage;
                    if(null != jmsMessage) {
                        System.out.println("["+ messageCounter+ "] Message received : " + textMessage.getText());
                        messageCounter ++;
                    }
                } catch (JMSException e) {
                    System.out.println("Error while sending message. Trying again");
                    try {
                        Message jmsMessage = receiver.consume();
                        TextMessage textMessage = (TextMessage) jmsMessage;
                        if(null != jmsMessage) {
                            System.out.println("["+ messageCounter+ "] Message received : " + textMessage.getText());
                            messageCounter++;
                        }
                    } catch (JMSException e1) {
                        System.out.println("Error while sending message. Giving up...");
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws NamingException, JMSException {

        String queueName = "MyQueue";
        int numberOfMessages  = 10000;
        int acknowledgementType = QueueSession.AUTO_ACKNOWLEDGE;

        //consumer thread
        Thread consumerThread = new Thread(new RunnableQueueReceiver(queueName,acknowledgementType, numberOfMessages));
        consumerThread.start();

        //publisher thread
        Thread publisherThread = new Thread(new RunnableQueueSender(queueName,acknowledgementType,numberOfMessages));
        publisherThread.start();
    }
}
