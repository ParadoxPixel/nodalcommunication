package nl.iobyte.nodalcommunication.generic.interfaces;

import nl.iobyte.nodalcommunication.generic.interfaces.packet.IPacket;
import nl.iobyte.nodalcommunication.generic.Node;

public interface IPacketSource {

    /**
     * Send message on channel to target
     * @param node Node
     * @param target String
     * @param packet IPacket<?>
     */
    void send(Node node, String target, IPacket<?> packet);

    /**
     * Register listening to channel
     * @param node Node
     * @param channel String
     */
    void register(Node node, String channel);

    /**
     * Get Node from Source
     * @param id String
     * @param factory IPacketFactory
     * @return Node
     */
    Node newNode(String id, IPacketFactory factory);

    /**
     * Handle at start/stop
     * @param start Runnable
     * @param stop Runnable
     */
    void on(Runnable start, Runnable stop);

    /**
     * Start source
     */
    void start();

    /**
     * Stop source
     */
    void stop();

}
