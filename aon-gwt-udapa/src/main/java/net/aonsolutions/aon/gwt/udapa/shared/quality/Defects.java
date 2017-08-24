package net.aonsolutions.aon.gwt.udapa.shared.quality;

import java.util.LinkedList;

public enum Defects {
	AGUSANADO("Agusanado"),
	CORTADAS("Cortadas"),
	GOLPES_EXTERNOS("Golpes Externos"),
	GOLPES_INTERNOS("Golpes Internos"),
	HUECAS("Huecas"),
	LIMACO("Limaco"),
	MANCHAS_HIERRO("Manchas Hierro"),
	RIZOCTONIA("Rizoctonia"),
	SARNA("Sarna"),
	TALLOS("Tallos"),
	VERDES("Verdes"),
	TIERRAS_PIEDRA("Tierras Piedra")
;
	
	String name;
	
	private Defects(String name) {
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
