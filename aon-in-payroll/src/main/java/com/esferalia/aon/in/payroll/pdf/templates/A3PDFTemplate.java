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

public class A3PDFTemplate implements SalaryPDFTemplate {

	@Override
	public SalaryPDFTemplate parse(String text, ISalaryBuilder<?> salaryBuilder) throws IOException, UnknownPDFException {
		salaryBuilder.createNewSalary();
		salaryBuilder.setType(SalaryType.SALARY); // TODOD:
		salaryBuilder.setRegistration(Integer.MIN_VALUE);
		
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			
		}
		return this;
	}
	
	
	//    IVANOV , PETAR GEORGIEV
	private static final Pattern EMPLOYEE_NAME = 
	Pattern.compile("^(?<name>[^,]+),(?<surname>.*)$"
	, Pattern.CASE_INSENSITIVE);
	//    CL    ALFONSO VI             30       3  DC                 
	private static final Pattern EMPLOYEE_ADDRESS = 
	Pattern.compile("^\\s*(?<tipo>[^\\s]+)\\s+(?<address>.*)$"
	, Pattern.CASE_INSENSITIVE);

	

	private static Matcher check(Pattern pattern, String str) {
		Matcher matcher =
		pattern.matcher(str);
		matcher.matches();
//		for ( int g = 1; g <= matcher.groupCount(); g++) 
//			System.out.println(matcher.group(g));
		return matcher;		
	}
	
	public static void main(String[] args) {
		Matcher matcher = check(EMPLOYEE_NAME, "    IVANOV , PETAR GEORGIEV                                     ");
		System.out.println("name : "+matcher.group("name"));
		System.out.println("surname : " + matcher.group("surname"));

		matcher = check(EMPLOYEE_ADDRESS, "   CL    ALFONSO VI             30       3  DC                 ");
		System.out.println("tipo : "+ matcher.group("tipo"));
		System.out.println("address : " + matcher.group("address"));
		
	}
	
	//    09000  MIRANDA DE EBRO                                      
	//    BURGOS                                                      
	//NIF. J01409838                                                      8052                        
	//EMPRESA DOMICILIO Nº INS. S.S.
	//RESTAURANTE EL VISO, S.C          CL REAL 32 BJ                     01/1034816-96               
	//TRABAJADOR/A CATEGORIA NºMATRIC ANTIGUEDAD D.N.I.
	//IVANOV , PETAR GEORGIEV           FREGADOR                  1 OCT 08   X8865220P   
	//Nº AFILIACION. S.S. TARIFA COD.CT SECCION NRO. PERIODO TOT. DIAS
	//48/10454983-40     7  200              4  MENS 01 ENE 20 a 31 ENE 20          30  
	//CUANTIA PRECIO CONCEPTO DEVENGOS DEDUCCIONES
	//30,00     26,741     1  *Salario Base                               802,24                   
	//4  *Antigüedad                                 128,36                   
	//30,00      1,039    95  *Plus Manutención                            31,17                   
	//124  *P.p.extras                                 169,00                   
	//147  *Bonus octubre                               78,20                   
	//240  *Domingos-festiv                             51,34                   
	//789   Dcto.Conceptos en Especie                                 31,17     
	//995   COTIZACION CONT.COMU 4,70                                 59,23     
	//996   COTIZACION FORMACION 0,10                                  1,26     
	//997   COTIZACION DESEMPLEO 1,55                                 19,53     
	//999   TRIBUTACION I.R.P.F. 4,00                                 50,42     
	//Horas en Alta a Tiempo Parcial: 105,00                              
	//                                                                  
	//                                                                  
	//                                                                  
	//                                                                  
	//                                                                  
	//                                                                  
	//                                                                  
	//                                                                  
	//REM. TOTAL P.P.EXTRAS BASE S.S. BASE A.T. Y DES. BASE I.R.P.F. T. DEVENGADO T.  A DEDUCIR
	//1.260,31                  1.260,31        1.260,31     1.260,31    1.260,31        161,61     
	//* Percepciones Salariales  sujetas a Cot. S.S. - Percepciones no Salariales excluídas Cot. S.S.
	//                                                                  
	//FECHA                                                        SELLO EMPRESA RECIBI
	//31 ENERO      2020                                                              
	//ARMIÑON                                                                         
	//LIQUIDO A PERCIBIR
	//                                         1.098,70        
	//IBAN:                                                                                                     
	//SWIFT/BIC:                                                                                                     COSTE EMPRESA:        1.656,05   
	//DETERMINACIÓN DE LAS B. DE COTIZACIÓN A LA S.S. Y CONCEPTOS DE RECAUDACIÓN CONJUNTA Y APORTACIÓN DE LA EMPRESA
	//CONCEPTO BASE TIPO APORTACIÓN EMPRESARIAL
	//1. Contingencias comunes.................................................... .. .. ..    1.260,31          23,60           297,43          
	//AT y EP................................. .. .. ..    1.260,31           1,50            18,90           
	//2. Contingencias profe- Desempleo............................ .. .. ..    1.260,31           5,50            69,32          
	//sionales y conceptos de
	//recaudación conjunta Formación Profesional..........
	//.. ... .    1.260,31           0,60             7,56           
	//Fondo Garantía Salarial......... .. .. .    1.260,31           0,20             2,52           
	//3. Cotización adicional horas extraordinarias........................ .. .. .                                                       

}
