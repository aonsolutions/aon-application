package net.aonsolutions.watson.server;

public class AonEnumUtils {
	
	private AonEnumUtils() {
		
	}

	public static Byte getByte(Boolean bool) {
		if (bool== null) return null;
		return (byte) (bool ? 1 : 0); 
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
	
}
