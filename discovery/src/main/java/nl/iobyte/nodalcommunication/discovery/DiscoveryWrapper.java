package nl.iobyte.nodalcommunication.discovery;

import nl.iobyte.nodalcommunication.discovery.network.interfaces.Network;
import nl.iobyte.nodalcommunication.discovery.packet.NodeState;
import nl.iobyte.nodalcommunication.discovery.packet.NodeStateHandler;
import nl.iobyte.nodalcommunication.dsljson.JsonWrapper;
import nl.iobyte.nodalcommunication.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.interfaces.IPacketSource;
import nl.iobyte.nodalcommunication.interfaces.ISerializer;
import nl.iobyte.nodalcommunication.objects.Node;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryWrapper extends JsonWrapper {

    private final Map<String, Node> nodes = new ConcurrentHashMap<>();
    private final Network network = new Network();

    public DiscoveryWrapper(IPacketSource source) {
        super(source);
    }

    public DiscoveryWrapper(ISerializer serializer, IPacketSource source, IPacketFactory factory) {
        super(serializer, source, factory);
    }

    /**
     * Get Node's from source
     * @return Map<String, Node>
     */
    public Map<String, Node> getNodes() {
        return nodes;
    }

    /**
     * Get Network
     * @return Network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * {@inheritDoc}
     * @param target String
     * @param channel String
     * @param message String
     */
    public void send(String target, String channel, String message) {
        //Check if Node is local
        if(nodes.containsKey(target)) {
            //Skip packet source
            nodes.get(target).handle(channel, message.getBytes(StandardCharsets.UTF_8));
            return;
        }

        //Send through packet source
        super.send(target, channel, message);
    }

    /**
     * {@inheritDoc}
     * @param id String
     * @param serializer ISerializer
     * @param factory IPacketFactory
     * @return Node
     */
    public Node newNode(String id, ISerializer serializer, IPacketFactory factory) {
        assert id != null;
        assert serializer != null;
        assert factory != null;

        //Lowercase
        id = id.toLowerCase(Locale.ROOT);

        //Return if exists
        Node node = nodes.get(id);
        if(node != null)
            return node;

        //Node instance
        node = super.newNode(id);
        node.setSource(this);
        nodes.put(id, node);

        return node;
    }

    /**
     * {@inheritDoc}
     * @param id String
     * @return Node
     */
    public Node newNode(String id) {
        return this.newNode(id, getSerializer(), getFactory());
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        super.start();
        for(Node node : nodes.values()) {
            node.register("node-state", NodeState.class, new NodeStateHandler(this, node));
            node.broadcast("node-state", new NodeState(node.getId(), true));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        for(Node node : nodes.values())
            node.broadcast("node-state", new NodeState(node.getId(), false));

        network.clear();
        super.stop();
    }

}
