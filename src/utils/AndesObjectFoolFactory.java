package utils;

import objectPools.AndesConnectionPool;
import objectPools.AndesMessageConsumerPool;
import objectPools.AndesMessageProducerPool;
import objectPools.AndesSessionPool;
import poolObjects.AndesConnection;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by admin on 8/1/15.
 */
public class AndesObjectFoolFactory {

    private AndesConnectionPool andesConnectionPool;
    private AndesSessionPool andesSessionPool;
    private AndesMessageConsumerPool andesMessageConsumerPool;
    private AndesMessageProducerPool andesMessageProducerPool;

    private final String QPID_ICF = "org.wso2.andes.jndi.PropertiesFileInitialContextFactory";
    private final String CF_NAME_PREFIX = "connectionfactory.";
    private final String CF_NAME = "qpidConnectionfactory";
    private String userName = "admin";
    private String password = "admin";
    private static String CARBON_CLIENT_ID = "carbon";
    private static String CARBON_VIRTUAL_HOST_NAME = "carbon";


    private static AndesObjectFoolFactory andesObjectFoolFactory;

    public static synchronized AndesObjectFoolFactory getInstance() {
        if(null == andesObjectFoolFactory) {
            andesObjectFoolFactory = new AndesObjectFoolFactory();
        }
        return andesObjectFoolFactory;
    }

    public AndesConnectionPool getAndesConnectionPool() throws JMSException, NamingException {
        if(null == andesConnectionPool) {
            andesConnectionPool = new AndesConnectionPool();

            //populate initial context
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, QPID_ICF);

            //analyse broker url and make connections and cache
            String brokerURL  = ConfigurationReader.getInstance().getBrokerURL();
            String[] brokers = brokerURL.split(";");


            for (int brokerCount = 0 ; brokerCount < brokers.length; brokerCount ++) {
                properties.put(CF_NAME_PREFIX + CF_NAME + "_" + brokerCount , getTCPConnectionURL(brokers[brokerCount],
                        userName, password));
            }

            InitialContext ctx = new InitialContext(properties);

            for (int brokerCount = 0 ; brokerCount < brokers.length; brokerCount ++) {
                ConnectionFactory connFactory = (ConnectionFactory) ctx.lookup(CF_NAME + "_" + brokerCount);

                int connectionsPerBroker = ConfigurationReader.getInstance().getNumOfConnectionsPerBroker();
                for(int connectionCount = 0; connectionCount < connectionsPerBroker ; connectionCount++) {
                    Connection jmsConnection= connFactory.createConnection();
                    jmsConnection.start();
                    AndesConnection andesConnection = new AndesConnection(jmsConnection, brokers[brokerCount]);
                    andesConnectionPool.addConnection(andesConnection);
                }
            }
        }
        return andesConnectionPool;
    }

    public AndesSessionPool getAndesSessionPool() throws NamingException, JMSException {
        if(null == andesSessionPool) {

            //make sessions for each connection and cache
            andesSessionPool = new AndesSessionPool();
        }

        return andesSessionPool;
    }


/*    public AndesMessageProducerPool getAndesMessageProducerPool() {
        if(null == andesMessageProducerPool) {
            andesMessageProducerPool = new AndesMessageProducerPool();
        }

        return andesMessageProducerPool;
    }

    public AndesMessageConsumerPool getAndesMessageConsumerPool() {
        if(null == andesMessageConsumerPool) {
            andesMessageConsumerPool = new AndesMessageConsumerPool();
        }

        return andesMessageConsumerPool;
    }*/

    private String getTCPConnectionURL(String brokerURL, String username, String password) {
        // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{port}'
        return new StringBuffer()
                .append("amqp://").append(username).append(":").append(password)
                .append("@").append(CARBON_CLIENT_ID)
                .append("/").append(CARBON_VIRTUAL_HOST_NAME)
                .append("?brokerlist='tcp://").append(brokerURL).append("'")
                .toString();
    }


}
