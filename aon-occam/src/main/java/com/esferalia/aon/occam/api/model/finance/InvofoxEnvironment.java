package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvofoxEnvironment implements Serializable{

	PRODUCCION("64804a43d883e2000ac0423a", "Producción"),
	DEMO_COMERCIAL_CP("6641fb719668517cfd939cb3", "Demo-Comercial (CP)"),
	DEMO_COMERCIAL_MP("6641fc74e44a57ce1d83a366", "Demo-Comercial (MP)"),
	DEMO_COMERCIAL_MR("6641fc31fc862711a4b5c80e", "Demo-Comercial (MR)"),
	DEMO_DESARROLLO("65491eee49f881000dd14c72","Demo-Desarrollo"),
	DEMO_SOPORTE("663a3c9c77a8e69b58d69b84", "Demo-Soporte"),
	TEST5("6641fcd385c047bd8b6df0da", "TEST5");
	
	String id;
	String description;
	
	private InvofoxEnvironment(String id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InvofoxEnvironment safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvofoxEnvironment safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvofoxEnvironment.values().length) return null;
		return InvofoxEnvironment.values()[i];
	}
	
	public static InvofoxEnvironment safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvofoxEnvironment rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public static List<InvofoxEnvironment> list(){ 
		return Arrays.asList(values());
	}
	
	public static Stream<InvofoxEnvironment> stream(){ 
		return list().stream();
	}
}
