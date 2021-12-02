package nl.iobyte.nodalcommunication.dsljson;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;
import nl.iobyte.nodalcommunication.dsljson.adapters.PacketAdapter;
import nl.iobyte.nodalcommunication.dsljson.adapters.PacketPayloadAdapter;
import nl.iobyte.nodalcommunication.dsljson.packet.Packet;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;
import nl.iobyte.nodalcommunication.generic.interfaces.ISerializer;
import java.nio.charset.StandardCharsets;

public class JsonSerializer implements ISerializer {

    private final DslJson<Object> json;
    private final ThreadLocal<JsonWriter> writer;

    public JsonSerializer() {
        json = new DslJson<>(
                Settings.withRuntime()
                        .allowArrayFormat(true)
                        .includeServiceLoader()
        );

        PacketAdapter packetAdapter = new PacketAdapter(json);
        json.registerReader(Packet.class, packetAdapter);
        json.registerWriter(Packet.class, packetAdapter);

        PacketPayloadAdapter packetPayloadAdapter = new PacketPayloadAdapter(json);
        json.registerReader(PacketPayload.class, packetPayloadAdapter);
        json.registerWriter(PacketPayload.class, packetPayloadAdapter);

        writer = ThreadLocal.withInitial(json::newWriter);
    }

    /**
     * Get json
     * @return DslJson<Object>
     */
    public DslJson<Object> getJson() {
        return json;
    }

    /**
     * {@inheritDoc}
     * @param obj Object
     * @return String
     */
    public String serialize(Object obj) {
        //Get writer and reset buffer
        JsonWriter w = writer.get();
        w.reset();

        //Serialize object
        try {
            json.serialize(w, obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //Return result
        return new String(w.getByteBuffer(), 0, w.size(), StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     * @param bytes byte[]
     * @param clazz Class<T>
     * @param <T> T
     * @return T
     */
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T obj;
        try {
            obj = json.deserialize(clazz, bytes, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return obj;
    }

}
