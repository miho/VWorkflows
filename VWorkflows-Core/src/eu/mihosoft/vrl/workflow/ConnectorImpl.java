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
    private String id;

    public ConnectorImpl(VNode node, String type, String id) {
        this.type = type;
        this.id = id;
        this.node = node;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getId() {
        return this.node.getId() + ":" + this.id;
    }

    @Override
    public String getLocalId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public VNode getNode() {
        return this.node;
    }
}
