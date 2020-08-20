package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediNif implements Serializable {
	
	private static final long serialVersionUID = 6865210693414641809L;
	
	private String str;
	private TediNifType type;

	public String getStr() {
		return str;
	}
	
	public TediNif setStr(String str) {
		this.str = str;
		return this;
	}
	
	public TediNifType getType() {
		return type;
	}
	
	public TediNif setType(TediNifType type) {
		this.type = type;
		return this;
	}
}
