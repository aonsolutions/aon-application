package com.esferalia.aon.watson.server;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AonEnumUtils {
	
	private AonEnumUtils() {
		
	}
	private static final Byte ZERO_BYTE = Byte.valueOf("0");
	private static final Byte ONE_BYTE = Byte.valueOf("1");
	
	public static Byte getByte(Optional<Boolean> opt) {
		return AonObjectUtils.ifOptionalPresent(opt, (Boolean bool) -> Boolean.TRUE.equals(bool) ? ONE_BYTE : ZERO_BYTE);
	}

	public static Byte getByte(Boolean bool) {
		if (bool== null) return null;
		return (byte) (bool ? 1 : 0); 
	}
	
	public static <T extends Enum<?>> Byte getOptEnumByte(Optional<T> opt) {
		return AonObjectUtils.ifOptionalPresent(opt, (T enume) -> Byte.valueOf((byte) enume.ordinal()));
	}
	public static Byte getByte(Enum<?> enume) {
		return (enume == null) ? null : (byte) enume.ordinal();
	}

	public static boolean getBoolean(Byte value) {
		if (value == null) return false;
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
