package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;

public class ProvinceContract {
	
	private static final Map<String, String> PROVINCES = new HashMap<String,String>(){
		{
			put("01","ARABA/ALAVA");
			put("02","ALBACETE");
			put("03","ALICANTE");
			put("04","ALMERIA");
			put("05","AVILA");
			put("06","BADAJOZ");
			put("07","ISLAS BALEARES");
			put("08","BARCELONA");
			put("09","BURGOS");
			put("10","CACERES");
			put("11","CADIZ");
			put("12","CASTELLON");
			put("13","CIUDAD REAL");
			put("14","CORDOBA");
			put("15","A CORU" + String.valueOf("\u00D1") + "A");
			put("16","CUENCA");
			put("17","GIRONA");
			put("18","GRANADA");
			put("19","GUADALAJARA");
			put("20","GIPUZKUA");
			put("21","HUELVA");
			put("22","HUESCA");
			put("23","JAEN");
			put("24","LEON");
			put("25","LLEIDA");
			put("26","LA RIOJA");
			put("27","LUGO");
			put("28","MADRID");
			put("29","MALAGA");
			put("30","MURCIA");
			put("31","NAVARRA");
			put("32","OURENSE");
			put("33","ASTURIAS");
			put("34","PALENCIA");
			put("35","LAS PALMAS");
			put("36","PONTEVEDRA");	
			put("37","SALAMANCA");
			put("38","SANTA CRUZ DE TENERIFE");
			put("39","CANTABRIA");
			put("40","SEGOVIA");
			put("41","SEVILLA");
			put("42","SORIA");
			put("43","TARRAGONA");
			put("44","TERUEL");
			put("45","TOLEDO");
			put("46","VALENCIA");
			put("47","VALLADOLID");
			put("48","BIZKAIA");
			put("49","ZAMORA");
			put("50","ZARAGOZA");
			put("51","CEUTA");
			put("52","MELILLA");	
		}
	};
	
	public static String getName(String code)  {
		return PROVINCES.get(code);
	}
	
	public static Integer getProvinceIndex(String province) {
		if(null == province || "-" == province || "" == province)
			return 0;
		
		Integer index = 1;
		for(String provinceStr : PROVINCES.values()) {
//			Window.alert(provinceStr + " == " + province);
			if(provinceStr.equalsIgnoreCase(province))
				return index;
			index++;
		}
			return 0;
	}
	
	public static Map<String, String> getProvinces() {
		return PROVINCES;
	} 

}
