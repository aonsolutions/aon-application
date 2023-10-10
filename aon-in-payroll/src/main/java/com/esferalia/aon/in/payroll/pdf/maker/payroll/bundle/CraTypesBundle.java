package com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle;

import static com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate.INFO;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate.NOTE;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate.WARNING;

import java.util.ListResourceBundle;

import com.esferalia.aon.in.payroll.pdf.maker.payroll.IPayrollTemplate;

public class CraTypesBundle   extends ListResourceBundle{
	  private Object[][] contents = {
		{"0","Otras percepciones"},
	  	{"1","Percepciones salariales"},
		{"2","Horas extraordinarias"},
		{"3","Horas extraordinarias de fuerza mayor"},
		{"4","Prorrateo de las pagas extraordinarias"},
		{"5","Otras retrib. de vencimiento sup. al mensual distintas a  la paga extra"},
		{"6","Vacaciones retribuidas y no disfutadas,cotización tras fin de contrato"},
		{"7","Salarios de tramitación"},
		{"8","Retribución por atrasos"},
		{"9","Retribución por atrasos. Convenio colectivo"},
		{"10","Retribucion por atrasos. Sentencia judicial"},
		{"11","Retribución por atrasos. Normativa"},
		{"12","Retribución por atrasos. Acta conciliación"},
		{"13","Otras retribuciones en especie"},
		{"14","Retribución en especie vivienda con valor catastral"},
		{"15","Retribución en especie vivienda. Pagad.Pte.Valor catastral"},
		{"16","Retribución en especie vivienda. No propiedad pagador"},
		{"17","Retribución en especie vehiculo. Entrega al trabajador"},
		{"18","Retribución en especie vehiculo. Uso propiedad pagador"},
		{"19","Retribución en especie vehiculo. Uso.No propiedad pagador"},
		{"20","Retribución en especie vehiculo. Uso y posterior entrega"},
		{"21","Retribución en especie. Prestamo tipo interés legal"},
		{"22","Retribución en especie. Manutención y similares"},
		{"23","Retribución especie hospedaje y similares"},
		{"24","Retribución especie viajes y similares"},
		{"25","Retribución especie gastos de estudios y manutención"},
		{"26","Retribución especie derechos fundadores de sociedades"},
		{"27","Quebranto de moneda"},
		{"28","Desgaste útiles y herramientas"},
		{"29","Adquisición y mantenimiento ropa de trabajo"},
		{"30","Percepciones por matrimonio"},
		{"31","Donaciones promocionales"},
		{"32","Pluses de transporte y de mercancia"},
		{"33","Planes de pensiones y sistemas Alternativos"},
		{"34","Acciones o participaciones empresa"},
		{"35","Gastos estudio Act.Capacit. o reciclaje"},
		{"36","Productos.Prec.Reb.Cantin.Comed.Econom"},
		{"37","Bienes destinados a servicios sociales y cultura"},
		{"38","Primas seguro AT o Responsabilidad civil trabajador"},
		{"39","Primas seguro enfermedad común trabajador"},
		{"40","Primas seguro enfermedad común familiar"},
		{"41","Prestación educación por centro Aut. a hijos trabajador"},
		{"42","Gastos de estancia"},
		{"43","Gastos manutención pernocta Espa\u00f1a"},
		{"44","Gastos manutención pernocta extranjero"},
		{"45","Gastos de manutención sin pernocta Espa\u00f1a"},
		{"46","Gastos de manutención sin pernocta extranjero"},
		{"47","Gastos de manutención personal vuelo Espa\u00f1a"},
		{"48","Gastos de manutención personal vuelo extranjero"},
		{"49","Gastos locomoción transporte público"},
		{"50","Gastos de locomoción sin justificante de importe"},
		{"51","Indemnizaciones por fallecimiento"},
		{"52","Indemnizaciones por traslados"},
		{"53","Indemnizaciones por suspensiones"},
		{"54","Indemnizaciones por despido o cese"},
		{"55","Mejoras prestación S.S. distintas de incapacidad temporal"},
		{"56","Mejoras prestación S.S. distintas de incapacidad temporal"},
		{"57","Horas complementarias pactadas"},
		{"58","Horas complementarias voluntarias"},
		{"59","Vacaciones no disfrutadas retribuidas tras fallecimiento"},
		{"60","Vacaciones retribuidas no disfrutadas. Cotización durante contrato"},
		{"61","Pluses de transporte y de distancia"},
		{"100","Prestaciones"},
		
		
		{Integer.toString(INFO),""},
		{Integer.toString(NOTE),"Notas"},
		{Integer.toString(WARNING),"Avisos"}
	  };

	@Override
	protected Object[][] getContents() {
		return contents;
	}
}
