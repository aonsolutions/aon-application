package com.esferalia.aon.occam.api.model.type;

public enum TagType {

	RATTACH,
    PRODUCT,    
    PRIORITY,
    MARKETPLACE,
    OFFICE_NOTICE,
    OFFICE_PRIORITY,
    OFFICE_STATUS,
    OFFICE_TYPE; // Tipo de Aviso
	
	

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}