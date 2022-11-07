package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TargetStatus implements Serializable {

	ACTIVE("Activo"),
	INACTIVE("Inactivo");
	
	private String description;
	
	private TargetStatus(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public static TargetStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TargetStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TargetStatus.values().length) return null;
		return TargetStatus.values()[i];
	}
	
	public static TargetStatus safeValueOf( String str) {
		if(AonStringUtils.isBlank(str)) return ACTIVE;
		for (TargetStatus rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getDescription().equalsIgnoreCase(str))
				return rs;
		}
		return ACTIVE;
	}
	
	public static List<TargetStatus> safeValueOf( List<String> str) {
		List<TargetStatus> list = new ArrayList<>();
		
		for (TargetStatus rs : values()) {
			if(str.contains(rs.name()) || str.contains(rs.getDescription())) {
				list.add(rs);
			}
		}
		
		return list;
	}

}