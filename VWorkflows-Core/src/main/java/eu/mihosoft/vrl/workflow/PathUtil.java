/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class PathUtil {
    private PathUtil() {
        throw new AssertionError("Instantiation not allowed.", null);
    }
    
    private static List<Connector> computeSendersAndReceivers(Connector s, Connector r) {
        List<Connector> result = new ArrayList<>();
        
        Optional<VFlowModel> commonAncestorResult = WorkflowUtil.
                getCommonAncestor(s.getNode(), r.getNode());
        
        if (commonAncestorResult.isPresent()) {
            
            VFlowModel commonAncestor = commonAncestorResult.get();
            
            List<VFlowModel> ancestorsOfReceiver = 
                    WorkflowUtil.getAncestors(r.getNode());
            
            int splitIndex = ancestorsOfReceiver.indexOf(commonAncestor);
            
            ancestorsOfReceiver = ancestorsOfReceiver.subList(
                    splitIndex, ancestorsOfReceiver.size());
            
            for(VFlowModel fm : ancestorsOfReceiver) {
                
            }
            
        }
        
        return result;
    }
}
