package nl.iobyte.nodalcommunication.discovery.packet;

import nl.iobyte.nodalcommunication.discovery.DiscoveryWrapper;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacketHandler;
import nl.iobyte.nodalcommunication.generic.Node;

public class NodeStateHandler implements IPacketHandler<NodeState> {

    private final DiscoveryWrapper wrapper;
    private final Node node;

    public NodeStateHandler(DiscoveryWrapper wrapper, Node node) {
        this.wrapper = wrapper;
        this.node = node;
    }

    /**
     * {@inheritDoc}
     * @param packet IPacket<NodeState>
     */
    public void handle(IPacket<NodeState> packet) {
        NodeState state = packet.getPayload();
        if (wrapper.getNodes().containsKey(state.getNodeId()))
            return;

        //Send state to received node
        if (state.isOnline())
            packet.reply(new NodeState(node.getId(), true));

        //Update Node state in Network
        wrapper.getNetwork().toggle(state.getNodeId(), state.isOnline());
    }

}
