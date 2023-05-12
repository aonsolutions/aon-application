package net.aonsolutions.watson.client;

import java.io.Serializable;

public class Pair<L extends Serializable, R extends Serializable> implements Serializable {

	private static final long serialVersionUID = 9208539290697311361L;

	private L l;
	private R r;
	
    public static <L extends Serializable, R extends Serializable> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public Pair(L l, R r) {
    	this.l = l;	
    	this.r = r;
    }

    public L getLeft() {
    	return l;
    }
    public Pair<L, R> setLeft( L l) {
    	this.l = l;
    	return this;
    }
    
    public R getRight() {
    	return r;
    }
    public Pair<L, R> setRight( R r) {
    	this.r = r;
    	return this;
    }

    public final L getKey() {
        return getLeft();
    }
    public R getValue() {
        return getRight();
    }
}
