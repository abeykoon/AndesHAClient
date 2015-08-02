package messageProducers;


import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.NamingException;

public class BasicAndesHAPublisher {


    public void BasicAndesHAPublisher(Connection jmsConnection, Session jmsSession) {

    }

    public void publish(String message) throws JMSException{

        //if an error came we need to try with another connection
        //also should remove the existing connection
    }

    public void start() throws JMSException{

    }

    public void stop() throws JMSException, NamingException {

    }

}
