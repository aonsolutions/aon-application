package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Arrays;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AeatEstadoCivil implements Serializable {
	
	 DESCONOCIDO("0","Desconocido")
	,SOLTERO("1","Soltero")
	,CASADO("2","Casado")
	,VIUDO("3","Viudo")
	,DIVORCIADO("4","Divorciado/Separado")
									;
	private String code;
	private String description;
	
	private AeatEstadoCivil(String code,  String description ) {
		this.code = code;
		this.description = description; 
	}

	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AeatEstadoCivil getByValue(String code) {
		if (AonStringUtils.isBlank(code)) return null;
		return Arrays.stream(AeatEstadoCivil.values())
			.filter(bt -> bt.getCode().equals(code))
			.findFirst()
			.orElse(AeatEstadoCivil.DESCONOCIDO);
	}
}