package nl.iobyte.nodalcommunication.discovery;

import nl.iobyte.nodalcommunication.Node;
import nl.iobyte.nodalcommunication.discovery.network.objects.Network;
import nl.iobyte.nodalcommunication.discovery.packet.NodeState;
import nl.iobyte.nodalcommunication.discovery.packet.NodeStateHandler;
import nl.iobyte.nodalcommunication.dsljson.JsonWrapper;
import nl.iobyte.nodalcommunication.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.interfaces.IPacketSource;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryWrapper extends JsonWrapper {

    private final Map<String, Node> nodes = new ConcurrentHashMap<>();
    private final Network network = new Network();

    public DiscoveryWrapper(IPacketSource source) {
        super(source);
    }

    public DiscoveryWrapper(IPacketSource source, IPacketFactory factory) {
        super(source, factory);
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
     * @param node Node
     * @param target String
     * @param packet IPacket<?>
     */
    public void send(Node node, String target, IPacket<?> packet) {
        //Check if Node is local
        if(nodes.containsKey(target)) {
            //Skip packet source
            nodes.get(target).handle(packet);
            return;
        }

        //Send through packet source
        super.send(node, target, packet);
    }

    /**
     * {@inheritDoc}
     * @param id String
     * @param factory IPacketFactory
     * @return Node
     */
    public Node newNode(String id, IPacketFactory factory) {
        assert id != null;

        //Lowercase
        id = id.toLowerCase(Locale.ROOT);

        //Return if exists
        Node node = nodes.get(id);
        if(node != null)
            return node;

        //Node instance
        node = super.newNode(id, factory);
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
        return this.newNode(id, getFactory());
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
