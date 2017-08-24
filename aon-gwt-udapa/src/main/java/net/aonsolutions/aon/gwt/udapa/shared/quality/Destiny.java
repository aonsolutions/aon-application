package net.aonsolutions.aon.gwt.udapa.shared.quality;

import java.util.LinkedList;

public enum Destiny {
	BASERRI("Baserri"),
	BUTI("Buti"),
	CAJA_BLANCA("Caja Blanca"),
	CALIDAD("Calidad"),
	COMUN("Com\u00fan"),
	ECI_COMUN("ECI Com\u00fan"),
	ECI_FREIR("ECI Freir"),
	ECI_GUISAR("ECI Guisar"),
	EUROPOOL("Europool"),
	EUSKOLABEL("Euskolabel"),
	GUARNICION("Guarnici\u00f3n"),
	MERCADOS("Mercados"),
	MICROONDAS("Microondas"),
	MIRALOBUENO_3KG("Miralobueno 3 Kg."),
	MIRALOBUENO_5KG("Miralobueno 5 Kg."),
	MIRALOBUENO_GRANEL("Miralobueno Granel"),
	NATUR_FREIR("Natur Freir"),
	NATUR_GUISAR("Natur Guisar"),
	ROJA("Roja"),
	SIEMBRA("Siembra"),
	VERDIFRESH("Verdifresh")
	;
	
	String name;
	
	private Destiny(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static LinkedList<String> valueLinkedList(){
		LinkedList<String> list = new LinkedList<>();
		for(Integer i = 0; i < values().length; i++){
			list.add(values()[i].getName());
		}
		return list;
	}
}
