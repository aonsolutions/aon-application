package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

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

	public static Optional<WithholdingTypeGroup> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<WithholdingTypeGroup> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= WithholdingTypeGroup.values().length) return Optional.empty();
		return Optional.of( WithholdingTypeGroup.values()[i]);
	}
	
	public static Optional<WithholdingTypeGroup> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}

	public WithholdingType[] getTypes() {
		return WithholdingType.getTypes(this);
	}
	
	public Byte[] getValueTypes() {
		return WithholdingType.getValueTypes(this);
	}
}

