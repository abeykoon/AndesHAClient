package messageConsumers;

import objectPools.AndesMessageConsumerPool;
import poolObjects.AndesConnection;
import poolObjects.AndesMessageConsumer;
import poolObjects.AndesSession;
import utils.AndesObjectFoolFactory;
import utils.ConfigurationReader;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.List;

/**
 * Created by admin on 8/1/15.
 */
public class AndesHAQueueConsumer extends BasicAndesHAConsumer {

    private Queue queue;
    private AndesMessageConsumerPool consumerPool;


    public AndesHAQueueConsumer(String queueName, Integer acknowledgementType) throws NamingException, JMSException {

        consumerPool = new AndesMessageConsumerPool();

        //create a connection (or more) for each broker and add to connection pool
        // create a (or more) session from each connection and add to session pool
        //create a (pr more) publishers from each session and add to producer pool
        int numberOfConsumers = ConfigurationReader.getInstance().getNumOfProducerConsumersPerSession();

        int numberOfSessionsToCreate = ConfigurationReader.getInstance().getNumOfConnectionsPerBroker() *
                ConfigurationReader.getInstance().getNumOfSessionsPerConnection();

        boolean isSessionTransactional = ConfigurationReader.getInstance().isSessionTransactional();
        for(int count = 0 ; count < numberOfSessionsToCreate ; count ++) {

            //this will get connections in a round robin fashion between brokers
            AndesConnection connection = AndesObjectFoolFactory.getInstance().getAndesConnectionPool()
                    .getNextConnection();
            Session session = connection.getJmsConnection().createSession(isSessionTransactional, acknowledgementType);
            AndesSession andesSession = new AndesSession(connection, session);
            AndesObjectFoolFactory.getInstance().getAndesSessionPool().addSession(andesSession);

            Queue queue = session.createQueue(queueName);
            this.queue = queue;
            //create and cache the topic publisher
            for(int consumerCount = 0; consumerCount < numberOfConsumers; consumerCount++) {
                //javax.jms.QueueReceiver queueReceiver = (QueueReceiver) session.createConsumer(queue);
                MessageConsumer queueReceiver = session.createConsumer(queue);
                AndesMessageConsumer andesMessageConsumer = new AndesMessageConsumer(andesSession, queueReceiver);
                consumerPool.addJMSConsumer(queue, andesMessageConsumer);
            }
        }
    }

    public Message consume() throws JMSException {
        AndesMessageConsumer consumer = consumerPool.getRandomConsumer(queue);
        return consumer.getJmsMessageConsumer().receive();
    }

    public void start() throws JMSException{

    }

    public void stop() throws JMSException, NamingException {
        List<AndesMessageConsumer> removedConsumers = consumerPool.removeAllConsumersofDestination(queue);
        for (AndesMessageConsumer removedConsumer : removedConsumers) {
            removedConsumer.getJmsMessageConsumer().close();
            AndesSession andesSession = removedConsumer.getAndesSession();
            AndesObjectFoolFactory.getInstance().getAndesSessionPool().removeSession(andesSession.getAndesConnection()
                    .getConnectionID(), andesSession.getSessionID());
        }
    }
}
