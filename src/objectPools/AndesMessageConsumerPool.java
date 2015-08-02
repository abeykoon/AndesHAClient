package objectPools;

import poolObjects.AndesMessageConsumer;
import javax.jms.Destination;
import java.util.*;

/**
 * Created by admin on 8/1/15.
 */
public class AndesMessageConsumerPool {

    //map to keep connections. <destination, List of BasicConsumers>
    private Map<Destination, List<AndesMessageConsumer>> consumerPool;

    public AndesMessageConsumerPool() {
        consumerPool = new HashMap<Destination, List<AndesMessageConsumer>>();
    }

    public void addJMSConsumer(Destination destination, AndesMessageConsumer messageConsumer) {
        List<AndesMessageConsumer> consumers = consumerPool.get(destination);
        if(null == consumers) {
            consumers = new ArrayList<AndesMessageConsumer>();
        }
        consumers.add(messageConsumer);
        consumerPool.put(destination, consumers);
    }


    public void removeAllConsumersOfSession(UUID sessionID) {
        for (Map.Entry<Destination, List<AndesMessageConsumer>> entry : consumerPool.entrySet()) {
            List<AndesMessageConsumer> consumers = entry.getValue();
            for(AndesMessageConsumer consumer : consumers) {
                //if()
            }
        }
    }

    public List<AndesMessageConsumer> removeAllConsumersofDestination(Destination destination) {
        return consumerPool.remove(destination);
    }

    public AndesMessageConsumer getRandomConsumer(Destination destination) {
        List<AndesMessageConsumer> consumers = consumerPool.get(destination);
        int randomConsumerIndex = new Random().nextInt(consumers.size());
        AndesMessageConsumer consumer  =  consumers.get(randomConsumerIndex);
        return consumer;
    }

    public List<AndesMessageConsumer> getAllConsumersOfDestination(Destination destination) {
        return consumerPool.get(destination);
    }

}
