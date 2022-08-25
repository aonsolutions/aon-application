package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.news.NewsType;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum CategoryType {
		
	REGISTRY_ATTACHMENT,
	ARTICLE;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
	
	   
    public static CategoryType valueOf(Integer index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static CategoryType valueOf(Byte index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static CategoryType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static CategoryType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= NewsType.values().length) return null;
		return CategoryType.values()[i];
	}
	
	public static CategoryType safeValueOf(String name) {
		return valueNameOf(name);
	}
	
	public static CategoryType valueNameOf(String name) {
		if(AonStringUtils.isBlank(name)) return null;
		for(CategoryType p :CategoryType.values())
			if(name.equalsIgnoreCase(p.getName()) || name.equalsIgnoreCase(p.name()))
				return p;
		return null;
	}
}
