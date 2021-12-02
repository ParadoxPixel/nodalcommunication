package nl.iobyte.nodalcommunication.objects;

import nl.iobyte.nodalcommunication.interfaces.IPacketSource;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPacketSource implements IPacketSource {

    private final List<Runnable> starts = new ArrayList<>();
    private final List<Runnable> stops = new ArrayList<>();

    /**
     * {@index}
     * @param start Runnable
     * @param stop Runnable
     */
    public void on(Runnable start, Runnable stop) {
        if(start != null)
            starts.add(start);

        if(stop != null)
            stops.add(stop);
    }

    /**
     * Call start listeners
     */
    public void onStart() {
        starts.forEach(Runnable::run);
    }

    /**
     * Call stop listeners
     */
    public void onStop() {
        stops.forEach(Runnable::run);
    }

}
