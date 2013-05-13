/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow.fx;

import jfxtras.labs.scene.control.window.CloseIcon;
import jfxtras.labs.scene.control.window.MinimizeIcon;
import jfxtras.labs.scene.control.window.Window;



/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class WindowUtil {
    public static Window createWindow(String title) {
        Window w = new Window(title);
        
        w.setStyle("-fx-font-size: 14;");
        
        w.getLeftIcons().add(new CloseIcon(w));
        w.getLeftIcons().add(new MinimizeIcon(w));
        
        return w;
    }
}
