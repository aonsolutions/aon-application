package com.esferalia.aon.occam.api.model.fiscal.mod390;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AEATActicityType {
	
	A01 	("Arrendadores de bienes inmuebles urbanos"),
	A02 	("Ganadería independiente"),
	A03 	("Resto de actividades empresariales no incluidas en otros apartados"),
	A04 	("Actividades profesionales de carácter artístico o deportivo"),
	A05 	("Resto de actividades profesionales"),
	B01 	("Actividad agrícola"),
	B02 	("Actividad ganadera dependiente"),
	B03 	("Actividad forestal"),
	B04 	("Producción de mejillón en batea"),
	B05 	("Actividad pesquera, excepto la actividad de producción de mejillón en batea"),
	C		("Actividades no iniciadas"),
	;
	

	private String description;
	
	private AEATActicityType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static AEATActicityType get(String code) {
		if ( AonStringUtils.isEmpty(code)) return null;
		return AEATActicityType.valueOf(code);
	}
	
}
