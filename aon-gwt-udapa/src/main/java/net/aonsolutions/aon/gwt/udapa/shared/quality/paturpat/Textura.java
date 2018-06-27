package net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat;

import java.util.LinkedList;

public enum Textura {
	C("Cremosa"),
	H("Harinosa"),
	F("Firme"),
	D("Dura");
	
	String name;
	
	private Textura(String name) {
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
