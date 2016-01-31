package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod131Key implements IFiscalModelKey {
	
	 AC11("131-AC11",null,"Actividad (epigrafe IAE)")
	,AC12("131-AC12",null,"Rendimiento neto de la actividad a efectos del pago fraccionado")
	,AC13("131-AC13",null,"Porcentaje aplicable")
	,AC14("131-AC14",null,"Resultado de aplicar el porcentaje correspondiente a cada actividad")
	,AC21("131-AC21",null,"Actividad (epigrafe IAE)")
	,AC22("131-AC22",null,"Rendimiento neto de la actividad a efectos del pago fraccionado")
	,AC23("131-AC23",null,"Porcentaje aplicable")
	,AC24("131-AC24",null,"Resultado de aplicar el porcentaje correspondiente a cada actividad")
	,AC31("131-AC31",null,"Actividad (epigrafe IAE)")
	,AC32("131-AC32",null,"Rendimiento neto de la actividad a efectos del pago fraccionado")
	,AC33("131-AC33",null,"Porcentaje aplicable")
	,AC34("131-AC34",null,"Resultado de aplicar el porcentaje correspondiente a cada actividad")
	,AC41("131-AC41",null,"Actividad (epigrafe IAE)")
	,AC42("131-AC42",null,"Rendimiento neto de la actividad a efectos del pago fraccionado")
	,AC43("131-AC43",null,"Porcentaje aplicable")
	,AC44("131-AC44",null,"Resultado de aplicar el porcentaje correspondiente a cada actividad")
	,AC51("131-AC51",null,"Actividad (epigrafe IAE)")
	,AC52("131-AC52",null,"Rendimiento neto de la actividad a efectos del pago fraccionado")
	,AC53("131-AC53",null,"Porcentaje aplicable")
	,AC54("131-AC54",null,"Resultado de aplicar el porcentaje correspondiente a cada actividad")
	,AC01("131-AC01","01","01 - Suma de rendimientos netos") 
	,AC02("131-AC02","02","02 - Pago fraccionado previo del trimestre. Suma de resultados.") 
	,C03 ("131-03"  ,"03","03 - Volumen de ventas o  ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)")
	,C04 ("131-04"  ,"04","04 - Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 03.")
	,C05 ("131-05"  ,"05","05 - Volumen de ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)")
	,C06 ("131-06"  ,"06","06 - Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 05.")
	,C07 ("131-07"  ,"07","07 - Suma de los pagos fraccionados previos de trimestre (02 + 04 + 06) ")
	,C08 ("131-08"  ,"08","08 - A deducir. Retenciones e ingresos a cuenta soportados correspondientes al trimestre. ")
	,C09 ("131-09"  ,"09","09 - A deducir. Minoraci\u00F3n  por aplicaci\u00F3n de la deducci\u00F3n a que se refiere el art\u00EDculo 80 bis de la ley de Impuesto.")
	,C091("131-091" ,"09","09 - A deducir. Minoraci\u00F3n por aplicaci\u00F3n de la deducci\u00F3n a que se refiere el art\u00EDculo 110.3 C) del reglamento del impuesto.")
	,C10 ("131-10"  ,"10","10 - Diferencia.")
	,C11 ("131-11"  ,"11","11 - Resultados negativos de trimestres anteriores.  ")
	,C12 ("131-12"  ,"12","12 - Por destinar cantidades al pago de pr\u00E9stamos por adquisici\u00F3n o rehabilitaci\u00F3n de vivienda habitual.")
	,C13 ("131-13"  ,"13","13 - Total.")
	,C14 ("131-14"  ,"14","14 - A deducir (exclusivamentes en caso de decl. complm.): Resultados a ingresar de anteriores declaraciones por el mismo concepto, ejercicio y periodo.")
	,C15 ("131-15"  ,"15","15 - Resultado de la declaraci\u00F3n.")
	;
	
	public static Mod131Key[][] ACTIVITIES = {
		  new Mod131Key[]{AC11,AC12,AC13,AC14}
		 ,new Mod131Key[]{AC21,AC22,AC23,AC24}
		 ,new Mod131Key[]{AC31,AC32,AC33,AC34}
		 ,new Mod131Key[]{AC41,AC42,AC43,AC44}
		 ,new Mod131Key[]{AC51,AC52,AC53,AC54}
	};
	
	private String value;
	private String box;
	private String description;
	
	private Mod131Key(String value, String box, String description) {
    	this.value = value;
    	this.box = box;
    	this.description = description;
    }
	
	@Override
	public String getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getBox() {
		return box;
	}

	public static Mod131Key getKey(String value) {
		for (Mod131Key key : Mod131Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
	
}