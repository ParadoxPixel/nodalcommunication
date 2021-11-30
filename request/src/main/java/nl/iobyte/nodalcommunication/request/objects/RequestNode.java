package nl.iobyte.nodalcommunication.request.objects;

import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;
import nl.iobyte.nodalcommunication.Node;
import nl.iobyte.nodalcommunication.request.RequestProvider;
import nl.iobyte.nodalcommunication.request.interfaces.IRequestPacketHandler;
import nl.iobyte.nodalcommunication.request.objects.packet.RequestPayload;
import java.util.concurrent.CompletableFuture;

public class RequestNode {

    private final Node node;
    private final String channel;
    private final RequestProvider provider;

    public RequestNode(Node node, String channel, RequestProvider provider) {
        this.node = node;
        this.channel = channel;
        this.provider = provider;
    }

    /**
     * Start request node
     */
    public void register() {
        node.register(channel, RequestPayload.class, provider);
    }

    /**
     * Register request handler
     * @param channel String
     * @param handler IRequestPacketHandler<T>
     * @param <T>     extends PacketPayload
     */
    public <T extends PacketPayload> void register(String channel, IRequestPacketHandler<T> handler) {
        provider.register(channel, handler);
    }

    /**
     * Send request
     * @param target  String
     * @param channel String
     * @param payload PacketPayload
     * @param clazz   Class<T>
     * @param <T>     T
     * @return CompletableFuture<T>
     */
    public <T extends PacketPayload> CompletableFuture<T> request(String target, String channel, PacketPayload payload, Class<T> clazz) {
        CompletableFuture<T> future = new CompletableFuture<>();
        RequestPayload rp = provider.request(
                channel,
                payload,
                clazz,
                response -> future.complete(response.getPayload().asType(clazz)),
                () -> future.complete(null)
        );

        node.send(target, this.channel, rp);
        return future;
    }

}
