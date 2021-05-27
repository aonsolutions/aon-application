package com.esferalia.aon.gwt.common.shared;

public class EnumUtils {
	
	public static <T extends Enum<?>> T getEnumConstant(Class<T> enumClass, Byte ordinal) {
		if ( ordinal == null )
			return null;
		if ( ordinal < 0 )
			return null;
		
		T constants [] = enumClass.getEnumConstants();
		if ( ordinal >= constants.length )
			return null;
		
		return constants[ordinal];
	}


}
