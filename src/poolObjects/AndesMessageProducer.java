package poolObjects;

import javax.jms.MessageProducer;

/**
 * Created by admin on 8/2/15.
 */
public class AndesMessageProducer {

    private AndesSession andesSession;
    private MessageProducer jmsMessageProducer;

    public AndesMessageProducer(AndesSession andesSession, MessageProducer messageProducer) {
        this.andesSession = andesSession;
        this.jmsMessageProducer = messageProducer;
    }

    public AndesSession getAndesSession() {
        return andesSession;
    }

    public MessageProducer getJmsMessageProducer() {
        return jmsMessageProducer;
    }
}
