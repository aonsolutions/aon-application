package com.esferalia.aon.in.payroll.pdf.template.regex;

import java.util.regex.Pattern;

public class A3Regex {
	//    IVANOV , PETAR GEORGIEV
	public static final Pattern EMPLOYEE_NAME = 
	Pattern.compile("^(?<name>[^,]+,.*)$"
	, Pattern.CASE_INSENSITIVE);
	//    CL    ALFONSO VI             30       3  DC                 
	public static final Pattern EMPLOYEE_ADDRESS = 
	Pattern.compile("^\\s*(?<tipo>[^\\s]+)?\\s+(?<address>.+)?$"
	, Pattern.CASE_INSENSITIVE);
	//  09000  MIRANDA DE EBRO 
	public static final Pattern PC_AND_MUNICIPALITY =
	Pattern.compile("^\\s*(?<postcode>\\d{5})?\\s*(?<municipality>.+)?$"
	, Pattern.CASE_INSENSITIVE);
	//  BURGOS    
	public static final Pattern PROVINCE =
	Pattern.compile("^\\s*(?<province>.+)$"
	, Pattern.CASE_INSENSITIVE);
	//NIF. J01409838                                                      8052                        
	public static final Pattern NIF =
	Pattern.compile("^\\s*(?:NIF\\.)\\s*(?<nif>.+?)\\s*(?<enterprisecode>\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
	//EMPRESA DOMICILIO Nº INS. S.S.
	public static final Pattern HOME_HEADER =
	Pattern.compile("EMPRESA\\s*DOMICILIO\\s*Nº\\s*INS\\.\\s*S\\.S\\.", Pattern.CASE_INSENSITIVE);
	//RESTAURANTE EL VISO, S.C          CL REAL 32 BJ                     01/1034816-96               
	public static final Pattern ENTERPRISE_HOME =
	Pattern.compile("^\\s*(?<enterprisename>.+?)\\s{2,}(?<address>.+?)\\s{2,}(?<nss>\\d+/\\d+-\\d+)\\s*",
	Pattern.CASE_INSENSITIVE);
	//TRABAJADOR/A CATEGORIA NºMATRIC ANTIGUEDAD D.N.I.
	public static final Pattern WORKER_HEADER =
	Pattern.compile("TRABAJADOR/A\\s*CATEGORIA\\s*NºMATRIC\\s*ANTIGUEDAD\\s*D\\.N\\.I\\."
	, Pattern.CASE_INSENSITIVE);
	//IVANOV , PETAR GEORGIEV           FREGADOR                  1 OCT 08   X8865220P   
	//    SANCHEZ REY, LORENA               COMERCIAL                 1 MAR 20   31725099A   
	//    SANZ CASTILLA, GENOVEVA MARIA                               2 ENE 20   44963245Q   
	//"(?<name>.+?)\\s{2,}(?<job>.+?)?\\s{2,}(?<nummatric>.*?)?\\s*(?<old>\\d{1,2}\\s+\\w+\\s+\\d{1,})\\s{2,}(?<nif>(\\d|\\w)\\d{8}\\w)\\s*"
	public static final Pattern WORKER =
	Pattern.compile("\\s*(?<name>.+?)\\s{3,}(?<job>.+?)?\\s{2,}(?<old>\\d+\\s\\w{3}\\s\\d+)(?<nif>.+?)\\s*"
	, Pattern.CASE_INSENSITIVE);
	//Nº AFILIACION. S.S. TARIFA COD.CT SECCION NRO. PERIODO TOT. DIAS
	public static final Pattern SS_INFO_HEADER =
	Pattern.compile("\\s*Nº AFILIACION\\.\\s*S\\.S\\.\\s*TARIFA\\s*COD\\.CT\\s*seccion\\s*NRO\\.\\s*PERIODO\\s*TOT\\.\\s*DIAS\\s*",
	Pattern.CASE_INSENSITIVE);
	//48/10454983-40     7  200              4  MENS 01 ENE 20 a 31 ENE 20          30  
	//04/10543848-78     7  189  0102        2  MENS 01 ENE 20 a 31 ENE 20          30  
	//14/10284281-20                       191  MENS 01 ENE 20 a 31 ENE 20          30  
	public static final Pattern SS_INFO =
	//Pattern.compile("\\s*(?<affnum>\\d+/\\d+-\\d+)\\s*(?<tarifa>\\d*)\\s*(?<codct>\\d*)\\s*(?<section>.*?)?\\s*(?<nro>\\d*)\\s*(?<period>.*\\s*a\\s*.*?)?\\s{2,}(?<days>\\d*)\\s*"
			Pattern.compile("\\s*(?<affnum>\\d+/\\d+-\\d+)\\s{4,5}(?<tarifa>\\d{1,2})?\\s*(?<codct>\\d{1,3})?\\s*((?<section>[^\\s]+?)\\s+)?(?<nro>\\d+)?\\s{2}(?<period>.*\\s*a\\s*.*?)?\\s{2,}(?<days>\\d*)\\s*"
	, Pattern.CASE_INSENSITIVE);
	//CUANTIA PRECIO CONCEPTO DEVENGOS DEDUCCIONES
	public static final Pattern CONCEPT_HEADER=
	Pattern.compile("\\s*CUANTIA\\s*PRECIO\\s*CONCEPTO\\s*DEVENGOS\\s*DEDUCCIONES\\s*"
	, Pattern.CASE_INSENSITIVE);
	//30,00     26,741     1  *Salario Base                               802,24                   
	public static final Pattern CONCEPT =
	Pattern.compile("\\s*(?<cuantia>(\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)?\\s*(?<price>(\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)?\\s*(?<unknownnumber>\\d+)?\\s*(?<concept>\\*?.+?)\\s{1,2}(?<tipo>\\d+[,]\\d*)?\\s*(?<devengos>\\d+[,]\\d*)?\\s{19}?(?<deducciones>(\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)?\\s*$"
	, Pattern.CASE_INSENSITIVE);
	//                                FINIQUITO ....................:                                     
	public static final Pattern SETTLEMENT =
	Pattern.compile("\\s*FINIQUITO\\s*\\.+:\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	public static final Pattern ATRASOS_CONV =
			Pattern.compile("\\s*PAGA\\s*ATRASOS\\s*DE\\s*CONVENIO\\s*\\.+:\\s*"
			, Pattern.CASE_INSENSITIVE);
	//REM. TOTAL P.P.EXTRAS BASE S.S. BASE A.T. Y DES. BASE I.R.P.F. T. DEVENGADO T.  A DEDUCIR
	public static final Pattern TOTAL_HEADER =
	Pattern.compile("REM\\.\\s*TOTAL\\s*P\\.P\\.EXTRAS\\s*BASE\\s*S\\.S\\.\\s*BASE\\s*A\\.T\\.\\s*Y\\s*DES\\.\\s*BASE\\s*I\\.R\\.P\\.F\\.\\s*T\\.\\s*DEVENGADO\\s*T\\.\\s*A\\s*DEDUCIR"
	, Pattern.CASE_INSENSITIVE);
	//1.260,31                  1.260,31        1.260,31     1.260,31    1.260,31        161,61     
	//1.175,00       166,66     1.341,66        1.341,66     1.175,00    1.175,00        306,26      
    //966,24                    966,24          966,24       966,24      966,24         81,16     
	//1.108,33                  1.108,33        1.108,33     1.108,33    1.108,33         93,10     
	//1.584,97                  1.584,97        1.584,97     1.584,97    1.584,97        284,97     
	//4.500,00                                               4.500,00    4.500,00      1.125,00     
	public static final Pattern TOTAL =
			Pattern.compile("(((\\d[\\d\\s]*\\.)?[\\s\\d]{1,4}[,]\\d+)|\\s{12,15})"
			, Pattern.CASE_INSENSITIVE);
	
	public static final Pattern TOTAL_ROW =
	Pattern.compile("\\s*(((\\d+\\.)?\\d+[,]\\d+)|\\s{9,10})+\\s*"
	, Pattern.CASE_INSENSITIVE);
	//* Percepciones Salariales  sujetas a Cot. S.S. - Percepciones no Salariales excluídas Cot. S.S.
	public static final Pattern LEYENDA =
	Pattern.compile("\\s*\\*\\s*Percepciones\\s*Salariales\\s*sujetas\\s*a\\s*Cot\\.\\s*S\\.S\\.\\s*-\\s*Percepciones\\s*no\\s*Salariales\\s*excluídas\\s*Cot\\.\\s*S\\.S\\.\\s*$"
	, Pattern.CASE_INSENSITIVE);
	//FECHA                                                        SELLO EMPRESA RECIBI
	public static final Pattern FECHASELLO =
	Pattern.compile("\\s*FECHA\\s*SELLO\\s*EMPRESA\\s*RECIBI\\S*"
	, Pattern.CASE_INSENSITIVE);
	//31 ENERO      2020                                                              
	public static final Pattern DATE =
	Pattern.compile("\\s*(?<day>\\d{1,2})\\s*(?<month>\\w+)\\s*(?<year>\\d+)\\s*",
	Pattern.CASE_INSENSITIVE);
	//ARMIÑON                                                                         
	public static final Pattern PLACE =
	Pattern.compile("\\s{2,}?(?<place>.+)\\s{2,}?"
	, Pattern.CASE_INSENSITIVE);
	//LIQUIDO A PERCIBIR
	public static final Pattern TOTAL_LIQUID_HEADER =
	Pattern.compile("\\s*LIQUIDO\\s*A\\s*PERCIBIR\\s*"
	, Pattern.CASE_INSENSITIVE);
	//                                         1.098,70        
	public static final Pattern TOTAL_LIQUID =
	Pattern.compile("\\s*(?<liquid>[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	//IBAN:                                                                                                     
	public static final Pattern IBAN =
	Pattern.compile("\\s*IBAN:\\s*(?<iban>[^\\s]+.*[^\\s]*)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	//SWIFT/BIC:                                                                                                     COSTE EMPRESA:        1.656,05   
	public static final Pattern COSTE_EMPRESA =
	Pattern.compile("\\s*SWIFT/BIC:\\s*(?<swift>[^\\s]+.*[^\\s])?\\s*C\\s*O\\s*STE\\s*EMPRESA:\\s*(?<cost>[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	//DETERMINACIÓN DE LAS B. DE COTIZACIÓN A LA S.S. Y CONCEPTOS DE RECAUDACIÓN CONJUNTA Y APORTACIÓN DE LA EMPRESA
	public static final Pattern APPORT_HEADER_TOP =
	Pattern.compile("\\s*DETERMINACIÓN\\s*DE\\s*LAS\\s*B\\.\\s*DE\\s*COTIZACIÓN\\s*A\\s*LA\\s*S\\.S\\.\\s*Y\\s*CONCEPTOS\\s*DE\\s*RECAUDACIÓN\\s*CONJUNTA\\s*Y\\s*APORTACIÓN\\s*DE\\s*LA\\s*EMPRESA\\s*"
	, Pattern.CASE_INSENSITIVE);
	//CONCEPTO BASE TIPO APORTACIÓN EMPRESARIAL
	public static final Pattern APPORT_HEADER_BOTTOM =
	Pattern.compile("\\s*CONCEPTO\\s*BASE\\s*TIPO\\s*APORTACIÓN\\s*EMPRESARIAL\\s*"
	, Pattern.CASE_INSENSITIVE);
	//1. Contingencias comunes.................................................... .. .. ..    1.260,31          23,60           297,43          
	//AT y EP................................. .. .. ..    1.260,31           1,50            18,90           
	//2. Contingencias profe- Desempleo............................ .. .. ..    1.260,31           5,50            69,32          
	//sionales y conceptos de
	//recaudación conjunta Formación Profesional..........
	//.. ... .    1.260,31           0,60             7,56           
	//Fondo Garantía Salarial......... .. .. .    1.260,31           0,20             2,52           
	//3. Cotización adicional horas extraordinarias........................ .. .. .                                                       
	
	
	//1. Contingencias comunes.................................................... .. .. ..    1.108,33          23,60           261,57          
	//AT y EP................................. .. .. ..    1.108,33           1,50            16,63           
	//2. Contingencias profe-    Desempleo............................ .. .. ..    1.108,33           6,70            74,26          
	//sionales y conceptos de
	//Formación Profesional.......... .. ... .    1.108,33           0,60             6,65           recaudación conjunta
	//Fondo Garantía Salarial......... .. .. .    1.108,33           0,20             2,22           
	//3. Cotización adicional horas extraordinarias........................ .. .. .                
	
	public static final Pattern APPORT =
	Pattern.compile("\\s*(?:.+?)(?<concept>[\\w\\d\\s])[\\.\\s]{2,}\\s*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	public static final Pattern APPORT_CC =
	Pattern.compile("\\s*1\\.\\s*Contingencias\\s*comunes[\\.\\s]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	public static final Pattern APPORT_AT_EP =
	Pattern.compile("\\s*AT\\s*y\\s*EP[\\.\\s]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	public static final Pattern APPORT_UNEMPLOYMENT =
	Pattern.compile("\\s*2\\.\\s*Contingencias\\s*profe-\\s*Desempleo[\\.\\s]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	//Formación Profesional.......... .. ... .    1.108,33           0,60             6,65           recaudación conjunta
	public static final Pattern APPORT_FP =
	Pattern.compile("\\s*Formación\\s*Profesional[\\.\\s]{2,}\\s*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*(recaudación\\s*conjunta\\s*)?"
	, Pattern.CASE_INSENSITIVE);
	//Fondo Garantía Salarial......... .. .. .    1.108,33           0,20             2,22           
	public static final Pattern APPORT_FOGASA =
	Pattern.compile("\\s*Fondo\\s*Garantía\\s*Salarial[\\s\\.]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
	
	public static final Pattern APPORT_EXTRA_H =
	Pattern.compile("\\s*3\\.\\s*Cotización\\s*adicional\\s*horas\\s*extraordinarias[\\s\\.]*(?<base>\\d[\\d\\.,]+)?\\s*(?<type>\\d[\\d\\.,]+)?\\s*(?<apport>\\d[\\d\\.,]+)?\\s*"
	, Pattern.CASE_INSENSITIVE);
}
