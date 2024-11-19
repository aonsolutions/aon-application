package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvestAssetRegime implements Serializable{

	PROPERTY("Propiedad"),
	RENTING("Alquiler"),
	FINANCIAL_LEASING("Arrendamiento Financiero"),
	OTHER("Otro");

	private String description;
	
	private InvestAssetRegime(String description) {
		this.description = description;
	}
	
	public String description() {
		return description;
	}
	
	public byte value(){
		return (byte) ordinal();
	}
	
	public static Optional<InvestAssetRegime> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvestAssetRegime> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvestAssetRegime.values().length) return Optional.empty();
		return Optional.of(InvestAssetRegime.values()[i]);
	}
	
	public static Optional<InvestAssetRegime> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
}
