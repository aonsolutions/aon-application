package net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat;

import java.util.LinkedList;

public enum Color {
	A("Amarillo"),
	BA("Blanco Amarillento"),
	B("Blanco")
	;
	
	String name;
	
	private Color(String name) {
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
