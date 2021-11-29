package nl.iobyte.nodalcommunication.dsljson.adapters;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.StringConverter;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;
import java.io.IOException;

public class PacketPayloadAdapter implements JsonReader.ReadObject<PacketPayload>, JsonWriter.WriteObject<PacketPayload> {

    private final DslJson<Object> json;

    public PacketPayloadAdapter(DslJson<Object> json) {
        this.json = json;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PacketPayload read(JsonReader reader) throws IOException {
        if(reader.last() != 123)
            throw reader.newParseError("Expecting '{' for object start");

        reader.getNextToken();
        reader.fillName();
        reader.getNextToken();
        String type = StringConverter.deserialize(reader);

        //Get class
        Class<?> c;
        try {
            c = Class.forName(type);
        } catch (Exception e) {
            throw new IOException("unable to find class: "+type);
        }

        //Cast type
        Class<? extends PacketPayload> clazz;
        try {
            clazz = (Class<? extends PacketPayload>) c;
        } catch (Exception e) {
            throw new IOException("class doesn't extend PacketPayload: "+type);
        }

        reader.getNextToken();
        reader.getNextToken();

        reader.fillName();
        reader.getNextToken();
        JsonReader.ReadObject<? extends PacketPayload> read = json.tryFindReader(clazz);
        if(read == null)
            throw new IOException("unable to find reader for: "+clazz);

        PacketPayload payload = read.read(reader);
        if(payload == null)
            throw new IOException("unable to read payload: "+clazz);

        reader.getNextToken();
        return payload;
    }

    @Override
    public void write(JsonWriter writer, PacketPayload payload) {
        assert payload != null;
        writer.writeAscii("{\"type\":\""+payload.getClass().getName()+"\":\"data\":");

        JsonWriter.WriteObject<? extends PacketPayload> write = json.tryFindWriter(payload.getClass());
        if(write == null) {
            writer.writeAscii("{}");
        } else {
            write.write(writer, payload.cast());
        }

        writer.writeAscii("}");
    }

}
