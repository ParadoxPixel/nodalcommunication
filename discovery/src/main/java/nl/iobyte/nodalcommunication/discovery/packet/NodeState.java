package nl.iobyte.nodalcommunication.discovery.packet;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import nl.iobyte.nodalcommunication.dsljson.packet.PacketPayload;

@CompiledJson
public class NodeState extends PacketPayload {

    @JsonAttribute(name = "node_id")
    private final String nodeId;
    private final boolean online;

    public NodeState(String nodeId, boolean online) {
        this.nodeId = nodeId;
        this.online = online;
    }

    /**
     * Get node identifier
     * @return String
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Check if node online
     * @return Boolean
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * {@inheritDoc}
     * @return NodeState
     */
    @SuppressWarnings("unchecked")
    public NodeState cast() {
        return this;
    }

}
