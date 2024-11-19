package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum VATExemptionCause implements Serializable {
	
	E1("Exenta por el articulo 20"),
    E2("Exenta por el articulo 21"),
    E3("Exenta por el articulo 22"),
    E4("Exenta por el articulo 23 y 24"),
    E5("Exenta por el articulo 25"),
    E6("Exenta por otros");

    private final String description;

	private VATExemptionCause(String description){
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public byte value(){
		return (byte) ordinal();
	}
	
	public static Optional<VATExemptionCause> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<VATExemptionCause> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= VATExemptionCause.values().length) return Optional.empty();
		return Optional.of(VATExemptionCause.values()[i]);
	}
	
	public static Optional<VATExemptionCause> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
}