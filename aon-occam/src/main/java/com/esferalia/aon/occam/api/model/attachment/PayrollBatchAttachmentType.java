package com.esferalia.aon.occam.api.model.attachment;

public enum PayrollBatchAttachmentType {

	GENERATED_DOCUMENT,
	RETURN_DOCUMENT;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
	
	public static Byte[] drive(){
		return new Byte[]{
			GENERATED_DOCUMENT.value(), RETURN_DOCUMENT.value()
		};
	}
}