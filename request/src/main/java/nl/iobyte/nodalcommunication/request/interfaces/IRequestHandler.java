package nl.iobyte.nodalcommunication.request.interfaces;

import nl.iobyte.nodalcommunication.request.objects.packet.RequestPayload;

public interface IRequestHandler {

    /**
     * Handle request payload
     * @param request RequestPayload
     */
    void handle(RequestPayload request);

}
