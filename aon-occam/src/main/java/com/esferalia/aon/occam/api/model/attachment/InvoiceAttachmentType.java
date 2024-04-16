package com.esferalia.aon.occam.api.model.attachment;

public enum InvoiceAttachmentType {

	INVOICE,
	RECEIPT;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
	
	public static Byte[] drive(){
		return new Byte[]{
			INVOICE.value(), RECEIPT.value()
		};
	}
	
	public static InvoiceAttachmentType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static InvoiceAttachmentType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceAttachmentType.values().length) return null;
		return InvoiceAttachmentType.values()[i];
	}
}