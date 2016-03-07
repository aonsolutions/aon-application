package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum Province implements Serializable {
	  DESCONOCIDO("Desconocido")
	, ARABA("Araba/\u00C1lava")
	, ALBACETE ("Albacete")
	, ALICANTE ("Alicante")
	, ALMERIA ("Almer\u00EDa")
	, AVILA ("\u00C1vila")
	, BADAJOZ ("Badajoz")
	, ILLES_BALEARS("Illes Balears")
	, BARCELONA("Barcelona")
	, BURGOS("Burgos")
	, CACERES("C\u00E1ceres")
	, CADIZ	("C\u00E1diz")
	, CASTELLON("Castell\u00F3n")
	, CIUDAD_REAL("Ciudad Real")
	, CORDOBA("C\u00F3rdoba")
	, A_CORUNA("A Coru\u00F1a")
	, CUENCA("Cuenca")
	, GIRONA("Girona")
	, GRANADA("Granada")
	, GUADALAJARA("Guadalajara")
	, GIPUZKOA("Gipuzkoa")
	, HUELVA("Huelva")
	, HUESCA("Huesca")
	, JAEN("Jaen")
	, LEON("Le\u00F3n")
	, LLEIDA("Lleida")
	, LA_RIOJA("La Rioja")
	, LUGO("Lugo")
	, MADRID("Madrid")
	, MALAGA("M\u00E1laga")
	, MURCIA("Murcia")
	, NAVARRA("Navarra")
	, OURENSE("Ourense")
	, ASTURIAS("Asturias")
	, PALENCIA("Palencia")
	, LAS_PALMAS("Las Palmas")
	, PONTEVEDRA("Pontevedra")
	, SALAMANCA("Salamanca")
	, TENERIFE("S.C. Tenerife")
	, CANTABRIA("Cantabria")
	, SEGOVIA("Segovia")
	, SEVILLA("Sevilla")
	, SORIA("Soria")
	, TARRAGONA("Tarragona")
	, TERUEL("Teruel")
	, TOLEDO("Toledo")
	, VALENCIA("Valencia")
	, VALLADOLID("Valladolid")
	, BIZKAIA("Bizkaia")
	, ZAMORA("Zamora")
	, ZARAGOZA("Zaragoza")
	, CEUTA("Ceuta")
	, MELILLA("Melilla")
	, NO_RESIDENTE("No residente")
	;


	private String name;

	private Province(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static Province safeValueOf( Integer i ) {
		if (i == null) {
			return null;
		}
		if (i < 0 || i >= Province.values().length) {
			return null;
		}
		return Province.values()[i];
		
	}
	
	public static Province safeValueOf( String code ) {
		if (AonStringUtils.isBlank(code)) {
			return null;
		}
		code = AonStringUtils.trim( code);
		if (!AonStringUtils.isNumeric(code)) {
			return null;
		}
		int i = Integer.parseInt(code);
		return safeValueOf(i);
	}
	
	public static Province getByName( String name ) {
		if (AonStringUtils.isBlank(name)) {
			return DESCONOCIDO;
		}
		for (Province p : Province.values() ) {
			if (AonStringUtils.equalsIgnoreCase(p.toString(), name)) {
				return p;
			}
			if (AonStringUtils.equalsIgnoreCase(p.getName(), name)) {
				return p;
			}
		}
		return DESCONOCIDO;
	}
	
}
