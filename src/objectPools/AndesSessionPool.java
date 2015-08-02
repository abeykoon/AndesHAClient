package objectPools;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import poolObjects.AndesSession;
import utils.ConfigurationReader;

import java.util.*;

/**
 * Created by admin on 8/1/15.
 */
public class AndesSessionPool {

    //map to keep connections. <connectionID, SessionID> / Session>
    private Table<UUID, UUID, AndesSession> sessionPool;

    private Iterator connectionIterator;
    private int sessionIndex = 0;

    public AndesSessionPool() {

        sessionPool = HashBasedTable.create();
        connectionIterator = sessionPool.rowKeySet().iterator();
    }

    public void addSession(AndesSession andesSession) {
        sessionPool.put(andesSession.getAndesConnection().getConnectionID(),
                andesSession.getSessionID(), andesSession);
    }

    public void removeSession(UUID connectionID, UUID sessionID) {
        sessionPool.remove(connectionID, sessionID);
    }

    public void removeAllSessionsOfConnection(UUID connectionID) {
        Map<UUID, AndesSession> sessionsOfBroker  = sessionPool.row(connectionID);
        for (UUID sessionID : sessionsOfBroker.keySet()) {
            removeSession(connectionID, sessionID);
        }
    }

    public Map<UUID,AndesSession> getAllSessionsOfConnection(UUID connectionID) {
        return sessionPool.row(connectionID);
    }

    public AndesSession getSession(UUID connectionID, UUID sessionID) {
        return sessionPool.get(connectionID, sessionID);
    }

    public AndesSession getRandomSession() {
        Set<UUID> connectionIDs = sessionPool.rowKeySet();
        int randomConnectionIndex = new Random().nextInt(connectionIDs.size());
        UUID randomConnection = (UUID) connectionIDs.toArray()[randomConnectionIndex];
        return getRandomSessionOfConnection(randomConnection);
    }

    //get sessions in round robin manner
    public AndesSession getNextSession() {
        if(connectionIterator.hasNext()) {
            UUID connectionID = (UUID) connectionIterator.next();
            Map<UUID, AndesSession> sessionsOfConnection = sessionPool.row(connectionID);
            AndesSession session = (AndesSession) sessionsOfConnection.values().toArray()[sessionIndex];
            return session;
        } else {
            connectionIterator = sessionPool.rowKeySet().iterator();
            sessionIndex = sessionIndex + 1;
            if(sessionIndex >= ConfigurationReader.getInstance().getNumOfSessionsPerConnection()) {
                sessionIndex = 0;
            }
            return getNextSession();
        }
    }

    public AndesSession getRandomSessionOfConnection(UUID connectionID) {
        Map<UUID, AndesSession> sessionssOfBroker = sessionPool.row(connectionID);
        int randomSessionIndex = new Random().nextInt(sessionssOfBroker.size());
        UUID sessionID = (UUID) sessionssOfBroker.keySet().toArray()[randomSessionIndex];
        return getSession(sessionID, connectionID);
    }
}
