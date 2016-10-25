package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum PurchaseSourceType implements Serializable {

	PROPOSAL,
	PURCHASE,
	SALES;

	public byte value() {
		return (byte) ordinal();
	}
}