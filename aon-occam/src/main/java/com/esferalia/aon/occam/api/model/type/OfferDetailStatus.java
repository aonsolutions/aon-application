package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum OfferDetailStatus implements Serializable {

	PENDING,
	ON_SALE,
	ON_INVOICE;
	
	public static OfferDetailStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static OfferDetailStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= OfferDetailStatus.values().length) return null;
		return OfferDetailStatus.values()[i];
	}

}
