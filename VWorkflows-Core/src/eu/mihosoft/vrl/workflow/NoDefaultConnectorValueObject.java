/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public class NoDefaultConnectorValueObject extends DefaultValueObject {

    public NoDefaultConnectorValueObject() {
    }

    public NoDefaultConnectorValueObject(VNode parent) {
        super(parent);
    }
    
    

    @Override
    public CompatibilityResult compatible(final ValueObject sender, final String flowType) {
        return new CompatibilityResult() {
            @Override
            public boolean isCompatible() {
                return false;
            }

            @Override
            public String getMessage() {
                return "No default connector has been specified for type: " + flowType + " in node" + getParent().getId();
            }

            @Override
            public String getStatus() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
}
