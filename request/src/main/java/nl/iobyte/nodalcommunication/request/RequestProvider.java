package nl.iobyte.nodalcommunication.request;

import de.mkammerer.snowflakeid.SnowflakeIdGenerator;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpiringMap;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacketHandler;
import nl.iobyte.nodalcommunication.Node;
import nl.iobyte.nodalcommunication.request.interfaces.IRequestHandler;
import nl.iobyte.nodalcommunication.request.interfaces.IRequestPacketHandler;
import nl.iobyte.nodalcommunication.request.objects.Request;
import nl.iobyte.nodalcommunication.request.objects.RequestNode;
import nl.iobyte.nodalcommunication.request.objects.packet.RequestPacket;
import nl.iobyte.nodalcommunication.request.objects.packet.RequestPayload;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RequestProvider implements IPacketHandler<RequestPayload> {

    private final SnowflakeIdGenerator generator;
    private final Map<Long, Request> requests;
    private final Map<String, IRequestPacketHandler<?>> handlers = new ConcurrentHashMap<>();

    public RequestProvider() {
        this(
                ThreadLocalRandom.current().nextInt(1024),
                15,
                50
        );
    }

    public RequestProvider(int generatorId, int timeout, int max_alive) {
        this.generator = SnowflakeIdGenerator.createDefault(generatorId);
        this.requests = ExpiringMap.builder()
                .expiration(timeout, TimeUnit.SECONDS)
                .maxSize(max_alive)
                .asyncExpirationListener((ExpirationListener<Long, Request>) (id, request) -> request.fail())
                .build();
    }

    /**
     * Create new request
     * @param channel String
     * @param payload PacketPayload
     * @param type Class<?>
     * @param onSuccess IRequestHandler
     * @param onFail Runnable
     * @return RequestPayload
     */
    public RequestPayload request(String channel, PacketPayload payload, Class<?> type, IRequestHandler onSuccess, Runnable onFail) {
        long id = generator.getGeneratorId();

        //New request
        Request request = new Request(id, type, onSuccess, onFail);
        requests.put(id, request);

        return new RequestPayload(id, channel, payload);
    }

    /**
     * Wrap node for easier request handling
     * @param node Node
     * @param channel String
     * @return RequestNode
     */
    public RequestNode wrapNode(Node node, String channel) {
        return new RequestNode(node, channel, this);
    }

    /**
     * Register request handler
     * @param channel String
     * @param handler IRequestPacketHandler<T>
     * @param <T> extends PacketPayload
     */
    public <T extends PacketPayload> void register(String channel, IRequestPacketHandler<T> handler) {
        handlers.put(channel, handler);
    }

    /**
     * Handle request packet
     * @param packet IPacket<RequestPayload>
     */
    public void handle(IPacket<RequestPayload> packet) {
        //Get payload
        RequestPayload payload = packet.getPayload();

        //Check if response
        Request request = requests.remove(payload.getRequestId());
        if(request != null) {
            //Check if valid response type
            if(!request.getType().isInstance(payload.getPayload()) && !request.getType().isAssignableFrom(payload.getPayload().getClass())) {
                request.fail();
                return;
            }

            //Handle response
            request.success(payload);
            return;
        }

        //Get request handler
        IRequestPacketHandler<?> handler = handlers.get(payload.getChannel());
        if(handler == null)
            return;

        //Handle request
        RequestPacket<?> requestPacket = new RequestPacket<>(
                payload.getPayload().getClass(),
                packet
        );
        handler.handleRaw(requestPacket);
    }

}
