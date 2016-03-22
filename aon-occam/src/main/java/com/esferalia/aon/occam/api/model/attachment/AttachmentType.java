package com.esferalia.aon.occam.api.model.attachment;

public enum AttachmentType {

	THUMBNAIL,
    IMAGE,
	DOCUMENT,
	ECOMMERCE_PRODUCT;

	public byte value() {
		return (byte) this.ordinal();
	}

}
