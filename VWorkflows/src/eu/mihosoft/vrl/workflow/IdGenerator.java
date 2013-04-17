/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.Set;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface IdGenerator {
    public String addId(String id);
    public void addIds(IdGenerator generator);
    public String newId();
    public Set<String> getIds();
    public IdGenerator newChild();
}
