package nl.iobyte.nodalcommunication.redis;

import io.lettuce.core.pubsub.RedisPubSubAdapter;
import nl.iobyte.nodalcommunication.namespace.NamespaceComparator;
import nl.iobyte.nodalcommunication.objects.Node;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

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

        node.handle(channel, parts[1].getBytes(StandardCharsets.UTF_8));
    }

}
