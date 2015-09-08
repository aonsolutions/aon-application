package com.esferalia.aon.gwt.office.client.values;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum LabelValue {
	ACADEMY("ACD", "Academy"),	
	BUG("BUG", "Bug"),
	COMERCIAL("COMER", "Comercial"),
	CONFIGURACION("CONF", "Configuracion"),
	CONTABILIDAD("CONT", "Contabilidad"),
	DOCUMENTAL("DOC", "Documental"),	
	DUPLICATE("DUP", "Duplicar"),
	ENHANCEMENT("ENH", "Mejorar"),
	EXPEDIENTES("EXP", "Expedientes"),
	FISCAL("FIS", "Fiscal"),
	GESTION("GEST", "Gestion"),	
	HELP_WANTED("HELP", "Ayuda"),
	HOT_FIX("HOTFIX", "HotFix"),
	HOTEL("HOT", "Hotel"),	
	INVALID("INV", "Invalid"),
	LABORAL("LAB", "Laboral"),
	MARKETING("MARK", "Marketing"),
	MARKET_PLACE("MP", "MarketPlace"),
	QA_SUCCESS("QAS", "qaSuccess"),
	QUESTION("QUEST", "Pregunta"),
	TESORERIA("TESO", "Tesoreria"),
	WONT_FIX("WNTFIX", "WontFix")
	;
	
	private String value;
	private String name;
	
	private LabelValue(String value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static LabelValue safeValueOf(String value) {
		if (value == null) return null;
		
		for (LabelValue t : LabelValue.values())
			if (AonStringUtils.equals(value, t.getValue()))
				return t;
		
		return null;
	}

}
