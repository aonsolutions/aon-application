package com.esferalia.aon.occam.api.model.news;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum NewsType {

	NEWS,
	MESSAGE,
	COMMUNICATION;

	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
    
    public static NewsType valueOf(Integer index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static NewsType valueOf(Byte index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static NewsType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static NewsType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= NewsType.values().length) return null;
		return NewsType.values()[i];
	}
	
	public static NewsType safeValueOf(String name) {
		return valueNameOf(name);
	}
	
	public static NewsType valueNameOf(String name) {
		if(AonStringUtils.isBlank(name)) return NEWS;
		for(NewsType p :NewsType.values())
			if(name.equalsIgnoreCase(p.getName()) || name.equalsIgnoreCase(p.name()))
				return p;
		return NEWS;
	}
}
