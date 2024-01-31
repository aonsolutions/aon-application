package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public class InvofoxConfiguration implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private boolean test;
	
	public boolean isTest() {
		return test;
	}
	
	public InvofoxConfiguration setTest(boolean test) {
		this.test = test;
		return this;
	}
	
}
