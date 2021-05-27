package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum TagType implements Serializable{

	RATTACH,
    PRODUCT,    
    PRIORITY,
    MARKETPLACE,
    OFFICE_NOTICE,
    OFFICE_PRIORITY,
    OFFICE_STATUS,
    OFFICE_TYPE,
	PACKING,
	TASK_TYPE,
	TASK_PRIORITY,
	TASK_LABEL,
	TASK_DOCUMENT
	; // Tipo de Aviso

	
	

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}