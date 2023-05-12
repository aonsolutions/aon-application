package net.aonsolutions.watson.client;

public interface Mutable<T> {

    T getValue();
    void setValue(T value);

}
