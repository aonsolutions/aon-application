package net.aonsolutions.watson.client;

import java.io.Serializable;

public class MutableBoolean implements Mutable<Boolean>, Serializable, Comparable<MutableBoolean> {


    private static final long serialVersionUID = -3528169648482919544L;
    private boolean value;

    public MutableBoolean() {
    }

    public MutableBoolean(final boolean value) {
        this.value = value;
    }

    public MutableBoolean(final Boolean value) {
        this.value = value.booleanValue();
    }

    @Override
    public Boolean getValue() {
        return Boolean.valueOf(this.value);
    }

    public void setValue(final boolean value) {
        this.value = value;
    }

    public void setFalse() {
        this.value = false;
    }

    public void setTrue() {
        this.value = true;
    }
    
    @Override
    public void setValue(final Boolean value) {
        this.value = value.booleanValue();
    }
    
    public boolean isTrue() {
        return value;
    }
    
    public boolean isFalse() {
        return !value;
    }

    public boolean booleanValue() {
        return value;
    }

    public Boolean toBoolean() {
        return Boolean.valueOf(booleanValue());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MutableBoolean valueObj) {
            return value == valueObj.booleanValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
    }

    @Override
    public int compareTo(final MutableBoolean other) {
        if (this.value == other.value) {
            return 0;
        }
        return this.value ? 1 : -1;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
