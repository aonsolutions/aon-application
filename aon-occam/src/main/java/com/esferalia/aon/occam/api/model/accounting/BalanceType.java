package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

public enum BalanceType implements Serializable {
	
	 BALANCE_NORMAL		(false,"Balance de Situaci\u00F3n (Normal)") 
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceNormal(); }}
	,BALANCE_ABBREVIATE	(false,"Balance de Situaci\u00F3n (Abreviado)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceAbbreviate();}}
	,BALANCE_PYMES		(false,"Balance de Situaci\u00F3n (PYMES)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalancePymes(); }}
	,PYG_NORMAL			(true ,"Cuenta de explotaci\u00F3n (Normal)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygNormal(); }}
	,PYG_ABBREVIATE		(true,"Cuenta de explotaci\u00F3n (Abreviado)")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygAbbreviate(); }}
	,PYG_PYMES			(true ,"Cuenta de explotaci\u00F3n (PYMES)")
 		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygPymes(); }}
	
	,BALANCE_COOP_NORMAL(false,"Balance de Situaci\u00F3n (COOPERATIVAS Normal)")
 		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceCoopNormal(); }}
	,BALANCE_COOP_ABBREV(false,"Balance de Situaci\u00F3n (COOPERATIVAS Abreviado)")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceCoopAbbreviate(); }}
	,PYG_COOP_NORMAL	(true ,"Cuenta de explotaci\u00F3n (COOPERATIVAS Normal)")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygCoopNormal(); }}
	,PYG_COOP_ABBREV	(true ,"Cuenta de explotaci\u00F3n (COOPERATIVAS Abreviado)")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygCoopAbbreviate(); }}
	;
	
	private String name;
	private boolean pyg;
	
	private BalanceType(boolean pyg,String name) {
		this.name = name;
		this.pyg = pyg;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isPyG() {
		return this.pyg;
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
