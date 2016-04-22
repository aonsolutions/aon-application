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
}