package com.esferalia.aon.in.payroll.pdf.template.regex;

import java.util.regex.Pattern;

public class AplifisaRegex {
//	EMPRESA    TRABAJADOR
	public final static Pattern ENTERPRISE_EMPLOYEE = Pattern.compile("\\s*EMPRESA\\s*TRABAJADOR\\s*",
			Pattern.CASE_INSENSITIVE);

//	TABIKET SOCIEDAD LIMITADA    RAHOUI , HICHAM
	public final static Pattern ENT_EMP_NAMES = Pattern.compile("(?<enterprise>.+?)\\s{2,}(?<employee>[^,]+\\s*,.+)",
			Pattern.CASE_INSENSITIVE);

//	Domicilio :    PZ JESÚS DE MEDINACELLI, 6   22    N.I.F.:    X7379673P
//	Domicilio :    CL POETA MAS Y ROS, 104       N.I.F.:    044515153X
//	Domicilio :    CL TOMAS BRETON, 9       N.I.F.:    0X9265678J
	public final static Pattern ENTHOME_NIF = Pattern.compile(
			"\\s*Domicilio\\s*:\\s*(?<enthome>.+?)\\s*N\\.I\\.F\\.:\\s*(?<nif>\\d?(\\w|\\d)\\d{7,8}\\w)\\s*",
			Pattern.CASE_INSENSITIVE);

//	C.P.: 46024 VALENCIA    Número de afiliación a la Seguridad Social:    44-10043688-89
	public final static Pattern ENTCITY_NSS = Pattern.compile(
			"(?:\\s*C\\.P\\.:\\s*\\d+)?\\s*(?<entcity>.+?)\\s*Número\\s*de\\s*afiliación\\s*a\\s*la\\s*Seguridad\\s*Social:\\s*(?<nss>\\d{2}-\\d{8}-\\d{2})",
			Pattern.CASE_INSENSITIVE);

//	C.I.F. :    B40589533    Cat. Profesional :    OFICIAL 1ª
//	C.I.F. :    B98812092    Cat. Profesional :
	public final static Pattern CIF_CATEGORY = Pattern.compile(
			"\\s*C\\.I\\.F\\.\\s*:\\s*(?<cif>[^\\s]+)\\s*Cat\\.\\s*Profesional\\s*:\\s*(?<category>.+?)?\\s*$",
			Pattern.CASE_INSENSITIVE);

//	Cta. cotización S.S. :    46-1515177-41    Grupo de cotización:    8    Fecha Antigüedad :    14/09/2020
	public static final Pattern CCC_QUOTEGROUP_SENIORITY = Pattern.compile(
			"\\s*Cta\\.\\s*cotización\\s*S\\.S\\.\\s*:\\s*(?<ccc>\\d{2}-\\d{7}-(\\d{2})?)\\s*Grupo\\s*de\\s*cotización:\\s*(?<quotegroup>\\d+)?\\s*Fecha\\s*Antigüedad\\s*:\\s*(?<seniority>\\d{1,2}/\\d{1,2}/\\d{2,4})\\s*",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern DATE_FORMAT = Pattern.compile("\\s*(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*",
			Pattern.CASE_INSENSITIVE);

//	Periodo de liquidación :      del    01    de    Diciembre    al    02    de    Diciembre    de    2020    Total días / horas    2
//	Periodo de liquidación :      PAGA EXTRAORDINARIA DE Diciembre 2020    Total días / horas    184
//	Periodo de liquidación :      LIQUIDACIÓN DE VACACIONES Y PAGAS EXTRAORDINARIAS

	// (?<liqperiod>del\s*(?<from>\d+\s*de\s*\w+)\s*al\s*(?<to>\d+\s*de\s*\w+)\s*de|(?<salarytype>.+?)(\s*de\s*(?<month>\w+)\)?)\s*(?<year>\d+)?\\s*(Total\s*días\s*/\s*horas\s*(?<timeunits>\d+)?)?
	// \s*Periodo\s*de\s*liquidación\s*:\s*(?<liqperiod>del\s*(?<from>\d+\s*de\s*\w+)\s*al\s*(?<to>\d+\s*de\s*\w+)\s*de|(?<salarytype>.+?))(\s*de\s*(?<month>\w+)\)?\s*(?<year>\d+)?\s*(?<year>\d+)?\\s*(Total\s*días\s*/\s*horas\s*(?<timeunits>\d+)?)?
	public static final Pattern LIQPERIOD_TIMEUNITS = Pattern.compile(
			"\\s*Periodo\\s*de\\s*liquidación\\s*:\\s*(?<liqperiod>del\\s*(?<from>\\d+\\s*de\\s*\\w+)\\s*al\\s*(?<to>\\d+\\s*de\\s*\\w+)\\s*de|(?<salarytype>.+?))(\\s*de\\s*(?<month>\\w{4,10}))?\\s*(?<year>\\d+)?\\s*(Total\\s*días\\s*/\\s*horas\\s*(?<timeunits>\\d+)?)?",
			Pattern.CASE_INSENSITIVE);

//	I. DEVENGOS    TOTALES
	public static final Pattern PAYMENTS_HEADER = Pattern.compile("\\s*I\\.\\s*DEVENGOS\\s*TOTALES\\s*",
			Pattern.CASE_INSENSITIVE);

//	I. Percepciones salariales    Devengo
	public static final Pattern SALARY_PAYMENT_HEADER = Pattern
			.compile("\\s*I\\.\\s*Percepciones\\s*salariales\\s*Devengo\\s*", Pattern.CASE_INSENSITIVE);

//	SALARIO BASE    57,98
//	PLUS DE TRANSPORTE    6,56
//	COMPL. ACTIVIDAD    34,66
	public static final Pattern PAYMENT = Pattern.compile("(?<concept>.+?)\\s*(?<payment>\\d+(\\.\\d+)?,\\d+)\\s*",
			Pattern.CASE_INSENSITIVE);

//	2. Percepciones no salariales
	public static final Pattern NON_SALARY_PAYMENT_HEADER = Pattern
			.compile("\\s*2\\.\\s*Percepciones\\s*no\\s*salariales\\s*", Pattern.CASE_INSENSITIVE);

//	A. TOTAL DEVENGADO ...........................................................................    99,20
//	A. TOTAL DEVENGADO ...........................................................................
	public static final Pattern TOTAL_PAYMENT = Pattern.compile(
			"\\s*A\\.\\s*TOTAL\\s*DEVENGADO\\s*\\.{2,}\\s*(?<payment>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	II. DEDUCCIONES
	public static final Pattern DEDUCTIONS_HEADER = Pattern.compile("\\s*II\\.\\s*DEDUCCIONES\\s*",
			Pattern.CASE_INSENSITIVE);

//	I. Aportación del trabajador a las cotizaciones a la Seguridad Social y conceptos de recaudación conjunta
	public static final Pattern SS_APPORTS_HEADER = Pattern.compile(
			"\\s*I\\.\\s*Aportación\\s*del\\s*trabajador\\s*a\\s*las\\s*cotizaciones\\s*a\\s*la\\s*Seguridad\\s*Social\\s*y\\s*conceptos\\s*de\\s*recaudación\\s*conjunta\\s*",
			Pattern.CASE_INSENSITIVE);

//	Contingencias Comunes    115,50    4,7000    %    5,43
//	Contingencias Comunes    1.050,00    %    8,49
//	Contingencias Comunes    4,7000    %
	public static final Pattern COMMON_CONTINGENCY = Pattern.compile(
			"\\s*Contingencias\\s*Comunes\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Desempleo    115,50    1,60    %    1,85
	public static final Pattern UNEMPLOYMENT = Pattern.compile(
			"\\s*Desempleo\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Formación Profesional    115,50    0,10    %    0,12
	public static final Pattern FORMACION_PROFESIONAL = Pattern.compile(
			"\\s*Formación\\s*Profesional\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Horas Extraordinarias
	public static final Pattern EXTRA_HOURS = Pattern.compile("\\s*Horas\\s*Extraordinarias\\s*",
			Pattern.CASE_INSENSITIVE);

//	Fuerza mayor o estructurales    2,00    %
	public static final Pattern CHUCK_NORRIS_HOURS = Pattern.compile(
			"\\s*Fuerza\\s*mayor\\s*o\\s*estructurales\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	No estructurales    4,70    %
	public static final Pattern NON_STRUCTURAL_HOURS = Pattern.compile(
			"\\s*No\\s*estructurales\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)\\s{0,5}%|%)?\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	TOTAL APORTACIONES ....................    7,40
	public static final Pattern TOTAL_APPORT = Pattern.compile(
			"\\s*TOTAL\\s*APORTACIONES\\s*\\.{2,}\\s*(?<apport>\\d+(\\.\\d+)?,\\d+)?\\s*", Pattern.CASE_INSENSITIVE);

//	2. Impuesto sobre la renta de las personas físicas    99,20    2,00    %    1,98
//	2. Impuesto sobre la renta de las personas físicas    300,21    %
	public static final Pattern IRPF = Pattern.compile(
			"\\s*2\\.\\s*Impuesto\\s*sobre\\s*la\\s*renta\\s*de\\s*las\\s*personas\\s*físicas\\s*(((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	3. Anticipos
//	3. Anticipos    -21,11
	public static final Pattern ADVANCED_PAYMENTS = Pattern
			.compile("\\s*3\\.\\s*Anticipos\\s*-?(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*", Pattern.CASE_INSENSITIVE);
//	4. Valor de los productos recibidos en especie
	public static final Pattern IN_KIND = Pattern.compile(
			"\\s*4\\.\\s*Valor\\s*de\\s*los\\s*productos\\s*recibidos\\s*en\\s*especie\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	5. Otras deducciones
	public static final Pattern OTHER = Pattern.compile("\\s*5\\.\\s*Otras\\s*deducciones\\s*",
			Pattern.CASE_INSENSITIVE);

//	EMBARGO DE SALARIOS    102,00
	public static final Pattern OTHER_DEDUCTION = Pattern
			.compile("\\s*(?<concept>.+?)\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?\\s*", Pattern.CASE_INSENSITIVE);

//	B. TOTAL A DEDUCIR ..............................................................................    9,38
	public static final Pattern TOTAL_DEDUCTION = Pattern.compile(
			"\\s*B\\.\\s*TOTAL\\s*A\\s*DEDUCIR\\s*\\.{2,}\\s*(?<deduction>\\d+(\\.\\d+)?,\\d+)?",
			Pattern.CASE_INSENSITIVE);

//	LIQUIDO TOTAL A PERCIBIR (A - B) ...........................................................................    89,82
	public static final Pattern TOTAL_LIQUID = Pattern.compile(
			"\\s*LIQUIDO\\s*TOTAL\\s*A\\s*PERCIBIR\\s*\\(A\\s*-\\s*B\\)\\s*\\.{2,}\\s*(?<totalliquid>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	Firma y sello de la Empresa    VALENCIA    ,    02    de    Diciembre    de    2020RECIBI,
//	Firma y sello de la Empresa    RIBAMONTÁN AL MONTE    ,    31    de    Enero    de    2020
	public static final Pattern ISSUE_DATE = Pattern.compile(
			"\\s*Firma\\s*y\\s*sello\\s*de\\s*la\\s*Empresa\\s*(?<city>.+?)\\s*,\\s*(?<issuedate>(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*de\\s*(?<year>\\d+))(?:\\s*RECIBI,)?\\s*",
			Pattern.CASE_INSENSITIVE);

	public static final Pattern DATE_FORMAT_YEAR = Pattern.compile(
			"\\s*(?<day>\\d{1,2})\\s*de\\s*(?<month>\\w+)\\s*de\\s*(?<year>\\d+)\\s*", Pattern.CASE_INSENSITIVE);

//	Determinación de las Bases de Cotización a la Seg.Social y conceptos de recaudación conjunta de la base sujeta a ret. del IRPF
	public static final Pattern COSTS_HEADER = Pattern.compile(
			"\\s*Determinación\\s*de\\s*las\\s*Bases\\s*de\\s*Cotización\\s*a\\s*la\\s*Seg\\.Social\\s*y\\s*conceptos\\s*de\\s*recaudación\\s*conjunta\\s*de\\s*la\\s*base\\s*sujeta\\s*a\\s*ret\\.\\s*del\\s*IRPF\\s*",
			Pattern.CASE_INSENSITIVE);

//	1. Contingencias comunes    Base    Tipo    Aportación Importe remuneración mensual ...................    99,20    Empresarial
//	1. Contingencias comunes    Base    Tipo    Aportación 
	public static final Pattern REMUNERATION = Pattern.compile(
			"\\s*1\\.\\s*Contingencias\\s*comunes\\s*Base\\s*Tipo\\s*Aportación\\s*(Importe\\s*remuneración\\s*mensual\\s*\\.{2,}\\s*-?(?<remuneration>\\d+(\\.\\d+)*,\\d+)?\\s*Empresarial\\s*)?",
			Pattern.CASE_INSENSITIVE);

//	Importe remuneración mensual ...................    Empresarial
	public static final Pattern REMUNERATION_2 = Pattern.compile(
			"Importe\\s*remuneración\\s*mensual\\s*\\.{2,}\\s*(?<remuneration>\\d+(\\.\\d+)*,\\d+)?\\s*Empresarial\\s*",
			Pattern.CASE_INSENSITIVE);

//	Importe prorrata pagas extraordinarias ......    16,30
	public static final Pattern PRORATION_BASE = Pattern.compile(
			"\\s*Importe\\s*prorrata\\s*pagas\\s*extraordinarias\\s*\\.{2,}\\s*(?<proextbase>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	TOTAL .................................    115,50    23,60    %    27,26
	public static final Pattern CGC_E = Pattern.compile(
			"\\s*TOTAL\\s*\\.{2,}\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<cost>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);
	// ((?<base>\d+(\.\d+)?,\d+)?\s*(?<percent>\d+,\d+)\s{0,5}%|%)?\s*(?<cost>\d+(\.\d+)?,\d+)?\s*

//	AT y EP ......................................    6,70    %    7,74
	public static final Pattern AT_EP = Pattern.compile(
			"\\s*AT\\s*y\\s*EP\\s*\\.{2,}\\s*(?<percent>\\d+,\\d+)?\\s*%?\\s*(?<cost>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	2. Contingencias profesionales    Desempleo ..................................    115,50    6,70    %    7,74
//	2. Contingencias profesionales    Desempleo ..................................    1.050,00    %    57,75
	public static final Pattern UNEMPLOYMENT_E = Pattern.compile(
			"\\s*2\\.\\s*Contingencias\\s*profesionales\\s*Desempleo\\s*\\.{2,}\\s*((?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<percent>\\d+,\\d+)?\\s{0,5}%|%)?\\s*(?<cost>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	y conceptos de recaudación conjunta    Formación Profesional ................    0,60    %    0,69Fondo de Garantía Salarial .........    0,20    %    0,23
//	y conceptos de recaudación conjunta    Formación Profesional ................    0,60    %	
	public static final Pattern FP_FOGASA_E = Pattern.compile(
			"\\s*y\\s*conceptos\\s*de\\s*recaudación\\s*conjunta\\s*Formación\\s*Profesional\\s*\\.{2,}\\s*(?<percentfp>\\d+,\\d+)?\\s*%?\\s*(?<costfp>\\d+(\\.\\d+)?,\\d+)?\\s*(Fondo\\s*de\\s*Garantía\\s*Salarial\\s*\\.{2,}\\s*(?<percentfogasa>\\d+,\\d+)?\\s*%?\\s*(?<costfogasa>\\d+(\\.\\d+)?,\\d+)?\\s*)?",
			Pattern.CASE_INSENSITIVE);

//	Fondo de Garantía Salarial .........    0,20    %
	public static final Pattern FP_FOGASA_E_2 = Pattern.compile(
			"\\s*Fondo\\s*de\\s*Garantía\\s*Salarial\\s*\\.{2,}\\s*(?<percentfogasa>\\d+,\\d+)?\\s*%?\\s*(?<costfogasa>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	3. Cotización adicional horas extraordinarias .........................................................
//	3. Cotización adicional horas extraordinarias .........................................................    33,44    7,89
	public static final Pattern EXTRA_HOURS_E = Pattern.compile(
			"\\s*3\\.\\s*Cotización\\s*adicional\\s*horas\\s*extraordinarias\\s*\\.{2,}\\s*(?<base>\\d+(\\.\\d+)?,\\d+)?\\s*(?<cost>\\d+(\\.\\d+)?,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	4. Base sujeta a retención del I.R.P.F. .....................................................................    99,20
	public static final Pattern IRPF_E = Pattern.compile(
			"\\s*4\\.\\s*Base\\s*sujeta\\s*a\\s*retención\\s*del\\s*I\\.R\\.P\\.F\\.\\s*\\.{2,}\\s*(?<base>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);

//	5. Coste total mensual (TOTAL DEVENGADO+SEG.SOC. EMPRESA) ....................    142,86
	public static final Pattern TOTAL_ENTERPRISE = Pattern.compile(
			"\\s*5\\.\\s*Coste\\s*total\\s*mensual\\s*\\(TOTAL\\s*DEVENGADO\\+SEG\\.SOC\\.\\s*EMPRESA\\)\\s*\\.{2,}\\s*(?<total>\\d+(\\.\\d+)*,\\d+)?\\s*",
			Pattern.CASE_INSENSITIVE);
}
