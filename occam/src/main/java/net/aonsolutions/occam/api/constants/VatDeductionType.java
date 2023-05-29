package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum VatDeductionType implements Serializable {

	 WITH_RIGHT		("Con Drcho. Deduc.","CON Dcho.")
	,WITHOUT_RIGHT	("Sin Drcho. Deduc.","SIN Dcho.")
	,NON_TAXABLE	("No sujeto"		,"No Suj.")
	; 
	
	private String name;
	private String abbr;

	private VatDeductionType(String name, String abbr) {
		this.name = name;
		this.abbr = abbr;
	}

	public String getName() {
		return name;
	}
	public String getAbbr() {
		return abbr;
	}
	
	public byte value() {
		return (byte) ordinal();
	}

	public static Optional<VatDeductionType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<VatDeductionType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= VatDeductionType.values().length) return Optional.empty();
		return Optional.of( VatDeductionType.values()[i]);
	}
	
	public static Optional<VatDeductionType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
}