package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceIntegrityCheckError implements Serializable{

	DATES_DIFFERENT_YEARS("El a\u00F1o de la fecha de emisi\u00F3n es diferente al a\u00F1o de la fecha de impuestos.", false),
	REGISTRY_DOCUMENT_DIFFERENT("El NIF de la ficha de ClI/PRO/ACR no coincide con el NIF de la factura", false),
	ACCOUNT_ENTRY_SUM_VS_INVOICE_TOTAL("El Total del apunte no coincide con el total factura", false),
	ACCOUNT_ENTRY_VAT_VS_INVOICE_TAX("El Sumatorio IVA del apunte (472 y/o 477) no coincide con el total IVA de la factura", false),
	INVOICE_TAX_DUPLICATE("El valor de 'invoice_tax' está duplicado.", true),
	INVOICE_TAXABLE_BASE0("El valor de 'taxable_base' en la cabecera de 'invoice' es 0.", true);
	
	String message;
	boolean fix;
	
	private InvoiceIntegrityCheckError(String message, boolean fix) {
		this.message = message;
		this.fix = fix;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isFix() {
		return fix;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InvoiceIntegrityCheckError safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceIntegrityCheckError safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceIntegrityCheckError.values().length) return null;
		return InvoiceIntegrityCheckError.values()[i];
	}
	
	public static InvoiceIntegrityCheckError safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvoiceIntegrityCheckError rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public static List<InvoiceIntegrityCheckError> list(){ 
		return Arrays.asList(values());
	}
	
	public static Stream<InvoiceIntegrityCheckError> stream(){ 
		return list().stream();
	}
}
