package nl.iobyte.nodalcommunication.dsljson.packet;

import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketPayload;

public class PacketFactory implements IPacketFactory {

    /**
     * {@inheritDoc}
     * @return Class<? extends IPacket<?>>
     */
    public Class<? extends IPacket> getClazz() {
        return Packet.class;
    }

    /**
     * {@inheritDoc}
     * @param node_id String
     * @param channel String
     * @param payload IPacketPayload
     * @return IPacket<PacketPayload>
     */
    public IPacket<?> create(String node_id, String channel, IPacketPayload payload) {
        if(!(payload instanceof PacketPayload pp))
            return null;

        return new Packet(node_id, channel, pp);
    }

}
