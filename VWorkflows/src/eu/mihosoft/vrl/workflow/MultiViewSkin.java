/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MultiViewSkin<T extends Skin<V>, V extends Model> implements Skin<V> {

    private List<T> skins = new ArrayList<>();
    private ObjectProperty<V> modelProperty = new SimpleObjectProperty<>();
    private ObjectProperty<VFlow> controllerProperty = new SimpleObjectProperty<>();

    public MultiViewSkin() {

        modelProperty.addListener(new ChangeListener<V>() {
            @Override
            public void changed(ObservableValue<? extends V> ov, V t, V t1) {
                for (T s : getSkins()) {
                    s.setModel(t1);
                }
            }
        });
    }

    @Override
    public void add() {
        for (T s : getSkins()) {
            s.add();
        }
    }

    @Override
    public void remove() {
        for (T s : getSkins()) {
            s.remove();
        }
    }

    @Override
    public final void setModel(V model) {
        modelProperty().set(model);
    }

    @Override
    public final V getModel() {
        return modelProperty().get();
    }

    @Override
    public ObjectProperty<V> modelProperty() {
        return modelProperty;
    }

    @Override
    public VFlow getController() {
        return controllerProperty().get();
    }

    @Override
    public void setController(VFlow flow) {
        controllerProperty().set(flow);
    }

    /**
     * @return the skins
     */
    public List<T> getSkins() {
        return skins;
    }

    public void addSkin(T skin) {
        getSkins().add(skin);
    }

    /**
     * @return the controllerProperty
     */
    protected ObjectProperty<VFlow> controllerProperty() {
        return controllerProperty;
    }
}
