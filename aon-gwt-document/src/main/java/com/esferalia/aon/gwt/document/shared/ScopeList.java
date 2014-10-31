package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ScopeList implements IsSerializable {

	Vector<Scope> list = new Vector<Scope>();

	public ScopeList() {

	}
	
	public Vector<Scope> getList() {
		return list;
	}

	public void setList(Vector<Scope> list) {
		this.list = list;
	}
	
}
