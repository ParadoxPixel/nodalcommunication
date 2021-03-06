package nl.iobyte.nodalcommunication.generic.interfaces.packet;

public interface IPacketHandler<T extends IPacketPayload> {

    /**
     * Handle packet
     * @param packet IPacket<T>
     */
    void handle(IPacket<T> packet);

}
