package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Arrays;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AeatComunidadAutonoma implements Serializable {
	
	 DESCONOCIDO("-","Desconocido")
	,ANDALUCIA	("01","Andalucia")
	,ARAGON		("02","ARAGON")
	,ASTURIAS	("03","PRINCIPADO DE ASTURIAS")
	,BALEARS	("04","ILLES BALEARS")
	,CANARIAS	("05","CANARIAS")
	,CANTABRIA	("06","CANTABRIA")
	,CASTILLA_LA_MANCHA("07","CASTILLA-LA MANCHA")
	,CASTILLA_LEON("08 ","CASTILLA Y LEON")
	,CATALUNYA("09","CATALUNYA")
	,EXTREMADURA("10","EXTREMADURA")
	,GALICIA("11","GALICIA")
	,MADRID("12","MADRID")
	,MURCIA("13","REGION DE MURCIA")
	,LA_RIOJA("14","LA RIOJA")
	,COMUNITAT_VALENCIANA("15","COMUNITAT VALENCIANA")
	,CEUTA("18","Ceuta")  
	,MELILLA("19","Melilla") 
	,EXTRANJERO("20","Extranjero")
	;
	
	private String code;
	private String description;
	
	private AeatComunidadAutonoma( String code, String description ) {
		this.code = code;
		this.description = description; 
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AeatComunidadAutonoma getByValue(String code) {
		if (AonStringUtils.isBlank(code)) return null;
		return Arrays.stream(AeatComunidadAutonoma.values())
			.filter(bt -> bt.getCode().equals(code))
			.findFirst()
			.orElse(AeatComunidadAutonoma.DESCONOCIDO);
	}
}