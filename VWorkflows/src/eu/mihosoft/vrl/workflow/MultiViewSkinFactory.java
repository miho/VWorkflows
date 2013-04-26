/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MultiViewSkinFactory<T extends Skin, V extends Skin> implements SkinFactory<T, V> {

    private List<SkinFactory<?, ?>> skinFactories = new ArrayList<>();

    @Override
    public SkinFactory<T, V> createChild(Skin parent) {
        if (!(parent instanceof MultiViewSkin)) {
            throw new IllegalStateException("Only " + MultiViewSkin.class + " is supported as parent!");
        }

        MultiViewSkin<Skin<Model>, Model> mParent = (MultiViewSkin) parent;

        MultiViewSkinFactory<T, V> child = new MultiViewSkinFactory<>();

        for (int i = 0; i < mParent.getSkins().size(); i++) {
            child.skinFactories.add(skinFactories.get(i).createChild(mParent.getSkins().get(i)));
        }

        return child;
    }

    @Override
    public ConnectionSkin createSkin(Connection c, VFlow flow, String type) {
        return new MultiViewConnectionSkin();
    }

    @Override
    public VNodeSkin createSkin(VNode n, VFlow controller) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
    }
}
