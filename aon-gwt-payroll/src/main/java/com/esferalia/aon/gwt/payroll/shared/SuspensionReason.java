package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SuspensionReason {
	
	private static final Map<String, String> SUSPENSIONREASONS = new HashMap<String,String>(){
		{
//			01 UNFAIR("Despido Improcedente"),
//			02 OBJECTIVE("Despido por Causas Objetivas"),
//			11 WORK_END("Fin Contrato Fijo de Obra"),
//			11 TEMP_END("Fin Contrato Temporal"),
//			 DEFINITE_END("Fin Contrato Duraci\u00F3n Determinada"),
//			21 CONDITIONS_CHANGE("Baja Voluntaria Modificaci\u00F3n Condiciones"),
			put("01","Despido del trabajador");
			put("02","Despido por causas objetivas");
			put("03","Muerte del empresario");
			put("04","Jubilaci" + String.valueOf("\u00F3") + "n del empresario");
			put("05","Incapacidad del empresario");
			put("06","Cese por declaraci" + String.valueOf("\u00F3") + "n de invalidez permanente total del trabajador");
			put("07","Cese en periodo de prueba a instancia del empresario");
			put("08","Cese en periodo de prueba por acuerdo del consejo rector en el supuesto de socios de cooperativas");
			put("09","Cese en periodo de prueba a instancia del trabajador");
			put("10","Cese por voluntad del empresario en la relaci" + String.valueOf("\u00F3") + "n laboral de alta direcci" + String.valueOf("\u00F3") + "n");
			put("11","Fin contrato temporal");
			put("12","Fin contrato temporal a instancia del trabajador (rechazo pr" + String.valueOf("\u00F3") + "rroga)");
			put("13","Fin de la relaci" + String.valueOf("\u00F3") + "n administrativa temporal de funcionarios de empleo y contratados administrativos");
			put("14","Resoluci" + String.valueOf("\u00F3") + "n del trabajador por traslado o midificaci" + String.valueOf("\u00F3") + "n sustancial de la condiciones de trabajo");
			put("15","Fin o interrupci" + String.valueOf("\u00F3") + "n de la actividad de los trabajadores fijo-discontinuos");
			put("16","Extinci" + String.valueOf("\u00F3") + "n del contrato autorizada en ERE o por auto judicial o constatada por la autoridad laboral en cooperativas");
			put("17","Suspensi" + String.valueOf("\u00F3") + "n del contrato autorizada en ERE o por auto judicial o constatada por la autoridad laboral en cooperativas");
			put("18","Reducci" + String.valueOf("\u00F3") + "n temporal de jornada autorizada en ERE o por auto judicial o constatada por la autoridad laboral en cooperativas");
			put("19","Suspensi" + String.valueOf("\u00F3") + "n o extinci" + String.valueOf("\u00F3") + "n voluntaria del contrato en caso de v" + String.valueOf("\u00ED") + "ctimas de violencia de g" + String.valueOf("\u00E9") + "nero");
			put("20","Expulsi" + String.valueOf("\u00F3") + "n del socio de la cooperativa, por acuerdo del consejo rector");
			put("21","Baja voluntaria del trabajador");
			put("22","Finalizaci" + String.valueOf("\u00F3") + "n o resoluci" + String.valueOf("\u00F3") + "n involuntaria del compromiso con las fuerzas armadas (indicar con o sin derecho a pensi" + String.valueOf("\u00F3") + "n de retiro)");
			put("23","Fin de la actuaci" + String.valueOf("\u00F3") + "n sin finalizaci" + String.valueOf("\u00F3") + "n de contrato, en el caso de artista");
			put("24","Fin de la actividad fija discontinua por la realizaci" + String.valueOf("\u00F3") + "n de trabajos fijo y peri" + String.valueOf("\u00F3") + "dicos que se repiten en fechas ciertas");
			put("25","Finalizaci" + String.valueOf("\u00ED") + "n del v" + String.valueOf("\u00F3") + "nculo societario de duraci" + String.valueOf("\u00F3") + "n determinada fijado en el acuerdo de admisi" + String.valueOf("\u00F3") + "n y en los estatutos de cooperativa");
			put("26","Excedencia");
			put("27","Cese involuntario y con caracter definitivo en cargo p" + String.valueOf("\u00FA") + "blico o sindical");
			put("28","P" + String.valueOf("\u00E9") + "rdida con caracter involuntario y definitivo de la dedicaci" + String.valueOf("\u00F3") + "n exclusiva o parcial por parte de un cargo p" + String.valueOf("\u00FA") + "blico o sindical");
			put("29","Conclusi" + String.valueOf("\u00F3") + "n del servicio o del tiempo m" + String.valueOf("\u00E1") + "ximo como reservista voluntario activado en las fuerza armadas");
		}
	};
	
	public static String getDescription(String code)  {
		return SUSPENSIONREASONS.get(code);
	}
	
	public static Map<String, String> getProvinces() {
		return SUSPENSIONREASONS;
	} 
	
	public static String getSuspensionCode( String description ) {
		for(Entry<String, String> e : SUSPENSIONREASONS.entrySet()) {
			if(e.getValue().equals(description))
				return e.getKey();
		}
		return null;
	}

}
