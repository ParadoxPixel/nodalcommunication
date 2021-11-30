package nl.iobyte.nodalcommunication.discovery.network.interfaces;

public interface NetworkListener {

    /**
     * Called when Node joins the Network
     * @param node_id String
     */
    void onJoin(String node_id);

    /**
     * Called when Node leaves the Network
     * @param node_id String
     */
    void onLeave(String node_id);

}
