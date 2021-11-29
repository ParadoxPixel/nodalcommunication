package nl.iobyte.nodalcommunication.objects;

import nl.iobyte.nodalcommunication.interfaces.*;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketHandler;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketPayload;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class Node {

    private final String id;
    private final ISerializer serializer;
    private IPacketSource source;
    private final IPacketFactory factory;
    private final Map<String, List<Channel<?>>> channels = new HashMap<>();

    public Node(String id, ISerializer serializer, IPacketSource source, IPacketFactory factory) {
        this.id = id;
        this.serializer = serializer;
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
     * Change source of Node
     */
    public void setSource(IPacketSource source) {
        this.source = source;
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

        //Object to String
        String str = serializer.serialize(packet);
        if(str == null)
            return;

        //Send on channel
        source.send(target, packet.getChannel(), str);
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
                key -> new ArrayList<>()
        );

        //Find channel or create new
        Channel<T> channel = (Channel<T>) list.stream()
                .filter(ch -> ch.getClazz() == clazz)
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

        //Register at source
        source.register(this, channel_id);
    }

    /**
     * Handle incoming message
     * @param id String
     * @param bytes byte[]
     */
    public void handle(String id, byte[] bytes) {
        if(!channels.containsKey(id))
            return;

        //Get applicable channels
        channels.get(id)
                //Handle async for all channels
                .forEach(channel -> ForkJoinPool.commonPool()
                        .execute(() -> {

                            //String to channel specific object
                            IPacket<?> packet = serializer.deserialize(bytes, factory.getClazz());
                            if(packet == null)
                                return;

                            //Set node to packet if possible
                            if(packet instanceof AbstractPacket<?> ap)
                                ap.setNode(this);

                            //Handle message
                            channel.handleRaw(packet);
                        })
                );
    }

}
