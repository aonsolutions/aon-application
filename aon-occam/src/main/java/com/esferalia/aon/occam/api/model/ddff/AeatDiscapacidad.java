package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Arrays;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AeatDiscapacidad implements Serializable {
	 D0("0","Sin discapacidad o discapacidad < 33%")                  
	,D1("1","Discapacidad >= 33% o < 65%")                  
	,D2("2","Discapacidad >= 33% o < 65%  y necesita ayuda de 3ª persona")                  
	,D3("3","Discapacidad >= 65%")
	,D4("4","Incapacitación judicial por sentencia de la jurisdicción civil")
	;
	
	private String description;
	private String code;
	
	private AeatDiscapacidad(String code, String description ) {
		this.code = code;
		this.description = description; 
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AeatDiscapacidad getByValue(String code) {
		if (AonStringUtils.isBlank(code)) return null;
		return Arrays.stream(AeatDiscapacidad.values())
			.filter(bt -> bt.getCode().equals(code))
			.findFirst()
			.orElse(AeatDiscapacidad.D0);
	}
}