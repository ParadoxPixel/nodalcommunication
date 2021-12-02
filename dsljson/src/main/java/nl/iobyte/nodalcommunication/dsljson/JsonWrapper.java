package nl.iobyte.nodalcommunication.dsljson;

import nl.iobyte.nodalcommunication.generic.Node;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketFactory;
import nl.iobyte.nodalcommunication.generic.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.generic.interfaces.IPacketSource;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.generic.objects.AbstractPacketSource;

public class JsonWrapper extends AbstractPacketSource {

    private final IPacketSource source;
    private final IPacketFactory factory;

    public JsonWrapper(IPacketSource source) {
        this(source, new PacketFactory());
    }

    public JsonWrapper(IPacketSource source, IPacketFactory factory) {
        this.source = source;
        this.factory = factory;
    }

    /**
     * Get packet source
     * @return IPacketSource
     */
    public IPacketSource getSource() {
        return source;
    }

    /**
     * Get packet factory
     * @return IPacketFactory
     */
    public IPacketFactory getFactory() {
        return factory;
    }

    /**
     * {@inheritDoc}
     * @param target String
     * @param packet IPacket<?>
     */
    public void send(Node node, String target, IPacket<?> packet) {
        source.send(node, target, packet);
    }

    /**
     * {@inheritDoc}
     * @param node Node
     * @param channel String
     */
    public void register(Node node, String channel) {
        source.register(node, channel);
    }

    /**
     * {@inheritDoc}
     * @param id String
     * @param factory IPacketFactory
     * @return Node
     */
    public Node newNode(String id, IPacketFactory factory) {
        return source.newNode(id, factory);
    }

    /**
     * Get node from Source
     * @param id String
     * @return Node
     */
    public Node newNode(String id) {
        return source.newNode(id, factory);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        source.start();
        onStart();
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        onStop();
        source.stop();
    }

}
