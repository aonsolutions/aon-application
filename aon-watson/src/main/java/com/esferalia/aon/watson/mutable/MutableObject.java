package com.esferalia.aon.watson.mutable;

import java.io.Serializable;
import java.util.Objects;

public class MutableObject<T> implements Mutable<T>, Serializable {

    private static final long serialVersionUID = 86241875189L;

    private T value;

    public MutableObject() {
    }
    
    public MutableObject(final T value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() == obj.getClass()) {
            final MutableObject<?> that = (MutableObject<?>) obj;
            return Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public void setValue(final T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }
}
