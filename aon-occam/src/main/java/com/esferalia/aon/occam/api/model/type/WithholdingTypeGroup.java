package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum WithholdingTypeGroup implements Serializable {

	 PROFESIONAL					("Actividades profesionales") 
	,OTRAS							("Otras actividades econ\u00F3micas")
	,DERECHOS_IMAGEN				("Imputaci\u00F3n rentas por cesi\u00F3n derechos imagen.")
	,GANANCIAS_PATRIMONIALES		("Ganancias patrimoniales")
	,CAPITAL_MOBILIARIO 			("Capital mobiliario")
	,CAPITAL_INMOBILIARIO			("Capital inmobiliario")
	,TRABAJO						("Trabajo")
//	,OTRAS_GANANCIAS_PATRIMONIALES	("Otras ganancias patrimoniales")
	;
	
	private String description;
	
	private WithholdingTypeGroup(String description){
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public byte value() {
		return (byte) ordinal();
	}	

	public static WithholdingTypeGroup safeValueOf(Byte i) {
		if (i == null) return null;
		return safeValueOf(i.intValue());
	}

	public static WithholdingTypeGroup safeValueOf(Integer i) {
		if (i == null) return null;
		if (i < 0 || i >= WithholdingTypeGroup.values().length) return null;
		return WithholdingTypeGroup.values()[i];
	}
	
}

