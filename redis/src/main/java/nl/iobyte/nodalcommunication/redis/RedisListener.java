package nl.iobyte.nodalcommunication.redis;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import nl.iobyte.nodalcommunication.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.namespace.NamespaceComparator;
import nl.iobyte.nodalcommunication.Node;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ForkJoinPool;

public class RedisListener extends RedisPubSubAdapter<String, String> {

    private final Node node;
    private final NamespaceComparator comparator;

    public RedisListener(Node node) {
        this.node = node;
        comparator = new NamespaceComparator(node.getId());
    }

    /**
     * Handle incoming data
     * @param channel String
     * @param message String
     */
    public void message(String channel, String message) {
        if(!message.contains("_"))
            return;

        String[] parts = message.split("_", 2);
        if(comparator.compare(parts[0]) < 0)
            return;

        if(!parts[1].startsWith("{"))
            return;

        //Run async
        ForkJoinPool.commonPool().execute(() -> {
            IPacket<?> packet = node.getFactory().getSerializer().deserialize(
                    parts[1].getBytes(StandardCharsets.UTF_8),
                    node.getFactory().getType()
            );
            if(packet == null)
                return;

            node.handle(packet);
        });
    }

}
