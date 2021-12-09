package nl.iobyte.nodalcommunication.rabbitmq;

import nl.iobyte.nodalcommunication.generic.Node;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.generic.namespace.NamespaceComparator;
import nl.iobyte.rabbitwrapper.thirdparty.com.rabbitmq.client.DeliverCallback;
import nl.iobyte.rabbitwrapper.thirdparty.com.rabbitmq.client.Delivery;
import java.util.concurrent.ForkJoinPool;

public class RabbitMQListener implements DeliverCallback {

    private final Node node;
    private final NamespaceComparator comparator;

    public RabbitMQListener(Node node) {
        this.node = node;
        comparator = new NamespaceComparator(node.getId());
    }

    /**
     * {@inheritDoc}
     * @param tag String
     * @param delivery Delivery
     */
    public void handle(String tag, Delivery delivery) {
        handle(
                delivery.getEnvelope().getExchange(),
                delivery.getEnvelope().getRoutingKey(),
                delivery.getBody()
        );
    }

    /**
     * Handle incoming data
     * @param channel String
     * @param message String
     */
    public void handle(String channel, String node_id, byte[] message) {
        if(comparator.compare(node_id) < 0)
            return;

        if(message[0] != '{')
            return;

        //Run async
        ForkJoinPool.commonPool().execute(() -> {
            IPacket<?> packet = node.getFactory().getSerializer().deserialize(
                    message,
                    node.getFactory().getType()
            );
            if(packet == null)
                return;

            node.handle(packet);
        });
    }

}
