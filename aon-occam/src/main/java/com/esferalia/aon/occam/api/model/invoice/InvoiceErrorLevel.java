package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Optional;

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
	
	public abstract <T> T visit( InvoiceErrorLevelVisitor<T> visitor );
	public static interface InvoiceErrorLevelVisitor<T> {
		T visitINF();
		T visitWRN();
		T visitERR();
	}
}