package com.esferalia.aon.occam.api.model.type;

public enum ContractLeaveType {
	
	ENFERMEDAD_COMUN(1), 
	ACCIDENTE_LABORAL(3), 
	MATERNIDAD(6), 
	PATERNIDAD(7), 
	RIESGO_EMBARAZO(8),
	RIESGO_LACTANCIA(9),
	ACCIDENTE_NO_LABORAL(2),
	ENFERMEDAD_COMUN_CARENCIA(5),
	ENFERMEDAD_COMUN_PRESTACION(4),
	;
	
	//TGSS
//	1.- ENFERMEDAD COMÚN
//	2.- ACCIDENTE NO LABORAL
//	3.- ACCIDENTE LABORAL
//	4.- ENFERMEDAD PROFESIONAL
//	5.- PERIODOS OBSERVACIÓN ENFERMEDAD PROFESIONAL
//	
	private Integer value;
	
	private ContractLeaveType(Integer value) {
		this.value = value;
	}
	
	public Integer getValueTGSS() {
		return value;
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
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
