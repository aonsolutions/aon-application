package com.esferalia.aon.occam.api.model.type;

public enum ContractLeaveDischargeCause {
	
	CURACION, 
	FALLECIMIENTO, 
	INSPECCION_MEDICA, 
	PROPUESTA_INCAPACIDAD,
	AGOTAMIENTO_PLAZO,
	MEJORIA,
	INCOMPARECENCIA,
	CONTROL_INSS,
	RECUPERACION_CAPACIDAD_PROFESIONAL,
	INCOMPARECENCIA_CONTRATO_FORMACION,
	INICIO_MATERNIDAD,
	ALTA_MEDICA_INSPECCION_INSS,
	PROPUESTA_IP_INSS,
	FALLECIMIENTO_COMUNICADO_NSS,
	ALTA_MATEPSS
	;
	
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

	public static ContractLeaveDischargeCause safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ContractLeaveDischargeCause safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ContractLeaveDischargeCause.values().length) return null;
		return ContractLeaveDischargeCause.values()[i];
	}
	
	public static ContractLeaveDischargeCause safeValueOf( String i ) {
		for (ContractLeaveDischargeCause rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return ContractLeaveDischargeCause.CURACION;
	}

}
