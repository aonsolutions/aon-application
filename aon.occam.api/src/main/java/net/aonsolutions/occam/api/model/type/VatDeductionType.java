package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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

	public static Optional<VatDeductionType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<VatDeductionType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= VatDeductionType.values().length) return Optional.empty();
		return Optional.of(VatDeductionType.values()[i]);
	}
	
	public static Optional<VatDeductionType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

	public static Byte value(VatDeductionType t) {
		return t == null ? null : t.value();
	}
}