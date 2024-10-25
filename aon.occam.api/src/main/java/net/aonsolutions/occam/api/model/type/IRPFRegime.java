package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum IRPFRegime implements Serializable {

	 NORMAL 	("Estimaci\u00F3n directa normal"		,"Est. Normal")
	,SIMPLIFIED ("Estimaci\u00F3n directa simplificada"	,"Est. Simpl.")
	,OBJECTIVE 	("Estimaci\u00F3n objetiva"				,"Est. Objet.")	
	,EXEMPT 	("Exento"								,"Exento")
	;
	
	private String name;
	private String description;
	
	private IRPFRegime(String name,String description){
		this.name = name;
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public byte value() {
		return (byte) ordinal();
	}	
	public static Optional<IRPFRegime> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<IRPFRegime> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= IRPFRegime.values().length) return Optional.empty();
		return Optional.of(IRPFRegime.values()[i]);
	}

	public static Optional<IRPFRegime> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
}