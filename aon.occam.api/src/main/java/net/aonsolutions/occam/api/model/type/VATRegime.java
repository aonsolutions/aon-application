package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum VATRegime implements Serializable {

	 GENERAL	("R\u00E9gimen General"		, "GENE", "R\u00E9g. Genr.")
	,SIMPLIFIED ("R\u00E9gimen Simplificado", "SIMP", "R\u00E9g. Simpl.")
	,EXEMPT 	("Exento"					, "EXEN", "Exento")
	;
	
	private String name;
	private String abbr;
	private String description;
	
	private VATRegime(String name,String abbr, String description){
		this.name = name;
		this.abbr = abbr;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAbbr() {
		return abbr;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) ordinal();
	}	
	
	public static Optional<VATRegime> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<VATRegime> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= VATRegime.values().length) return Optional.empty();
		return Optional.of(VATRegime.values()[i]);
	}
	
	public static Optional<VATRegime> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public boolean isGeneral() {
		return this.equals(GENERAL);
	}
	public boolean isSimplified() {
		return this.equals(SIMPLIFIED);
	}
	public boolean isExempt() {
		return this.equals(EXEMPT);
	}


}