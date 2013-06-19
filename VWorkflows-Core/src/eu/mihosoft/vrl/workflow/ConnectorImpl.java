/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class ConnectorImpl implements Connector {

    private VNode node;
    private String type;
    private String localId;
    private VisualizationRequest vRequest;

    public ConnectorImpl(VNode node, String type, String localId) {
        this.type = type;
        this.localId = localId;
        this.node = node;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getId() {
        return this.node.getId() + ":" + this.localId;
    }

    @Override
    public String getLocalId() {
        return this.localId;
    }

    @Override
    public void setLocalId(String id) {
        this.localId = id;
    }

    @Override
    public VNode getNode() {
        return this.node;
    }

    @Override
    public VisualizationRequest getVisualizationRequest() {
        return this.vRequest;
    }

    @Override
    public void setVisualizationRequest(VisualizationRequest vReq) {
        this.vRequest = vReq;
    }
}
