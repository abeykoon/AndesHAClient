package objectPools;

import poolObjects.AndesMessageProducer;
import javax.jms.Destination;
import java.util.*;

/**
 * Created by admin on 8/1/15.
 */
public class AndesMessageProducerPool {

    //map to keep connections. <destination, List of BasicProducers>
    private Map<Destination, List<AndesMessageProducer>> producerPool;

    public AndesMessageProducerPool() {

        producerPool = new HashMap<Destination, List<AndesMessageProducer>>();
    }

    public void addJMSProducer(Destination destination, AndesMessageProducer messageProducer) {
        List<AndesMessageProducer> producers = producerPool.get(destination);
        if(null == producers) {
            producers = new ArrayList<AndesMessageProducer>();
        }
        producers.add(messageProducer);
        producerPool.put(destination, producers);
    }


    public void removeAllProducerssOfSession(UUID sessionID) {
        for (Map.Entry<Destination, List<AndesMessageProducer>> entry : producerPool.entrySet()) {
            List<AndesMessageProducer> producers = entry.getValue();
            for(AndesMessageProducer producer : producers) {
                //if()
            }
        }
    }

    public List<AndesMessageProducer> removeAllProducersOfDestination(Destination destination) {
        return producerPool.remove(destination);
    }

    public AndesMessageProducer getRandomProducer(Destination destination) {
        List<AndesMessageProducer> producers= producerPool.get(destination);
        int randomProducerIndex = new Random().nextInt(producers.size());
        AndesMessageProducer producer  =  producers.get(randomProducerIndex);
        return producer;
    }

    public List<AndesMessageProducer> getAllProducersOfDestination(Destination destination) {
        return producerPool.get(destination);
    }

}
