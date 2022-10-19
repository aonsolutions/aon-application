package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRegistryStatusVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum RegistryStatus implements Serializable {

	ACTIVE("Activo")	{ @Override public void visit(IRegistryStatusVisitor visitor) { visitor.visitActive();} },
	INACTIVE("Inactivo"){ @Override public void visit(IRegistryStatusVisitor visitor) { visitor.visitInactive();} },
	BLOCKED("Bloqueado"){ @Override public void visit(IRegistryStatusVisitor visitor) { visitor.visitBlocked();} };
    
	private String description;

	private RegistryStatus(String description) {
		this.description = description;
	}

	public String getDescription(){
		return this.description;
	}
	
//	@Deprecated
//	public String getName(){
//		return this.toString();
//	}

	public byte value(){
		return (byte) this.ordinal();
	}
	
	public void visit(IRegistryStatusVisitor visitor) {
	}

	public static RegistryStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static RegistryStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RegistryStatus.values().length) return null;
		return RegistryStatus.values()[i];
	}
	
	public static RegistryStatus safeValueOf( String str) {
		if(AonStringUtils.isBlank(str)) return ACTIVE;
		for (RegistryStatus rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getDescription().equalsIgnoreCase(str))
				return rs;
		}
		return ACTIVE;
	}
	
	public static List<RegistryStatus> safeValueOf( List<String> str) {
		List<RegistryStatus> list = new ArrayList<>();
		
		for (RegistryStatus rs : values()) {
			if(str.contains(rs.name()) || str.contains(rs.getDescription())) {
				list.add(rs);
			}
		}
		
		return list;
	}
}