package messageProducers;


import objectPools.AndesMessageProducerPool;
import poolObjects.AndesConnection;
import poolObjects.AndesMessageProducer;
import poolObjects.AndesSession;
import utils.AndesObjectFoolFactory;
import utils.ConfigurationReader;

import javax.jms.*;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

public class AndesHATopicPublisher extends BasicAndesHAPublisher {

    private Topic topic;
    private AndesMessageProducerPool producerPool;

    public AndesHATopicPublisher(String topicName, Integer acknowledgementType) throws NamingException, JMSException {

        producerPool = new AndesMessageProducerPool();

         //create a connection (or more) for each broker and add to connection pool
        // create a (or more) session from each connection and add to session pool
        //create a (pr more) publishers from each session and add to producer pool
        int numberOfPublishers = ConfigurationReader.getInstance().getNumOfProducerConsumersPerSession();

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

            Topic topic = session.createTopic(topicName);
            this.topic = topic;
            //create and cache the topic publisher
            for(int producerCount = 0; producerCount < numberOfPublishers; producerCount++) {
                javax.jms.TopicPublisher topicPublisher = (TopicPublisher) session.createProducer(topic);
                AndesMessageProducer andesMessageProducer = new AndesMessageProducer(andesSession, topicPublisher);
                producerPool.addJMSProducer(topic, andesMessageProducer);
            }
        }
    }

    public void publish(String message) throws JMSException {
        AndesMessageProducer publisher = producerPool.getRandomProducer(topic);
        Message jmsMessage = publisher.getAndesSession().getJmsSession().createTextMessage(message);
        publisher.getJmsMessageProducer().send(jmsMessage);

        //TODO: if an exception occurred try again
    }

    public void start() throws JMSException{
/*        List<MessageProducer> producers = producerPool.getAllProducersOfDestination(topic);
        for (MessageProducer producer : producers) {

        }*/
    }

    public void stop() throws JMSException, NamingException {
        //stop publishers. Do not do anything to connections/sessions
        List<AndesMessageProducer> removedPublishers = producerPool.removeAllProducersOfDestination(topic);
        for (AndesMessageProducer removedPublisher : removedPublishers) {
            removedPublisher.getJmsMessageProducer().close();
            AndesSession andesSession = removedPublisher.getAndesSession();
            AndesObjectFoolFactory.getInstance().getAndesSessionPool().removeSession(andesSession.getAndesConnection()
                    .getConnectionID(), andesSession.getSessionID());
        }
    }
}
