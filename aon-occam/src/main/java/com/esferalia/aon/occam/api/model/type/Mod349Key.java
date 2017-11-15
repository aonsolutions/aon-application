package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod349Key {
	
	E("E","Entregas intracomunitarias de bienes"),
	M("M","Entregas intracomunitarias de bienes posteriores a una importaci\u00F3n exenta"),
	H("H","Entregas intracomunitarias de bienes posteriores a una importaci\u00F3n exenta, efectuadas por el representante fiscal"),
	A("A","Adquisiciones intracomunitarias de bienes"),
	T("T","Entregas en otro estado miembro subsiguientes a adquisiciones intracomunitarias exentas"),
	S("S","Prestaciones de servicios intracomunitarias"),
	I("I","Adquisiciones intracomunitarias de servicios");

	private String value;
	private String description;
	
	private Mod349Key(String value, String description) {
		this.value = value;	
		this.description = description;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static Mod349Key safeValueOf( String key ) {		
		if (AonStringUtils.isBlank(key)) {
			return null;
		}
		try {			
			return Mod349Key.valueOf(key);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static String safeValue( Mod349Key key ) {		
		return key==null ? null : key.getValue();
	}
	
}
