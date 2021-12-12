package nl.iobyte.nodalcommunication.rabbitmq;

import nl.iobyte.nodalcommunication.generic.Node;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.rabbitwrapper.thirdparty.com.rabbitmq.client.DeliverCallback;
import nl.iobyte.rabbitwrapper.thirdparty.com.rabbitmq.client.Delivery;
import java.util.concurrent.ForkJoinPool;

public class RabbitMQListener implements DeliverCallback {

    private final Node node;

    public RabbitMQListener(Node node) {
        this.node = node;
    }

    /**
     * {@inheritDoc}
     * @param tag String
     * @param delivery Delivery
     */
    public void handle(String tag, Delivery delivery) {
        IPacket<?> packet = node.getFactory().getSerializer().deserialize(
                delivery.getBody(),
                node.getFactory().getType()
        );
        if(packet == null)
            return;

        //Run async
        ForkJoinPool.commonPool().execute(() -> node.handle(packet));
    }

}
