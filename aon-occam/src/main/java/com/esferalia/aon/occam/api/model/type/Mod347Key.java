package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod347Key {
	
	A("A","Adquisiciones de bienes y servicios superiores a 3.005,06 euros"),
	B("B","Entregas de bienes y prestaciones de servicios superiores a 3.005,06 euros"),
	C("C","Cobros por cuenta de terceros superiores a 300,51 euros"),
	D("D","Adq. de bienes o serv. sup. a 3.005,06 euros, realizadas por Ent. P\u00FAbl., part. pol., sindicatos o asoc. empresariales ..."),
	E("E","Subvenciones, auxilios y ayudas satisfechos por las Administraciones P\u00FAblicas cualquiera que sea su importe"),
	F("F","Ventas agencia viaje"),
	G("G","Compras agencia viaje");

	private String value;
	private String description;
	
	private Mod347Key(String value, String description) {
		this.value = value;	
		this.description = description;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static Mod347Key safeValueOf( String key ) {		
		if (AonStringUtils.isBlank(key)) {
			return null;
		}
		try {			
			return Mod347Key.valueOf(key);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static String safeValue( Mod347Key key ) {		
		return key==null ? null : key.getValue();
	}
	
}
