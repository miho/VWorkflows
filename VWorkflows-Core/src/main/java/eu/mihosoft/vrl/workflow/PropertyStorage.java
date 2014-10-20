/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.vrl.workflow;

import java.util.Optional;
import javafx.collections.MapChangeListener;

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
    
}
