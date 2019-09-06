package com.gluonhq.vworkflows.views.helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class FunctionValue {

    public enum FUNCTION {
        SINE    ("   SINE   "),
        COSINE  ("  COSINE  ");
        // TAN     ("   TAN    "),
        // COT     ("   COT    ");

        private final String Function;

        private FUNCTION(String Function) {
            this.Function = Function;
        }

        public String getFunction() {
            return Function;
        }
    }

    private String title;
    private ObjectProperty<FUNCTION> value = new SimpleObjectProperty<>(FUNCTION.SINE);

    public FunctionValue(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public final FUNCTION getValue() {
        return value.get();
    }

    public final ObjectProperty<FUNCTION> valueProperty() {
        return value;
    }

    public final void setValue(FUNCTION value) {
        this.value.set(value);
    }
}
