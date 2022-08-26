package com.esferalia.aon.occam.api.model.warehouse;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum GS1128Codes {
	
	/**
	 * 00 - Código Seriado de la Unidad de Envío (SSCC) - n2 + n18
	 */
	CODE_00("00", 2, 18, false),
	/**
	 * 01 - Código de agrupación - n2 + n14
	 */
	CODE_01("01", 2, 14, false),
	/**
	 * 02 - Código del artículo / agrupación de contenido - n2 + n14
	 */
	CODE_02("02", 2, 14, false),
	/**
	 * 37 - Cantidades (acompañando al IA 02) - n2 + n...8
	 */
	CODE_37("37", 2, 8, true),
	/**
	 * 10 - Número de lote - n2 + an...20
	 */
	CODE_10("10", 2, 20, true),
	/**
	 * 11 - Fecha de Fabricación - n2 + n6
	 */
	CODE_11("11", 2, 6, false),
	/**
	 * 11 - Fecha de consumo preferente - n2 + n6
	 */
	CODE_15("15", 2, 6, false),
	/**
	 * 11 - Fecha de caducidad - n2 + n6
	 */
	CODE_17("17", 2, 6, false),
	/**
	 * 310X - Peso neto en kilos - n4 + n6
	 */
	CODE_310X("310", 4, 6, false),
	/**
	 * 330X - Peso bruto en kilos - n4 + n6
	 */
	CODE_330X("330", 4, 6, false);
	
	String code;
	Integer keyLength;
	Integer valueLength;
	boolean separator;

	private GS1128Codes(String code, Integer keyLength, Integer valueLength, boolean separator) {
		this.code = code;
		this.keyLength = keyLength;
		this.valueLength = valueLength;
		this.separator = separator;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public String getCode() {
		return code;
	}
	
	public Integer getKeyLength() {
		return keyLength;
	}
	
	public Integer getValueLength() {
		return valueLength;
	}
	
	public boolean isSeparator() {
		return separator;
	}
	
	public static GS1128Codes safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static GS1128Codes safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= GS1128Codes.values().length) return null;
		return GS1128Codes.values()[i];
	}
	
	public static GS1128Codes safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (GS1128Codes rs : values()) {
			if(rs.getCode().equalsIgnoreCase(i) || rs.name().equalsIgnoreCase(i))
				return rs;
		}
		return null;
	}
}
