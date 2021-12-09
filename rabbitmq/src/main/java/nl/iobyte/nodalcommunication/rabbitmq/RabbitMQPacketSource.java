package nl.iobyte.nodalcommunication.rabbitmq;

import nl.iobyte.nodalcommunication.generic.Node;
import nl.iobyte.nodalcommunication.generic.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.generic.objects.AbstractPacketSource;
import nl.iobyte.rabbitwrapper.rabbit.RabbitWrapper;
import nl.iobyte.rabbitwrapper.rabbit.objects.RabbitConfig;
import nl.iobyte.rabbitwrapper.thirdparty.com.rabbitmq.client.Connection;
import nl.iobyte.rabbitwrapper.thirdparty.com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RabbitMQPacketSource extends AbstractPacketSource {

    //Settings
    private final ConnectionFactory connectionFactory;
    private final RabbitConfig config;

    //State
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private Connection connection;
    private RabbitWrapper wrapper;

    //Listeners
    private final List<String> nodeListeners = new ArrayList<>();
    private final List<String> exchanges = new ArrayList<>();

    public RabbitMQPacketSource(ConnectionFactory connectionFactory, RabbitConfig config) {
        this.connectionFactory = connectionFactory;
        this.config = config;
    }

    public void exchange(String name, String type, String routing_key) throws Exception {
        if(wrapper == null)
            return;

        if(exchanges.contains(name))
            return;

        try {
            wrapper.getChannel().exchangeDeclare(
                    "nodal."+name,
                    type,
                    false,
                    true,
                    null
            );
        } finally {
            exchanges.add(name);
        }

        wrapper.getChannel().queueBind(wrapper.getId(), "nodal."+name, routing_key);
    }

    /**
     * {@inheritDoc}
     * @param node Node
     * @param target String
     * @param packet IPacket<?>
     */
    public void send(Node node, String target, IPacket<?> packet) {
        if(!enabled.get())
            return;

        String message = node.getFactory().getSerializer().serialize(packet);
        if(message == null)
            return;

        wrapper.sendCatch(
                "nodal."+packet.getChannel(),
                target,
                null,
               message.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * {@inheritDoc}
     * @param node Node
     * @param channel String
     */
    public void register(Node node, String channel) {
        if(!enabled.get())
            return;

        if(!nodeListeners.contains(node.getId())) {
            nodeListeners.add(node.getId());
            try {
                wrapper.consume(new RabbitMQListener(node));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            exchange(
                    channel,
                    "topic",
                    node.getId()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * @param id String
     * @param factory IPacketFactory
     * @return Node
     */
    public Node newNode(String id, IPacketFactory factory) {
        return new Node(id, this, factory);
    }

    @Override
    public void start() {
        if(!enabled.compareAndSet(false, true))
            return;

        try {
            connection = connectionFactory.newConnection();
            wrapper = new RabbitWrapper(
                    UUID.randomUUID().toString(),
                    connection,
                    config,
                    5,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if(!enabled.compareAndSet(true, false))
            return;

        try {
            wrapper.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
