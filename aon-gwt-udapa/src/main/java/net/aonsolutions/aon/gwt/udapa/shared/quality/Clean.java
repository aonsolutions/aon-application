package net.aonsolutions.aon.gwt.udapa.shared.quality;

import java.util.LinkedList;

public enum Clean {
	LIBRE("Libre"),
	CONTAMINADA("Contaminada")
	;
	
	String name;
	
	private Clean(String name) {
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
