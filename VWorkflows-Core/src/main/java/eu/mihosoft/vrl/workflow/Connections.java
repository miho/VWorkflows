/*
 * Copyright 2012-2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
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

import java.util.Collection;
import javafx.collections.ObservableList;

/**
 * This interface defines a collection of {@code Connection}
 * 
 * @author Michael Hoffer  &lt;info@michaelhoffer.de&gt;
 */
public interface Connections extends Model {

    public void add(Connection c);

    public Connection add(Connector s, Connector r);

    public Connection add(String id, Connector s, Connector r, VisualizationRequest vReq);

    public void remove(Connection c);

    public Connection get(String id, Connector s, Connector r);

    public Iterable<Connection> getAll(Connector s, Connector r);

    public void remove(String id, Connector s, Connector r);

    public void removeAll(Connector s, Connector r);

    public void setConnectionClass(Class<? extends Connection> cls);

    public Class<? extends Connection> getConnectionClass();

    public ObservableList<Connection> getConnections();

    public Collection<Connection> getAllWith(Connector c);
    public Collection<Connection> getAllWithNode(VNode n);

    public boolean isInputConnected(Connector id);

    public boolean isOutputConnected(Connector id);

    public boolean contains(Connector s, Connector r);

    public String getType();
}
