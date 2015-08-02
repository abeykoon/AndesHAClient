package poolObjects;

import javax.jms.Connection;
import java.util.UUID;

/**
 * Created by admin on 8/1/15.
 */
public class AndesConnection {

    private Connection jmsConnection;
    private String brokerURL;
    private UUID connectionID;

    public AndesConnection(Connection jmsConnection, String brokerURL) {
        this.jmsConnection = jmsConnection;
        this.brokerURL = brokerURL;
        this.connectionID = UUID.randomUUID();
    }

    public Connection getJmsConnection() {
        return jmsConnection;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public UUID getConnectionID() {
        return connectionID;
    }
}
