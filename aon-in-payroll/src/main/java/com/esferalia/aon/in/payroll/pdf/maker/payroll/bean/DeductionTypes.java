package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

public class DeductionTypes {
	
	public static String getType(int i) {
		switch (i) {
		case 1: return "Aportaciones del trabajador a la S.S. y conceptos de recaudación";
		case 2: return "Impuesto sobre la renta de las personas físicas (IRPF)";
		case 3: return "Anticipos";
		case 4: return "Valor de los productos recibidos en especie";
		default: return "Otras deducciones";
		}
	}
}
