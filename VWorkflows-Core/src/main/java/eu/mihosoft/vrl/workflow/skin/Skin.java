/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
package eu.mihosoft.vrl.workflow.skin;

import eu.mihosoft.vrl.workflow.Model;
import eu.mihosoft.vrl.workflow.VFlow;
import javafx.beans.property.ObjectProperty;

/**
 * A skin is a toolkit independent visual representation of a model.
 * 
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 * @param <T> model type that shall be skinned
 */
public interface Skin<T extends Model> {
    /**
     * Adds this skin to its view parent.
     */
    public void add();
    /**
     * Removes this skin from its view parent.
     */
    public void remove();
    /**
     * Defines the model that shall be represented by this skin.
     * @param model model to set
     */
    public void setModel(T model);
    /**
     * Returns the model represented by this skin.
     * @return model represented by this skin
     */
    public T getModel();
    /**
     * Returns the property the model represented by this skin.
     * @return model represented by this skin
     */
    public ObjectProperty<T> modelProperty();
    /**
     * Returns the flow controller that is used to manipulate the model.
     * @return the flow controller that is used to manipulate the model
     */
    public VFlow getController();
    
    /**
     * Defines the flow controller that shall be used to manipulate the model.
     * @param flow flow controller to set
     */
    public void setController(VFlow flow);
    
    /**
     * Returns the skin factory that created this skin.
     * @return the skin factory that created this skin
     */
    public SkinFactory getSkinFactory();
}
