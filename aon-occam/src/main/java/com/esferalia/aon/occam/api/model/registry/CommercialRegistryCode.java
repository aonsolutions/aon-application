package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public enum CommercialRegistryCode implements Serializable {

	ARABA_ALAVA					("01005", "Araba-Alava"),
	ALBACETE					("02010", "Albacete"),
	ALACANT_ALICANTE			("03026", "Alacant-Alicante"),
	ALMERIA						("04015", "Almer\u00EDa"),
	AVILA						("05003", "\u00C1vila"),
	BADAJOZ						("06017", "Badajoz"),
	EIVISSA						("07009", "Eivissa"),
	MAO							("07013", "Mao"),
	PALMA_DE_MALLORCA			("07017", "Palma De Mallorca"),
	BARCELONA					("08005", "Barcelona"),
	BURGOS						("09014", "Burgos"),
	CACERES						("10015", "C\u00E1ceres"),
	CADIZ						("11016", "C\u00E1diz"),
	CASTELLO_CASTELLON			("12011", "Castello-Castell\u00F3n"),
	CIUDAD_REAL					("13013", "Ciudad Real"),
	CORDOBA						("14022", "C\u00F3rdoba"),
	A_CORUNA					("15021", "A Coru\u00F1a"),
	SANTIAGO_DE_COMPOSTELA		("15028", "Santiago De Compostela"),
	CUENCA						("16003", "Cuenca"),
	GIRONA						("17010", "Girona"),
	GRANADA						("18020", "Granada"),
	GUADALAJARA					("19010", "Guadalajara"),
	GIPUZKOA					("20014", "Gipuzkoa"),
	HUELVA						("21007", "Huelva"),
	HUESCA						("22010", "Huesca"),
	JAEN						("23015", "Ja\u00E9n"),
	LEON						("24014", "Le\u00F3n"),
	LLEIDA						("25011", "Lleida"),
	LOGRONO						("26010", "Logro\u00F1o"),
	LUGO						("27013", "Lugo"),
	MADRID						("28065", "Madrid"),
	MALAGA						("29023", "M\u00E1laga"),
	MURCIA						("30011", "Murcia"),
	PAMPLONA					("31015", "Pamplona"),
	OURENSE						("32013", "Ourense"),
	ASTURIAS					("33029", "Asturias"),
	PALENCIA					("34009", "Palencia"),
	LAS_PALMAS_DE_GRAN_CANARIA	("35009", "Las Palmas de Gran Canaria"),
	ARRECIFE					("35016", "Arrecife"),
	PUERTO_DEL_ROSARIO			("35018", "Puerto Del Rosario"),
	PONTEVEDRA					("36015", "Pontevedra"),
	SALAMANCA					("37010", "Salamanca"),
	SANTA_CRUZ_DE_LA_PALMA		("38004", "Santa Cruz De La Palma"),
	SANTA_CRUZ_DE_TENERIFE		("38013", "Santa Cruz De Tenerife"),
	SAN_SEBASTIAN_DE_LA_GOMERA	("38018", "San Sebasti\u00E1n De La Gomera"),
	VALVERDE					("38019", "Valverde"),
	SANTANDER					("39014", "Santander"),
	SEGOVIA						("40008", "Segovia"),
	SEVILLA						("41021", "Sevilla"),
	SORIA						("42007", "Soria"),
	TARRAGONA					("43017", "Tarragona"),
	TERUEL						("44009", "Teruel"),
	TOLEDO						("45019", "Toledo"),
	VALENCIA					("46030", "Valencia"),
	VALLADOLID					("47015", "Valladolid"),
	BIZKAIA						("48001", "Bizkaia"),
	ZAMORA						("49010", "Zamora"),
	ZARAGOZA					("50020", "Zaragoza"),
	CEUTA						("51001", "Ceuta"),
	MELILLA						("52001", "Melilla"),
	;

	private final String code;
	private final String description;

	private CommercialRegistryCode(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public byte value() {
		return (byte) ordinal();
	}

	public static CommercialRegistryCode safeValueOf(Byte i) {
		if (i == null) return null;
		return safeValueOf(i.intValue());
	}

	public static CommercialRegistryCode safeValueOf(Integer i) {
		if (i == null) return null;
		if (i < 0 || i >= CommercialRegistryCode.values().length) return null;
		return CommercialRegistryCode.values()[i];
	}

	public static CommercialRegistryCode safeValueOfCode(String code) {
		if (code == null) return null;
		for (CommercialRegistryCode r : values()) {
			if (r.code.equals(code)) return r;
		}
		return null;
	}

}
