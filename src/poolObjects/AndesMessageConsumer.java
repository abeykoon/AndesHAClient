package poolObjects;

import javax.jms.MessageConsumer;

/**
 * Created by admin on 8/2/15.
 */
public class AndesMessageConsumer {

    private AndesSession andesSession;
    private MessageConsumer jmsMessageConsumer;

    public AndesMessageConsumer(AndesSession andesSession, MessageConsumer messageConsumer) {
        this.andesSession = andesSession;
        this.jmsMessageConsumer = messageConsumer;
    }

    public AndesSession getAndesSession() {
        return andesSession;
    }

    public MessageConsumer getJmsMessageConsumer() {
        return jmsMessageConsumer;
    }
}
