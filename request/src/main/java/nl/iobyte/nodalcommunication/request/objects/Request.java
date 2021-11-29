package nl.iobyte.nodalcommunication.request.objects;

import nl.iobyte.nodalcommunication.request.interfaces.IRequestHandler;
import nl.iobyte.nodalcommunication.request.objects.packet.RequestPayload;
import java.util.concurrent.atomic.AtomicBoolean;

public class Request {

    private final long id;
    private final Class<?> type;
    private final IRequestHandler onSuccess;
    private final Runnable onFail;
    private final AtomicBoolean b = new AtomicBoolean(false);

    public Request(long id, Class<?> type, IRequestHandler onSuccess, Runnable onFail) {
        this.id = id;
        this.type = type;
        this.onSuccess = onSuccess;
        this.onFail = onFail;
    }

    /**
     * Get identifier
     * @return Long
     */
    public long getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * Handle on success
     * @param request RequestPayload
     */
    public void success(RequestPayload request) {
        if(b.getAndSet(true))
            return;

        onSuccess.handle(request);
    }

    /**
     * Handle on fail
     */
    public void fail() {
        if(onFail == null)
            return;

        if(b.getAndSet(true))
            return;

        onFail.run();
    }

}
