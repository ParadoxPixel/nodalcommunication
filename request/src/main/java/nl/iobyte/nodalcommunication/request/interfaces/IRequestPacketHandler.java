package nl.iobyte.nodalcommunication.request.interfaces;

import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;

public interface IRequestPacketHandler<T extends PacketPayload> {

    /**
     * Handle request
     * @param request IRequestPacket<T>
     */
    void handle(IRequestPacket<T> request);

    /**
     * Handle request raw
     * @param o Object
     */
    @SuppressWarnings("unchecked")
    default void handleRaw(IRequestPacket<?> o) {
        IRequestPacket<T> obj;
        try {
            obj = (IRequestPacket<T>) o;
        } catch (Exception e) {
            return;
        }

        handle(obj);
    }

}
