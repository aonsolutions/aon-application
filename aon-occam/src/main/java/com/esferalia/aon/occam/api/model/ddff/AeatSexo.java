package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Arrays;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AeatSexo implements Serializable {
	
	 DESCONOCIDO("-","Desconocido")
	,HOMBRE("H","Hombre")                  
	,MUJER ("M","Mujer")                  
	;
	
	private String code;
	private String description;
	private AeatSexo( String code, String description ) {
		this.code = code;
		this.description = description; 
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AeatSexo getByValue(String code) {
		if (AonStringUtils.isBlank(code)) return null;
		return Arrays.stream(AeatSexo.values())
			.filter(bt -> bt.getCode().equals(code))
			.findFirst()
			.orElse(AeatSexo.DESCONOCIDO);
	}
}