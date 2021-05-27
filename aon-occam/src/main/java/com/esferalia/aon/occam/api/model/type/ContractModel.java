package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;


public enum ContractModel  implements Serializable {
	
	PE151,
	PE166,
	PE170,
	PE174,
	PE175,
	PE176,
	PE177,
	PE179,
	PE181,
	PE182,
	PE183,
	PE185,
	PE186,
	PE187,
	PE190,
	PE191,
	PE192,
	PE193,
	PE195,
	PE196,
	PE197,
	PE200,
	PE201,
	PE202,
	PE203,
	PE204,
	PE205,
	PE206,
	PE213,
	PE217,
	PE218,
	PE220,
	PE221,
	PE226;

	public Byte getValue() {
		return (byte) ordinal();
	}
}