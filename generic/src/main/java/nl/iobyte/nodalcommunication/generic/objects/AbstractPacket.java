package nl.iobyte.nodalcommunication.generic.objects;

import nl.iobyte.nodalcommunication.generic.Node;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacketPayload;

public abstract class AbstractPacket<T extends IPacketPayload> implements IPacket<T> {

    private transient Node node = null;

    /**
     * Set node to packet
     * @param node Node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * {@inheritDoc}
     * @param payload Object
     */
    public void reply(IPacketPayload payload) {
        reply(getChannel(), payload);
    }

    /**
     * {@inheritDoc}
     * @param channel String
     * @param payload Object
     */
    public void reply(String channel, IPacketPayload payload) {
        if(node == null)
            return;

        node.send(getNodeId(), getChannel(), payload);
    }

    /**
     * {@inheritDoc}
     * @param target String
     * @param channel String
     * @param payload Object
     */
    public void send(String target, String channel, IPacketPayload payload) {
        if(node == null)
            return;

        node.send(getNodeId(), getChannel(), payload);
    }

}
