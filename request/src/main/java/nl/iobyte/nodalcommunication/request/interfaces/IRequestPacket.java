package nl.iobyte.nodalcommunication.request.interfaces;

import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;

public interface IRequestPacket<T extends PacketPayload> {

    /**
     * Get request id
     * @return Long
     */
    long getRequestId();

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
    void reply(PacketPayload payload);

}
