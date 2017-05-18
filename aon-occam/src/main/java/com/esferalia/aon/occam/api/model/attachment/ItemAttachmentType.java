package com.esferalia.aon.occam.api.model.attachment;

public enum ItemAttachmentType {

	THUMBNAIL,
    IMAGE,
	DOCUMENT,
	ECOMMERCE_PRODUCT;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
	
	public static Byte[] drive(){
		return new Byte[]{
			THUMBNAIL.value(), IMAGE.value(),
			DOCUMENT.value(), ECOMMERCE_PRODUCT.value()
		};
	}
}
