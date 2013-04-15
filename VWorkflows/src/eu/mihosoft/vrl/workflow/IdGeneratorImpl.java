/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
class IdGeneratorImpl implements IdGenerator{
    
    private Set<String> ids = new HashSet<>();

    public IdGeneratorImpl() {
        //
    }
    
    

    @Override
    public void addId(String id) {
        ids.add(id);
    }

    @Override
    public void addIds(IdGenerator generator) {
        ids.addAll(generator.getIds());
    }

    @Override
    public String newId() {
        int counter = 0;
        
        String id = ""+counter;
        
        while(ids.contains(id)) {
            counter++;
            id = "" + counter;
        }
        
        ids.add(id);
        
        return id;
    }

    @Override
    public Set<String> getIds() {
        Set<String> result = new HashSet<>(ids);
        return result;
    }

    @Override
    public IdGenerator newChild() {
        return this;
    }
    
}
