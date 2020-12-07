package com.esferalia.aon.in.payroll.pdf.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DSIPDFTemplate implements SalaryPDFTemplate{
	
	public static final DSIPDFTemplate DSI_PDF_TEMPLATE = new DSIPDFTemplate();
	
	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))){
			Matcher matcher = find(reader, ENTERPRISE_WORKER);
			salaryBuilder.setEmployeeName(AonStringUtils.trimToNull(matcher.group("name")+" "));
		}
		return this;
	}
	//	Empresa: EMPRESA S.L. Trabajador: ANDREA ANDREA, MARIA
	private static Pattern ENTERPRISE_WORKER =
	Pattern.compile("\\s*Empresa:\\s*(?<enterprise>\\.+?)\\s*Trabajador:\\s*(?<?surname>[\\w\\s]+),\\s*(?<name>\\w)+\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	

//	Domicilio: CL VIA, 12 N.I.F.: 37723953C Número Libro de Matrícula:
//	C.I.F.: B50671908 Nº de Afiliación a la Seguridad Social: 08/02983860/69
//	Código de Cuenta de Cotización a la Categoría o Grupo Profesional:
//	Seguridad Social: 50/8745111/93 Grupo de Cotización: 02 Fecha antigüedad: 14/09/1972
//	Periodo de Liquidación:    del 1 de febrero al 29 de febrero de 2020 Total días 30
//	I. DEVENGOS TOTALES
//	1. Percepciones salariales 2. Percepciones no salariales
//	SALARIO BASE............................................................... 389,35 Indemnizaciones o suplidos
//	HORAS EXTRAORDINARIAS........................................... ...................................................................................
//	GRATIF.EXTRAORDINARIAS........................................... Prestaciones e indemnizaciones de la Seguridad Social
//	SALARIO EN ESPECIE................................................... ...................................................................................
//	Complementos salariales ...................................................................................
//	ANTIGUEDAD.................................................................. 118,16 Indemnizaciones por traslados, suspensiones o despidos
//	COMPL............................................................................ 8,99 ...................................................................................
//	NOCTURNO.................................................................... 95,03 Otras percepciones no salariales
//	ESTUDIOS....................................................................... 147,31 ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	........................................................................................ ...................................................................................
//	A. TOTAL SALARIO DEVENGADO........................ 758,84
//	II. DEDUCCIONES
//	1. Aportación del trabajador a las cotizaciones a la 2. Impuesto sobre la renta 
//	   Seguridad Social y conceptos de recaudación conjunta de la personas físicas..................... 16,00% 121,41
//	Contingencias Comunes 1.215,90 4,70% 57,15 3. Anticipos......................................................  
//	Desempleo 1.050,00 1,55% 16,28 4. Valor de los productos
//	Formación Profesional 1.050,00 0,10% 1,05 recibidos en especie.................    
//	Horas Extraordinarias 5. Otras deducciones
//	Fuerza Mayor  2,00%  DESCUENTO 1........................................ 25,00
//	Resto Horas Extras  4,70%  ................................................................  
//	TOTAL APORTACIONES................................................. 74,48
//	B. TOTAL DEDUCCIONES (S.SOCIAL-IRPF-...)...................... 220,89
//	Firma y Sello de la Empresa TOTAL SALARIO LIQUIDO........................ 537,95
//	ZARAGOZA, 29 de febrero de 2020
//	RECIBI,
//	INFORMACION ADICIONAL:
//	Texto de informacion adicional
//	DETERMINACION DE LAS BASES DE COTIZACION A LA SEGURIDAD SOCIAL Y CONCEPTOS DE RECAUDACION CONJUNTA Y DE LA BASE 
//	SUJETA A RETENCION DEL I.R.P.F. Y APORTACIÓN DE LA EMPRESA:
//	1. Contingencias comunes BASE BASE TIPO APORTACIÓN
//	Importe remuneración mensual............................................................... 758,84 NORMALIZADA EMPRESA
//	Prorrata pagas extraordinarias................................................................ 126,47
//	Base incapacidad temporal.....................................................................  
//	TOTAL.............. 885,31 1.215,90 23,60% 286,95
//	AT y EP..................... 1,50% 15,75
//	2. Contingencias profesionales
//	(AT.y EP.) y conceptos de
//	Desempleo............................................
//	885,31 1.050,00
//	5,50% 57,75
//	recaudación conjunta 
//	Formación Profesional............................ 0,60% 6,30
//	Fondo Garantía Salarial........................... 0,20% 2,10
//	3. Cotización adicional por horas extraordinarias...................................................................   
//	4. Base sujeta a retención del I.R.P.F.............................................................................. 758,84
	
	
	private Matcher find( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) {
//				System.out.println(line);
				continue;
			}
			
			return matcher;
		}
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
				
	}

}
