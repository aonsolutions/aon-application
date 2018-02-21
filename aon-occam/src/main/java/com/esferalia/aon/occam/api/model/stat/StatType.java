package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;

public enum StatType implements Serializable {

	 INVOICE	( chartVisitor -> chartVisitor.visitInvoice())
	,TASK		( chartVisitor -> chartVisitor.visitTask())
	,FEE		( chartVisitor -> chartVisitor.visitFee())
	;
	
	public static interface IStatTypeVisitor {
		void visitInvoice();
		void visitTask();
		void visitFee();
	}
	
	@FunctionalInterface
	protected static interface IVisitor {
		void visit(IStatTypeVisitor chartVisitor);
	}

	private IVisitor visitor;
	
	private StatType(IVisitor visitor) {
		this.visitor = visitor;
	}
	
	public void visit(IStatTypeVisitor chartVisitor) {
		visitor.visit(chartVisitor);
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}

	public static StatType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static StatType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= StatType.values().length) return null;
		return StatType.values()[i];
	}
}
