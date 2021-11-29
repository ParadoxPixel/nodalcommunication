package nl.iobyte.nodalcommunication.dsljson.adapters;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.StringConverter;
import nl.iobyte.nodalcommunication.dsljson.packet.Packet;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;
import java.io.IOException;

public class PacketAdapter implements JsonReader.ReadObject<Packet>, JsonWriter.WriteObject<Packet> {

    private final DslJson<Object> json;

    public PacketAdapter(DslJson<Object> json) {
        this.json = json;
    }

    @Override
    public Packet read(JsonReader reader) throws IOException {
        if(reader.last() != 123)
            throw reader.newParseError("Expecting '{' for object start");

        reader.getNextToken();
        reader.fillName();
        reader.getNextToken();
        String node_id = StringConverter.deserialize(reader);

        reader.getNextToken();
        reader.getNextToken();

        reader.fillName();
        reader.getNextToken();
        String channel = StringConverter.deserialize(reader);

        reader.getNextToken();
        reader.getNextToken();

        reader.fillName();
        reader.getNextToken();
        JsonReader.ReadObject<PacketPayload> read = json.tryFindReader(PacketPayload.class);
        if(read == null)
            throw new IOException("unable to find reader for: "+PacketPayload.class);

        PacketPayload payload = read.read(reader);
        if(payload == null)
            throw new IOException("unable to read: "+PacketPayload.class);

        return new Packet(node_id, channel, payload);
    }

    @Override
    public void write(JsonWriter writer, Packet packet) {
        assert packet != null;
        writer.writeAscii("{\"node_id\":\""+packet.getNodeId()+"\",\"channel\":\""+packet.getChannel()+"\",\"payload\":");

        JsonWriter.WriteObject<PacketPayload> write = json.tryFindWriter(PacketPayload.class);
        if(write == null) {
            writer.writeAscii("{}");
        } else {
            write.write(writer, packet.getPayload());
        }

        writer.writeAscii("}");
    }

}
