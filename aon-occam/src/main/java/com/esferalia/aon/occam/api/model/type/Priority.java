package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum Priority implements Serializable {
	NONE("NINGUNA"),
	LOW("BAJA"),
	NORMAL("NORMAL"),
	HIGH("ALTA");
	
	private String name;
	
	private Priority(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static Priority valueNameOf(String name) {
		for(Priority p :Priority.values())
			if(name.equals(p.getName())) return p;
		return NONE;
	}
}
