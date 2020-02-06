package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

public enum BalanceType implements Serializable {
	
	 BALANCE_NORMAL		(false,"Balance de Situaci\u00F3n (Normal)","https://www.boe.es/buscar/act.php?id=BOE-A-2007-19884&tn=6&p=20161217") 
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceNormal(); }}
	,BALANCE_ABBREVIATE	(false,"Balance de Situaci\u00F3n (Abreviado)","https://www.boe.es/buscar/act.php?id=BOE-A-2007-19884&tn=6&p=20161217")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceAbbreviate();}}
	,BALANCE_PYMES		(false,"Balance de Situaci\u00F3n (PYMES)","https://www.boe.es/buscar/act.php?id=BOE-A-2007-19884&tn=6&p=20161217")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalancePymes(); }}
	,PYG_NORMAL			(true ,"Cuenta de explotaci\u00F3n (Normal)","https://www.boe.es/buscar/act.php?id=BOE-A-2007-19884&tn=6&p=20161217")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygNormal(); }}
	,PYG_ABBREVIATE		(true,"Cuenta de explotaci\u00F3n (Abreviado)","https://www.boe.es/buscar/act.php?id=BOE-A-2007-19884&tn=6&p=20161217")
	 	{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygAbbreviate(); }}
	,PYG_PYMES			(true ,"Cuenta de explotaci\u00F3n (PYMES)","https://www.boe.es/buscar/act.php?id=BOE-A-2007-19884&tn=6&p=20161217")
 		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygPymes(); }}
	
	,BALANCE_COOP_NORMAL(false,"Balance de Situaci\u00F3n (COOPERATIVAS Normal)","https://www.boe.es/buscar/pdf/2010/BOE-A-2010-20034-consolidado.pdf")
 		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceCoopNormal(); }}
	,BALANCE_COOP_ABBREV(false,"Balance de Situaci\u00F3n (COOPERATIVAS Abreviado)","https://www.boe.es/buscar/pdf/2010/BOE-A-2010-20034-consolidado.pdf")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceCoopAbbreviate(); }}
	,PYG_COOP_NORMAL	(true ,"Cuenta de explotaci\u00F3n (COOPERATIVAS Normal)","https://www.boe.es/buscar/pdf/2010/BOE-A-2010-20034-consolidado.pdf")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygCoopNormal(); }}
	,PYG_COOP_ABBREV	(true ,"Cuenta de explotaci\u00F3n (COOPERATIVAS Abreviado)","https://www.boe.es/buscar/pdf/2010/BOE-A-2010-20034-consolidado.pdf")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygCoopAbbreviate(); }}

	,BALANCE_ASOC_ABBREV(false,"Balance (Entidades sin fines lucrativos)","https://www.boe.es/boe/dias/2013/04/09/pdfs/BOE-A-2013-3736.pdf")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitBalanceAsocAbbreviate(); }}
	,PYG_ASOC_ABBREV(true,"Cuenta de resultados (Entidades sin fines lucrativos)","https://www.boe.es/boe/dias/2013/04/09/pdfs/BOE-A-2013-3736.pdf")
		{ @Override public void visit(IBalanceTypeVisitor visitor) { visitor.visitPygAsocAbbreviate(); }}
	;
	
	private String name;
	private String helpLink;
	private boolean pyg;
	
	private BalanceType(boolean pyg,String name,String helpLink) {
		this.name = name;
		this.pyg = pyg;
		this.helpLink = helpLink;
	}
	
	public String getName() {
		return name;
	}
	public String getHelpLink() {
		return helpLink;
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
		
		void visitBalanceAsocAbbreviate();
		
		void visitPygNormal();
		void visitPygAbbreviate();
		void visitPygPymes();
		
		void visitPygCoopNormal();
		void visitPygCoopAbbreviate();
		
		void visitPygAsocAbbreviate();
	}

}
