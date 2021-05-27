package net.aonsolutions.aon.gwt.udapa.shared.quality;

import java.util.LinkedList;

public enum CleanAptitude {
	FIVE("5"),
	SIX("6"),
	SERVEN("7"),
	EIGHT("8")
	;
	
	String name;
	
	private CleanAptitude(String name) {
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
