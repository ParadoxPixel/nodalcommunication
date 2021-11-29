package nl.iobyte.nodalcommunication.dsljson.packet;

import nl.iobyte.nodalcommunication.objects.AbstractPacket;

public class Packet extends AbstractPacket<PacketPayload> {

    private final String node_id,channel;
    private final PacketPayload payload;

    public Packet(String node_id, String channel, PacketPayload payload) {
        this.node_id = node_id;
        this.channel = channel;
        this.payload = payload;
    }

    /**
     * {@inheritDoc}
     * @return String
     */
    public String getNodeId() {
        return node_id;
    }

    /**
     * {@inheritDoc}
     * @return String
     */
    public String getChannel() {
        return channel;
    }

    /**
     * {@inheritDoc}
     * @return PacketPayload
     */
    public PacketPayload getPayload() {
        return payload;
    }

    /**
     * Get payload as Type
     * @param clazz T
     * @param <T> extends PacketPayload
     * @return T
     */
    public <T extends PacketPayload> T getPayloadAs(Class<T> clazz) {
        return payload.asType(clazz);
    }

}
