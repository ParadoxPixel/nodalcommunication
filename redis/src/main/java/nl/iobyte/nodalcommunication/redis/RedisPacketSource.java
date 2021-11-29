package nl.iobyte.nodalcommunication.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import nl.iobyte.nodalcommunication.interfaces.IPacketFactory;
import nl.iobyte.nodalcommunication.interfaces.IPacketSource;
import nl.iobyte.nodalcommunication.interfaces.ISerializer;
import nl.iobyte.nodalcommunication.objects.Node;
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
    private final List<String> listeners = new ArrayList<>();

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
     * @param channel String
     * @param message byte[]
     */
    public void send(String target, String channel, String message) {
        if(!enabled.get())
            return;

        asyncPub.publish(channel, target+"_"+message);
    }

    /**
     * {@inheritDoc}
     * @param node Node
     * @param channel String
     */
    public void register(Node node, String channel) {
        if(!enabled.get())
            return;

        if(!listeners.contains(node.getId())) {
            listeners.add(node.getId());
            redisSubConnection.addListener(new RedisListener(node));
        }

        asyncSub.subscribe(channel);
    }

    /**
     * {@inheritDoc}
     * @param id String
     * @param serializer ISerializer
     * @param factory IPacketFactory
     * @return Node
     */
    public Node newNode(String id, ISerializer serializer, IPacketFactory factory) {
        return new Node(
                id,
                serializer,
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
