package com.gluonhq.vworkflows.views.helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DoubleValue {

    private String title;
    private DoubleProperty value = new SimpleDoubleProperty(0);

    public DoubleValue(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public final double getValue() {
        return value.get();
    }

    public final DoubleProperty valueProperty() {
        return value;
    }

    public final void setValue(double value) {
        this.value.set(value);
    }
}
