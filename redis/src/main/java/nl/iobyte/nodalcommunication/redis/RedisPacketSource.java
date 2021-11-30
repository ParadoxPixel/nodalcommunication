package nl.iobyte.nodalcommunication.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import nl.iobyte.nodalcommunication.Node;
import nl.iobyte.nodalcommunication.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.interfaces.IPacketSource;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RedisPacketSource implements IPacketSource {

    //Redis Client
    private final RedisURI uri;
    private RedisClient redisPub, redisSub;
    private final AtomicBoolean enabled = new AtomicBoolean(false);

    //Redis Pub/Sub
    private StatefulRedisPubSubConnection<String, String> redisSubConnection;
    private StatefulRedisPubSubConnection<String, String> redisPubConnection;
    private RedisPubSubAsyncCommands<String, String> asyncPub;
    private RedisPubSubAsyncCommands<String, String> asyncSub;

    //Listeners
    private final List<String> nodeListeners = new ArrayList<>();

    public RedisPacketSource(String host, int port, boolean use_ssl, String password) {
        assert host != null;
        assert port >= 1204 && port <= 49151;
        assert password != null;

        RedisURI.Builder builder = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withSsl(use_ssl);
        if (!password.equals("none"))
            builder.withPassword((CharSequence) password);

        //Url
        uri = builder.build();
    }

    /**
     * {@inheritDoc}
     * @param target String
     * @param packet IPacket<?>
     */
    public void send(Node node, String target, IPacket<?> packet) {
        if(!enabled.get())
            return;

        String message = node.getFactory().getSerializer().serialize(packet);
        if(message == null)
            return;

        asyncPub.publish(packet.getChannel(), target+"_"+message);
    }

    /**
     * {@inheritDoc}
     * @param node Node
     * @param channel String
     */
    public void register(Node node, String channel) {
        if(!enabled.get())
            return;

        if(!nodeListeners.contains(node.getId())) {
            nodeListeners.add(node.getId());
            redisSubConnection.addListener(new RedisListener(node));
        }

        asyncSub.subscribe(channel);
    }

    /**
     * {@inheritDoc}
     * @param id String
     * @param factory IPacketFactory
     * @return Node
     */
    public Node newNode(String id, IPacketFactory factory) {
        return new Node(
                id,
                this,
                factory
        );
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        if(enabled.get())
            return;

        //Subscriber
        redisSub = RedisClient.create(uri);
        redisSub.setOptions(
                ClientOptions.builder().autoReconnect(true).build()
        );
        redisSubConnection = redisSub.connectPubSub();
        asyncSub = redisSubConnection.async();

        //Publisher
        redisPub = RedisClient.create(uri);
        redisPub.setOptions(
                ClientOptions.builder().autoReconnect(true).build()
        );
        redisPubConnection = redisPub.connectPubSub();
        asyncPub = redisPubConnection.async();

        //Enable
        enabled.set(true);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        if(!enabled.get())
            return;

        //Disable
        enabled.set(false);

        //Sub
        asyncSub.shutdown(true);
        redisSubConnection.close();
        redisSub.shutdown();

        //Pub
        asyncPub.shutdown(true);
        redisPubConnection.close();
        redisPub.shutdown();
    }

}
