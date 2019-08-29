package com.gluonhq.vworkflows.views.helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class OperatorValue {

    public enum OPERATOR {
        ADD("  +  "),
        SUBTRACT("  -  "),
        MULTIPLY("  *  "),
        DIVIDE("  /  ");

        private final String operation;

        OPERATOR(String operation) {
            this.operation = operation;
        }

        public String getOperation() {
            return operation;
        }
    }

    private String title;
    private ObjectProperty<OPERATOR> value = new SimpleObjectProperty<>(OPERATOR.ADD);

    public OperatorValue(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public final OPERATOR getValue() {
        return value.get();
    }

    public final ObjectProperty<OPERATOR> valueProperty() {
        return value;
    }

    public final void setValue(OPERATOR value) {
        this.value.set(value);
    }
}
