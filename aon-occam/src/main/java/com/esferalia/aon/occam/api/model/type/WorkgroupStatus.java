package com.esferalia.aon.occam.api.model.type;

public enum WorkgroupStatus {
	
    ACTIVE,
    INACTIVE;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }

	public static WorkgroupStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static WorkgroupStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= WorkgroupStatus.values().length) return null;
		return WorkgroupStatus.values()[i];
	}
	
	public static WorkgroupStatus safeValueOf( String i ) {
		for (WorkgroupStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}

}
