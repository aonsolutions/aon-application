package com.esferalia.aon.occam.api.model.attachment;

public enum SepeBatchAttachmentType {

	GENERATED_FILE,
	COMMUNICATION_ID,
	RESPONSE_FILE;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
	
	public static Byte[] drive(){
		return new Byte[]{
			GENERATED_FILE.value(), COMMUNICATION_ID.value(),
			RESPONSE_FILE.value()
		};
	}
}