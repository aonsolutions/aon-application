package com.esferalia.aon.watson.server;

import java.util.Objects;

import com.esferalia.aon.watson.util.AonCollectionUtils;
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
	
    public static boolean in(Enum<?> value, Enum<?> ... options) {
		if (value == null || options == null || options.length == 0) {
			return false;
		}
		return AonCollectionUtils.stream(options)
			.filter(Objects::nonNull)
			.anyMatch(option -> option == value);
	}
    
    public static boolean notIn(Enum<?> value, Enum<?> ... options) {
    	return !in(value,options);
    }

}
