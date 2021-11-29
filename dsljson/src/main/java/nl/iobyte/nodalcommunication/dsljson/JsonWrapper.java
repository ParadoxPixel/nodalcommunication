package nl.iobyte.nodalcommunication.dsljson;

import nl.iobyte.nodalcommunication.dsljson.packet.PacketFactory;
import nl.iobyte.nodalcommunication.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.interfaces.IPacketSource;
import nl.iobyte.nodalcommunication.interfaces.ISerializer;
import nl.iobyte.nodalcommunication.objects.Node;

public class JsonWrapper implements IPacketSource {

    private final ISerializer serializer;
    private final IPacketSource source;
    private final IPacketFactory factory;

    public JsonWrapper(IPacketSource source) {
        this(new JsonSerializer(), source, new PacketFactory());
    }

    public JsonWrapper(ISerializer serializer, IPacketSource source, IPacketFactory factory) {
        this.serializer = serializer;
        this.source = source;
        this.factory = factory;
    }

    /**
     * Get serializer
     * @return ISerializer
     */
    public ISerializer getSerializer() {
        return serializer;
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
     * @param channel String
     * @param message String
     */
    public void send(String target, String channel, String message) {
        source.send(target, channel, message);
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
     * @param serializer ISerializer
     * @param factory IPacketFactory
     * @return Node
     */
    public Node newNode(String id, ISerializer serializer, IPacketFactory factory) {
        return source.newNode(id, serializer, factory);
    }

    /**
     * Get node from Source
     * @param id String
     * @return Node
     */
    public Node newNode(String id) {
        return source.newNode(id, serializer, factory);
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        source.start();
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        source.stop();
    }

}
