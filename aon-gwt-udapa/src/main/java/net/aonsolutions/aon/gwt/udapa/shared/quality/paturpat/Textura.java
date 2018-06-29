package net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat;

import java.util.LinkedList;

public enum Textura {
	EMPTY("-", "-"),
	C("Cremosa", "C"),
	H("Harinosa", "H"),
	F("Firme", "F"),
	D("Dura", "D");
	
	String name;
	String key;
	private Textura(String name, String key) {
		this.name = name;
		this.key = key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public static LinkedList<String> valueLinkedList(){
		LinkedList<String> list = new LinkedList<>();
		for(Integer i = 0; i < values().length; i++){
			list.add(values()[i].getName());
		}
		return list;
	}
}
