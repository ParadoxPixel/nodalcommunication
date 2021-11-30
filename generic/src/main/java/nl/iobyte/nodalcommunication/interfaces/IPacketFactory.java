package nl.iobyte.nodalcommunication.interfaces;

import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketPayload;

public interface IPacketFactory {

    /**
     * Get class of IPacket extension
     * @return Class<? extends IPacket<?>>
     */
    Class<? extends IPacket<?>> getType();

    /**
     * Get serializer
     * @return ISerializer
     */
    ISerializer getSerializer();

    /**
     * Create packet
     * @param node_id String
     * @param channel String
     * @param payload IPacketPayload
     * @return IPacket<?>
     */
    IPacket<?> create(String node_id, String channel, IPacketPayload payload);

}
