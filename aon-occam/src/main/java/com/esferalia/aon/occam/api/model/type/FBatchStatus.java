package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFBatchStatusVisitor;

public enum FBatchStatus implements Serializable {

	UNKNOWN("Desconocido")			{ @Override public void visit(IFBatchStatusVisitor visitor) { visitor.visitUnknown();} },
	PENDING("Pendiente")			{ @Override public void visit(IFBatchStatusVisitor visitor) { visitor.visitPending();} },
	GENERATED("Fichero Generado" )	{ @Override public void visit(IFBatchStatusVisitor visitor) { visitor.visitGenerated();} },
	ACCOUNTED("Contabilizado")		{ @Override public void visit(IFBatchStatusVisitor visitor) { visitor.visitAccounted();} },
	;

	private String description;
	
	private FBatchStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public void visit(IFBatchStatusVisitor visitor) {
	}

	public static FBatchStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static FBatchStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= FBatchStatus.values().length) return null;
		return FBatchStatus.values()[i];
	}
}