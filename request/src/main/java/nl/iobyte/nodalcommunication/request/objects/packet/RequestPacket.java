package nl.iobyte.nodalcommunication.request.objects.packet;

import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.request.interfaces.IRequestPacket;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestPacket<T extends PacketPayload> implements IRequestPacket<T> {

    private final Class<T> clazz;
    private final IPacket<RequestPayload> packet;
    private final AtomicBoolean replied;

    public RequestPacket(Class<T> clazz, IPacket<RequestPayload> packet) {
        this.clazz = clazz;
        this.packet = packet;
        this.replied = new AtomicBoolean(false);
    }

    /**
     * {@inheritDoc}
     * @return Long
     */
    public long getRequestId() {
        return packet.getPayload().getRequestId();
    }

    /**
     * {@inheritDoc}
     * @return String
     */
    public String getNodeId() {
        return packet.getNodeId();
    }

    /**
     * {@inheritDoc}
     * @return String
     */
    public String getChannel() {
        return packet.getPayload().getChannel();
    }

    /**
     * {@inheritDoc}
     * @return T
     */
    public T getPayload() {
        return packet.getPayload().getPayload().asType(clazz);
    }

    /**
     * {@inheritDoc}
     * @param payload PacketPayload
     */
    public void reply(PacketPayload payload) {
        if(replied.getAndSet(true))
            return;

        packet.getPayload().setPayload(payload);
        packet.reply(packet.getPayload());
    }

    /**
     * Check if got reply
     * @return Boolean
     */
    public boolean replied() {
        return replied.get();
    }

}
