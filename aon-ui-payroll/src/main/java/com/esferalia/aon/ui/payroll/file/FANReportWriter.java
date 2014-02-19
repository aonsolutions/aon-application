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
import com.esferalia.aon.file.payroll.fan.data.EMP;
import com.esferalia.aon.file.payroll.fan.data.ETI;
import com.esferalia.aon.file.payroll.fan.data.TRA;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.LiquidationType;


public class FANReportWriter {
	
	private FANWriter writer;
	private ETI eti;
	
	// BASES
	private final ReportColumnMetadata BA01 = new ReportColumnMetadata("BA01",Types.DOUBLE,"Contingencias comunes",10);
	private final ReportColumnMetadata BA02 = new ReportColumnMetadata("BA02",Types.DOUBLE,"AT y EP",10);
	private final ReportColumnMetadata BA05 = new ReportColumnMetadata("BA05",Types.DOUBLE,"Exceso del tope (Minería del Carbón)",10);
	private final ReportColumnMetadata BA06 = new ReportColumnMetadata("BA06",Types.DOUBLE,"Importe percepciones Integras (Artistas.)",10);
	// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911). Baja a partir de 2002
	private final ReportColumnMetadata BA07 = new ReportColumnMetadata("BA07",Types.DOUBLE,"AT y EP sin horas extraordinarias",10);
	// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
	private final ReportColumnMetadata BA08 = new ReportColumnMetadata("BA08",Types.DOUBLE,"Diferencia Bases (contingencias comunes y salario normalizado)",10);
	private final ReportColumnMetadata BA09 = new ReportColumnMetadata("BA09",Types.DOUBLE,"Horas complementarias No será de utilización para Régimen General de Artistas (0112)",10);
	// No será de utilización para Régimen General de Artistas (0112), Régimen Especial de Minería del Carbón (0911).
	private final ReportColumnMetadata BA10 = new ReportColumnMetadata("BA010",Types.DOUBLE,"Horas extras estructurales / Causa de fuerza mayor desde 1/1/98",10);
	private final ReportColumnMetadata BA11 = new ReportColumnMetadata("BA011",Types.DOUBLE,"Horas extras no estructurales / Otras horas extras desde 1/1/98",10);
	private final ReportColumnMetadata BA20 = new ReportColumnMetadata("BA020",Types.DOUBLE,"Base de cotización empresarial C.Comunes = AT y EP",10);
	// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	private final ReportColumnMetadata BA21 = new ReportColumnMetadata("BA021",Types.DOUBLE,"Base de cotización empresarial por contingencias comunes Base de cotización empresarial por desempleo y FOGASA",10); 
	private final ReportColumnMetadata BA22 = new ReportColumnMetadata("BA022",Types.DOUBLE,"Base de cotización empresarial por AT y EP y Otras Cotizaciones",10);
	private final ReportColumnMetadata BA23 = new ReportColumnMetadata("BA023",Types.DOUBLE,"Base de cotización tipo total desempleo y FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
	private final ReportColumnMetadata BA28 = new ReportColumnMetadata("BA028",Types.DOUBLE,"Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial",10); 
	private final ReportColumnMetadata BA30 = new ReportColumnMetadata("BA030",Types.DOUBLE,"Cotización por Jornadas Reales. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata BA31 = new ReportColumnMetadata("BA031",Types.DOUBLE,"Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)",10); 
	// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
	private final ReportColumnMetadata BA32 = new ReportColumnMetadata("BA032",Types.DOUBLE,"Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA.",10); 
	// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
	private final ReportColumnMetadata BA33 = new ReportColumnMetadata("BA033",Types.DOUBLE,"Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo.",10); 
	private final ReportColumnMetadata BA34 = new ReportColumnMetadata("BA034",Types.DOUBLE,"Base de AT en vacaciones. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata BA35 = new ReportColumnMetadata("BA035",Types.DOUBLE,"Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata BA36 = new ReportColumnMetadata("BA036",Types.DOUBLE,"Base exclusiva Desempleo/FOGASA tipo total. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata BA37 = new ReportColumnMetadata("BA037",Types.DOUBLE,"Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012). (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata BA38 = new ReportColumnMetadata("BA038",Types.DOUBLE,"Cotización exclusivamente por FOGASA. (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10); 
	private final ReportColumnMetadata BA41 = new ReportColumnMetadata("BA041",Types.DOUBLE,"Contingencias Comunes y FOGASA, (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)",10);
	private final ReportColumnMetadata BA42 = new ReportColumnMetadata("BA042",Types.DOUBLE,"Base exclusiva de AT y EP sin cotización de Otras Cotizaciones",10);
	
	// COMPENSACIONES - DEDUCCIONES
	private final ReportColumnMetadata CD01 = new ReportColumnMetadata("CD01",Types.DOUBLE,"IT enfermedad común y accidente no laboral",10); 
	private final ReportColumnMetadata CD03 = new ReportColumnMetadata("CD03",Types.DOUBLE,"IT por AT y EP",10);
	private final ReportColumnMetadata CD06 = new ReportColumnMetadata("CD06",Types.DOUBLE,"Reducciones Contratos con derecho a reducción (casilla 209 de TC1)",10);
	private final ReportColumnMetadata CD07 = new ReportColumnMetadata("CD07",Types.DOUBLE,"Bonificaciones Contratos con derecho a bonificación/reducción (casilla 601 de TC1)",10);
	private final ReportColumnMetadata CD10 = new ReportColumnMetadata("CD10",Types.DOUBLE,"Bonificación por formación teórica presencial",10);
	private final ReportColumnMetadata CD11 = new ReportColumnMetadata("CD11",Types.DOUBLE,"Bonificación por formación teórica a distancia",10);
	private final ReportColumnMetadata CD12 = new ReportColumnMetadata("CD12",Types.DOUBLE,"Bonificación por Ley 19/94 (Registro Canario) Régimen del Mar",10);
	private final ReportColumnMetadata CD13 = new ReportColumnMetadata("CD13",Types.DOUBLE,"Bonificación minusvalidos en Centros Especiales de Empleo",10);
	private final ReportColumnMetadata CD17 = new ReportColumnMetadata("CD17",Types.DOUBLE,"Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)",10);
	private final ReportColumnMetadata CD20 = new ReportColumnMetadata("CD20",Types.DOUBLE,"Bonificación Ceuta y Melilla (O. TAS/471/2004)",10);
	private final ReportColumnMetadata CD22 = new ReportColumnMetadata("CD22",Types.DOUBLE,"Bonificación Fom. Empleo Cuantía fija. ",10);
	private final ReportColumnMetadata CD23 = new ReportColumnMetadata("CD23",Types.DOUBLE,"Bonificación Sector Industrial Incentivado",10);
	private final ReportColumnMetadata CD25 = new ReportColumnMetadata("CD25",Types.DOUBLE,"Exención de desempleo hijos<30años Autonomos",10);
	private final ReportColumnMetadata CD28 = new ReportColumnMetadata("CD28",Types.DOUBLE,"Bonificación por ERE",10);
	private final ReportColumnMetadata CD29 = new ReportColumnMetadata("CD29",Types.DOUBLE,"Reducciones SEA. Contingencias comunes",10); 
	private final ReportColumnMetadata CD30 = new ReportColumnMetadata("CD30",Types.DOUBLE,"Reducciones SEA. Desempleo",10);
	
	private final ReportColumnMetadata[] COLUMN_LABELS = new ReportColumnMetadata[]{
//		new ReportColumnMetadata("CCC",Types.VARCHAR,"CCC",30)
//		,new ReportColumnMetadata("Nombre",Types.VARCHAR,"Nombre",30)
//		,new ReportColumnMetadata("Indicador",Types.VARCHAR,"Indicador",5)
//		,new ReportColumnMetadata("Dias",Types.VARCHAR,"Días",5)
//		,new ReportColumnMetadata("Horas",Types.VARCHAR,"Horas",5)
//		,new ReportColumnMetadata("BA01",Types.DOUBLE,"CGC",7)
//		,new ReportColumnMetadata("BA02",Types.DOUBLE,"CGP",7)
//		,new ReportColumnMetadata("CD01",Types.DOUBLE,"DEDUCCION CD01",8)
//		,new ReportColumnMetadata("CD22",Types.DOUBLE,"BONIFICACION CD22",8)
		
		BA01
		,BA02
		,BA05
		,BA06
		,BA07
		,BA08
		,BA09
		,BA10
		,BA11
		,BA20
		,BA21 
		,BA22
		,BA23
		,BA28 
		,BA30
		,BA31 
		,BA32 
		,BA33 
		,BA34
		,BA35 
		,BA36 
		,BA37 
		,BA38 
		,BA41
		,BA42
		,CD01 
		,CD03
		,CD06
		,CD07
		,CD10
		,CD11
		,CD12
		,CD13
		,CD17
		,CD20
		,CD22
		,CD23
		,CD25
		,CD28
		,CD29 
		,CD30
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
							keyList.add(key);
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
			
			exporter.startLine();
			exporter.startLine();
			
//			exporter.exportColumn(metadata.getColumns().get(7), (dat.getIndicadorCotizacion()==null?dat.getDiasHoras().toString():"") );
			
			
			
			
			
			
//////////////////////////////////////
// TOTALES
//////////////////////////////////////
/**
* EDTTT91 A Ingresar
* EDTTT92 A percibir
* se crea el segmento tt91 o tt92 dependiendo del signo del importe

* EDTTT30 Liquido otras cotizaciones

* EDTTT20 Liquido accidentes de trabajo y enfermedad profesional

* EDTTT10 Liquido contingencias generales
*/

	
//////////////////////////////////////
// ELEMENTO CALCULADO TOTALES
//////////////////////////////////////

// EDTCA90 Recargo de mora
// EDTCA80 Bonificación INEM formación continua
/**
*  EDTCA60 Suma de bonificaciones, subvenciones y compensaciones
*/

// EDTCA57 Cuota empresarial por Otras Cotizaciones
// EDTCA56 Total Otras Cotizaciones cuota empresarial (TC1/16) Régimen Especial del Mar
// EDTCA55 Cotización empresarial por Fogasa y FP (TC1/16) Régimen Especial del Mar

// EDTCA54 Cotización empresarial por desempleo ( TC1/16) - Régimen Especial del Mar
// Cotización empresarial por Desempleo y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco

// EDTCA53 Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar
// Total Otras Cotizaciones (Desempleo, FOGASA y Formación Profesional (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco

// EDTCA52 Otras cotizaciones (FOGASA y Formación Profesional) (TC1/16) - Régimen Especial del Mar 
// Otras Cotizaciones (FOGASA) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco

// EDTCA51 Otras cotizaciones (Desempleo) (Tc1/16) - Régimen Especial del Mar
// Otra cotizaciones (Desempleo y Formación Profesional cuota obrera) (TC1/25) - Sistema Especial de Manipulado y Empaquetado de Tomate Fresco


/**
*  EDTCA01 Contingencias Comunes
// Cotización empresarial / Toneladas Régimen Especial de Manipulado y Empaquetado de Tomate Fresco (0134)
// EDTCA02 Cuota empresarial por Contingencias Comunes
 * 
// EDTCA03 Cuota fija trabajador cuenta ajena extranjero (Baja a partir del 1 de enero de 2009) Es de aplicación solo para el Régimen Especial Agrario
// EDTCA11 Otros conceptos
 * 
// No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
// EDTCA12 Aportación a los servicios comunes 

// No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
// EDTCA20 Deducción por contingencias excluidas 
 * 
// No es de aplicación para el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
// EDTCA21 Deducción colaboración voluntaria enfermedades comunes y accidente no laboral
 * 
 *  EDTCA22 Suma de compensaciones y reducciones
 *  EDTCA30 Total cuotas AT y EP
 *  EDTCA31 Cuotas por Incapacidad Temporal por AT y EP
 *  EDTCA32 Cuotas por Invalidez, muerte y supervivencia (IMS) por AT y EP

 * 
 *  Resto de regímenes excepto Régimen Especial del Mar, Régimen Especial de Manipulado y Empaquetado y Tomate Fresco y Régimen Especial Agrario (0613).
 *  EDTCA50 Otras cotizaciones (Desempleo, FOGASA y Formación Profesional)
 * 
*/


//////////////////////////////////////
// COMPENSACION - DEDUCCION TOTALES
////////////////////////////////////// 
/**

 *  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
*  EDTCD01 IT enfermedad común y accidente no laboral
*  
*  No es de aplicación en el Régimen General de Artistas (0112) y Régimen Especial Agrario (0613)
*  EDTCD03 IT por AT y EP
*  
// EDTCD05 IT O.M. 3/4/73. Minería del Carbón
 *  EDTCD06 Reducciones
 *  EDTCD07 Bonificaciones
 *  EDTCD10 Bonificación por formación teórica presencial
 *  EDTCD11 Bonificación por formación teórica a distancia
// EDTCD12 Bonificación por Ley 19/94 (Registro Canario) Régimen Especial del Mar
 *  EDTCD13 Bonificación minusvalidos en Centros Especiales de Empleo
 *  EDTCD16 Bonificación por trabajadores con 60 o más años
*  
*  No es de aplicación en el Régimen Especial Agrario (0613)
*  EDTCD17 Reducción contingencias comunes excepto I.T. (R.D.L. 16/2001)

// EDTCD18 Reducción por Exencón de desempleo (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)
 *  EDTCD20 Bonificación Ceuta y Melilla (O. TAS/471/2004)
 *  EDTCD21 Bonificación Copa del America (R.D.L. 2146/2004)
 *  EDTCD22 Bonificación Form. Empleo Cuantía fija. Excepto Rég. Gral. Artistas (0112)
 *  EDTCD23 Bonificación Sector industrial incentivado
 *  EDTCD24 Bonificación I+D+I Régimen General (0111)
 *  EDTCD25 Exención de desempleo hijos<30años Autonomos
// EDTCD26 Reducciones REA Cuantía mensual (modalidad G y J). (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
// EDTCD27 Reducciones REA "Jornadas reales". (Baja a partir del 1 de enero de 2012) Régimen Especial Agrario 
 *  EDTCD28 Bonificación por ERE 
// EDTCD29 Reducciones SEA. Contingencias comunes Sistema Especial Agrario 
// EDTCD30 Reducciones. SEA Desempleo Sistema Especial Agrario
*  
*/

//////////////////////////
// BASES TOTALES
//////////////////////////
/**

EDTBA01 Contingencias comunes
EDTBA02 AT y EP
EDTBA05 Exceso del tope (Minería del Carbón)
EDTBA06 Importe percepciones Integras (Artistas)

// No será de utilización para Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911). Baja a partir de 2002
EDTBA07 AT y EP sin horas extraordinarias

// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002
EDTBA08 Diferencia Bases (Contingencias comunes y salario normalizado)

EDTBA09 Horas complementarias No se utilizará para Régimen General de Artistas (0112)

// No se podrá utilizar para el Régimen General de Artistas (0112), ni Régimen Especial de Minería del Carbón (0911).
EDTBA10 Horas extras estructurales / Causa de fuerza mayor desde 1/1/98

// No se podrá utilizar para e Régimen General de Artistas (0112) , ni Régimen Especial de Minería del Carbón (0911)
EDTBA11 Horas extras no estructurales / Otras horas extras desde 1/1/98

// Base de cotización empresarial desempleo y FOGASA (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
EDTBA21 Base de cotización empresarial por contingencias comunes

EDTBA22 Base de cotización empresarial por AT y EP y Otras Cotizaciones
EDTBA23 Base de cotización tipo total desempleo y FOGASA(Baja a partir del 1 de enero de 2012) . (Régimen Especial Agrario)

// Solo para liquidaciones anteriores al año 2002 del Régimen Especial de la Minería del Carbón (0911). Baja a partir de 2002 
EDTBA28 Diferencia en bases (contingencias comunes y salario normalizado), cotización exclusivamente empresarial

EDTBA30 Cotización por Jornadas Reales (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
EDTBA31 Contingencias comunes trajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario)

// (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
EDTBA32 Base de cotización Jornadas Reales en situación de IT por desempleo empresarial y FOGASA. 
 
// Trabajadores no fijos (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
EDTBA33 Base de cotización exclusiva de Otras Cotizaciones en situación de IT/maternidad/riesgo durante el embarazo. 
 
EDTBA34 Base de AT en vacaciones (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
EDTBA35 Base fija cotización trabajadores cuenta ajena extranjeros (Baja a partir del 1 de enero de 2009) (Régimen Especial Agrario) 
EDTBA36 Base exclusiva Desempleo/FOGASA tipo total (Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
EDTBA37 Cotización jornadas reales y FOGASA excluido desempleo(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
EDTBA38 Cotización exclusivamente por FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario) 
EDTBA41 Contingencias Comunes y FOGASA(Baja a partir del 1 de enero de 2012) (Régimen Especial Agrario)
EDTBA42 Base exclusiva de AT y EP sin cotización de Otras Cotizaciones
 
*  
*/
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
//				for (EDT edt: emp.getEdt().values()) {
//					properties.put(EDT , edt);
//				}
//				if (emp.getMpg() != null) {
//					properties.put(MPG , emp.getMpg());
//				}
		
		}
	}
	
	
}
