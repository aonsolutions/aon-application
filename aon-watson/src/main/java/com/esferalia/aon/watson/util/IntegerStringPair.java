package com.esferalia.aon.watson.util;

public class IntegerStringPair extends Pair<Integer, String> {
	
	private static final long serialVersionUID = 5574413958092758030L;

    public static IntegerStringPair of(Integer left, String right) {
        return new IntegerStringPair(left, right);
    }

    public IntegerStringPair(Integer l, String r) {
		super(l, r);
	}

}
