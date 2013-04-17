/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class DefaultWorkflow extends FlowControllerImpl {

    public DefaultWorkflow(SkinFactory<? extends ConnectionSkin,? extends FlowNodeSkin> skinFactory) {

        super(skinFactory);
        
        FlowFlowNode model = FlowFactory.newFlowModel();
        setNodeLookup(new NodeLookupImpl(model));
        setModel(model);

        addConnections(VConnections.newConnections("control"), "control");
        addConnections(VConnections.newConnections("data"), "data");
        addConnections(VConnections.newConnections("event"), "event");
    }

    public DefaultWorkflow() {

        super(null);

        FlowFlowNode model = FlowFactory.newFlowModel();
        setNodeLookup(new NodeLookupImpl(model));
        setModel(model);

        addConnections(VConnections.newConnections("control"), "control");
        addConnections(VConnections.newConnections("data"), "data");
        addConnections(VConnections.newConnections("event"), "event");
    }
}


class DummySkinFactoryImpl implements SkinFactory<ConnectionSkin,FlowNodeSkin> {

    @Override
    public SkinFactory<ConnectionSkin, FlowNodeSkin> createChild(Skin parent) {
        return this;
    }

    @Override
    public ConnectionSkin createSkin(Connection c, FlowController flow, String type) {
        return null;
    }

    @Override
    public FlowNodeSkin createSkin(FlowNode n, FlowController flow) {
        return null;
    }
}

