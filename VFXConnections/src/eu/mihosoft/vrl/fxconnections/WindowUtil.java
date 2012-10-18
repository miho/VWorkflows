/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.fxconnections;

import eu.mihosoft.vrl.fxwindows.CloseIcon;
import eu.mihosoft.vrl.fxwindows.MinimizeIcon;
import eu.mihosoft.vrl.fxwindows.Window;

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
