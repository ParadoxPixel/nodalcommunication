package nl.iobyte.nodalcommunication.basic;

import nl.iobyte.nodalcommunication.generic.Node;
import nl.iobyte.nodalcommunication.discovery.DiscoveryWrapper;
import nl.iobyte.nodalcommunication.discovery.network.objects.Network;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacketHandler;
import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacketPayload;
import nl.iobyte.nodalcommunication.request.RequestProvider;
import nl.iobyte.nodalcommunication.request.interfaces.IRequestPacketHandler;
import nl.iobyte.nodalcommunication.request.objects.packet.RequestPayload;
import java.util.concurrent.CompletableFuture;

public class BasicNode {

    private final Node node;
    private final RequestProvider provider;
    private final Network network;

    public BasicNode(Node node, Network network) {
        this.node = node;
        this.provider = new RequestProvider();
        this.network = network;

        //Start Listener
        node.getSource().on(() -> node.register(
                "requests",
                RequestPayload.class,
                provider
        ), null);
    }

    /**
     * Get node identifier
     * @return String
     */
    public String getId() {
        return node.getId();
    }

    /**
     * Get node's network
     * @return Network
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * Send packet to target
     * @param target String
     * @param channel String
     * @param payload IPacketPayload
     */
    public void send(String target, String channel, IPacketPayload payload) {
        node.send(target, channel, payload);
    }

    /**
     * Broadcast message
     * @param channel String
     * @param payload IPacketPayload
     */
    public void broadcast(String channel, IPacketPayload payload) {
       node.broadcast(channel, payload);
    }

    /**
     * Register message handler for channel
     * @param channel_id String
     * @param clazz Class<T>
     * @param handler IPacketHandler<T>
     * @param <T> extends IPacketPayload
     */
    public <T extends IPacketPayload> void register(String channel_id, Class<T> clazz, IPacketHandler<T> handler) {
        node.register(channel_id, clazz, handler);
    }

    /**
     * Send request to target on channel
     * @param target String
     * @param channel String
     * @param payload PacketPayload
     * @param type Class<T>
     * @param <T> extends PacketPayload
     * @return CompletableFuture<T>
     */
    @SuppressWarnings("unchecked")
    public <T extends PacketPayload> CompletableFuture<T> request(String target, String channel, PacketPayload payload, Class<T> type) {
        //Create payload
        CompletableFuture<T> future = new CompletableFuture<>();
        RequestPayload request = provider.request(
                channel,
                payload,
                type,
                response -> {
                    try {
                        future.complete((T) response);
                    } catch (Exception e) {
                        future.complete(null);
                    }
                },
                () -> future.complete(null)
        );

        //Send request
        node.send(target, "requests", request);
        return future;
    }

    /**
     * Register request handler
     * @param channel String
     * @param handler IRequestPacketHandler<T>
     * @param <T> extends PacketPayload
     */
    public <T extends PacketPayload> void register(String channel, IRequestPacketHandler<T> handler) {
        provider.register(channel, handler);
    }

    /**
     * Get BasicNode of id and discovery wrapper
     * @param id String
     * @param discovery DiscoveryWrapper
     * @return BasicNode
     */
    public static BasicNode of(String id, DiscoveryWrapper discovery) {
        return new BasicNode(
                discovery.newNode(id),
                discovery.getNetwork()
        );
    }

}
