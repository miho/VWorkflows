package eu.mihosoft.vrl.workflow.fx;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class FontUtil {

    private FontUtil() {
        throw new AssertionError("Don't instantiate me");
    }

    private static class FontMetrics
    {
        final private Text internal;
        public float ascent, descent, lineHeight;
        public FontMetrics(Font fnt)
        {
            internal =new Text();
            internal.setFont(fnt);
            Bounds b= internal.getLayoutBounds();
            lineHeight= (float) b.getHeight();
            ascent= (float) -b.getMinY();
            descent=(float) b.getMaxY();
        }

        public double computeStringWidth(String txt)
        {
            internal.setText(txt);
            return internal.getLayoutBounds().getWidth();
        }
    }

    /**
     * Returns the width of the specified text if rendered with the specified font.
     * @param font font used for rendering
     * @param text text to measure
     * @return the width of the specified text
     */
    public static double computeStringWidth(Font font, String text) {
        return new FontMetrics(font).computeStringWidth(text);
    }

}


