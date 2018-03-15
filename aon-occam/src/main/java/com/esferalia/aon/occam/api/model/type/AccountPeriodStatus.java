package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum AccountPeriodStatus implements Serializable {

	 ACTIVE
	,INACTIVE
	,OPENING
	,OPERATING
	,CLOSED;

	 public boolean isActive() {
		 return (this == ACTIVE || this == OPENING); 
	 }
	 
	 public byte getValue() {
		 return (byte) this.ordinal();
	 }
	 
	public static AccountPeriodStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static AccountPeriodStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AccountPeriodStatus.values().length) return null;
		return AccountPeriodStatus.values()[i];
	}
	 
}