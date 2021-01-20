package com.esferalia.aon.in.payroll.pdf.creators.payroll;

public class DeductionTypes {
	
	public static String getType(int i) {
		switch (i) {
		case 1: return "Aportaciones del trabajador a las cotizaciones de la Seguridad Social y conceptos de recaudación";
		case 2: return "Impuesto sobre la renta de las personas físicas";
		case 3: return "Anticipos";
		case 4: return "Valor de los productos recibidos en especie";
		default: return "Otras deducciones";
		}
	}
}
