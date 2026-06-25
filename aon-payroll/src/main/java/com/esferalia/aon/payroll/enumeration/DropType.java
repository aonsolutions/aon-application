package com.esferalia.aon.payroll.enumeration;

public enum DropType {
	
	
	 
        DROP_NOT_JUSTIFIED {
        	@Override
        	public <T> T accept(Visitor<T> visitor) {
        	    return visitor.visitDropNotJustified(this);
        	}
        };	 
	 
	public static interface Visitor<T> {
		T visitDropNotJustified(DropType type);
	}
	
	public abstract <T> T accept(Visitor<T> visitor );
	
	public static DropType getByOrdinal(int ordinal){
		if ( ordinal < 0 )
			return null;
		DropType values [] = DropType.values();
		if ( ordinal >= values.length)
			return null;
		return values[ordinal];
	}
	
	
}
