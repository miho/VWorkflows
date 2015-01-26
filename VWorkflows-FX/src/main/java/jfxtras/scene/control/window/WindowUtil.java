/**
 * WindowUtil.java
 *
 * Copyright (c) 2011-2014, JFXtras
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jfxtras.scene.control.window;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Window utility class.
 *
 * This class contains several methods for managing window nodes (selection
 * etc.).
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class WindowUtil {

    private static Clipboard clipboard;

    /**
     * Returns the default clipboard (used for window selection etc.).
     *
     * @return the default clipboard
     *
     * @see
     * jfxtras.labs.util.event.MouseControlUtil#addSelectionRectangleGesture(javafx.scene.Parent,
     * javafx.scene.shape.Rectangle)
     * @see jfxtras.labs.scene.control.window.SelectableNode
     */
    public static Clipboard getDefaultClipboard() {

        if (clipboard == null) {
            clipboard = new ClipboardImpl();
        }

        return clipboard;
    }
}

/**
 * Default clipboard implementation.
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ClipboardImpl implements Clipboard {

    private final ObservableList<SelectableNode> items =
            FXCollections.observableArrayList();

    @Override
    public boolean select(SelectableNode n, boolean selected) {
        if (n.requestSelection(selected)) {
            if (selected) {
                items.add(n);
            } else {
                items.remove(n);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public ObservableList<SelectableNode> getSelectedItems() {
        return items;
    }

    @Override
    public void unselectAll() {

        List<SelectableNode> unselectList = new ArrayList<>();
        unselectList.addAll(items);

        for (SelectableNode sN : unselectList) {
            select(sN, false);
        }
    }
}