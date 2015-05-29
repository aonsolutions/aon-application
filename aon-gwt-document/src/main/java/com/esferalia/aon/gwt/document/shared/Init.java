package com.esferalia.aon.gwt.document.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Init implements IsSerializable{

	Vector<Boolean> vector;
	Integer domainId;
	
	public Vector<Boolean> getVector() {
		return vector;
	}
	public void setVector(Vector<Boolean> vector) {
		this.vector = vector;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
}
