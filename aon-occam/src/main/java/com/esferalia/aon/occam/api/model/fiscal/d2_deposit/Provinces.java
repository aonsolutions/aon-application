package com.esferalia.aon.occam.api.model.fiscal.d2_deposit;

import java.io.Serializable;

public enum Provinces implements Serializable {

	ACORUNA("A CORU\u00d1A", "15"),
	ARABA("ARABA/\u00c1LAVA","01"),
	ALBACETE("ALBACETE","02"),
	ALICANTE("ALICANTE","03"),
	ALMERIA("ALMERIA","04"),
	ASTURIAS("ASTURIAS","33"),
	AVILA("AVILA","05"),
	BADAJOZ("BADAJOZ","06"),
	BALEARES("BALEARES","07"),
	BARCELONA("BARCELONA","08"),
	BURGOS("BURGOS","09"),
	CACERES("C\u00c1CERES","10"),
	CADIZ("C\u00c1DIZ","11"),
	CANTABRIA("CANTABRIA","39"),
	CASTELLON("CASTELL\u00d3N","12"),
	CEUTA("CEUTA","51"),
	CIUDAD_REAL("CIUDAD REAL","13"),
	CORDOBA("C\u00d3RDOBA","14"),
	CUENCA("CUENCA","16"),
	GIRONA("GIRONA","17"),
	GRANADA("GRANADA","18"),
	GUADALAJARA("GUADALAJARA","19"),
	GIPUZKOA("GIPUZKOA","20"),
	HUELVA("HUELVA","21"),
	HUESCA("HUESCA","22"),
	JAEN("JA\u00c9N","23"),
	LARIOJA("LA RIOJA","26"),
	LEON("LE\u00d3N","24"),
	LLEIDA("LLEIDA","25"),
	LUGO("LUGO","27"),
	MADRID("MADRID","28"),
	MALAGA("M\u00c1LAGA","29"),
	MELILLA("MELILLA","52"),
	MURCIA("MURCIA","30"),
	NAVARRA("NAVARRA","31"),
	ORENSE("ORENSE","32"),
	PALENCIA("PALENCIA","34"),
	PALMAS("PALMAS","35"),
	PONTEVEDRA("PONTEVEDRA","36"),
	SALAMANCA("SALAMANCA","37"),
	SEGOVIA("SEGOVIA","40"),
	SEVILLA("SEVILLA","41"),
	SORIA("SORIA","42"),
	TARRAGONA("TARRAGONA","43"),
	TENERIFE("TENERIFE","38"),
	TERUEL("TERUEL","44"),
	TOLEDO("TOLEDO","45"),
	VALENCIA("VALENCIA","46"),
	VALLADOLID("VALLADOLID","47"),
	BIZKAIA("BIZKAIA","48"),
	ZAMORA("ZAMORA","49"),
	ZARAGOZA("ZARAGOZA","50")
	
	;

	private String name;
	private String id;

	
	
	private Provinces(String name, String id) {
		this.id = id;
		this.name = name;
	}
	
	public String getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public static Provinces getProvince(String value) {
		Provinces pr = ARABA;
		for(Integer i = 0; i < Provinces.values().length; i++) {
			if(Provinces.values()[i].getName().equalsIgnoreCase(value)
			 || Provinces.values()[i].toString().equalsIgnoreCase(value)) {
				pr = Provinces.values()[i];
			}
		}
		return pr;
	}
	
	public static void main(String[] args) {
		
	}
}
