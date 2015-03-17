package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class FiscalActivityInfo implements Serializable {
	
	private static final long serialVersionUID = -7095500555384381798L;
	
	private Integer id;
	private Integer fiscalActivity;
	private FiscalActivityInfoKey infoKey;
	private int line;
	private String value;
	
	public Integer getId() {
		return id;
	}
	public FiscalActivityInfo setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getFiscalActivity() {
		return fiscalActivity;
	}
	public FiscalActivityInfo setFiscalActivity(Integer fiscalActivity) {
		this.fiscalActivity = fiscalActivity;
		return this;
	}
	
	public FiscalActivityInfoKey getInfoKey() {
		return infoKey;
	}
	public FiscalActivityInfo setInfoKey(FiscalActivityInfoKey infoKey) {
		this.infoKey = infoKey;
		return this;
	}
	
	public int getLine() {
		return line;
	}
	public FiscalActivityInfo setLine(int line) {
		this.line = line;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	public FiscalActivityInfo setValue(String value) {
		this.value = value;
		return this;
	}
	
}

