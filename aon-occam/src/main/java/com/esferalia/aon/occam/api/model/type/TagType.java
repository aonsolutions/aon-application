package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

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
	TASK_DOCUMENT,
	CERTIFICATE,
	NOTE
	; // Tipo de Aviso


	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    
    public static TagType valueOf(Integer index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TagType valueOf(Byte index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TagType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TagType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TagType.values().length) return null;
		return TagType.values()[i];
	}
	
	public static TagType safeValueOf(String name) {
		return valueNameOf(name);
	}
	
	public static TagType valueNameOf(String name) {
		if(AonStringUtils.isBlank(name)) return TASK_LABEL;
		for(TagType p :TagType.values())
			if(name.equalsIgnoreCase(p.getName()) || name.equalsIgnoreCase(p.name()))
				return p;
		return TASK_LABEL;
	}
}