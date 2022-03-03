package com.esferalia.aon.occam.api.model.type;

public enum ContractLeaveType {
	
	ENFERMEDAD_COMUN(1, "Enfermedad Com\u00fan"), 
	ACCIDENTE_LABORAL(3, "Accidente de trabajo"), 
	MATERNIDAD(6, "Maternidad"), 
	PATERNIDAD(7, "Paternidad"), 
	RIESGO_EMBARAZO(8, "Riesgo para el embarazo"),
	RIESGO_LACTANCIA(9, "Riesgo durante la lactancia"),
	ACCIDENTE_NO_LABORAL(2, "Accidente no laboral"),
	ENFERMEDAD_COMUN_CARENCIA(5, "Enfermedad com\u00fan periodo de carencia"),
	ENFERMEDAD_COMUN_PRESTACION(3,"Enfermedad com\u00fan, prestación profesional (COVID-19)"),
	;
	
//  TGSS
//	1.- ENFERMEDAD COMÚN
//	2.- ACCIDENTE NO LABORAL
//	3.- ACCIDENTE LABORAL
//	4.- ENFERMEDAD PROFESIONAL
//	5.- PERIODOS OBSERVACIÓN ENFERMEDAD PROFESIONAL
	
	private Integer valueTGSS;
	private String name;
	
	private ContractLeaveType(Integer value, String name) {
		this.valueTGSS = value;
		this.name = name;
	}
	
	public Integer getValueTGSS() {
		return valueTGSS;
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return name;
    }
    
	public static ContractLeaveType valueOfTGSS(Integer value) {
		for (ContractLeaveType type : ContractLeaveType.values()) {
			if (type.getValueTGSS().equals(value)) 
				return type;
		}
		return ContractLeaveType.ENFERMEDAD_COMUN;
	}
	 

	public static ContractLeaveType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ContractLeaveType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ContractLeaveType.values().length) return null;
		return ContractLeaveType.values()[i];
	}
	
	public static ContractLeaveType safeValueOf( String i ) {
		for (ContractLeaveType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return ContractLeaveType.ENFERMEDAD_COMUN;
	}

}
