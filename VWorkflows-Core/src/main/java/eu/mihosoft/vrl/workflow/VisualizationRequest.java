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
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface VisualizationRequest extends PropertyStorage{
    
    static final String KEY_STYLE = "visualization-request:style";
    /**
     * Defines the maximum connector size.
     */
    static final String KEY_MAX_CONNECTOR_SIZE = "visualization-request:connector:max-size";
    /**
     * Defines whether connectors can change their layout position (switch edges).
     */
    static final String KEY_CONNECTOR_AUTO_LAYOUT = "visualization-request:connector:auto-layout";
    /**
     * Defines whether to disable editing, i.e., changes to nodes and connections.
     */
    static final String KEY_DISABLE_EDITING = "visualization-request:skin:disable-editing";
    /**
     * Defines whether nodes can be removed.
     */
    static final String KEY_NODE_NOT_REMOVABLE = "visualization-request:skin:node-not-removable";
    
    /**
     * Defines whether connector prefers top-down layout.
     */
    static final String KEY_CONNECTOR_PREFER_TOP_DOWN = "visualization-request:connector:prefer-top-down";
    
    public void setStyle(String style);
    public String getStyle();
}
