package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;

public class Province {
	
	private static final Map<String, String> PROVINCES = new HashMap<String,String>(){
		{
			put("02","Albacete");
			put("03","Alicante/Alacant");
			put("04","Almería");
			put("01","Araba/\u00C1lava");
			put("33","Asturias");
			put("05","\u00C1vila");
			put("06","Badajoz");
			put("07","Balears, Illes");
			put("08","Barcelona");
			put("48","Bizkaia");
			put("09","Burgos");
			put("10","C\u00E1ceres");
			put("11","C\u00E1diz");
			put("39","Cantabria");
			put("12","Castell\u00F3n/Castell\u00F3");
			put("13","Ciudad Real");
			put("14","C\u00F3rdoba");
			put("15","Coru\u00F1a, A	");
			put("16","Cuenca");
			put("20","Gipuzkoa");
			put("17","Girona");
			put("18","Granada");
			put("19","Guadalajara");
			put("21","Huelva");
			put("22","Huesca");
			put("23","Ja\u00E9n");
			put("24","Le\u00F3n");
			put("25","Lleida");
			put("27","Lugo");
			put("28","Madrid");
			put("29","M\u00E1laga");
			put("30","Murcia");
			put("31","Navarra");
			put("32","Ourense");
			put("34","Palencia");
			put("35","Palmas, Las");
			put("36","Pontevedra");
			put("26","Rioja, La");
			put("37","Salamanca");
			put("38","Santa Cruz de Tenerife");
			put("40","Segovia");
			put("41","Sevilla");
			put("42","Soria");
			put("43","Tarragona");
			put("44","Teruel");
			put("45","Toledo");
			put("46","Valencia/Val\u00E8ncia");
			put("47","Valladolid");
			put("49","Zamora");
			put("50","Zaragoza");
			put("51","Ceuta");
			put("52","Melilla");
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
			Window.alert(provinceStr + " == " + province);
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
