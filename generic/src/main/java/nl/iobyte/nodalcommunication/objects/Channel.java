package nl.iobyte.nodalcommunication.objects;

import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketHandler;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Channel<T extends IPacketPayload> {

    private final String id;
    private final Class<T> clazz;
    private final List<IPacketHandler<T>> handlers = new ArrayList<>();

    public Channel(String id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    /**
     * Get channel identifier
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Get clazz of channel message
     * @return Class<T>
     */
    public Class<T> getClazz() {
        return clazz;
    }

    /**
     * Register message handler
     * @param handler IPacketHandler<T>
     */
    public void register(IPacketHandler<T> handler) {
        assert handler != null;
        handlers.add(handler);
    }

    /**
     * Handle message with reuse of current Thread
     * @param packet AbstractPacket<T>
     */
    public void handle(IPacket<T> packet) {
        assert packet != null;

        //If only 1 handler, handle in current Thread
        if(handlers.size() == 1) {
            handlers.get(0).handle(packet);
            return;
        }

        //New Thread for each handler(reuse current) to run in parallel
        int i = 0;
        for(IPacketHandler<T> handler : handlers) {
            //New Thread, except last entry
            if((i++ + 1) < handlers.size()) {
                ForkJoinPool.commonPool().execute(() -> handler.handle(packet));
                continue;
            }

            //Handle in current thread
            handler.handle(packet);
        }
    }

    /**
     * Handle raw message
     * @param obj Object
     */
    @SuppressWarnings("unchecked")
    public void handleRaw(Object obj) {
        assert obj != null;

        //Cast
        IPacket<T> o;
        try {
            o = (IPacket<T>) obj;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //Handle as normal
        handle(o);
    }

}
