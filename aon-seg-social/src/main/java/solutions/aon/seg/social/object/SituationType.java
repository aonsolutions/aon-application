package solutions.aon.seg.social.object;


public enum SituationType {
	ALTA,
	BAJA;
	
	public byte value() {
		return (byte) ordinal();
	}
	public static SituationType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static SituationType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= SituationType.values().length) return null;
		return SituationType.values()[i];
	}
	
	public static SituationType safeValueOf( String i ) {
		for (SituationType rs : values()) {
			if(rs.name().indexOf(i.toUpperCase())>=0)
				return rs;
		}
		return SituationType.ALTA;
	}
}
