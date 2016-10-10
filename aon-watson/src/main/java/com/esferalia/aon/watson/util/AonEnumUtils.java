package com.esferalia.aon.watson.util;


public class AonEnumUtils {
	
	public static Byte getByte(Boolean bool) {
		return (bool == null) ? null : (byte) (bool ? 1 : 0); 
	}
	
	public static Byte getByte(Enum<?> enume) {
		return (enume == null) ? null : (byte) enume.ordinal();
	}
	
	public static <T extends Enum<?>, N extends Number> T enumValue(Class<T> clazz, N n){
		if ( n == null )
			return null;
		int i = n.intValue();
		if ( i < 0 ) 
			return null;
		T values [] = clazz.getEnumConstants();
		if ( i >= values.length ) 
			return null;
		return values[i];
	}
	
	public static <T extends Enum<?>, N extends Number> T enumValue(N n, T def ){
		if ( n == null )
			return def;
		int i = n.intValue();
		if ( i < 0 ) 
			return def;
		T values []= (T[]) def.getClass().getEnumConstants();
		if ( i >= values.length ) 
			return def;
		return values[i];
	}
	

}
