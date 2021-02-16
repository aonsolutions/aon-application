package com.esferalia.aon.payroll.enumeration;

public enum OffType {
	
	
	 
	 NOT_PAID_PERMISSION {
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitNotPaidPermission(this);
		}
	 };
	 
	 
	public static interface Visitor<T> {
		T visitNotPaidPermission(OffType type);
	}
	
	public abstract <T> T accept(Visitor<T> visitor );
	
	public static OffType getByOrdinal(int ordinal){
		if ( ordinal < 0 )
			return null;
		OffType values [] = OffType.values();
		if ( ordinal >= values.length)
			return null;
		return values[ordinal];
	}
	
	
}
