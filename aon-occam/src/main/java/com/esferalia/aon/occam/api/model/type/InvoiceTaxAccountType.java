package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

public enum InvoiceTaxAccountType implements Serializable {
	/*SIN TIPO*/	  		  NO_TYPE ()	{ @Override public <T> T visit(InvoiceTaxAccountTypeVisitor<T> visitor) { return visitor.visitNoType(); }}
	/*IVA REPERCUTIDO*/		, OUTPUT 		{ @Override public <T> T visit(InvoiceTaxAccountTypeVisitor<T> visitor) { return visitor.visitOutput(); }}
	/*IVA SOPORTADO*/		, INPUT			{ @Override public <T> T visit(InvoiceTaxAccountTypeVisitor<T> visitor) { return visitor.visitInput(); }}	
	/*AJUSTE IVA*/			, ADJ			{ @Override public <T> T visit(InvoiceTaxAccountTypeVisitor<T> visitor) { return visitor.visitAdj(); }}
	/*AJUSTE IMP. DIRECTA*/	, ADJ_DIRECT_TAX{ @Override public <T> T visit(InvoiceTaxAccountTypeVisitor<T> visitor) { return visitor.visitAdjDirectTax(); }}
	/*RETENCION IRPF*/		, WITHHOLDING 	{ @Override public <T> T visit(InvoiceTaxAccountTypeVisitor<T> visitor) { return visitor.visitWithholding(); }}
	;

	private InvoiceTaxAccountType() {
	}

	public byte value() {
		return (byte) ordinal();
	}

	public static Optional<InvoiceTaxAccountType> safeValueOf(Byte i) {
		if (i == null) return Optional.empty();
		return safeValueOf(i.intValue());
	}

	public static Optional<InvoiceTaxAccountType> safeValueOf(Integer i) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceTaxAccountType.values().length) return Optional.empty();
		return Optional.of(InvoiceTaxAccountType.values()[i]);
	}
	
	public abstract <T> T visit(InvoiceTaxAccountTypeVisitor<T> visitor);
	
	public static interface InvoiceTaxAccountTypeVisitor<T> {
		T visitNoType();
		T visitOutput();
		T visitInput();
		T visitAdj();
		T visitAdjDirectTax();
		T visitWithholding();
	}	

}