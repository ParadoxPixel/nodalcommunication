package nl.iobyte.nodalcommunication.dsljson.packet;

import nl.iobyte.nodalcommunication.dsljson.JsonSerializer;
import nl.iobyte.nodalcommunication.interfaces.ISerializer;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketPayload;

public class PacketFactory implements IPacketFactory {

    private final ISerializer serializer;

    public PacketFactory() {
        this(new JsonSerializer());
    }

    public PacketFactory(ISerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * {@inheritDoc}
     * @return Class<? extends IPacket<?>>
     */
    public Class<? extends IPacket<?>> getType() {
        return Packet.class;
    }

    /**
     * {@inheritDoc}
     * @return ISerializer
     */
    public ISerializer getSerializer() {
        return serializer;
    }

    /**
     * {@inheritDoc}
     * @param node_id String
     * @param channel String
     * @param payload IPacketPayload
     * @return IPacket<PacketPayload>
     */
    public IPacket<?> create(String node_id, String channel, IPacketPayload payload) {
        if(!(payload instanceof PacketPayload))
            return null;

        return new Packet(node_id, channel, (PacketPayload) payload);
    }

}
