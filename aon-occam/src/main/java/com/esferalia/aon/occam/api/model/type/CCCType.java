package com.esferalia.aon.occam.api.model.type;

public enum CCCType {


	PRINCIPAL(SSRegimeType.GENERAL),

	TRAINING(SSRegimeType.GENERAL),
    
	LEARNING(SSRegimeType.GENERAL),
    
	TRADE_REPRESENTATIVE(SSRegimeType.GENERAL),
	
	ASSIMILATEDS(SSRegimeType.GENERAL),
	
	FELLOWS(SSRegimeType.GENERAL),
	
	HOME_EMPLOYEES(SSRegimeType.DOMESTIC_EMPLOYEES),
	
	AGRICULTURAL(SSRegimeType.AGRICULTURAL),
	
	ARTIST(SSRegimeType.GENERAL);
	
	private SSRegimeType ssRegimeType;
	
	private CCCType(SSRegimeType ssRegimeType) {
		this.ssRegimeType = ssRegimeType;
	}
	
	public Byte getValue() {
		return (byte) ordinal();
	}
	
	public SSRegimeType getSsRegimeType() {
		return ssRegimeType;
	}
	
	public static SSRegimeType getSsRegimeType( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= CCCType.values().length) return null;
		return CCCType.values()[i].getSsRegimeType();
	}
}
