package net.aonsolutions.occam.api.model.type;

import java.util.Optional;

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
		return new Byte[]{INVOICE.value(), RECEIPT.value()};
	}
	
	public static Optional<InvoiceAttachmentType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvoiceAttachmentType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceAttachmentType.values().length) return Optional.empty();
		return Optional.of(InvoiceAttachmentType.values()[i]);
	}
}