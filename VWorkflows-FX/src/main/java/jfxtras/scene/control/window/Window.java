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
package jfxtras.scene.control.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import jfxtras.internal.scene.control.skin.window.DefaultWindowSkin;

/**
 * Window control. A window control is a window node as known from Swing, e.g
 * {@link javax.swing.JInternalFrame}. It can be used to realize MDI based
 * applications. See <a href=https://github.com/miho/VFXWindows-Samples>
 * https://github.com/miho/VFXWindows-Samples</a> for sample code.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Window extends Control implements SelectableNode {
    private static Timer timer = new Timer("jfxtras-Timer", true);
    /**
     * Default css style.
     */
    public static final String DEFAULT_STYLE
            = "/jfxtras/scene/control/window/default.css";
    /**
     * Default style class for css.
     */
    public static final String DEFAULT_STYLE_CLASS = "window";
    /**
     * Defines whether window is moved to front when user clicks on it
     */
    private boolean moveToFront = true;
    /**
     * Window title property (used by titlebar)
     */
    private final StringProperty titleProperty = new SimpleStringProperty("Title");
    /**
     * Minimize property (defines whether to minimize the window,performed by
     * skin)
     */
    private final BooleanProperty minimizeProperty = new SimpleBooleanProperty();
    /**
     * Resize property (defines whether is the window resizeable,performed by
     * skin)
     */
    private final BooleanProperty resizableProperty = new SimpleBooleanProperty(true);
    /**
     * Resize property (defines whether is the window movable,performed by skin)
     */
    private final BooleanProperty movableProperty = new SimpleBooleanProperty(true);
    /**
     * Content pane property. The content pane is the pane that is responsible
     * for showing user defined nodes/content.
     */
    private final Property<Pane> contentPaneProperty = new SimpleObjectProperty<>();
    /**
     * List of icons shown on the left. TODO replace left/right with more
     * generic position property?
     */
    private final ObservableList<WindowIcon> leftIcons
            = FXCollections.observableArrayList();
    /**
     * List of icons shown on the right. TODO replace left/right with more
     * generic position property?
     */
    private final ObservableList<WindowIcon> rightIcons = FXCollections.observableArrayList();
    /**
     * Defines the width of the border /area where the user can grab the window
     * and resize it.
     */
    private final DoubleProperty resizableBorderWidthProperty = new SimpleDoubleProperty(10);
    /**
     * Defines the titlebar class name. This can be used to define css
     * properties specifically for the titlebar, e.g., background.
     */
    private final StringProperty titleBarStyleClassProperty
            = new SimpleStringProperty("window-titlebar");
    /**
     * defines the action that shall be performed before the window is closed.
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onCloseActionProperty
            = new SimpleObjectProperty<>();
    /**
     * defines the action that shall be performed after the window has been
     * closed.
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onClosedActionProperty
            = new SimpleObjectProperty<>();
    /**
     * defines the transition that shall be played when closing the window.
     */
    private final ObjectProperty<Transition> closeTransitionProperty
            = new SimpleObjectProperty<>();
    /**
     * Selected property (defines whether this window is selected.
     */
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty(false);
    /**
     * Selectable property (defines whether this window is selectable.
     */
    private final BooleanProperty selectableProperty = new SimpleBooleanProperty(true);

    /* 
     * Closed property (defines whether window is closed).
     */
    private final BooleanProperty closeRequested = new SimpleBooleanProperty();

    private StyleableObjectProperty<Paint> selectionBorderColor;

    private final BooleanProperty movingProperty = new SimpleBooleanProperty();
    private final BooleanProperty resizingProperty = new SimpleBooleanProperty();

    public Paint getSelectionBorderColor() {
        return selectionBorderColor == null ? new Color(0.3, 0.7, 1.0, 1.0) : selectionBorderColor.get();
    }

    public void setSelectionBorderColor(Paint color) {
        this.selectionBorderColorProperty().set(color);
    }

    public StyleableObjectProperty<Paint> selectionBorderColorProperty() {
        if (selectionBorderColor == null) {
            selectionBorderColor = new SimpleStyleableObjectProperty<>(
                    StyleableProperties.SELECTION_BORDER_COLOR,
                    Window.this, "selectionBorderColor",
                    new Color(0.3, 0.7, 1.0, 1.0));
        }
        return selectionBorderColor;
    }

    private StyleableBooleanProperty selectionEffectEnabled;

    public boolean isSelectionEffectEnabled() {
        return selectionEffectEnabled == null ? true : selectionEffectEnabled.get();
    }

    public void setSelectionEffectEnabled(boolean enabled) {
        this.selectionEffectEnabledProperty().set(enabled);
    }

    public StyleableBooleanProperty selectionEffectEnabledProperty() {
        if (selectionBorderColor == null) {
            selectionEffectEnabled = new SimpleStyleableBooleanProperty(
                    StyleableProperties.SELECTION_EFFECT_ENABLED,
                    Window.this, "selectionEffectEnabled",
                    true);
        }
        return selectionEffectEnabled;
    }

    private static class StyleableProperties {

        private static final CssMetaData< Window, Paint> SELECTION_BORDER_COLOR
                = new CssMetaData< Window, Paint>("-fx-selection-border-color",
                        StyleConverter.getPaintConverter(), Color.GRAY) {
            @Override
            public boolean isSettable(Window control) {
                return control.selectionBorderColor == null
                        || !control.selectionBorderColor.isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(Window control) {
                return control.selectionBorderColorProperty();
            }
        };
        private static final CssMetaData< Window, Boolean> SELECTION_EFFECT_ENABLED
                = new CssMetaData< Window, Boolean>("-fx-selection-effect-enabled",
                        StyleConverter.getBooleanConverter(), true) {
            @Override
            public boolean isSettable(Window control) {
                return control.selectionBorderColor == null
                        || !control.selectionBorderColor.isBound();
            }

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(Window control) {
                return control.selectionEffectEnabledProperty();
            }
        };
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables
                    = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                    SELECTION_BORDER_COLOR, SELECTION_EFFECT_ENABLED
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * Constructor.
     */
    public Window() {
        init();
    }

    /**
     * Constructor.
     *
     * @param title window title
     */
    public Window(String title) {
        setTitle(title);
        init();
    }

    private void init() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        setContentPane(new StackPane());

        closeTransitionProperty.addListener(new ChangeListener<Transition>() {
            @Override
            public void changed(ObservableValue<? extends Transition> ov, Transition t, Transition t1) {
                t1.statusProperty().addListener(new ChangeListener<Animation.Status>() {
                    @Override
                    public void changed(ObservableValue<? extends Animation.Status> observableValue,
                            Animation.Status oldValue, Animation.Status newValue) {

                        if (newValue == Animation.Status.STOPPED) {

                            // we don't fire action events twice
                            if (Window.this.getParent() == null) {
                                return;
                            }

                            if (getOnCloseAction() != null) {
                                getOnCloseAction().handle(new ActionEvent(this, Window.this));
                            }

                            // if someone manually removed us from parent, we don't
                            // do anything
                            if (Window.this.getParent() != null) {
                                NodeUtil.removeFromParent(Window.this);
                            }

                            closeRequested.set(true);

                            if (getOnClosedAction() != null) {
                                getOnClosedAction().handle(new ActionEvent(this, Window.this));
                            }
                        }
                    }
                });
            }
        });

        ScaleTransition st = new ScaleTransition();
        st.setNode(this);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(0);
        st.setToY(0);
        st.setDuration(Duration.seconds(0.2));

        setCloseTransition(st);

        minimizedProperty().addListener((ov, oldValue, newValue) -> {
            minimizeSelectedWindows(newValue);
        });

        initializeMovingPropertyMonitor();
        initializeResizingPropertyMonitor();
        
        

    }

    private void initializeResizingPropertyMonitor() {
        // detects whether the window is currently moving
        boolean[] timerTaskSet = {false};
        double[] wh = {0, 0};
//        int[] cancelledTasks = {0};
        InvalidationListener resizingListener = (ov) -> {

//            // purge cancelled tasks regularily to prevent memory leaks
//            if (cancelledTasks[0] > 1000) {
//                timer[0].purge();
//                cancelledTasks[0] = 0;
//            }
            if (!timerTaskSet[0]) {
                timerTaskSet[0] = true;
                timer.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {
                        double w = getWidth();
                        double h = getHeight();
                        Platform.runLater(
                                () -> {
                                    resizingProperty.set(
                                            w != wh[0] || h != wh[1]);

                                    wh[0] = w;
                                    wh[1] = h;
                                    if (!isResizing()) {
                                        cancel();
//                                        cancelledTasks[0]++;
                                        timerTaskSet[0] = false;
                                    }
                                }
                        );
                    }
                }, 0, 250);
            }
        };

        widthProperty().addListener(resizingListener);
        heightProperty().addListener(resizingListener);
    }

    private void initializeMovingPropertyMonitor() {
        // detects whether the window is currently moving
        boolean [] timerTaskSet = {false};
        double[] xy = {0, 0};
//        boolean[] running = {false};
//        int[] cancelledTasks = {0};
        InvalidationListener movingListener = (ov) -> {

//            // purge cancelled tasks regularily to prevent memory leaks
//            if (cancelledTasks[0] > 1000) {
//                timer[0].purge();
//                cancelledTasks[0] = 0;
//            }
            if (!timerTaskSet[0] && (getLayoutX() != 0 || getLayoutY() != 0)) {
                timerTaskSet[0] = true;
                timer.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {

                        double x = getLayoutX();
                        double y = getLayoutY();
                        Platform.runLater(
                                () -> {
                                    movingProperty.set(
                                            x != xy[0] || y != xy[1]);

                                    xy[0] = x;
                                    xy[1] = y;
                                    if (!isMoving()) {
                                        cancel();
//                                        cancelledTasks[0]++;
                                        timerTaskSet[0] = false;
                                    }
                                }
                        );
                    }
                }, 0, 250);
            }
        };

        layoutXProperty().addListener(movingListener);
        layoutYProperty().addListener(movingListener);
    }

    // TODO move from control to behavior class (a lot of other stuff here too)
    private void closeSelectedWindows() {
        for (SelectableNode sN : WindowUtil.
                getDefaultClipboard().getSelectedItems()) {

            if (sN == this
                    || !(sN instanceof Window)) {
                continue;
            }

            Window selectedWindow = (Window) sN;

            if (selectedWindow.closeRequested.get()) {
                continue;
            }

            if (this.getParent().
                    equals(selectedWindow.getParent())) {

                selectedWindow.close();
            }
        } // end for sN
    }

    public double minWidthWithTitle() {
        if (getSkin() instanceof DefaultWindowSkin) {
            DefaultWindowSkin skin = (DefaultWindowSkin) getSkin();
//            return skin.computeMinWidth();
        }

        return minWidth(0);
    }

    @Override
    protected double computeMinWidth(double height) {
        double result = super.computeMinWidth(height);
        return result;
    }

    // TODO move from control to behavior class (a lot of other stuff here too)
    private void minimizeSelectedWindows(boolean state) {
        for (SelectableNode sN : WindowUtil.
                getDefaultClipboard().getSelectedItems()) {

            if (sN == this
                    || !(sN instanceof Window)) {
                continue;
            }

            Window selectedWindow = (Window) sN;

            if (selectedWindow.isMinimized() == state) {
                continue;
            }

            if (this.getParent().
                    equals(selectedWindow.getParent())) {

                selectedWindow.setMinimized(state);
            }
        } // end for sN
    }

    @Override
    public String getUserAgentStylesheet() {
        return this.getClass().getResource(DEFAULT_STYLE).toExternalForm();
    }

    /**
     * @return the content pane of this window
     */
    public Pane getContentPane() {
        return contentPaneProperty.getValue();
    }

    /**
     * Defines the content pane of this window.
     *
     * @param contentPane content pane to set
     */
    public void setContentPane(Pane contentPane) {
        contentPaneProperty.setValue(contentPane);
    }

    /**
     * Content pane property.
     *
     * @return content pane property
     */
    public Property<Pane> contentPaneProperty() {
        return contentPaneProperty;
    }

    /**
     * Defines whether this window shall be moved to front when a user clicks on
     * the window.
     *
     * @param moveToFront the state to set
     */
    public void setMoveToFront(boolean moveToFront) {
        this.moveToFront = moveToFront;
    }

    /**
     * Indicates whether the window shall be moved to front when a user clicks
     * on the window.
     *
     * @return <code>true</code> if the window shall be moved to front when a
     * user clicks on the window; <code>false</code> otherwise
     */
    public boolean isMoveToFront() {
        return moveToFront;
    }

    /**
     * Returns the window title.
     *
     * @return the title
     */
    public final String getTitle() {
        return titleProperty.get();
    }

    /**
     * Defines the window title.
     *
     * @param title the title to set
     */
    public final void setTitle(String title) {
        this.titleProperty.set(title);
    }

    /**
     * Returns the window title property.
     *
     * @return the window title property
     */
    public final StringProperty titleProperty() {
        return titleProperty;
    }

    /**
     * Returns a list that contains the icons that are placed on the left side
     * of the titlebar. Add icons to the list to add them to the left side of
     * the window titlebar.
     *
     * @return a list containing the left icons
     *
     * @see #getRightIcons()
     */
    public ObservableList<WindowIcon> getLeftIcons() {
        return leftIcons;
    }

    /**
     * Returns a list that contains the icons that are placed on the right side
     * of the titlebar. Add icons to the list to add them to the right side of
     * the window titlebar.
     *
     * @return a list containing the right icons
     *
     * @see #getLeftIcons()
     */
    public ObservableList<WindowIcon> getRightIcons() {
        return rightIcons;
    }

    /**
     * Defines whether this window shall be minimized.
     *
     * @param v the state to set
     */
    public void setMinimized(boolean v) {
        minimizeProperty.set(v);
    }

    /**
     * Indicates whether the window is currently minimized.
     *
     * @return <code>true</code> if the window is currently minimized;
     * <code>false</code> otherwise
     */
    public boolean isMinimized() {
        return minimizeProperty.get();
    }

    /**
     * Returns the minimize property.
     *
     * @return the minimize property
     */
    public BooleanProperty minimizedProperty() {
        return minimizeProperty;
    }

    /**
     * Defines whether this window shall be resizeable by the user.
     *
     * @param v the state to set
     */
    public void setResizableWindow(boolean v) {
        resizableProperty.set(v);
    }

    /**
     * Indicates whether the window is resizeable by the user.
     *
     * @return <code>true</code> if the window is resizeable; <code>false</code>
     * otherwise
     */
    public boolean isResizableWindow() {
        return resizableProperty.get();
    }

    /**
     * Returns the resize property.
     *
     * @return the minimize property
     */
    public BooleanProperty resizeableWindowProperty() {
        return resizableProperty;
    }

    /**
     * Defines whether this window shall be movable.
     *
     * @param v the state to set
     */
    public void setMovable(boolean v) {
        movableProperty.set(v);
    }

    /**
     * Indicates whether the window is movable.
     *
     * @return <code>true</code> if the window is movable; <code>false</code>
     * otherwise
     */
    public boolean isMovable() {
        return movableProperty.get();
    }

    /**
     * Returns the movable property.
     *
     * @return the minimize property
     */
    public BooleanProperty movableProperty() {
        return movableProperty;
    }

    /**
     * Returns the titlebar style class property.
     *
     * @return the titlebar style class property
     */
    public StringProperty titleBarStyleClassProperty() {
        return titleBarStyleClassProperty;
    }

    /**
     * Defines the CSS style class of the titlebar.
     *
     * @param name the CSS style class name
     */
    public void setTitleBarStyleClass(String name) {
        titleBarStyleClassProperty.set(name);
    }

    /**
     * Returns the CSS style class of the titlebar.
     *
     * @return the CSS style class of the titlebar
     */
    public String getTitleBarStyleClass() {
        return titleBarStyleClassProperty.get();
    }

    /**
     * Returns the resizable border width property.
     *
     * @return the resizable border width property
     *
     * @see #setResizableBorderWidth(double)
     */
    public DoubleProperty resizableBorderWidthProperty() {
        return resizableBorderWidthProperty;
    }

    /**
     * Defines the width of the "resizable border" of the window. The resizable
     * border is usually defined as a rectangular border around the layout
     * bounds of the window where the mouse cursor changes to "resizable" and
     * which allows to resize the window by performing a "dragging gesture",
     * i.e., the user can "grab" the window border and change the size of the
     * window.
     *
     * @param v border width
     */
    public void setResizableBorderWidth(double v) {
        resizableBorderWidthProperty.set(v);
    }

    /**
     * Returns the width of the "resizable border" of the window.
     *
     * @return the width of the "resizable border" of the window
     *
     * @see #setResizableBorderWidth(double)
     */
    public double getResizableBorderWidth() {
        return resizableBorderWidthProperty.get();
    }

    /**
     * Closes this window.
     */
    public void close() {

        // if already closed, we do nothing 
        if (this.getParent() == null) {
            return;
        }

        closeRequested.set(true);

        closeSelectedWindows();

        if (getCloseTransition() != null) {
            getCloseTransition().play();
        } else {
            if (getOnCloseAction() != null) {
                getOnCloseAction().handle(new ActionEvent(this, Window.this));
            }
            NodeUtil.removeFromParent(Window.this);
            if (getOnClosedAction() != null) {
                getOnClosedAction().handle(new ActionEvent(this, Window.this));
            }
        }
    }

    /**
     * Returns the "on-closed-action" property.
     *
     * @return the "on-closed-action" property.
     *
     * @see #setOnClosedAction(javafx.event.EventHandler)
     */
    public ObjectProperty<EventHandler<ActionEvent>> onClosedActionProperty() {
        return onClosedActionProperty;
    }

    /**
     * Defines the action that shall be performed after the window has been
     * closed.
     *
     * @param onClosedAction the action to set
     */
    public void setOnClosedAction(EventHandler<ActionEvent> onClosedAction) {
        this.onClosedActionProperty.set(onClosedAction);
    }

    /**
     * Returns the action that shall be performed after the window has been
     * closed.
     *
     * @return the action that shall be performed after the window has been
     * closed or <code>null</code> if no such action has been defined
     */
    public EventHandler<ActionEvent> getOnClosedAction() {
        return this.onClosedActionProperty.get();
    }

    /**
     * Returns the "on-close-action" property.
     *
     * @return the "on-close-action" property.
     *
     * @see #setOnCloseAction(javafx.event.EventHandler)
     */
    public ObjectProperty<EventHandler<ActionEvent>> onCloseActionProperty() {
        return onCloseActionProperty;
    }

    /**
     * Defines the action that shall be performed before the window will be
     * closed.
     *
     * @param onClosedAction the action to set
     */
    public void setOnCloseAction(EventHandler<ActionEvent> onClosedAction) {
        this.onCloseActionProperty.set(onClosedAction);
    }

    /**
     * Returns the action that shall be performed before the window will be
     * closed.
     *
     * @return the action that shall be performed before the window will be
     * closed or <code>null</code> if no such action has been defined
     */
    public EventHandler<ActionEvent> getOnCloseAction() {
        return this.onCloseActionProperty.get();
    }

    /**
     * Returns the "close-transition" property.
     *
     * @return the "close-transition" property.
     *
     * @see #setCloseTransition(javafx.animation.Transition)
     */
    public ObjectProperty<Transition> closeTransitionProperty() {
        return closeTransitionProperty;
    }

    /**
     * Defines the transition that shall be used to indicate window closing.
     *
     * @param t the transition that shall be used to indicate window closing or
     * <code>null</code> if no transition shall be used.
     */
    public void setCloseTransition(Transition t) {
        closeTransitionProperty.set(t);
    }

    /**
     * Returns the transition that shall be used to indicate window closing.
     *
     * @return the transition that shall be used to indicate window closing or
     * <code>null</code> if no such transition has been defined
     */
    public Transition getCloseTransition() {
        return closeTransitionProperty.get();
    }

    @Override
    public boolean requestSelection(boolean select) {

        if (!isSelectable()) {
            return false;
        }

        selectedProperty.set(select);

        return true;
    }

    /**
     * @return the selectableProperty
     */
    public BooleanProperty selectableProperty() {
        return selectableProperty;
    }

    /**
     * Defines whether this window is selectable.
     *
     * @param selectable state to set
     */
    public void setSelectable(Boolean selectable) {
        selectableProperty.set(selectable);
    }

    /**
     * Indicates whether this window is selectable.
     *
     * @return {@code true} if this window is selectable; {@code false}
     * otherwise
     */
    public boolean isSelectable() {
        return selectableProperty.get();
    }

    /**
     * Indicates whether this window is selectable.
     *
     * @return the selectedProperty
     */
    @Override
    public ReadOnlyBooleanProperty selectedProperty() {
        return selectedProperty;
    }

    /**
     *
     * @return {@code true} if the window is selected; {@code false} otherwise
     */
    @Override
    public boolean isSelected() {
        return selectedProperty().get();
    }

    /**
     * Indicates whether this window is currently moving.
     *
     * @return the movingProperty
     */
    public ReadOnlyBooleanProperty movingProperty() {
        return movingProperty;
    }

    /**
     * Indicates whether this window is currently moving.
     *
     * @return {@code} if this window is currently moving; {@code false}
     * otherwise
     */
    public boolean isMoving() {
        return movingProperty().get();
    }

    /**
     * Indicates whether this window is currently resizing.
     *
     * @return the resizingProperty
     */
    public ReadOnlyBooleanProperty resizingProperty() {
        return resizingProperty;
    }

    /**
     * Indicates whether this window is currently resizing.
     *
     * @return {@code} if this window is currently resizing; {@code false}
     * otherwise
     */
    public boolean isResizing() {
        return resizingProperty().get();
    }

}
