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
}