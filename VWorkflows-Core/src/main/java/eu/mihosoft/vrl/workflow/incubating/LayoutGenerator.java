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
package eu.mihosoft.vrl.workflow.incubating;

import eu.mihosoft.vrl.workflow.VFlowModel;

/**
 * This interface describes a layout generator. A layout generator is used to 
 * calculate an aesthetically pleasing arrangement of nodes.
 * 
 * @author Tobias Mertz
 */
public interface LayoutGenerator {
    
    /**
     * Returns the workflow to be laid out.
     * @return VFlowModel
     */
    public VFlowModel getWorkflow();
    
    /**
     * If set to true, the layout is applied to all subflows of the given 
     * workflow recursively.
     * @return boolean
     */
    public boolean getRecursive();
    
    /**
     * If set to true, subflow nodes in the given workflow are automatically 
     * scaled to fit their contents.
     * @return boolean
     */
    public boolean getAutoscaleNodes();
    
    /**
     * If set to true, debugging output will be printed in the command line.
     * @return boolean
     */
    public boolean getDebug();
    
    /**
     * Sets the workflow to be laid out.
     * @param pworkflow VFlowModel
     */
    public void setWorkflow(VFlowModel pworkflow);
    
    /**
     * If set to true, the layout is applied to all subflows of the given 
     * workflow recursively.
     * @param precursive boolean
     */
    public void setRecursive(boolean precursive);
    
    /**
     * If set to true, subflow nodes in the given workflow are automatically 
     * scaled to fit their contents.
     * @param pautoscaleNodes boolean
     */
    public void setAutoscaleNodes(boolean pautoscaleNodes);
    
    /**
     * If set to true, debugging output will be printed in the command line.
     * @param pdebug boolean
     */
    public void setDebug(boolean pdebug);
    
    /**
     * Generates the Layout.
     */
    public void generateLayout() ;
    
}
