package nl.iobyte.nodalcommunication.request.objects.packet;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;

@CompiledJson
public class RequestPayload extends PacketPayload {

    @JsonAttribute(name = "request_id")
    private final long requestId;
    private final String channel;
    private PacketPayload payload;

    public RequestPayload(long requestId, String channel, PacketPayload payload) {
        this.requestId = requestId;
        this.channel = channel;
        this.payload = payload;
    }

    /**
     * Get request identifier
     * @return Long
     */
    public long getRequestId() {
        return requestId;
    }

    /**
     * Get request channel
     * @return String
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Get request payload
     * @return PacketPayload
     */
    public PacketPayload getPayload() {
        return payload;
    }

    /**
     * Set request payload
     * @param payload PacketPayload
     */
    public void setPayload(PacketPayload payload) {
        this.payload = payload;
    }

    /**
     * {@inheritDoc}
     * @return RequestPayload
     */
    @SuppressWarnings("unchecked")
    public RequestPayload cast() {
        return this;
    }

}
