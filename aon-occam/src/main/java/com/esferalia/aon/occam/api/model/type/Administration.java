package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum Administration implements Serializable {
	  ALAVA
	, BIZKAIA
	, GIPUZKOA
	, NAVARRA
	, COMMON_TERRITORY
	, UNKNOWN;

	public byte getValue() {
		return (byte) ordinal();
	}
}
