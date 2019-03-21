package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

public enum BalanceType implements Serializable {
	
	 BALANCE_NORMAL		("Balance de Situaci\u00F3n (Normal)") 
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceNormal(); }}
	,BALANCE_ABBREVIATE	("Balance de Situaci\u00F3n (Abreviado)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceAbbreviate();}}
	,BALANCE_PYMES		("Balance de Situaci\u00F3n (PYMES)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalancePymes(); }}
	,PYG_NORMAL			("Cuenta de explotaci\u00F3n (Normal)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygNormal(); }}
	,PYG_ABBREVIATE		("Cuenta de explotaci\u00F3n (Abreviado)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygAbbreviate(); }}
	,PYG_PYMES			("Cuenta de explotaci\u00F3n (PYMES)")
 		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygPymes(); }}
	,BALANCE_COOP_NORMAL("Balance de Situaci\u00F3n (COOPERATIVAS Normal)")
 		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceCoopNormal(); }}
	,PYG_COOP_NORMAL	("Cuenta de explotaci\u00F3n (COOPERATIVAS Normal)")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygCoopNormal(); }}
	,BALANCE_COOP_ABBREV("Balance de Situaci\u00F3n (COOPERATIVAS Abreviado)")
 		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceCoopAbbreviate(); }}
	,PYG_COOP_ABBREV	("Cuenta de explotaci\u00F3n (COOPERATIVAS Abreviado)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygCoopAbbreviate(); }}
	;
	
	private String name;
	
	private BalanceType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void visit(IBalanceTypeVisitor visitor);
	
	public static BalanceType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static BalanceType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= BalanceType.values().length) return null;
		return BalanceType.values()[i];
	}
	
	public static interface IBalanceTypeVisitor extends Serializable {
		void visitBalanceNormal();
		void visitBalanceAbbreviate();
		void visitBalancePymes();
		
		void visitBalanceCoopNormal();
		void visitBalanceCoopAbbreviate();
		
		void visitPygNormal();
		void visitPygAbbreviate();
		void visitPygPymes();
		
		void visitPygCoopNormal();
		void visitPygCoopAbbreviate();
	}
}
