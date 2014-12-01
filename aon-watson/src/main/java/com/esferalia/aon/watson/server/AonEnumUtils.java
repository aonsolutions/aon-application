package com.esferalia.aon.watson.server;

import com.esferalia.aon.watson.AonCoreException;

public class AonEnumUtils {
	
	public static Byte getByte(Boolean bool) {
		return (bool == null) ? null : (byte) (bool ? 1 : 0); 
	}
	
	public static Byte getByte(Enum<?> enume) {
		return (enume == null) ? null : (byte) enume.ordinal();
	}

	public static boolean getBoolean(Byte value) throws AonCoreException {
		if (value == null)
			return false;
		return (value == 1);
	}
	
}
