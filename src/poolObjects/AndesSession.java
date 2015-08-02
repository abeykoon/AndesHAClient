package poolObjects;

import javax.jms.Session;
import java.util.UUID;

/**
 * Created by admin on 8/1/15.
 */
public class AndesSession {

    private Session jmsSession;
    private AndesConnection andesConnection;
    private UUID sessionID;

    public AndesSession(AndesConnection andesConnection, Session jmsSession) {
        this.andesConnection = andesConnection;
        this.jmsSession = jmsSession;
        this.sessionID = UUID.randomUUID();
    }

    public Session getJmsSession() {
        return jmsSession;
    }

    public AndesConnection getAndesConnection() {
        return andesConnection;
    }

    public UUID getSessionID() {
        return sessionID;
    }
}
