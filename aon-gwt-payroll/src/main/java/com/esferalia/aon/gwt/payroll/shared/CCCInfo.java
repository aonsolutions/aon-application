package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class CCCInfo implements Serializable{
	private String ccc;
	private Byte type;
	
	public CCCInfo(){
		super();
	}
	
	public CCCInfo(String ccc, Byte type) {
		super();
		this.ccc = ccc;
		this.type = type;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}
}