package objectPools;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import poolObjects.AndesConnection;
import utils.ConfigurationReader;

import java.util.*;

/**
 * Created by admin on 8/1/15.
 */
public class AndesConnectionPool {

    //map to keep connections. <BrokerURL, connectionID> / Connection>
    private Table<String, UUID, AndesConnection> connectionPool;
    private Iterator brokerIterator;
    private int connectionIndex = 0;

    public AndesConnectionPool() {

        connectionPool = HashBasedTable.create();
        brokerIterator = connectionPool.rowKeySet().iterator();
    }

    public void addConnection(AndesConnection andesConnection) {
        connectionPool.put(andesConnection.getBrokerURL(),andesConnection.getConnectionID(), andesConnection);
    }

    public void removeConnection(String brokerURL, UUID connectionID) {
        connectionPool.remove(brokerURL, connectionID);
    }

    public void removeAllConnectionsOfBroker(String brokerURL) {
        Map<UUID, AndesConnection> connectionsOfBroker  = connectionPool.row(brokerURL);
        for (UUID connectionID : connectionsOfBroker.keySet()) {
            removeConnection(brokerURL,connectionID);
        }
    }

    public Map<UUID,AndesConnection> getAllConnectionsOfBroker(String brokerURL) {
        return connectionPool.row(brokerURL);
    }

    public AndesConnection getConnection(String brokerURL, UUID connectionID) {
        return connectionPool.get(brokerURL, connectionID);
    }

    public AndesConnection getRandomConnection() {
        Set<String> brokers = connectionPool.rowKeySet();
        int randomBrokerIndex = new Random().nextInt(brokers.size());
        String broker  = (String) brokers.toArray()[randomBrokerIndex];
        return getRandomConnectionOfBroker(broker);
    }

    //this is used to get connections in round robin manner between brokers
    public AndesConnection getNextConnection() {
        if(brokerIterator.hasNext()) {
            String broker = (String) brokerIterator.next();
            Map<UUID, AndesConnection> connectionsOfBroker = connectionPool.row(broker);
            AndesConnection connection = (AndesConnection) connectionsOfBroker.values().toArray()[connectionIndex];
            return connection;
        } else {
            brokerIterator = connectionPool.rowKeySet().iterator();
            connectionIndex = connectionIndex + 1;
            if(connectionIndex >= ConfigurationReader.getInstance().getNumOfConnectionsPerBroker()) {
                connectionIndex = 0;
            }
            return getNextConnection();
        }
    }

    public AndesConnection getRandomConnectionOfBroker(String brokerURL) {
        Map<UUID, AndesConnection> connectionsOfBroker = connectionPool.row(brokerURL);
        int randomConnectionIndex = new Random().nextInt(connectionsOfBroker.size());
        UUID connectionID = (UUID) connectionsOfBroker.keySet().toArray()[randomConnectionIndex];
        return getConnection(brokerURL,connectionID);
    }
}
