package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.IReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.esferalia.aon.file.payroll.fan.data.DAT;
import com.esferalia.aon.file.payroll.fan.data.EDL;
import com.esferalia.aon.file.payroll.fan.data.EDT;
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.LiquidationType;


public class FANReportWriter {
	
	private FANWriter writer;
	private ETI eti;
	///////////
	// BASES
	///////////
	private final ReportColumnMetadata EDLBA01 = new ReportColumnMetadata("EDLBA01",Types.DOUBLE,"Contingencias comunes",10);
	private final ReportColumnMetadata EDLBA02 = new ReportColumnMetadata("EDLBA02",Types.DOUBLE,"AT y EP",10);
	private final ReportColumnMetadata EDLBA05 = new ReportColumnMetadata("EDLBA05",Types.DOUBLE,"Exceso del tope (Minería del Carbón)",10);
	private final ReportColumnMetadata EDLBA06 = new ReportColumnMetadata("EDLBA06",Types.DOUBLE,"Importe percepciones Integras (Artistas.)",10);
	// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911). Baja a partir de 2002
	private final ReportColumnMetadata EDLBA07 = new ReportColumnMetadata("EDLBA07",Types.DOUBLE,"AT y EP sin horas extraordinarias",10);
	// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
	private final ReportColumnMetadata EDLBA08 = new ReportColumnMetadata("EDLBA08",Types.DOUBLE,"Diferencia Bases (contingencias comunes y salario normalizado)",10);
	private final ReportColumnMetadata EDLBA09 = new ReportColumnMetadata("EDLBA09",Types.DOUBLE,"Horas complementarias No será de utilización para Régimen General de Artistas (0112)",10);
	// No será de utilización para Régimen General de Artistas (0112), Régimen Especial de Minería del Carbón (0911).
	private final ReportColumnMetadata EDLBA10 = new ReportColumnMetadata("EDLBA010",Types.DOUBLE,"Horas extras estructurales / Causa de fuerza mayor desde 1/1/98",10);
	private final ReportColumnMetadata EDLBA11 = new ReportColumnMetadata("EDLBA011",Types.DOUBLE,"Horas extras no estructurales / Otras horas extras desde 1/1/98",10);
	private final ReportColumnMetadata EDLBA20 = new ReportColumnMetadata("EDLBA020",Types.DOUBLE,"Base de cotización empresarial C.Comunes = AT y EP",10);
	// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	private final ReportColumnMetadata EDLBA21 = new ReportColumnMetadata("EDLBA021",Types.DOUBLE,"Base de cotización empresarial por contingencias comunes Base de cotización empresarial por desempleo y FOGASA",10); 
	private final ReportColumnMetadata EDLBA22 = new ReportColumnMetadata("EDLBA022",Types.DOUBLE,"Base de cotización empresarial por AT y EP y Otras Cotizaciones",10);
	private final ReportColumnMetadata EDLBA23 = new ReportColumnMetadata("EDLBA023",Types.DOUBLE,"Base de cotización tipo total desempleo y FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
	private final ReportColumnMetadata EDLBA28 = new ReportColumnMetadata("EDLBA028",Types.DOUBLE,"Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial",10); 
	private final ReportColumnMetadata EDLBA30 = new ReportColumnMetadata("EDLBA030",Types.DOUBLE,"Cotización por Jornadas Reales. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata EDLBA31 = new ReportColumnMetadata("EDLBA031",Types.DOUBLE,"Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)",10); 
	// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	private final ReportColumnMetadata EDLBA32 = new ReportColumnMetadata("EDLBA032",Types.DOUBLE,"Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA.",10); 
	// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	private final ReportColumnMetadata EDLBA33 = new ReportColumnMetadata("EDLBA033",Types.DOUBLE,"Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo.",10); 
	private final ReportColumnMetadata EDLBA34 = new ReportColumnMetadata("EDLBA034",Types.DOUBLE,"Base de AT en vacaciones. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata EDLBA35 = new ReportColumnMetadata("EDLBA035",Types.DOUBLE,"Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDLBA36 = new ReportColumnMetadata("EDLBA036",Types.DOUBLE,"Base exclusiva Desempleo/FOGASA tipo total. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDLBA37 = new ReportColumnMetadata("EDLBA037",Types.DOUBLE,"Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012). (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDLBA38 = new ReportColumnMetadata("EDLBA038",Types.DOUBLE,"Cotización exclusivamente por FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDLBA41 = new ReportColumnMetadata("EDLBA041",Types.DOUBLE,"Contingencias Comunes y FOGASA, (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata EDLBA42 = new ReportColumnMetadata("EDLBA042",Types.DOUBLE,"Base exclusiva de AT y EP sin cotización de Otras Cotizaciones",10);
	/////////////////////////////////
	// COMPENSACIONES - DEDUCCIONES
	/////////////////////////////////
	private final ReportColumnMetadata EDLCD01 = new ReportColumnMetadata("EDLCD01",Types.DOUBLE,"IT enfermedad común y accidente no laboral",10); 
	private final ReportColumnMetadata EDLCD03 = new ReportColumnMetadata("EDLCD03",Types.DOUBLE,"IT por AT y EP",10);
	private final ReportColumnMetadata EDLCD06 = new ReportColumnMetadata("EDLCD06",Types.DOUBLE,"Reducciones Contratos con derecho a reducción (casilla 209 de TC1)",10);
	private final ReportColumnMetadata EDLCD07 = new ReportColumnMetadata("EDLCD07",Types.DOUBLE,"Bonificaciones Contratos con derecho a bonificación/reducción (casilla 601 de TC1)",10);
	private final ReportColumnMetadata EDLCD10 = new ReportColumnMetadata("EDLCD10",Types.DOUBLE,"Bonificación por formación teórica presencial",10);
	private final ReportColumnMetadata EDLCD11 = new ReportColumnMetadata("EDLCD11",Types.DOUBLE,"Bonificación por formación teórica a distancia",10);
	private final ReportColumnMetadata EDLCD12 = new ReportColumnMetadata("EDLCD12",Types.DOUBLE,"Bonificación por Ley 19/94 (Registro Canario) Régimen del Mar",10);
	private final ReportColumnMetadata EDLCD13 = new ReportColumnMetadata("EDLCD13",Types.DOUBLE,"Bonificación minusvalidos en Centros Especiales de Empleo",10);
	private final ReportColumnMetadata EDLCD17 = new ReportColumnMetadata("EDLCD17",Types.DOUBLE,"Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)",10);
	private final ReportColumnMetadata EDLCD20 = new ReportColumnMetadata("EDLCD20",Types.DOUBLE,"Bonificación Ceuta y Melilla (O. TAS/471/2004)",10);
	private final ReportColumnMetadata EDLCD22 = new ReportColumnMetadata("EDLCD22",Types.DOUBLE,"Bonificación Fom. Empleo Cuantía fija. ",10);
	private final ReportColumnMetadata EDLCD23 = new ReportColumnMetadata("EDLCD23",Types.DOUBLE,"Bonificación Sector Industrial Incentivado",10);
	private final ReportColumnMetadata EDLCD25 = new ReportColumnMetadata("EDLCD25",Types.DOUBLE,"Exención de desempleo hijos<30años Autonomos",10);
	private final ReportColumnMetadata EDLCD28 = new ReportColumnMetadata("EDLCD28",Types.DOUBLE,"Bonificación por ERE",10);
	private final ReportColumnMetadata EDLCD29 = new ReportColumnMetadata("EDLCD29",Types.DOUBLE,"Reducciones SEA. Contingencias comunes",10); 
	private final ReportColumnMetadata EDLCD30 = new ReportColumnMetadata("EDLCD30",Types.DOUBLE,"Reducciones SEA. Desempleo",10);
	
	

	//////////////////////////
	//BASES TOTALES
	//////////////////////////
	private final ReportColumnMetadata EDTBA01  = new ReportColumnMetadata("EDTBA01",Types.DOUBLE,"Contingencias comunes",10);
	private final ReportColumnMetadata EDTBA02  = new ReportColumnMetadata("EDTBA02",Types.DOUBLE,"AT y EP",10);
	private final ReportColumnMetadata EDTBA05  = new ReportColumnMetadata("EDTBA05",Types.DOUBLE,"Exceso del tope (Minería del Carbón)",10);
	private final ReportColumnMetadata EDTBA06  = new ReportColumnMetadata("EDTBA06",Types.DOUBLE,"Importe percepciones Integras (Artistas)",10);
	//No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911). Baja a partir de 2002
	private final ReportColumnMetadata EDTBA07  = new ReportColumnMetadata("EDTBA07",Types.DOUBLE,"AT y EP sin horas extraordinarias",10);
	//Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
	private final ReportColumnMetadata EDTBA08  = new ReportColumnMetadata("EDTBA08",Types.DOUBLE,"Diferencia Bases (Contingencias comunes y salario normalizado)",10);
	private final ReportColumnMetadata EDTBA09  = new ReportColumnMetadata("EDTBA09",Types.DOUBLE,"Horas complementarias No se utilizará para Régimen General de Artistas (0112)",10);
	//No se podrá utilizar para el Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911).
	private final ReportColumnMetadata EDTBA10  = new ReportColumnMetadata("EDTBA10",Types.DOUBLE,"Horas extras estructurales / Causa de fuerza mayor desde 1/1/98",10);
	//No se podrá utilizar para e Régimen General de Artistas (0112) , ni Régimen Especial de Minería del Carbón (0911)
	private final ReportColumnMetadata EDTBA11  = new ReportColumnMetadata("EDTBA11",Types.DOUBLE,"Horas extras no estructurales / Otras horas extras desde 1/1/98",10);
	//Base de cotización empresarial desempleo y FOGASA (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	private final ReportColumnMetadata EDTBA21  = new ReportColumnMetadata("EDTBA21",Types.DOUBLE,"Base de cotización empresarial por contingencias comunes",10);
	private final ReportColumnMetadata EDTBA22  = new ReportColumnMetadata("EDTBA22",Types.DOUBLE,"Base de cotización empresarial por AT y EP y Otras Cotizaciones",10);
	private final ReportColumnMetadata EDTBA23  = new ReportColumnMetadata("EDTBA23",Types.DOUBLE,"Base de cotización tipo total desempleo y FOGASA(Baja a partir del 1 de enero de 2012) . (Régimen Especial Agrario)",10);
	//Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
	private final ReportColumnMetadata EDTBA28  = new ReportColumnMetadata("EDTBA28",Types.DOUBLE,"Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial",10);
	private final ReportColumnMetadata EDTBA30  = new ReportColumnMetadata("EDTBA30",Types.DOUBLE,"Cotización por Jornadas Reales (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata EDTBA31  = new ReportColumnMetadata("EDTBA31",Types.DOUBLE,"Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)",10);
	//(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	private final ReportColumnMetadata EDTBA32  = new ReportColumnMetadata("EDTBA32",Types.DOUBLE,"Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA.",10); 
	//Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	private final ReportColumnMetadata EDTBA33  = new ReportColumnMetadata("EDTBA33",Types.DOUBLE,"Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo.",10); 
	private final ReportColumnMetadata EDTBA34  = new ReportColumnMetadata("EDTBA34",Types.DOUBLE,"Base de AT en vacaciones (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata EDTBA35  = new ReportColumnMetadata("EDTBA35",Types.DOUBLE,"Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDTBA36  = new ReportColumnMetadata("EDTBA36",Types.DOUBLE,"Base exclusiva Desempleo/FOGASA tipo total (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDTBA37  = new ReportColumnMetadata("EDTBA37",Types.DOUBLE,"Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDTBA38  = new ReportColumnMetadata("EDTBA38",Types.DOUBLE,"Cotización exclusivamente por FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata EDTBA41  = new ReportColumnMetadata("EDTBA41",Types.DOUBLE,"Contingencias Comunes y FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata EDTBA42  = new ReportColumnMetadata("EDTBA42",Types.DOUBLE,"Base exclusiva de AT y EP sin cotización de Otras Cotizaciones",10);
	
	
	//////////////////////////////////////
	//COMPENSACION - DEDUCCION TOTALES
	//////////////////////////////////////
	
	//No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	private final ReportColumnMetadata EDTCD01  = new ReportColumnMetadata("EDTCD01",Types.DOUBLE,"IT enfermedad común y accidente no laboral",10);
	//No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	private final ReportColumnMetadata EDTCD03  = new ReportColumnMetadata("EDTCD03",Types.DOUBLE,"IT por AT y EP",10);
	private final ReportColumnMetadata EDTCD05  = new ReportColumnMetadata("EDTCD05",Types.DOUBLE,"IT O.M. 3/4/73. Minería del Carbón",10);
	private final ReportColumnMetadata EDTCD06  = new ReportColumnMetadata("EDTCD06",Types.DOUBLE,"Reducciones",10);
	private final ReportColumnMetadata EDTCD07  = new ReportColumnMetadata("EDTCD07",Types.DOUBLE,"Bonificaciones",10);
	private final ReportColumnMetadata EDTCD10  = new ReportColumnMetadata("EDTCD10",Types.DOUBLE,"Bonificación por formación teórica presencial",10);
	private final ReportColumnMetadata EDTCD11  = new ReportColumnMetadata("EDTCD11",Types.DOUBLE,"Bonificación por formación teórica a distancia",10);
	private final ReportColumnMetadata EDTCD12  = new ReportColumnMetadata("EDTCD12",Types.DOUBLE,"Bonificación por Ley 19/94 (Registro Canario) Régimen Especial del Mar",10);
	private final ReportColumnMetadata EDTCD13  = new ReportColumnMetadata("EDTCD13",Types.DOUBLE,"Bonificación minusvalidos en Centros Especiales de Empleo",10);
	private final ReportColumnMetadata EDTCD16  = new ReportColumnMetadata("EDTCD16",Types.DOUBLE,"Bonificación por trabajadores con 60 o más años",10);
	//No es de aplicación en el Régimen Especial Agrario (0613)
	private final ReportColumnMetadata EDTCD17  = new ReportColumnMetadata("EDTCD17",Types.DOUBLE,"Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)",10);
	private final ReportColumnMetadata EDTCD18  = new ReportColumnMetadata("EDTCD18",Types.DOUBLE,"Reducción por Exencón de desempleo (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata EDTCD20  = new ReportColumnMetadata("EDTCD20",Types.DOUBLE,"Bonificación Ceuta y Melilla (O. TAS/471/2004)",10);
	private final ReportColumnMetadata EDTCD21  = new ReportColumnMetadata("EDTCD21",Types.DOUBLE,"Bonificación Copa del America (R.D.L. 2146/2004)",10);
	private final ReportColumnMetadata EDTCD22  = new ReportColumnMetadata("EDTCD22",Types.DOUBLE,"Bonificación Form. Empleo Cuantía fija. Excepto Rég. Gral. Artistas (0112)",10);
	private final ReportColumnMetadata EDTCD23  = new ReportColumnMetadata("EDTCD23",Types.DOUBLE,"Bonificación Sector industrial incentivado",10);
	private final ReportColumnMetadata EDTCD24  = new ReportColumnMetadata("EDTCD24",Types.DOUBLE,"Bonificación I+D+I Régimen General (0111)",10);
	private final ReportColumnMetadata EDTCD25  = new ReportColumnMetadata("EDTCD25",Types.DOUBLE,"Exención de desempleo hijos<30años Autonomos",10);
	private final ReportColumnMetadata EDTCD26  = new ReportColumnMetadata("EDTCD26",Types.DOUBLE,"Reducciones REA Cuantía mensual (modalidad G y J). (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario",10); 
	private final ReportColumnMetadata EDTCD27  = new ReportColumnMetadata("EDTCD27",Types.DOUBLE,"Reducciones REA 'Jornadas reales'. (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario",10); 
	private final ReportColumnMetadata EDTCD28  = new ReportColumnMetadata("EDTCD28",Types.DOUBLE,"Bonificación por ERE",10); 
	private final ReportColumnMetadata EDTCD29  = new ReportColumnMetadata("EDTCD29",Types.DOUBLE,"Reducciones SEA. Contingencias comunes Sistema Especial Agrario",10); 
	private final ReportColumnMetadata EDTCD30  = new ReportColumnMetadata("EDTCD30",Types.DOUBLE,"Reducciones. SEA Desempleo Sistema Especial Agrario",10);
	
	//////////////////////////////////////
	//ELEMENTO CALCULADO TOTALES
	//////////////////////////////////////
	
	private final ReportColumnMetadata EDTCA01  = new ReportColumnMetadata("EDTCA01",Types.DOUBLE,"Contingencias Comunes",10);
	//Cotización empresarial / Toneladas Régimen Especial de Manipulado y Empaquetado de Tomate Fresco (0134)
	private final ReportColumnMetadata EDTCA02  = new ReportColumnMetadata("EDTCA02",Types.DOUBLE,"Cuota empresarial por Contingencias Comunes",10);
	private final ReportColumnMetadata EDTCA03  = new ReportColumnMetadata("EDTCA03",Types.DOUBLE,"Cuota fija trabajador cuenta ajena extranjero (Baja a partir del 1 de enero de 2009) Es de aplicación solo para el Régimen Especial Agrario",10);
	private final ReportColumnMetadata EDTCA11  = new ReportColumnMetadata("EDTCA11",Types.DOUBLE,"Otros conceptos",10);
	//No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	private final ReportColumnMetadata EDTCA12  = new ReportColumnMetadata("EDTCA12",Types.DOUBLE,"Aportación a los servicios comunes",10); 
	//No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	private final ReportColumnMetadata EDTCA20  = new ReportColumnMetadata("EDTCA20",Types.DOUBLE,"Deducción por contingencias excluidas",10); 
	//No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
	private final ReportColumnMetadata EDTCA21  = new ReportColumnMetadata("EDTCA21",Types.DOUBLE,"Deducción colaboración voluntaria enfermedades comunes y accidente no laboral",10);
	private final ReportColumnMetadata EDTCA22  = new ReportColumnMetadata("EDTCA22",Types.DOUBLE,"Suma de compensaciones y reducciones",10);
	private final ReportColumnMetadata EDTCA30  = new ReportColumnMetadata("EDTCA30",Types.DOUBLE,"Total cuotas AT y EP",10);
	private final ReportColumnMetadata EDTCA31  = new ReportColumnMetadata("EDTCA31",Types.DOUBLE,"Cuotas por Incapacidad Temporal por AT y EP",10);
	private final ReportColumnMetadata EDTCA32  = new ReportColumnMetadata("EDTCA32",Types.DOUBLE,"Cuotas por Invalidez, muerte y supervivencia (IMS) por AT y EP",10);
	//Resto de regímenes excepto Régimen Especial del Mar, Régimen Especial de Manipulado y Empaquetado y Tomate Fresco y Régimen Especial Agrario (0613).
	private final ReportColumnMetadata EDTCA50  = new ReportColumnMetadata("EDTCA50",Types.DOUBLE,"Otras cotizaciones (Desempleo, FOGASA y Formación Profesional)",10);
	//Otra cotizaciones (Desempleo y Formación Profesional cuota obrera) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	private final ReportColumnMetadata EDTCA51  = new ReportColumnMetadata("EDTCA51",Types.DOUBLE,"Otras cotizaciones (Desempleo) (Tc1/16) - Régimen Especial del Mar",10);
	//Otras Cotizaciones (FOGASA) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	private final ReportColumnMetadata EDTCA52  = new ReportColumnMetadata("EDTCA52",Types.DOUBLE,"Otras cotizaciones (FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar",10); 
	//Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	private final ReportColumnMetadata EDTCA53  = new ReportColumnMetadata("EDTCA53",Types.DOUBLE,"Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar",10);
	//Cotización empresarial por Desempleo y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco
	private final ReportColumnMetadata EDTCA54  = new ReportColumnMetadata("EDTCA54",Types.DOUBLE,"Cotización empresarial por desempleo ( TC1/16) - Régimen Especial del Mar",10);
	private final ReportColumnMetadata EDTCA55  = new ReportColumnMetadata("EDTCA55",Types.DOUBLE,"Cotización empresarial por Fogasa y FP (TC1/16) Régimen Especial del Mar",10);
	private final ReportColumnMetadata EDTCA56  = new ReportColumnMetadata("EDTCA56",Types.DOUBLE,"Total Otras Cotizaciones cuota empresarial (TC1/16) Régimen Especial del Mar",10);
	private final ReportColumnMetadata EDTCA57  = new ReportColumnMetadata("EDTCA57",Types.DOUBLE,"Cuota empresarial por Otras Cotizaciones",10);
	private final ReportColumnMetadata EDTCA60  = new ReportColumnMetadata("EDTCA60",Types.DOUBLE,"Suma de bonificaciones, subvenciones y compensaciones",10);
	private final ReportColumnMetadata EDTCA80  = new ReportColumnMetadata("EDTCA80",Types.DOUBLE,"Bonificación INEM formación continua",10);
	private final ReportColumnMetadata EDTCA90  = new ReportColumnMetadata("EDTCA90",Types.DOUBLE,"Recargo de mora",10);
				
	//////////////////////////////////////
	//TOTALES
	//////////////////////////////////////
	private final ReportColumnMetadata EDTTT10  = new ReportColumnMetadata("EDTTT10",Types.DOUBLE,"Liquido contingencias generales",10);
	private final ReportColumnMetadata EDTTT20  = new ReportColumnMetadata("EDTTT20",Types.DOUBLE,"Liquido accidentes de trabajo y enfermedad profesional",10);
	private final ReportColumnMetadata EDTTT30  = new ReportColumnMetadata("EDTTT30",Types.DOUBLE,"Liquido otras cotizaciones",10);
	private final ReportColumnMetadata EDTTT91  = new ReportColumnMetadata("EDTTT91",Types.DOUBLE,"A Ingresar",10);
	private final ReportColumnMetadata EDTTT92  = new ReportColumnMetadata("EDTTT92",Types.DOUBLE,"A percibir",10);
	
	
	private final ReportColumnMetadata[] COLUMN_LABELS = new ReportColumnMetadata[]{
		EDLBA01, EDLBA02, EDLBA05, EDLBA06, EDLBA07, EDLBA08, EDLBA09, EDLBA10, EDLBA11, EDLBA20, EDLBA21, EDLBA22, EDLBA23,
		EDLBA28, EDLBA30, EDLBA31, EDLBA32, EDLBA33, EDLBA34, EDLBA35, EDLBA36, EDLBA37, EDLBA38, EDLBA41, EDLBA42,
		EDLCD01, EDLCD03, EDLCD06, EDLCD07, EDLCD10, EDLCD11, EDLCD12, EDLCD13, EDLCD17, EDLCD20, EDLCD22, EDLCD23,
		EDLCD25, EDLCD28, EDLCD29, EDLCD30,
	};

	private final ReportColumnMetadata[] ROW_LABELS = new ReportColumnMetadata[]{
		EDTBA01, EDTBA02, EDTBA05, EDTBA06, EDTBA07, EDTBA08, EDTBA09, EDTBA10, EDTBA11, EDTBA21, EDTBA22, EDTBA23, EDTBA28,
		EDTBA30, EDTBA31, EDTBA32, EDTBA33, EDTBA34, EDTBA35, EDTBA36, EDTBA37, EDTBA38, EDTBA41, EDTBA42,
		EDTCD01, EDTCD03, EDTCD05, EDTCD06, EDTCD07, EDTCD10, EDTCD11, EDTCD12, EDTCD13, EDTCD16, EDTCD17, EDTCD18, EDTCD20,
		EDTCD21, EDTCD22, EDTCD23, EDTCD24, EDTCD25, EDTCD26, EDTCD27, EDTCD28, EDTCD29, EDTCD30,
		EDTCA01, EDTCA02, EDTCA03, EDTCA11, EDTCA12, EDTCA20, EDTCA21, EDTCA22, EDTCA30, EDTCA31, EDTCA32, EDTCA50, EDTCA51,
		EDTCA52, EDTCA53, EDTCA54, EDTCA55, EDTCA56, EDTCA57, EDTCA60, EDTCA80, EDTCA90, 
		EDTTT10, EDTTT20, EDTTT30, EDTTT91, EDTTT92,	
	};
	
	
	public void buildFANReport(List<EnterpriseCCC> list, LiquidationType liquidationType, Integer year, Month startMonth, Month endMonth ) throws ManagerBeanException {
		writer = new FANWriter();
		eti = writer.createETIRecord( true, list, liquidationType, year, startMonth, endMonth );
		
	}
	
	private ReportMetadata getMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		metadata.getColumns().add(new ReportColumnMetadata("CIF",Types.VARCHAR,"CIF",10));
		metadata.getColumns().add(new ReportColumnMetadata("EMPRESA",Types.VARCHAR,"Empresa",15));
		metadata.getColumns().add(new ReportColumnMetadata("CCC",Types.VARCHAR,"CCC",15));
		metadata.getColumns().add(new ReportColumnMetadata("Nombre",Types.VARCHAR,"Nombre",30));
		metadata.getColumns().add(new ReportColumnMetadata("Indicador",Types.VARCHAR,"Indicador",5));
		metadata.getColumns().add(new ReportColumnMetadata("Dias",Types.VARCHAR,"Días",5));
		metadata.getColumns().add(new ReportColumnMetadata("Horas",Types.VARCHAR,"Horas",5));
		
		List<String> keyList = new LinkedList<String>();
		for(EMP emp: eti.getEmpresas()){
			for(TRA tra: emp.getTrabajadores()){
				for(DAT dat: tra.getDat()){
					for(String key: dat.getEdl().keySet()){
						if(!keyList.contains(key)){
							keyList.add("EDL"+key);
						}
					}
				}
			}
		}
		
		for (ReportColumnMetadata rcm : COLUMN_LABELS) {
			if(keyList.contains(rcm.getName())){
				metadata.getColumns().add(rcm);
			}
		}
		return metadata;
	}
	
	public void excelReport(Connection conn, Locale locale,OutputStream output) throws IOException, ReportException {
		ReportMetadata metadata = getMetadata();
		ExcelReportExporter exporter = new ExcelReportExporter();
		exporter.startExport(IReportExporter.DEFAULT_NAME);
		exporter.exportHeader(metadata);

		excelReport(exporter, metadata);	
		
		exporter.endExport(output);
		output.flush();
		
	}
	
	public void excelReport(ExcelReportExporter exporter, ReportMetadata metadata) throws IOException, ReportException {
		
		for (int e=0; e<eti.getEmpresas().size(); e++) {
			EMP emp = eti.getEmpresas().get(e);
			
			for (int t=0; t<emp.getTrabajadores().size(); t++) {
				TRA tra = emp.getTrabajadores().get(t);
				for (int d=0; d<tra.getDat().size(); d++) {
					int column = 0;
					exporter.startLine();
					
					String cif = emp.getNumeroIdentificacion();
					while(cif.startsWith("0")){
						cif = cif.replaceFirst("0", "");
					}
					exporter.exportColumn(metadata.getColumns().get(column++), cif );
					exporter.exportColumn(metadata.getColumns().get(column++), emp.getRzs().getRazonSocial() );
					exporter.exportColumn(metadata.getColumns().get(column++), emp.getCodigoCuentaCotizacionSeguridadSocial() );
					if (emp.getTrabajadores().get(t).getAyn() != null) {
						exporter.exportColumn(metadata.getColumns().get(column++), 
								tra.getAyn().getPrimerApellido()
								+ " " + tra.getAyn().getSegundoApellido() 
								+ ", " + tra.getAyn().getNombre() );
					}
					DAT dat = tra.getDat().get(d);
					if (dat.getEdl() != null) {
						exporter.exportColumn(metadata.getColumns().get(column++), (dat.getIndicadoresPerfil()!=null && dat.getIndicadoresPerfil().contains("I"))?"IT":"" );
							exporter.exportColumn(metadata.getColumns().get(column++), (dat.getIndicadorCotizacion()==null?dat.getDiasHoras().toString():"") );
							exporter.exportColumn(metadata.getColumns().get(column++), (dat.getIndicadorCotizacion()!=null?dat.getDiasHoras().toString():"") );
							
							for(String key: dat.getEdl().keySet()){
								EDL edl = dat.getEdlSegment(key); 
								Double amount = 0.0;
								if(edl!=null && edl.getImporte()!=null){
									amount = new Double(dat.getEdlSegment(key).getImporte())/100; 
								}
								
								exporter.exportColumn(metadata.getColumns().get(column++), amount );
							}
						
					}
				}
			}
//				for (TCT tct: emp.getTcTotales()) {
//					properties.put(TCT , tct);
//				}
			
//			exporter.startLine();
//			exporter.startLine();
//			exporter.exportColumn(metadata.getColumns().get(1), "Concepto" );
//			exporter.exportColumn(metadata.getColumns().get(2), "Base" );
//			exporter.exportColumn(metadata.getColumns().get(3), "Tipo" );
//			exporter.exportColumn(metadata.getColumns().get(4), "Importe" );
//			exporter.startLine();
//			for (EDT edt: emp.getEdt().values()) {
//				
//				ReportColumnMetadata rcm = null;
//				for (ReportColumnMetadata _rcm : ROW_LABELS) {
//					String key = "EDT"+edt.getTipoElemento()+(edt.getClave()<10?"0":"")+edt.getClave().toString().length();
//
//					if(edt.getTipoElemento().equals("TT")){
//						System.out.println();
//					}
//					
//					
//					if(key.equals(_rcm.getName())){
//						rcm = _rcm;
//						break;
//					}
//				}
//				if(rcm!=null){
//					exporter.exportColumn(metadata.getColumns().get(1), rcm.getName() );
//					exporter.exportColumn(metadata.getColumns().get(2), (edt.getBase()!=null?edt.getBase().toString():"") );
//					exporter.exportColumn(metadata.getColumns().get(3), (edt.getParteDecimalFactorTipo()!=null?edt.getParteDecimalFactorTipo().toString():"") );
//					exporter.exportColumn(metadata.getColumns().get(4), (edt.getImporte()!=null?edt.getImporte().toString():"") );
//					exporter.startLine();
//				}
//			}
//			exporter.startLine();
//			exporter.startLine();
			
			
//				for (EDT edt: emp.getEdt().values()) {
//					properties.put(EDT , edt);
//				}
//				if (emp.getMpg() != null) {
//					properties.put(MPG , emp.getMpg());
//				}
		
		}
	}
	
	
}
