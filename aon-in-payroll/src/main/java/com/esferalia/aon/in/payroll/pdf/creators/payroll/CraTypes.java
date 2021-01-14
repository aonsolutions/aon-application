package com.esferalia.aon.in.payroll.pdf.creators.payroll;

import com.esferalia.aon.in.payroll.pdf.creators.payroll.complete.beans.UnknownCraException;

public class CraTypes {	
	
	public static String get_type(int type) throws UnknownCraException {
		
		switch (type) {
			case 1:  return "Percepciones salariales";
			case 2:  return "Horas extraordinarias";
			case 3:  return "Horas extraordinarias de fuerza mayor";
			case 4:  return "Prorrateo de las pagas extraordinarias";
			case 5:  return "Otras retrib. de vencimiento sup. al mensual distintas a  la paga extra";
			case 6:  return "Vacaciones retribuidas y no disfutadas: cotizaci\u00f3n tras fin de contrato";
			case 7:  return "Salarios de tramitaci\u00f3n";
			case 8:  return "Retr. por atrasos no incluida en otros apart.";
			case 9:  return "Retrib. por atrasos. Convenio colectivo";
			case 10: return "Retrib. por atrasos. Sentencia judicial";
			case 11: return "Retribuci\u00f3n por atrasos. Normativa";
			case 12: return "Retribuci\u00f3n por atrasos. Acta conciliaci\u00f3n";
			case 13: return "R.Especie no incluida en otros apartados";
			case 14: return "R.Esp.Vivienda.Prop.Pagad.c/Valor catastral";
			case 15: return "R.Esp.Vivienda.Prop.Pagad.Pte.Valor catastral";
			case 16: return "R.Esp.Vivienda.No propiedad pagador";
			case 17: return "R.Esp.Vehiculo.Entrega al trabajador";
			case 18: return "R.Esp.Vehiculo.Uso.Propiedad pagador.";
			case 19: return "R.Esp.Vehiculo.Uso.No propiedad pagador";
			case 20: return "R.Esp.Vehiculo.Uso y posterior entrega";
			case 21: return "R.Esp.Prestamo.Tipo.Inter\u00e9s < legal";
			case 22: return "R.Esp.Manutenci\u00f3n y similares";
			case 23: return "R.Esp.Hospedaje y similares";
			case 24: return "R.Esp.Viajes y similares";
			case 25: return "R.Esp.Gastos de estudios y manutenci\u00f3n";
			case 26: return "R.Esp.Derechos fundadores de sociedades";
			case 27: return "Quebranto de moneda";
			case 28: return "Desgaste \u00fatiles y herramientas";
			case 29: return "Adquisici\u00f3n y mantenimiento rop de trabajo";
			case 30: return "Percepciones por matrimonio";
			case 31: return "Donaciones promocionales";
			case 32: return "Pluses de transporte y de mercancia";
			case 33: return "Planes de pensiones y sistemas Alternativos";
			case 34: return "Acciones o participaciones empresa";
			case 35: return "Gastos estudio Act.Capacit. o reciclaje";
			case 36: return "Productos.Prec.Reb.-Cantin.Comed.Econom";
			case 37: return "Bienes destinados a servicos Sociales y cultura";
			case 38: return "Primas seguro AT o Responsabilidad civil trabajador";
			case 39: return "Primas seguro enfermedad com\u00fan trabajador";
			case 40: return "Primas seguro enfermedad com\u00fan familiar";
			case 41: return "Prestaci\u00f3n educaci\u00f3n por centro Aut. a hijos trabajador";
			case 42: return "Gastos de estancia";
			case 43: return "Gastos manutenci\u00f3n pernocta Espa\u00d1a";
			case 44: return "Gastos manutenci\u00f3n pernocta extranjero";
			case 45: return "Gastos de manutenci\u00f3n sin pernocta Espa\u00d1a";
			case 46: return "Gastos de manutenci\u00f3n sin pernocta extranjero";
			case 47: return "Gastos de manutenci\u00f3n personal vuelo Espa\u00d1a";
			case 48: return "Gastos de manutenci\u00f3n personal vuelo extranjero";
			case 49: return "Gastos locomoci\u00f3n transporte p\u00fablico";
			case 50: return "Gastos de locomoci\u00f3n sin justificante de importe";
			case 51: return "Indemnizaciones por fallecimiento";
			case 52: return "Indemnizaciones por traslados";
			case 53: return "Indemnizaciones por suspensiones";
			case 54: return "Indemnizaciones por despido o cese";
			case 55: return "Mejoras prestaci\u00f3n S.S Distintas incapacidad temporal";
			case 56: return "Mejoras prestaci\u00f3n S.S Distintas de incapacidad temporal";
			case 57: return "Horas complementarias pactadas";
			case 58: return "Horas complementarias voluntarias";
			case 59: return "Vacaciones no disfrutadas retribuidas tras fallecimiento";
			case 60: return "Vacaciones retribuidas no disfrutadas. Cotizaci\u00f3n durante contrato";
			case 61: return "Pluses de transporte y de distancia";
			default: throw new UnknownCraException(type + "is not a valid CRA type.");
		}
	}
}
