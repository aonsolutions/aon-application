package net.aonsolutions.aon.gwt.udapa.shared.quality;

import java.util.LinkedList;

public enum Destiny {
	BASERRI("Baserri"),
	CALIDAD("Calidad"),
	COMUN("Com\u00fan"),
	ECI_FREIR("ECI Freir"),
	ECI_GUISAR("ECI Guisar"),
	EUSKOLABEL("Euskolabel"),	
	GRANEL_EROSKI("Granel Eroski"),
	GRANEL_NATUR("Granel Natur"),
	MERCADOS("Mercados"),
	MIRALOBUENO("Miralobueno")
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
