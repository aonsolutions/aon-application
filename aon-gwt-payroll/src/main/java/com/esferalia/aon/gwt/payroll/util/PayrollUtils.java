package com.esferalia.aon.gwt.payroll.util;

public class PayrollUtils {

	public static int getDeductionPDFType(int type) {
		switch (type) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return 1;
		case 6:
			return 2;
		case 7:
			return 3;
		case 8:
			return 4;
		default:
			return 5;
		}
	}
	
	
	public static String getDeductionTypeDescription(int type) {

		switch (type) {
		case 0:
			return "Contingencias comunes";
		case 2:
			return "Desempleo";
		case 3:
			return "Formación profesional";
		case 4:
			return "Horas extraordinarias (Estruc.)";
		case 5:
			return "Horas extraordinarias (No Estruc.)";
		case 6:
			return "Retribuciones dinerarias";
		case 7:
			return "Anticipo";
		case 8:
			return "En especie";
		case 10:
			return "Embargo";
		default:
			return "Otras deducciones";
		}

	}
	
	
	

}
