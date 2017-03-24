package com.esferalia.aon.occam.api.model.attachment;

public enum ProjectAttachmentType {

	DOCUMENT,
	CRS,
	CONEXFLOW,
	PAYSLIP
	;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
}