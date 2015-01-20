/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.io;

import eu.mihosoft.vrl.workflow.VisualizationRequest;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class PersistentValueObject {

    private String parentId;
    private Object value;
    private Map<String, Object> storage;

    public PersistentValueObject(String parentId, Object value, VisualizationRequest vReq) {
        this.parentId = parentId;
        this.value = value;

        storage = new HashMap<>();

        for (String key : vReq.getKeys()) {
            storage.put(key, vReq.get(key));
        }
    }

    public PersistentValueObject() {
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String id) {
        this.parentId = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the storage
     */
    public Map<String, Object> getStorage() {
        return storage;
    }

    /**
     * @param storage the storage to set
     */
    public void setStorage(Map<String, Object> storage) {
        this.storage = storage;
    }
}
