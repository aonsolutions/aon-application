package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Arrays;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AeatRegCotizacion implements Serializable {

	 R0138("0138","R\u00E9gimen General de Empleados del Hogar")
	,R0140("0140","R\u00E9gimen General (Conv. Esp.)")
	,R0161("0161","R\u00E9gimen General Agrario")
	,R0521("0521","Reg. Esp. Trabajadores Aut\u00F3nomos")
	,R0540("0540","Reg.Espe Aut\u00F3nomos (Conv.Esp.)")
	,R0825("0825","Reg. Especial del Mar (Aut\u00F3nomos)")
	,R0840("0840","Reg. Especial del Mar (Conv.Esp.)")
	,R0940("0940","Reg. Esp. Miner\u00EDa del Carb\u00F3n (Conv. Esp.)")
	,R1211("1211","Reg. Esp. de empleados del hogar (Fijos)")
	;
	
	
	private String code;
	private String description;
	private AeatRegCotizacion( String code, String description ) {
		this.code = code;
		this.description = description; 
	}
	
	public String getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AeatRegCotizacion getByValue(String code) {
		if (AonStringUtils.isBlank(code)) return null;
		return Arrays.stream(AeatRegCotizacion.values())
			.filter(bt -> bt.getCode().equals(code))
			.findFirst()
			.orElse(null);
	}
}