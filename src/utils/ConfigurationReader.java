package utils;

/**
 * Created by admin on 8/1/15.
 */
public class ConfigurationReader {

    private boolean sessionTransactional = false;
    private int acknowledgementType = 1;

    public static synchronized ConfigurationReader getInstance() {
        ConfigurationReader configurationReader = new ConfigurationReader();
        return configurationReader;
    }

    public int getNumOfConnectionsPerBroker() {
        return 2;
    }

    public int getNumOfSessionsPerConnection() {
        return 2;
    }

    public int getNumOfProducerConsumersPerSession() {
        return 1;
    }

    public String getBrokerURL() {
        return "127.0.0.1:5672;127.0.0.1:5673";
    }

    public boolean isSessionTransactional() {
        return sessionTransactional;
    }

    public int getAcknowledgementType() {
        return acknowledgementType;
    }
}
