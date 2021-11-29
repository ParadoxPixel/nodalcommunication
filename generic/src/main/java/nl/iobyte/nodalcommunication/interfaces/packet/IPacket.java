package nl.iobyte.nodalcommunication.interfaces.packet;

public interface IPacket<T extends IPacketPayload> {

    /**
     * Get id of sender
     * @return String
     */
    String getNodeId();

    /**
     * Get channel
     * @return String
     */
    String getChannel();

    /**
     * Get packet payload
     * @return T
     */
    T getPayload();

    /**
     * Reply to node on same channel
     * @param payload Object
     */
    void reply(IPacketPayload payload);

    /**
     * Reply to node
     * @param channel String
     * @param payload IPacketPayload
     */
    void reply(String channel, IPacketPayload payload);

    /**
     * Send message through receiving node
     * @param target String
     * @param channel String
     * @param payload IPacketPayload
     */
    void send(String target, String channel, IPacketPayload payload);

}
