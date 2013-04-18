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
    private int lastId = 0;

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
        
        // TODO improve id generation
        // Question: do we really want strings as id?
        int counter = lastId+1;
        
        String id = "" + counter; // verified that java / & 8 uses stringbuilder
        
        while(ids.contains(id)) {
            counter++;
            id = "" + counter;
        }
        
        ids.add(id);
        
        lastId = counter;
        
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
