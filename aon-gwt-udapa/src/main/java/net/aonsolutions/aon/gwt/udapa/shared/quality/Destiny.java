package net.aonsolutions.aon.gwt.udapa.shared.quality;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum Destiny {
	BASERRI("Baserri", true),
	BUTI("Butis", true),
	CAJA_BLANCA("Caja Blanca", false),
	CALIDAD("Calidad", false),
	COMUN("Com\u00fan", true),
	CONSUMO_SIEMBRA("Consumo Siembra", false),
	ECI_COMUN("ECI Com\u00fan", false),
	ECI_FREIR("ECI Freir", false),
	ECI_HERVIR("ECI Hervir", false),
	EUROPOOL("Europool", false),
	EUSKOLABEL("Euskolabel", false),
	GUARNICION("Guarnici\u00f3n", true),
	INDUSTRIA("Industria", true),
	MERCADOS("Mercados", true),
	MICROONDAS("Microondas", true),
	MIRALOBUENO_3KG("Miralobueno 3 Kg.", false),
	MIRALOBUENO_5KG("Miralobueno 5 Kg.", false),
	MIRALOBUENO_GRANEL("Miralobueno Granel", false),
	NATUR_FREIR("Natur Freir", false),
	NATUR_GUISAR("Natur Guisar", false),
	PATURPAT("Paturpat", true),
	ROJA("Roja", false),
	SIEMBRA("Siembra", false),
	VERDIFRESH("Verdifresh", false),
	ESPECIAL_FREIR("Especial Freir", false),
	SACOS("Sacos", false),
	AGRIA_HOSTELERIA("Agria Hosteleria", true),
	AGRIA_TOP("Agria Top", true),
	AMANDINE("Amandine", true),
	BELTZA("Beltza", true),
	DESTRIO("Destrio", true),
	FREIR("Freir", true),
	GRANEL("Granel", true),
	GRANEL_TOP("Granel Top", true),
	GUISAR("Guisar", true),
	LABEL("Label", true),
	MERCADONA_3KG("Mercadona 3Kg", true),
	MERCADONA_5KG("Mercadona 5Kg", true),
	MERCADONA_BOLSA("Mercadona Bolsa", true),
	ROJA_TOP("Roja Top", true)	
	;
	
	String name;
	boolean active;
	
	private Destiny(String name, boolean active) {
		this.name = name;
		this.active = active;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
		
	public static Destiny safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static Destiny safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= Destiny.values().length) return null;
		return Destiny.values()[i];
	}
	
	public boolean isActive() {
		return active;
	}
	
	public String getName() {
		return name;
	}
	
	public static Destiny safeValueOf( String name ) {
		if(AonStringUtils.isBlank(name)) return null;
		for (Destiny rs : values()) {
			if(rs.getName().equalsIgnoreCase(name))
				return rs;
		}
		return null;
	}
	
	public static List<String> valueLinkedList(){
		LinkedList<String> list = new LinkedList<>();
		for(Integer i = 0; i < values().length; i++){
			list.add(values()[i].getName());
		}
		return list;
	}
	
	public static List<Destiny> getActiveList() {
		return Arrays.asList(values()).stream()
			.filter(Destiny::isActive)
			.sorted((s1, s2) -> s1.getName().compareTo(s2.getName()))
			.collect(Collectors.toCollection(LinkedList::new));
	}

}
