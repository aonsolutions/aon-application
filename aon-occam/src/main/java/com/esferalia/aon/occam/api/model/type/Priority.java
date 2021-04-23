package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import com.esferalia.aon.occam.api.model.task.TagColor;

public enum Priority implements Serializable {
	NONE("NINGUNA", TagColor.BLUE3),
	LOW("BAJA", TagColor.GREEN),
	NORMAL("NORMAL", TagColor.ORANGE),
	HIGH("ALTA", TagColor.RED);
	
	private String name;
	private TagColor color;
	
	private Priority(String name, TagColor color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName(){
		return name;
	}
	
	public TagColor getColor(){
		return color;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Byte value(Priority pt){
		return pt !=null ?  pt.value() : 0;
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static Priority valueNameOf(String name) {
		for(Priority p :Priority.values())
			if(name.equals(p.getName())) return p;
		return NONE;
	}
	
	public static Priority safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static Priority safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Priority.values().length) return null;
		return Priority.values()[i];
	}
}
