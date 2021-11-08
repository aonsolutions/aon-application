package com.esferalia.aon.watson.server;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AonEnumUtils {
	
	public static Byte getByte(Boolean bool) {
		return (bool == null) ? null : (byte) (bool ? 1 : 0); 
	}
	
	public static Byte getByte(Enum<?> enume) {
		return (enume == null) ? null : (byte) enume.ordinal();
	}

	public static boolean getBoolean(Byte value) {
		if (value == null)
			return false;
		return (value == 1);
	}
	
	public static boolean getBoolean(Boolean value) {
		if (value == null)
			return false;
		return (value.booleanValue());
	}
	
	public static boolean getAonBoolean(String value) {
		if (value == null) return false;
		if (AonStringUtils.equalsIgnoreCase("true", AonStringUtils.trim(value))) return true;
		if (AonStringUtils.equalsIgnoreCase("1", AonStringUtils.trim(value))) return true;
		return false;
	}
	
}
