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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.workflow;

import java.util.Collection;
import javafx.collections.MapChangeListener;

import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface PropertyStorage {

    /**
     * Indicates whether this storage contains the requested property.
     *
     * @param key key
     * @return {@code true} if this storage contains the requested property;
     * {@code false}
     */
    boolean contains(String key);

    /**
     * Deletes the requested property if present. Does nothing otherwise.
     *
     * @param key key
     */
    void remove(String key);

    /**
     * Returns a property.
     *
     * @param <T> property type
     * @param key key
     * @return the property; an empty {@link java.util.Optional} will be
     * returned if the property does not exist or the type does not match
     */
    <T> Optional<T> get(String key);

    /**
     * Sets a property. Existing properties are overwritten.
     *
     * @param key key
     * @param property property
     */
    <T> void set(String key, T property);

     /**
     * Adds the specified listener to the property map. 
     * @param l change listener
     */
    public void addListener(MapChangeListener<String, Object> l);
    
    /**
     * Removes the specified listener from the property map.
     * @param l change listener
     */
    public void removeListener(MapChangeListener<String, Object> l);
    
    /**
     * Returns the keys that are currently used by the property storage.
     * @return keys used by the property storage
     */
    public Collection<String> getKeys();
    
}
