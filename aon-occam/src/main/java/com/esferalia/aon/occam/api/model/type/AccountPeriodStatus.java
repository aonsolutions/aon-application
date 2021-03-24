package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAccountPeriodStatusVisitor;

public enum AccountPeriodStatus implements Serializable {

	 ACTIVE		("Activo")		
	 	{ @Override public void visit(IAccountPeriodStatusVisitor visitor) { visitor.visitActive();} }
	,INACTIVE	("Inactivo")
 		{ @Override public void visit(IAccountPeriodStatusVisitor visitor) { visitor.visitInactive();} }
	,OPENING	("Apertura")
 		{ @Override public void visit(IAccountPeriodStatusVisitor visitor) { visitor.visitOpening();} }
	,OPERATING	("Explotaci\u00F3n")
 		{ @Override public void visit(IAccountPeriodStatusVisitor visitor) { visitor.visitOperating();} }
	,CLOSED		("Cerrado")
 		{ @Override public void visit(IAccountPeriodStatusVisitor visitor) { visitor.visitClosed();} }
 	;

	private String description;
	
	private AccountPeriodStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return (this == ACTIVE || this == OPENING);
	}

	public byte getValue() {
		return (byte) this.ordinal();
	}

	public void visit(IAccountPeriodStatusVisitor visitor) {
	}

	public static AccountPeriodStatus safeValueOf(Byte i) {
		if (i == null)
			return null;
		return safeValueOf(i.intValue());
	}

	public static AccountPeriodStatus safeValueOf(Integer i) {
		if (i == null)
			return null;
		if (i < 0 || i >= AccountPeriodStatus.values().length)
			return null;
		return AccountPeriodStatus.values()[i];
	}

}