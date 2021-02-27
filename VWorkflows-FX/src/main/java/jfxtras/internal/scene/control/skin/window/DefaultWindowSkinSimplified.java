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
package jfxtras.internal.scene.control.skin.window;

import eu.mihosoft.vrl.workflow.fx.FontUtil;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import jfxtras.scene.control.window.SelectableNode;
import jfxtras.scene.control.window.Window;
import jfxtras.scene.control.window.WindowIcon;
import jfxtras.scene.control.window.WindowUtil;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class DefaultWindowSkinSimplified extends SkinBase<Window> {

    private double mouseX;
    private double mouseY;
    private double nodeX = 0;
    private double nodeY = 0;
    private BooleanProperty draggingProperty = new SimpleBooleanProperty();
    private boolean zoomable = true;
    private double minScale = 0.1;
    private double maxScale = 10;
    private double scaleIncrement = 0.001;
    private ResizeMode resizeMode;
    private boolean resizeTop;
    private boolean resizeLeft;
    private boolean resizeBottom;
    private boolean resizeRight;
    private TitleBarSimplified titleBar;
    private Window control;
    private VBox root = new VBox();
    private double contentScale = 1.0;
    private double oldHeight;
    private Timeline minimizeTimeLine;

    public DefaultWindowSkinSimplified(Window w) {
        super(w);
        this.control = w;

        init();
    }

    private void init() {

        titleBar = new TitleBarSimplified(control);
        titleBar.setTitle("");

        control.minWidthProperty().bind(titleBar.minWidthProperty());

        titleBar.minWidthProperty().addListener((ov) -> System.out.println("mmw- " + titleBar.getMinWidth()));

        root.setMinWidth(Pane.USE_PREF_SIZE);

        getChildren().add(root);
        root.getChildren().add(titleBar);
        VBox.setVgrow(titleBar, Priority.NEVER);

        for (WindowIcon i : control.getLeftIcons()) {
            titleBar.addLeftIcon(i);
        }

        for (WindowIcon i : control.getRightIcons()) {
            titleBar.addRightIcon(i);
        }

        control.getLeftIcons().addListener(
                (Change<? extends WindowIcon> change) -> {
                    while (change.next()) {
                        // TODO handle permutation
//                        if (change.wasPermutated()) {
//                            for (int i = change.getFrom(); i < change.getTo(); ++i) {
//                                //permutate
//                            }
//                        } else if (change.wasUpdated()) {
//                            //update item
//                        } else {
                        if (change.wasRemoved()) {
                            for (WindowIcon i : change.getRemoved()) {
                                titleBar.removeLeftIcon(i);
                            }
                        } else if (change.wasAdded()) {
                            for (WindowIcon i : change.getAddedSubList()) {
                                titleBar.addLeftIcon(i);
                            }
                        }

                    }
                });

        control.getRightIcons().addListener(
                (Change<? extends WindowIcon> change) -> {
                    while (change.next()) {
                        // TODO handle permutation
//                        if (change.wasPermutated()) {
//                            for (int i = change.getFrom(); i < change.getTo(); ++i) {
//                                //permutate
//                            }
//                        } else if (change.wasUpdated()) {
//                            //update item
//                        } else {
                        if (change.wasRemoved()) {
                            for (WindowIcon i : change.getRemoved()) {
                                titleBar.removeRightIcon(i);
                            }
                        } else if (change.wasAdded()) {
                            for (WindowIcon i : change.getAddedSubList()) {
                                titleBar.addRightIcon(i);
                            }
                        }
                    }
                });

        control.minimizedProperty().addListener(
                (ov, oldValue, newValue) -> {

                    boolean storeOldHeight = minimizeTimeLine == null && newValue;

                    if (minimizeTimeLine != null) {
                        minimizeTimeLine.stop();
                        minimizeTimeLine = null;
                    }

                    double newHeight;

                    if (newValue) {
                        newHeight = titleBar.getHeight();
                    } else {
                        newHeight = oldHeight;
                    }

                    if (storeOldHeight) {
                        oldHeight = control.getPrefHeight();
                    }

                    minimizeTimeLine = new Timeline(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(control.prefHeightProperty(), control.getPrefHeight())),
                            new KeyFrame(Duration.seconds(0.1),
                                    new KeyValue(control.prefHeightProperty(), newHeight)));

                    minimizeTimeLine.statusProperty().addListener(
                            (ObservableValue<? extends Status> ov2, Status oldStatus, Status newStatus) -> {
                                if (newStatus == Status.STOPPED) {

                                    // restore cache hint
                                    getSkinnable().setCache(true);
                                    getSkinnable().setCacheHint(CacheHint.SPEED);

                                    minimizeTimeLine = null;
                                    if (newValue) {
                                        control.getContentPane().setVisible(false);
                                    }
                                }
                            });

                    // temporarily disable cache hint due to rendering bugs
                    getSkinnable().setCache(false);
                    getSkinnable().setCacheHint(CacheHint.DEFAULT);

                    minimizeTimeLine.play();
                });

        control.prefHeightProperty().addListener(new MinimizeHeightListener(control, titleBar));

        initMouseEventHandlers();

        titleBar.setTitle(control.getTitle());

        control.titleProperty().addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
            titleBar.setTitle(newValue);
            control.requestLayout();
        });

        root.getChildren().add(control.getContentPane());
        VBox.setVgrow(control.getContentPane(), Priority.ALWAYS);

        control.contentPaneProperty().addListener((ObservableValue<? extends Pane> ov, Pane oldValue, Pane newValue) -> {
            root.getChildren().remove(oldValue);
            root.getChildren().add(newValue);
            VBox.setVgrow(newValue, Priority.ALWAYS);
        });

        titleBar.setStyle(control.getStyle());

        control.styleProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            titleBar.setStyle(t1);
        });

        titleBar.getStyleClass().setAll(control.getTitleBarStyleClass());
        titleBar.getLabel().getStyleClass().setAll(control.getTitleBarStyleClass());

        control.titleBarStyleClassProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            titleBar.getStyleClass().setAll(t1);
            titleBar.getLabel().getStyleClass().setAll(t1);
        });

        titleBar.getStylesheets().setAll(control.getStylesheets());

        control.getStylesheets().addListener((Change<? extends String> change) -> {
            while (change.next()) {
                // TODO handle permutation
//                if (change.wasPermutated()) {
//                    for (int i = change.getFrom(); i < change.getTo(); ++i) {
//                        //permutate
//                    }
//                } else if (change.wasUpdated()) {
//                    //update item
//                } else 

                if (change.wasRemoved()) {
                    for (String i : change.getRemoved()) {
                        titleBar.getStylesheets().remove(i);
                    }
                } else if (change.wasAdded()) {
                    for (String i : change.getAddedSubList()) {
                        titleBar.getStylesheets().add(i);
                    }
                }
            }
        });

        Border prevBorder = control.getBorder();

        control.selectedProperty().addListener(
                (ov, oldValue, newValue) -> {
                    if (newValue) {
                        control.setBorder(new Border(
                                new BorderStroke(control.getSelectionBorderColor(),
                                        BorderStrokeStyle.SOLID,
                                        new CornerRadii(3), new BorderWidths(2))));
                        if (control.isSelectionEffectEnabled()) {
                            ColorAdjust effect
                            = new ColorAdjust(-0.25, 0.2, 0.8, 0);
                            Glow glow = new Glow(0.5);
                            glow.setInput(effect);
                            control.setEffect(glow);
                        }
                    } else {
                        control.setBorder(prevBorder);
                        control.setEffect(null);
                    }
                });

        // we enable node caching for the whole window
        getSkinnable().setCache(true);
        getSkinnable().setCacheHint(CacheHint.SPEED);

    }

    private void initMouseEventHandlers() {

        getSkinnable().onMousePressedProperty().set((event) -> {

            final Node n = control;

            final double parentScaleX = n.getParent().
                    localToSceneTransformProperty().getValue().getMxx();
            final double parentScaleY = n.getParent().
                    localToSceneTransformProperty().getValue().getMyy();

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();

            nodeX = n.getLayoutX() * parentScaleX;
            nodeY = n.getLayoutY() * parentScaleY;

            if (control.isMoveToFront()) {
                control.toFront();
            }

            if (control.isSelected()) {
                selectedWindowsToFront();
            }
        });

        //Event Listener for MouseDragged
        getSkinnable().onMouseDraggedProperty().set((event) -> {
            final Node n = control;

            final double parentScaleX = n.getParent().
                    localToSceneTransformProperty().getValue().getMxx();
            final double parentScaleY = n.getParent().
                    localToSceneTransformProperty().getValue().getMyy();

            final double scaleX = n.localToSceneTransformProperty().
                    getValue().getMxx();
            final double scaleY = n.localToSceneTransformProperty().
                    getValue().getMyy();

            Bounds boundsInScene
                    = control.localToScene(control.getBoundsInLocal());

            double sceneX = boundsInScene.getMinX();
            double sceneY = boundsInScene.getMinY();

            double offsetX = event.getSceneX() - mouseX;
            double offsetY = event.getSceneY() - mouseY;

            if (resizeMode == ResizeMode.NONE && control.isMovable()) {

                nodeX += offsetX;
                nodeY += offsetY;

                double scaledX = nodeX * 1 / parentScaleX;
                double scaledY = nodeY * 1 / parentScaleY;

                double offsetForAllX = scaledX - n.getLayoutX();
                double offsetForAllY = scaledY - n.getLayoutY();

                n.setLayoutX(scaledX);
                n.setLayoutY(scaledY);

                setDragging(true);

                // move all selected windows
                if (control.isSelected()) {
                    dragSelectedWindows(offsetForAllX, offsetForAllY);
                }

            } else {

                double width = n.getBoundsInLocal().getMaxX()
                        - n.getBoundsInLocal().getMinX();
                double height = n.getBoundsInLocal().getMaxY()
                        - n.getBoundsInLocal().getMinY();

                if (resizeTop) {
//                        System.out.println("TOP");

                    double insetOffset = getSkinnable().getInsets().getTop() / 2;

                    double yDiff
                            = sceneY / parentScaleY
                            + insetOffset
                            - event.getSceneY() / parentScaleY;

                    double newHeight = control.getPrefHeight() + yDiff;

                    if (newHeight > control.minHeight(0)) {
                        control.setLayoutY(control.getLayoutY() - yDiff);
                        control.setPrefHeight(newHeight);
                    }
                }
                if (resizeLeft) {
//                        System.out.println("LEFT");

                    double insetOffset = getSkinnable().getInsets().getLeft() / 2;

                    double xDiff = sceneX / parentScaleX
                            + insetOffset
                            - event.getSceneX() / parentScaleX;

                    double newWidth = control.getPrefWidth() + xDiff;

                    if (newWidth > Math.max(control.minWidth(0),
                            control.getContentPane().minWidth(0))) {
                        control.setLayoutX(control.getLayoutX() - xDiff);
                        control.setPrefWidth(newWidth);
                    }
                }

                if (resizeBottom) {
//                        System.out.println("BOTTOM");

                    double insetOffset = getSkinnable().getInsets().getBottom() / 2;

                    double yDiff = event.getSceneY() / parentScaleY
                            - sceneY / parentScaleY - insetOffset;

                    double newHeight = yDiff;

                    newHeight = Math.max(
                            newHeight, control.minHeight(0));

                    if (newHeight < control.maxHeight(0)) {
                        control.setPrefHeight(newHeight);
                    }
                }
                if (resizeRight) {

                    double insetOffset = getSkinnable().getInsets().getRight() / 2;

                    double xDiff = event.getSceneX() / parentScaleX
                            - sceneX / parentScaleY - insetOffset;

                    double newWidth = xDiff;

                    newWidth = Math.max(
                            newWidth,
                            Math.max(control.getContentPane().minWidth(0),
                                    control.minWidth(0)));

                    if (newWidth < control.maxWidth(0)) {
                        control.setPrefWidth(newWidth);
                    }
                }

                if (resizeBottom || resizeTop || resizeLeft || resizeRight) {
                    getSkinnable().setCache(false);

                } else {
                    getSkinnable().setCache(true);
                    getSkinnable().setCacheHint(CacheHint.DEFAULT);
                }
            }

            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        getSkinnable().onMouseClickedProperty().set((MouseEvent event) -> {
            setDragging(false);
        });

        getSkinnable().onMouseMovedProperty().set((MouseEvent t) -> {
            if (control.isMinimized() || !control.isResizableWindow()) {

                resizeTop = false;
                resizeLeft = false;
                resizeBottom = false;
                resizeRight = false;

                resizeMode = ResizeMode.NONE;

                return;
            }

            final Node n = control;

            final double parentScaleX = n.getParent().localToSceneTransformProperty().getValue().getMxx();
            final double parentScaleY = n.getParent().localToSceneTransformProperty().getValue().getMyy();

            final double scaleX = n.localToSceneTransformProperty().getValue().getMxx();
            final double scaleY = n.localToSceneTransformProperty().getValue().getMyy();

            final double border = control.getResizableBorderWidth() * scaleX;

            double diffMinX = Math.abs(n.getLayoutBounds().getMinX() - t.getX() + getSkinnable().getInsets().getLeft());
            double diffMinY = Math.abs(n.getLayoutBounds().getMinY() - t.getY() + getSkinnable().getInsets().getTop());
            double diffMaxX = Math.abs(n.getLayoutBounds().getMaxX() - t.getX() - getSkinnable().getInsets().getRight());
            double diffMaxY = Math.abs(n.getLayoutBounds().getMaxY() - t.getY() - getSkinnable().getInsets().getBottom());

            boolean left = diffMinX * scaleX < Math.max(border, getSkinnable().getInsets().getLeft() / 2 * scaleX);
            boolean top = diffMinY * scaleY < Math.max(border, getSkinnable().getInsets().getTop() / 2 * scaleY);
            boolean right = diffMaxX * scaleX < Math.max(border, getSkinnable().getInsets().getRight() / 2 * scaleX);
            boolean bottom = diffMaxY * scaleY < Math.max(border, getSkinnable().getInsets().getBottom() / 2 * scaleY);

            resizeTop = false;
            resizeLeft = false;
            resizeBottom = false;
            resizeRight = false;

            if (left && !top && !bottom) {
                n.setCursor(Cursor.W_RESIZE);
                resizeMode = ResizeMode.LEFT;
                resizeLeft = true;
            } else if (left && top && !bottom) {
                n.setCursor(Cursor.NW_RESIZE);
                resizeMode = ResizeMode.TOP_LEFT;
                resizeLeft = true;
                resizeTop = true;
            } else if (left && !top && bottom) {
                n.setCursor(Cursor.SW_RESIZE);
                resizeMode = ResizeMode.BOTTOM_LEFT;
                resizeLeft = true;
                resizeBottom = true;
            } else if (right && !top && !bottom) {
                n.setCursor(Cursor.E_RESIZE);
                resizeMode = ResizeMode.RIGHT;
                resizeRight = true;
            } else if (right && top && !bottom) {
                n.setCursor(Cursor.NE_RESIZE);
                resizeMode = ResizeMode.TOP_RIGHT;
                resizeRight = true;
                resizeTop = true;
            } else if (right && !top && bottom) {
                n.setCursor(Cursor.SE_RESIZE);
                resizeMode = ResizeMode.BOTTOM_RIGHT;
                resizeRight = true;
                resizeBottom = true;
            } else if (top && !left && !right) {
                n.setCursor(Cursor.N_RESIZE);
                resizeMode = ResizeMode.TOP;
                resizeTop = true;
            } else if (bottom && !left && !right) {
                n.setCursor(Cursor.S_RESIZE);
                resizeMode = ResizeMode.BOTTOM;
                resizeBottom = true;
            } else {
                n.setCursor(Cursor.DEFAULT);
                resizeMode = ResizeMode.NONE;
            }

            control.autosize();
        });

//        setOnScroll(new EventHandler<ScrollEvent>() {
//            @Override
//            public void handle(ScrollEvent event) {
//
//                if (!isZoomable()) {
//                    return;
//                }
//
//                double scaleValue =
//                        control.getScaleY() + event.getDeltaY() * getScaleIncrement();
//
//                scaleValue = Math.max(scaleValue, getMinScale());
//                scaleValue = Math.min(scaleValue, getMaxScale());
//
//                control.setScaleX(scaleValue);
//                control.setScaleY(scaleValue);
//
//                event.consume();
//            }
//        });
    }

    /**
     * @return the zoomable
     */
    public boolean isZoomable() {
        return zoomable;
    }

    /**
     * @param zoomable the zoomable to set
     */
    public void setZoomable(boolean zoomable) {
        this.zoomable = zoomable;
    }

    /**
     * @return the dragging
     */
    protected boolean isDragging() {
        return draggingProperty().get();
    }

    public void removeNode(Node n) {
        getChildren().remove(n);
    }

    /**
     * @return the minScale
     */
    public double getMinScale() {
        return minScale;
    }

    /**
     * @param minScale the minScale to set
     */
    public void setMinScale(double minScale) {
        this.minScale = minScale;
    }

    /**
     * @return the maxScale
     */
    public double getMaxScale() {
        return maxScale;
    }

    /**
     * @param maxScale the maxScale to set
     */
    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
    }

    /**
     * @return the scaleIncrement
     */
    public double getScaleIncrement() {
        return scaleIncrement;
    }

    /**
     * @param scaleIncrement the scaleIncrement to set
     */
    public void setScaleIncrement(double scaleIncrement) {
        this.scaleIncrement = scaleIncrement;
    }

    private void setDragging(boolean b) {
        draggingProperty().set(b);
    }

    private BooleanProperty draggingProperty() {
        return draggingProperty;
    }

    // TODO move from skin to behavior class (a lot of other stuff here too)
    private void dragSelectedWindows(double offsetForAllX, double offsetForAllY) {
        for (SelectableNode sN : WindowUtil.
                getDefaultClipboard().getSelectedItems()) {

            if (sN == control
                    || !(sN instanceof Window)) {
                continue;
            }

            Window selectedWindow = (Window) sN;

            if (control.getParent().
                    equals(selectedWindow.getParent())) {

                selectedWindow.setLayoutX(
                        selectedWindow.getLayoutX()
                        + offsetForAllX);
                selectedWindow.setLayoutY(
                        selectedWindow.getLayoutY()
                        + offsetForAllY);
            }
        } // end for sN
    }

    // TODO move from skin to behavior class (a lot of other stuff here too)
    private void selectedWindowsToFront() {
        for (SelectableNode sN : WindowUtil.
                getDefaultClipboard().getSelectedItems()) {

            if (sN == control
                    || !(sN instanceof Window)) {
                continue;
            }

            Window selectedWindow = (Window) sN;

            if (control.getParent().
                    equals(selectedWindow.getParent())
                    && selectedWindow.isMoveToFront()) {

                selectedWindow.toFront();
            }
        } // end for sN
    }

    static class MinimizeHeightListener implements ChangeListener<Number> {

        private final Window control;
        private final TitleBarSimplified titleBar;

        public MinimizeHeightListener(Window control, TitleBarSimplified titleBar) {
            this.control = control;
            this.titleBar = titleBar;
        }

        @Override
        public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
            if (control.isMinimized()
                    && control.getPrefHeight()
                    < titleBar.minHeight(0) + control.getContentPane().minHeight(0)) {
                control.getContentPane().setVisible(false);
//                System.out.println("v: false");
            } else if (!control.isMinimized()
                    && control.getPrefHeight()
                    >= titleBar.minHeight(0) + control.getContentPane().minHeight(0)) {
                control.getContentPane().setVisible(true);
//                System.out.println("v: true");
            }
        }
    }
}

class TitleBarSimplified extends HBox {

    public static final String DEFAULT_STYLE_CLASS = "window-titlebar";
    private final Pane leftIconPane;
    private final Pane rightIconPane;
    private final Text label = new Text();
    private final double iconSpacing = 3;
    Window control;
    // estimated size of "...",
    // is there a way to find out text dimension without rendering it
    private final double offset = 40;
    private double originalTitleWidth;

    private float labelWidth;

    public TitleBarSimplified(Window w) {

        this.control = w;

        getStylesheets().setAll(w.getStylesheets());
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        setSpacing(8);

//        label.setTextAlignment(TextAlignment.CENTER);
//        label.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        leftIconPane = new IconPane();
        rightIconPane = new IconPane();

        getChildren().add(leftIconPane);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(label);
//        getChildren().add(VFXLayoutUtil.createHBoxFiller());
        getChildren().add(rightIconPane);

        control.boundsInParentProperty().addListener(
                (ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) -> {
                    if (control.getTitle() == null
                    || getLabel().getText() == null
                    || getLabel().getText().isEmpty()) {
                        return;
                    }

                    double maxIconWidth = Math.max(
                            leftIconPane.getWidth(), rightIconPane.getWidth());

                    if (!control.getTitle().equals(getLabel().getText())) {
                        if (originalTitleWidth
                        + maxIconWidth * 2 + offset < getWidth()) {
                            getLabel().setText(control.getTitle());
                        }
                    } else if (!"...".equals(getLabel().getText())) {
                        if (originalTitleWidth
                        + maxIconWidth * 2 + offset >= getWidth()) {
                            getLabel().setText("...");
                        }
                    }
                });

    }

    public void setTitle(String title) {
        getLabel().setText(title);

        originalTitleWidth = getLabel().getBoundsInParent().getWidth();

        double maxIconWidth = Math.max(
                leftIconPane.getWidth(), rightIconPane.getWidth());

        if (originalTitleWidth
                + maxIconWidth * 2 + offset >= getWidth()) {
            getLabel().setText("...");
        }

        labelWidth = (float) FontUtil.computeStringWidth(label.getFont(), title);

        // Fixed ? TODO replace with official API
        //labelWidth = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().
        //        computeStringWidth(title, label.getFont());

        control.layout();
        layout();
    }

    public String getTitle() {
        return getLabel().getText();
    }

    public void addLeftIcon(Node n) {
        leftIconPane.getChildren().add(n);
    }

    public void addRightIcon(Node n) {
        rightIconPane.getChildren().add(n);
    }

    public void removeLeftIcon(Node n) {
        leftIconPane.getChildren().remove(n);
    }

    public void removeRightIcon(Node n) {
        rightIconPane.getChildren().remove(n);
    }

    @Override
    public double computeMinWidth(double h) {
        double result = super.computeMinWidth(h);

        double iconWidth
                = Math.max(
                        leftIconPane.minWidth(h),
                        rightIconPane.minWidth(h)) * 2;

        result = Math.max(result,
                iconWidth
                + labelWidth
                + getInsets().getLeft()
                + getInsets().getRight());

        return result + iconSpacing * 2 + offset;
    }

    @Override
    protected double computePrefWidth(double h) {
        return computeMinWidth(h);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        leftIconPane.resizeRelocate(getInsets().getLeft(), getInsets().getTop(),
                leftIconPane.prefWidth(USE_PREF_SIZE),
                getHeight() - getInsets().getTop() - getInsets().getBottom());

        rightIconPane.resize(rightIconPane.prefWidth(USE_PREF_SIZE),
                getHeight() - getInsets().getTop() - getInsets().getBottom());
        rightIconPane.relocate(getWidth() - rightIconPane.getWidth() - getInsets().getRight(),
                getInsets().getTop());
    }

    /**
     * @return the label
     */
    public final Text getLabel() {
        return label;
    }

    private static class IconPane extends Pane {

        private final double spacing = 2;

        public IconPane() {
            setManaged(false);
            //
            setPrefWidth(USE_COMPUTED_SIZE);
            setMinWidth(USE_COMPUTED_SIZE);
        }

        @Override
        protected void layoutChildren() {

            int count = 0;

            double width = getHeight();
            double height = getHeight();

            for (Node n : getManagedChildren()) {

                double x = (width + spacing) * count;

                n.resizeRelocate(x, 0, width, height);

                count++;
            }
        }

        @Override
        protected double computeMinWidth(double h) {
            return getHeight() * getChildren().size()
                    + spacing * (getChildren().size() - 1);
        }

        @Override
        protected double computeMaxWidth(double h) {
            return computeMinWidth(h);
        }

        @Override
        protected double computePrefWidth(double h) {
            return computeMinWidth(h);
        }
    }
}
