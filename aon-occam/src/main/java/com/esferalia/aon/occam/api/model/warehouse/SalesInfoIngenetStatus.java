package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum SalesInfoIngenetStatus implements Serializable{

	PENDING("Pendiente"),
	RETRIEVED("Recuperado"),
	REOPENED("Reabierto"),
	PROCESSED("Procesado");
	
	String description;
	private SalesInfoIngenetStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static SalesInfoIngenetStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static SalesInfoIngenetStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SalesInfoIngenetStatus.values().length) return null;
		return SalesInfoIngenetStatus.values()[i];
	}
	
	public static SalesInfoIngenetStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (SalesInfoIngenetStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getDescription()))
				return rs;
		}
		return null;
	}
	
	public boolean isPending() {
		return SalesInfoIngenetStatus.PENDING.equals(this);
	}
	
	public boolean isRetrieved() {
		return SalesInfoIngenetStatus.RETRIEVED.equals(this);
	}
	
	public boolean isReopened() {
		return SalesInfoIngenetStatus.REOPENED.equals(this);
	}
	
	public boolean isProcessed() {
		return SalesInfoIngenetStatus.PROCESSED.equals(this);
	}
	
}
