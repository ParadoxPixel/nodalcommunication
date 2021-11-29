package nl.iobyte.nodalcommunication.dsljson.packet;

import nl.iobyte.nodalcommunication.interfaces.packet.IPacketPayload;

public abstract class PacketPayload implements IPacketPayload {

    /**
     * Cast general to capture
     * @param <T> extends PacketPayload
     * @return T
     */
    public abstract <T extends PacketPayload> T cast();

    /**
     * Get payload as type
     * @param clazz Class<T>
     * @param <T> extends PacketPayload
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T extends PacketPayload> T asType(Class<T> clazz) {
        T obj;
        try {
            obj = (T) this;
        } catch (Exception e) {
            return null;
        }

        return obj;
    }

}
