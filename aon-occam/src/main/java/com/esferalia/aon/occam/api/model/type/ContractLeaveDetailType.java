package com.esferalia.aon.occam.api.model.type;

public enum ContractLeaveDetailType {
	
	BAJA, 
	CONFIRMACION, 
	ALTA, 
	;
	
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

	public static ContractLeaveDetailType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ContractLeaveDetailType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ContractLeaveDetailType.values().length) return null;
		return ContractLeaveDetailType.values()[i];
	}
	
	public static ContractLeaveDetailType safeValueOf( String i ) {
		for (ContractLeaveDetailType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return ContractLeaveDetailType.BAJA;
	}

}
