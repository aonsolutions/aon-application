package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceErrorLevel implements Serializable{
	 INF ("INFO.")  { @Override public <T> T visit(InvoiceErrorLevelVisitor<T> visitor) {return visitor.visitINF();} }
	,WRN ("AVISO") { @Override public <T> T visit(InvoiceErrorLevelVisitor<T> visitor) {return visitor.visitWRN();} }
	,ERR ("ERROR") { @Override public <T> T visit(InvoiceErrorLevelVisitor<T> visitor) {return visitor.visitERR();} } 
	;
	
	private  String label;
	private InvoiceErrorLevel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static Optional<InvoiceErrorLevel> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceErrorLevel.values().length) return Optional.empty();
		return Optional.of(InvoiceErrorLevel.values()[i]);
	}
	
	public static InvoiceErrorLevel safeValueOf(String label) {
		for (InvoiceErrorLevel level : InvoiceErrorLevel.values()) {
			if (AonStringUtils.equalsIgnoreCase(level.name(), label)) {
				return level;
			} else if (AonStringUtils.equalsIgnoreCase(level.getLabel(), label)) {
				return level;
			}
		}
		return null;
	}
	
	public static String name(InvoiceErrorLevel l) {
		return l == null ? null : l.name();
	}
	
	public abstract <T> T visit( InvoiceErrorLevelVisitor<T> visitor );
	public static interface InvoiceErrorLevelVisitor<T> {
		T visitINF();
		T visitWRN();
		T visitERR();
	}
	public static InvoiceErrorLevel mostSeriousLevel(InvoiceErrorLevel level1, InvoiceErrorLevel level2) {
		if (level1 == null) return level2;
		if (level2 == null) return level1;
		return level1.ordinal() >= level2.ordinal() ? level1 : level2;
	}
}