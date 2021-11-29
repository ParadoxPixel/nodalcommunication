package nl.iobyte.nodalcommunication.interfaces;

import nl.iobyte.nodalcommunication.objects.Node;

public interface IPacketSource {

    /**
     * Send message on channel to target
     * @param target String
     * @param channel String
     * @param message String
     */
    void send(String target, String channel, String message);

    /**
     * Broadcast message
     * @param channel String
     * @param message String
     */
    default void broadcast(String channel, String message) {
        send("**", channel, message);
    }

    /**
     * Register listening to channel
     * @param node Node
     * @param channel String
     */
    void register(Node node, String channel);

    /**
     * Get Node from Source
     * @param id String
     * @param serializer ISerializer
     * @param factory IPacketFactory
     * @return Node
     */
    Node newNode(String id, ISerializer serializer, IPacketFactory factory);

    /**
     * Start source
     */
    void start();

    /**
     * Stop source
     */
    void stop();

}
