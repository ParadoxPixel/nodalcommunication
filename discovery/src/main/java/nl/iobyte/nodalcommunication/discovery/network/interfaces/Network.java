package nl.iobyte.nodalcommunication.discovery.network.interfaces;

import nl.iobyte.nodalcommunication.discovery.namespace.NamespaceSet;
import nl.iobyte.nodalcommunication.discovery.network.objects.NetworkListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Network extends NamespaceSet {

    private final List<NetworkListener> listeners = new ArrayList<>();

    /**
     * Listen to changes in the Network
     * @param listener NetworkListener
     */
    public void listen(NetworkListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean add(String s) {
        if(!super.add(s))
            return false;

        //Call listeners async
        ForkJoinPool.commonPool().execute(() ->listeners.forEach(listener -> listener.onJoin(s)));
        return true;
    }

    @Override
    public boolean remove(String s) {
        if(!super.remove(s))
            return false;

        //Call listeners async
        ForkJoinPool.commonPool().execute(() ->listeners.forEach(listener -> listener.onLeave(s)));
        return true;
    }

    /**
     * Toggle Node in Network
     * @param id String
     * @param b Boolean
     */
    public void toggle(String id, boolean b) {
        if(b) {
            add(id);
            return;
        }

        remove(id);
    }

}
