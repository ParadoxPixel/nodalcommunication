package nl.iobyte.nodalcommunication.generic;

import nl.iobyte.nodalcommunication.generic.interfaces.*;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacketHandler;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacketPayload;
import nl.iobyte.nodalcommunication.generic.objects.AbstractPacket;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Node {

    private final String id;
    private IPacketSource source;
    private final IPacketFactory factory;
    private final Map<String, List<Channel<?>>> channels = new HashMap<>();

    public Node(String id, IPacketSource source, IPacketFactory factory) {
        this.id = id;
        this.source = source;
        this.factory = factory;
    }

    /**
     * Get node identifier
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Get source of Node
     * @return IPacketSource
     */
    public IPacketSource getSource() {
        return source;
    }

    /**
     * Change source of Node
     */
    public void setSource(IPacketSource source) {
        this.source = source;
    }

    /**
     * Get packet factory
     * @return IPacketFactory
     */
    public IPacketFactory getFactory() {
        return factory;
    }

    /**
     * Send packet to target
     * @param target String
     * @param channel String
     * @param payload IPacketPayload
     */
    public void send(String target, String channel, IPacketPayload payload) {
        assert target != null;
        assert channel != null;
        assert payload != null;

        IPacket<?> packet = factory.create(id, channel, payload);
        if(packet == null)
            return;

        //Send on channel
        source.send(this, target, packet);
    }

    /**
     * Broadcast message
     * @param channel String
     * @param payload IPacketPayload
     */
    public void broadcast(String channel, IPacketPayload payload) {
        send("**", channel, payload);
    }

    /**
     * Register message handler for channel
     * @param channel_id String
     * @param clazz Class<T>
     * @param handler IPacketHandler<T>
     * @param <T> extends IPacketPayload
     */
    @SuppressWarnings("unchecked")
    public <T extends IPacketPayload> void register(String channel_id, Class<T> clazz, IPacketHandler<T> handler) {
        //Get channels
        List<Channel<?>> list = channels.computeIfAbsent(
                channel_id,
                key -> {
                    source.register(this, channel_id);
                    return new ArrayList<>();
                }
        );

        //Find channel or create new
        Channel<T> channel = (Channel<T>) list.stream()
                .filter(ch -> ch.geType() == clazz)
                .findAny()
                .orElse(null);

        //Create channel if none found
        if(channel == null) {
            channel = new Channel<>(
                    channel_id,
                    clazz
            );
            list.add(channel);
        }

        //Register handler
        channel.register(handler);
    }

    /**
     * Handle incoming message
     * @param packet IPacket<?>
     */
    public void handle(IPacket<?> packet) {
        if(!channels.containsKey(packet.getChannel()))
            return;

        //Set node to packet if possible
        if(packet instanceof AbstractPacket<?>)
            ((AbstractPacket<?>) packet).setNode(this);

        List<Channel<?>> list = channels.get(id);
        if(list == null || list.isEmpty())
            return;

        //If only 1 handler, handle in current Thread
        if(list.size() == 1) {
            list.get(0).handleRaw(packet);
            return;
        }

        //New Thread for each handler(reuse current) to run in parallel
        int i = 0;
        for(Channel<?> channel : list) {
            //New Thread, except last entry
            if((i++ + 1) < list.size()) {
                ForkJoinPool.commonPool().execute(() -> channel.handleRaw(packet));
                continue;
            }

            //Handle in current thread
            channel.handleRaw(packet);
        }
    }

}
