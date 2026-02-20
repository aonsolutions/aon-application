package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod421Key implements IFiscalModelKey  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 X00("421-X00",false,null,"Confecci\u00F3n manual")
	,X01("421-X01",false,null,"C\u00E1lculo por diferencia deshabilitado")
	,X02("421-X02",false,null,"C\u00F3digo de municipio")                                                                                // Código de municipio
	,X03("421-X03",false,null,"Concurso de acreedores")
	
//	,CM_002("303-CM002",false,null,"Sujeto pasivo inscrito en el Registro de devoluci\u00F3n mensual")
//	,CM_003("303-CM003",false,null,"Porcentaje de prorrata.")
//	,CM_004("303-CM004",false,null,"Tipo de declaraci\u00F3n")
//	,CM_005("303-CM005",false,null,"R\u00E9gimen por defecto")
//	,CM_006("303-CM006",false,null,"Tipo de prorrata (E/G).")
//	,CM_007("303-CM007",false,null,"Porcentaje de prorrata antes de la regularizaci\u00F3n.")
//	,CM_008("303-CM008",false,null,"Aplicar prorrata")

	// Casillas necesarias para el calculo de la prorrata definitiva.
//	,CM_070("303-CM070",false,null,"Importe anual de entregas de bienes y prestaciones de servicios que dan derecho a deducci\u00F3n, sin incluir el IVA (incluye operaciones con inversi\u00F3n del sujeto pasivo, exportaciones, y entregas intracomunitarias)")
//	,CM_071("303-CM071",false,null,"Importe anual total de las entregas de bienes y prestaciones de servicios (incluidas las que no dan derecho a deducir)")
//	,CM_072("303-CM072",false,null,"Importe acumulado de las casillas prorrateables. Para calculo de regulariacion.")
//	,CM_073("303-CM073",false,null,"Marca para indicar que es un borrador.")
//	,CM_074("303-CM074",false,null,"Importe acumulado de las casillas no prorrateables. Para calculo de regulariacion.")
	
	// 	----------------------------------------------------------------------------------  
	// 	--------------------------------------------------------------------  AEAT -------
	// 	----------------------------------------------------------------------------------
//	,CT_A02("303-CTA02",false,null,"Sujeto pasivo que tributa exclusivamente en r\u00E9gimen simplificado")
//	,CT_A03("303-CTA03",false,null,"Autoliquidaci\u00F3n conjunta")
//	,CT_A05("303-CTA05",false,null,"Fecha en que se dict\u00F3 el auto de declaraci\u00F3n de concurso")
//	,CT_A06("303-CTA06",false,null,"Auto de declaraci\u00F3n de concurso dictado en el per\u00EDodo (tipo de autoliquidaci\u00F3n)")
//	,CT_A07("303-CTA07",false,null,"Sujeto pasivo acogido al r\u00E9gimen especial del criterio de Caja (art. 163 undecies LIVA)")
//	,CT_A08("303-CTA08",false,null,"Sujeto pasivo destinatario de operaciones acogidas al r\u00E9gimen especial del criterio de caja")
//	,CT_A09("303-CTA09",false,null,"Opci\u00F3n por la aplicaci\u00F3n de la prorrata especial (art\u00EDculo 103.Dos.1\u00BA LIVA)")
//	,CT_A10("303-CTA10",false,null,"Revocaci\u00F3n de la opci\u00F3n por la aplicaci\u00F3n de la prorrata especial (art\u00EDculo 103.Dos.1\u00BA LIVA)")
//	,CT_A11("303-CTA11",false,null,"Sujeto pasivo con volumen anual de operaciones distinto de cero (art. 121 LIVA)")
//	,CT_A12("303-CTA12",false,null,"Sujeto pasivo que tributa exclusivamente a una Administraci\u00F3n tributaria Foral con IVA a la importaci\u00F3n liquidado por la Aduana pendiente de ingreso")
//	,CT_A13("303-CTA13",false,null,"Sujeto pasivo acogido voluntariamente al SII")
//	,CT_A14("303-CTA14",false,null,"Sujeto pasivo exonerado de la Declaraci\u00F3n-resumen anual del IVA, modelo 390")
	
//	,CT_R00("303-CTR00",false,null,"Como consecuencia de la presentaci\u00F3n de la autoliquidaci\u00F3n rectificativa solicito dar de baja/modificar la domiciliaci\u00F3n efectuada")
//	,CT_R01("303-CTR01",false,null,"Motivo de la rectificaci\u00F3n: Rectificaciones (excepto incluidas en el motivo siguiente)")
//	,CT_R02("303-CTR02",false,null,"Motivo de la rectificaci\u00F3n: Discrepancia criterio administrativo")
	
//	,CT_C150("303-CTC150",true ,"150","R\u00E9gimen general - Base imponible")
//	,CT_C151("303-CTC151",false,"151","R\u00E9gimen general - Tipo %")
//	,CT_C152("303-CTC152",true ,"152","R\u00E9gimen general - Cuota")
//	,CT_C165("303-CTC165",true ,"165",CT_C150.getDescription())
//	,CT_C166("303-CTC166",false,"166",CT_C151.getDescription())
//	,CT_C167("303-CTC167",true ,"167",CT_C152.getDescription())
//	,CT_C01 ("303-CTC01" ,true ,"1"  ,CT_C150.getDescription())
//	,CT_C02 ("303-CTC02" ,false,"2"  ,CT_C151.getDescription())
//	,CT_C03 ("303-CTC03" ,true ,"3"  ,CT_C152.getDescription())
//	,CT_C153("303-CTC153",true ,"153",CT_C150.getDescription())
//	,CT_C154("303-CTC154",false,"154",CT_C151.getDescription())
//	,CT_C155("303-CTC155",true ,"155",CT_C151.getDescription())
//	,CT_C04 ("303-CTC04" ,true ,"4"  ,CT_C150.getDescription())
//	,CT_C05 ("303-CTC05" ,false,"5"  ,CT_C151.getDescription())
//	,CT_C06 ("303-CTC06" ,true ,"6"  ,CT_C151.getDescription())
//	,CT_C07 ("303-CTC07" ,true ,"7"  ,CT_C150.getDescription())
//	,CT_C08 ("303-CTC08" ,false,"8"  ,CT_C151.getDescription())
//	,CT_C09 ("303-CTC09" ,true ,"9"  ,CT_C151.getDescription())
//	
//	,CT_C10("303-CTC10",true ,"10","Adquisiciones intracomunitarias de bienes y servicios - Base imponible")
//	,CT_C11("303-CTC11",true ,"11","Adquisiciones intracomunitarias de bienes y servicios - Cuota")
//	,CT_C12("303-CTC12",true ,"12","Otras operaciones con inversi\u00F3n del sujeto pasivo (excepto. adq. intracom) - Base imponible")
//	,CT_C13("303-CTC13",true ,"13","Otras operaciones con inversi\u00F3n del sujeto pasivo (excepto. adq. intracom) - Cuota")
//	,CT_C14("303-CTC14",true ,"14","Modificaci\u00F3n bases y cuotas - Base imponible")
//	,CT_C15("303-CTC15",true ,"15","Modificaci\u00F3n bases y cuotas - Cuota")
//	
//
//	,CT_C168("303-CTC168",true ,"168","Recargo equivalencia - Base imponible")
//	,CT_C169("303-CTC169",false,"169","Recargo equivalencia - Tipo %")
//	,CT_C170("303-CTC170",true ,"170","Recargo equivalencia - Cuota")
//	,CT_C156("303-CTC156",true ,"156",CT_C168.getDescription())
//	,CT_C157("303-CTC157",false,"157",CT_C169.getDescription())
//	,CT_C158("303-CTC158",true ,"158",CT_C170.getDescription())
//	,CT_C16("303-CTC16",true ,"16",CT_C168.getDescription())
//	,CT_C17("303-CTC17",false,"17",CT_C169.getDescription())
//	,CT_C18("303-CTC18",true ,"18",CT_C170.getDescription())
//	,CT_C19("303-CTC19",true ,"19",CT_C168.getDescription())
//	,CT_C20("303-CTC20",false,"20",CT_C169.getDescription())
//	,CT_C21("303-CTC21",true ,"21",CT_C170.getDescription())
//	,CT_C22("303-CTC22",true ,"22",CT_C168.getDescription())
//	,CT_C23("303-CTC23",false,"23",CT_C169.getDescription())
//	,CT_C24("303-CTC24",true ,"24",CT_C170.getDescription())
//	
//	,CT_C25("303-CTC25",true ,"25","Modificaciones bases y cuotas del recargo de equivalencia - Base imponible")
//	,CT_C26("303-CTC26",true ,"26","Modificaciones bases y cuotas del recargo de equivalencia - Cuota")
//	,CT_C27("303-CTC27",false,"27","Total cuota devengada")
//	
//	,CT_C28("303-CTC28",true ,"28","Por cuotas soportadas en operaciones interiores corrientes - Base")
//	,CT_C29("303-CTC29",true ,"29","Por cuotas soportadas en operaciones interiores corrientes - Cuota")
//	,CT_C30("303-CTC30",true ,"30","Por cuotas soportadas en operaciones interiores con bienes de inversi\u00F3n - Base") 
//	,CT_C31("303-CTC31",true ,"31","Por cuotas soportadas en operaciones interiores con bienes de inversi\u00F3n - Cuota")
//	,CT_C32("303-CTC32",true ,"32","Por cuotas soportadas en las importaciones de bienes corrientes - Base")
//	,CT_C33("303-CTC33",true ,"33","Por cuotas soportadas en las importaciones de bienes corrientes - Cuota")
//	,CT_C34("303-CTC34",true ,"34","Por cuotas soportadas en las importaciones de bienes de inversi\u00F3n - Base")
//	,CT_C35("303-CTC35",true ,"35","Por cuotas soportadas en las importaciones de bienes de inversi\u00F3n - Cuota")
//	,CT_C36("303-CTC36",true ,"36","En adquisiciones intracomunitarias de bienes y servicios corrientes - Base")
//	,CT_C37("303-CTC37",true ,"37","En adquisiciones intracomunitarias de bienes y servicios corrientes - Cuota")
//	,CT_C38("303-CTC38",true ,"38","En adquisiciones intracomunitarias de bienes de inversi\u00F3n - Base")
//	,CT_C39("303-CTC39",true ,"39","En adquisiciones intracomunitarias de bienes de inversi\u00F3n - Cuota")
//	,CT_C40("303-CTC40",true ,"40","Rectificaci\u00F3n de deducciones - Base")
//	,CT_C41("303-CTC41",true ,"41","Rectificaci\u00F3n de deducciones - Cuota")
//	,CT_C42("303-CTC42",true ,"42","Compensaciones R\u00E9gimen Especial A.G. y P. - Cuota")
//	,CT_C43("303-CTC43",false,"43","Regularizaci\u00F3n inversiones - Cuota")
//	,CT_C44("303-CTC44",false,"44","Regularizaci\u00F3n por aplicaci\u00F3n del porcentaje definitivo de prorrata - Cuota")
//	,CT_C45("303-CTC45",false,"45","Total a deducir - Cuota")
//	,CT_C46("303-CTC46",false,"46","Resultado r\u00E9gimen general - Cuota") 

	// ************************************************************** [ACTIVIDAD AGRICOLA 1]
//	,CT_SA11("303-CTAS11",false,null,"C\u00F3digo")
//	,CT_SA1D("303-CTAS1D",false,null,"Descripci\u00F3n")
//	,CT_SA12("303-CTAS12",false,null,"Volumen de ingresos")
//	,CT_SA13("303-CTAS13",false,null,"\u00CDndice de cuota")
//	,CT_SA14("303-CTAS14",false,null,"Cuota devengada")
//	,CT_SA1X("303-CTAS1X",false,null,"DANA 2024")
//	,CT_SA1R("303-CTAS1R",false,null,"Reducción DANA")
//	,CT_SA15("303-CTAS15",false,null,"Porcentaje trimestral (1T/2T/3T)")
//	,CT_SA16("303-CTAS16",false,"A1","Ingreso a cuenta (1T/2T/3T) [A]")
//	,CT_SA1A("303-CTAS1A",false,null,"Cuota soportada (4T)")
//	,CT_SA1B("303-CTAS1B",false,null,"Compensaciones satisfechas a sujetos pasivos en R.E.A.G.P. (4T)")
//	,CT_SA1C("303-CTAS1C",false,null,"1% de la cuota devengada por operaciones corrientes")
//	,CT_SA17("303-CTAS17",false,null,"Cuota soportada operaciones corrientes (4T)")
//	,CT_SA18("303-CTAS18",false,"B1","Cuota anual derivada del regimen simplificado (4T) [B]")
	
	// ************************************************************** [ACTIVIDAD AGRICOLA 2]
//	,CT_SA21("303-CTAS21",false,null,CT_SA11.getDescription())
//	,CT_SA2D("303-CTAS2D",false,null,CT_SA1D.getDescription())
//	,CT_SA22("303-CTAS22",false,null,CT_SA12.getDescription())
//	,CT_SA23("303-CTAS23",false,null,CT_SA13.getDescription())
//	,CT_SA24("303-CTAS24",false,null,CT_SA14.getDescription())
//	,CT_SA2X("303-CTAS2X",false,null,CT_SA1X.getDescription())
//	,CT_SA2R("303-CTAS2R",false,null,CT_SA1R.getDescription())
//	,CT_SA25("303-CTAS25",false,null,CT_SA15.getDescription())
//	,CT_SA26("303-CTAS26",false,"A2",CT_SA16.getDescription())
//	,CT_SA2A("303-CTAS2A",false,null,CT_SA1A.getDescription())
//	,CT_SA2B("303-CTAS2B",false,null,CT_SA1B.getDescription())
//	,CT_SA2C("303-CTAS2C",false,null,"1% de la cuota devengada por operaciones corrientes")
//	,CT_SA27("303-CTAS27",false,null,CT_SA17.getDescription())
//	,CT_SA28("303-CTAS28",false,"B2",CT_SA18.getDescription())
	
	// ************************************************************** [ACTIVIDAD AGRICOLA 3]
//	,CT_SA31("303-CTSA31",false,null,CT_SA11.getDescription())
//	,CT_SA3D("303-CTSA3D",false,null,CT_SA1D.getDescription())
//	,CT_SA32("303-CTSA32",false,null,CT_SA12.getDescription())
//	,CT_SA33("303-CTSA33",false,null,CT_SA13.getDescription())
//	,CT_SA34("303-CTSA34",false,null,CT_SA14.getDescription())
//	,CT_SA3X("303-CTAS3X",false,null,CT_SA1X.getDescription())
//	,CT_SA3R("303-CTAS3R",false,null,CT_SA1R.getDescription())
//	,CT_SA35("303-CTSA35",false,null,CT_SA15.getDescription())
//	,CT_SA36("303-CTSA36",false,"A3",CT_SA16.getDescription())
//	,CT_SA3A("303-CTAS3A",false,null,CT_SA1A.getDescription())
//	,CT_SA3B("303-CTAS3B",false,null,CT_SA1B.getDescription())
//	,CT_SA3C("303-CTAS3C",false,null,"1% de la cuota devengada por operaciones corrientes")
//	,CT_SA37("303-CTSA37",false,null,CT_SA17.getDescription())
//	,CT_SA38("303-CTSA38",false,"B3",CT_SA18.getDescription())
	
	// ************************************************************** [ACTIVIDAD AGRICOLA 4]
//	,CT_SA41("303-CTSA41",false,null,CT_SA11.getDescription())
//	,CT_SA4D("303-CTSA4D",false,null,CT_SA1D.getDescription())
//	,CT_SA42("303-CTSA42",false,null,CT_SA12.getDescription())
//	,CT_SA43("303-CTSA43",false,null,CT_SA13.getDescription())
//	,CT_SA44("303-CTSA44",false,null,CT_SA14.getDescription())
//	,CT_SA4X("303-CTAS4X",false,null,CT_SA1X.getDescription())
//	,CT_SA4R("303-CTAS4R",false,null,CT_SA1R.getDescription())
//	,CT_SA45("303-CTSA45",false,null,CT_SA15.getDescription())
//	,CT_SA46("303-CTSA46",false,"A4",CT_SA16.getDescription())
//	,CT_SA4A("303-CTAS4A",false,null,CT_SA1A.getDescription())
//	,CT_SA4B("303-CTAS4B",false,null,CT_SA1B.getDescription())
//	,CT_SA4C("303-CTAS4C",false,null,"1% de la cuota devengada por operaciones corrientes")
//	,CT_SA47("303-CTSA47",false,null,CT_SA17.getDescription())
//	,CT_SA48("303-CTSA48",false,"B4",CT_SA18.getDescription())

	// ACTIVIDAD 1
	
	,A1EP1("421-A1EP1",false,null,"Epigrafe IAE")
	,A1EP2("421-A1EP2",false,null,"Epigrafe IAE - Indicador auxiliar de actividad en el caso de ep\u00EDgrafes 691.9 y 722")
	,A1EPD("421-A1EPD",false,null,"Epigrafe IAE - Descripci\u00F3n")
	,A1TEM("421-A1TEM",false,null,"Actividad de Temporada: N\u00BA D\u00EDas en los que se ejerci\u00F3 la actividad en el a\u00F1o anterior")	
	,A1DIA("421-A1DIA",false,null,"N\u00FAmero de d\u00EDas de ejercicio de la actividad en el trimestre")
//	,A1D2("421-A1D2",false,null,"Actividad de temporada: N\u00BA D\u00EDas de ejercicio (4T)") // FALTA - ESTE NO SE SI SE NECESITA PUES CREO QUE SE USA EL MISMO CAMPO DE NUMERO DE DIAS PARA LOS TRIMESTRES Y PARA EL ULTIMO, EL ULTIMO LLEVA EL TOTAL DE DIAS DE LA ACTIVIDAD EN EL EJERCICIO Y EN EL TRIMESTRE EL NUMERO DE DIAS DE LA ACTIVIDAD EN EL TRIMESTRE
//	,CT_S1X3("303-CTS1X3",false,null,"N\u00FAmero de empleados al inicio del ejercicio (o al inicio de la actividad) (1T/2T/3T)")
//	,CT_S1Y2("303-CTS1Y2",false,null,"N\u00FA m\u00E1ximo de asalariados que han trabajado simult\u00E1neamente durante el ejercicio (4T)")
//	,CT_S1X4("303-CTS1X4",false,null,"Si realiza la actividad en LORCA")
//	,CT_S1X5("303-CTS1X5",false,null,"Reducci\u00F3n extraordinaria de la cuota anual devengada por operaciones corrientes (Reducci\u00F3n extraordinaria por covid-19, art. 9 RD-Ley 35/2020)")
//	,CT_S1X6("303-CTS1X6",false,null,"Realiza la actividad en municipios afectados por la DANA 2024")
	
	,A1M1D("421-A1M1D",false,null,"Descripci\u00F3n")					// Descripción
	,A1M1I("421-A1M1I",false,null,"Unidades") 							// Unidades 
	,A1M1U("421-A1M1U",false,null,"Unidad") 							// Unidad (texto)
	,A1M1F("421-A1M1F",false,null,"Cuota devengada anual por unidad")	// Cuota devengada anual por unidad
	,A1M1R("421-A1M1R",false,null,"Resultado")		 					// Resultado (Unidades * Cuota devengada anual por unidad)
	
	,A1M2D("421-A1M2D",false,null, A1M1D.getDescription())	
	,A1M2I("421-A1M2I",false,null, A1M1I.getDescription()) 	
	,A1M2U("421-A1M2U",false,null, A1M1U.getDescription()) 	
	,A1M2F("421-A1M2F",false,null, A1M1F.getDescription())	
	,A1M2R("421-A1M2R",false,null, A1M1R.getDescription())
	
	,A1M3D("421-A1M3D",false,null, A1M1D.getDescription())
	,A1M3I("421-A1M3I",false,null, A1M1I.getDescription())
	,A1M3U("421-A1M3U",false,null, A1M1U.getDescription())
	,A1M3F("421-A1M3F",false,null, A1M1F.getDescription())
	,A1M3R("421-A1M3R",false,null, A1M1R.getDescription())

	,A1M4D("421-A1M4D",false,null, A1M1D.getDescription())
	,A1M4I("421-A1M4I",false,null, A1M1I.getDescription())
	,A1M4U("421-A1M4U",false,null, A1M1U.getDescription())
	,A1M4F("421-A1M4F",false,null, A1M1F.getDescription())
	,A1M4R("421-A1M4R",false,null, A1M1R.getDescription())
	
	,A1M5D("421-A1M5D",false,null, A1M1D.getDescription())
	,A1M5I("421-A1M5I",false,null, A1M1I.getDescription())
	,A1M5U("421-A1M5U",false,null, A1M1U.getDescription())
	,A1M5F("421-A1M5F",false,null, A1M1F.getDescription())
	,A1M5R("421-A1M5R",false,null, A1M1R.getDescription())
	
	,A1M6D("421-A1M6D",false,null, A1M1D.getDescription())
	,A1M6I("421-A1M6I",false,null, A1M1I.getDescription())
	,A1M6U("421-A1M6U",false,null, A1M1U.getDescription())
	,A1M6F("421-A1M6F",false,null, A1M1F.getDescription())
	,A1M6R("421-A1M6R",false,null, A1M1R.getDescription())
	
	,A1M7D("421-A1M7D",false,null, A1M1D.getDescription())
	,A1M7I("421-A1M7I",false,null, A1M1I.getDescription())
	,A1M7U("421-A1M7U",false,null, A1M1U.getDescription())
	,A1M7F("421-A1M7F",false,null, A1M1F.getDescription())
	,A1M7R("421-A1M7R",false,null, A1M1R.getDescription())
	 
//	,A1M2D("303-CTS12D",false,null,CT_S11D.getDescription())
//	,A1M2I("303-CTS12I",false,null,CT_S11I.getDescription())
//	,A1M2U("303-CTS12U",false,null,CT_S11U.getDescription()) 
//	,A1M2F("303-CTS12F",false,null,CT_S11F.getDescription())
//	,A1M2R("303-CTS12R",false,null,CT_S11R.getDescription())
//	 
//	,A1M3D("303-CTS13D",false,null,CT_S11D.getDescription())
//	,A1M3I("303-CTS13I",false,null,CT_S11I.getDescription())
//	,A1M3U("303-CTS13U",false,null,CT_S11U.getDescription())
//	,A1M3F("303-CTS13F",false,null,CT_S11F.getDescription())
//	,A1M3R("303-CTS13R",false,null,CT_S11R.getDescription())
//	 
//	,A1M4D("303-CTS14D",false,null,CT_S11D.getDescription())
//	,A1M4I("303-CTS14I",false,null,CT_S11I.getDescription())
//	,A1M4U("303-CTS14U",false,null,CT_S11U.getDescription())
//	,A1M4F("303-CTS14F",false,null,CT_S11F.getDescription())
//	,A1M4R("303-CTS14R",false,null,CT_S11R.getDescription())
//	 
//	,A1M5D("303-CTS15D",false,null,CT_S11D.getDescription())
//	,A1M5I("303-CTS15I",false,null,CT_S11I.getDescription())
//	,A1M5U("303-CTS15U",false,null,CT_S11U.getDescription())
//	,A1M5F("303-CTS15F",false,null,CT_S11F.getDescription())
//	,A1M5R("303-CTS15R",false,null,CT_S11R.getDescription())
//	 
//	,A1M6D("303-CTS16D",false,null,CT_S11D.getDescription())
//	,A1M6I("303-CTS16I",false,null,CT_S11I.getDescription())
//	,A1M6U("303-CTS16U",false,null,CT_S11U.getDescription())
//	,A1M6F("303-CTS16F",false,null,CT_S11F.getDescription())
//	,A1M6R("303-CTS16R",false,null,CT_S11R.getDescription())
//	 
//	,A1M7D("303-CTS17D",false,null,CT_S11D.getDescription())
//	,A1M7I("303-CTS17I",false,null,CT_S11I.getDescription())
//	,A1M7U("303-CTS17U",false,null,CT_S11U.getDescription())
//	,A1M7F("303-CTS17F",false,null,CT_S11F.getDescription())
//	,A1M7R("303-CTS17R",false,null,CT_S11R.getDescription())
	
//	,CT_S1P1("303-CTS1P1",false,null,"Personal Asalariado - Horas anuales - Mayores de 19 a\u00F1os")
//	,CT_S1P2("303-CTS1P2",false,null,"Personal Asalariado - Horas anuales - Menores de 19 a\u00F1os y trabajadores con contratos de aprendizaje o formaci\u00F3n que no sean discapacitados")
//	,CT_S1P3("303-CTS1P3",false,null,"Personal Asalariado - Horas anuales - Discapacitados con grado de minusval\u00EDa igual o superior al 33 por 100")
//	,CT_S1P4("303-CTS1P4",false,null,"Personal Asalariado - Horas anuales - Horas anuales fijadas en el convenio colectivo vigente")
//	,CT_S1E1("303-CTS1E1",false,null,"Personal No Asalariado - Horas anuales: titular")
//	,CT_S1E2("303-CTS1E2",false,null,"Personal No Asalariado - El titular es discapacitado en grado igual o superior al 33 por 100.")
//	,CT_S1E3("303-CTS1E3",false,null,"Personal No Asalariado - Horas anuales: c\u00F3nyuge")
//	,CT_S1E4("303-CTS1E4",false,null,"Personal No Asalariado - Horas anuales: hijos menores de 18 a\u00F1os")
//	,CT_S1C1("303-CTS1C1",false,null,"M\u00F3dulo Mesas - Capacidad")
//	,CT_S1M1("303-CTS1M1",false,null,"M\u00F3dulo Mesas - Mesas")
//	,CT_S1D1("303-CTS1D1",false,null,"M\u00F3dulo Mesas - D\u00EDas (4T)")
//	,CT_S1C2("303-CTS1C2",false,null,"M\u00F3dulo Mesas - Capacidad")
//	,CT_S1M2("303-CTS1M2",false,null,"M\u00F3dulo Mesas - Mesas")
//	,CT_S1D2("303-CTS1D2",false,null,"M\u00F3dulo Mesas - D\u00EDas (4T)")
//	,CT_S1C3("303-CTS1C3",false,null,"M\u00F3dulo Mesas - Capacidad")
//	,CT_S1M3("303-CTS1M3",false,null,"M\u00F3dulo Mesas - Mesas")
//	,CT_S1D3("303-CTS1D3",false,null,"M\u00F3dulo Mesas - D\u00EDas (4T)")
//	,CT_S1C4("303-CTS1C4",false,null,"M\u00F3dulo Mesas - Capacidad")
//	,CT_S1M4("303-CTS1M4",false,null,"M\u00F3dulo Mesas - Mesas")
//	,CT_S1D4("303-CTS1D4",false,null,"M\u00F3dulo Mesas - D\u00EDas (4T)")
	
	// Desglose modulo "Superficie del horno" (solo 4T a partir del 2025) 4 lineas por cada actividad
//	,CT_S1H1("303-CTS1H1",false,null,"M\u00F3dulo Superficie del horno - Superficie del horno")
//	,CT_S1J1("303-CTS1J1",false,null,"M\u00F3dulo Superficie del horno - D\u00EDas)")
//	,CT_S1H2("303-CTS1H2",false,null,"M\u00F3dulo Superficie del horno - Superficie del horno")
//	,CT_S1J2("303-CTS1J2",false,null,"M\u00F3dulo Superficie del horno - D\u00EDas)")
//	,CT_S1H3("303-CTS1H3",false,null,"M\u00F3dulo Superficie del horno - Superficie del horno")
//	,CT_S1J3("303-CTS1J3",false,null,"M\u00F3dulo Superficie del horno - D\u00EDas)")
//	,CT_S1H4("303-CTS1H4",false,null,"M\u00F3dulo Superficie del horno - Superficie del horno")
//	,CT_S1J4("303-CTS1J4",false,null,"M\u00F3dulo Superficie del horno - D\u00EDas)")

	
//	,CT_S1R1("303-CTS1R1",false,"--","Reducci\u00F3n Lorca")
//	,CT_S1R2("303-CTS1R2",false,"--","Reducci\u00F3n DANA")
//	,CT_S118("303-CTS118",false,"D1","Reducciones")
//	,CT_S123("303-CTS123",false,"H1","\u00CDndice corrector de actividades de temporada")
//	,CT_S126("303-CTS126",false,"K1","Devoluci\u00F3n cuotas soportadas otros pa\u00EDses")
	
//	private double dev;			   // Cuota devengada operaciones corrientes
//	private double ict;			// Indice corrector de actividades de temporada
//	private double por;			// Porcentaje de ingreso a cuenta
//	private double ing;			// Ingreso a cuenta
//	private double sop1;		// 1% cuota devengada
//	private double sop2;		// Resto de cuotas soportadas
//	private double sop;			// Total cuotas soportadas operaciones corrientes
//	private double res;			// RESULTADO 
//	private double pcm;			// Porcentaje cuota mínima
//	private double cmn;			// Cuota mínima
//	private double cad;			// Cuota anual derivada régimen simplificado
	
	,A1DEV("421-A1DEV",false,"","Cuota devengada operaciones corrientes")
	,A1ICT("421-A1ICT",false,"","\u00CDndice corrector actividades de temporada")
	,A1POR("421-A1POR",false,"","Porcentaje de ingreso a cuenta (1T/2T/3T)")
	,A1ING("421-A1ING",false,"","Ingreso a cuenta (1T/2T/3T)")
	,A1SO1("421-A1SO1",false,"","1% de la cuota devengada por operaciones corrientes (4T)")
	,A1SOR("421-A1SOR",false,"","Resto de cuotas soportadas (4T)")
	,A1SOP("421-A1SOP",false,"","Total cuotas soportadas operaciones corrientes (4T)")
	,A1RES("421-A1RES",false,"","RESULTADO (4T)")
	,A1PCM("421-A1PCM",false,"","Porcentaje cuota m\u00EDnima (4T)")
	,A1CMN("421-A1CMN",false,"","Cuota m\u00EDnima (4T)")
	,A1CAD("421-A1CAD",false,"","Cuota anual derivada r\u00E9gimen simplificado (4T)")
	
	// ACTIVIDAD 2
	
	,A2EP1("421-A2EP1",false,null, A1EP1.getDescription())
	,A2EP2("421-A2EP2",false,null, A1EP2.getDescription())
	,A2EPD("421-A2EPD",false,null, A1EPD.getDescription())
	,A2TEM("421-A2TEM",false,null, A1TEM.getDescription())
	,A2DIA("421-A2DIA",false,null, A1DIA.getDescription())
	
	,A2M1D("421-A2M1D",false,null, A1M1D.getDescription())	
	,A2M1I("421-A2M1I",false,null, A1M1I.getDescription()) 	
	,A2M1U("421-A2M1U",false,null, A1M1U.getDescription()) 	
	,A2M1F("421-A2M1F",false,null, A1M1F.getDescription())	
	,A2M1R("421-A2M1R",false,null, A1M1R.getDescription())	
	,A2M2D("421-A2M2D",false,null, A1M1D.getDescription())
	,A2M2I("421-A2M2I",false,null, A1M1I.getDescription())
	,A2M2U("421-A2M2U",false,null, A1M1U.getDescription())
	,A2M2F("421-A2M2F",false,null, A1M1F.getDescription())
	,A2M2R("421-A2M2R",false,null, A1M1R.getDescription())
	,A2M3D("421-A2M3D",false,null, A1M1D.getDescription())
	,A2M3I("421-A2M3I",false,null, A1M1I.getDescription())
	,A2M3U("421-A2M3U",false,null, A1M1U.getDescription())
	,A2M3F("421-A2M3F",false,null, A1M1F.getDescription())
	,A2M3R("421-A2M3R",false,null, A1M1R.getDescription())
	,A2M4D("421-A2M4D",false,null, A1M1D.getDescription())
	,A2M4I("421-A2M4I",false,null, A1M1I.getDescription())
	,A2M4U("421-A2M4U",false,null, A1M1U.getDescription())
	,A2M4F("421-A2M4F",false,null, A1M1F.getDescription())
	,A2M4R("421-A2M4R",false,null, A1M1R.getDescription())
	,A2M5D("421-A2M5D",false,null, A1M1D.getDescription())
	,A2M5I("421-A2M5I",false,null, A1M1I.getDescription())
	,A2M5U("421-A2M5U",false,null, A1M1U.getDescription())
	,A2M5F("421-A2M5F",false,null, A1M1F.getDescription())
	,A2M5R("421-A2M5R",false,null, A1M1R.getDescription())
	,A2M6D("421-A2M6D",false,null, A1M1D.getDescription())
	,A2M6I("421-A2M6I",false,null, A1M1I.getDescription())
	,A2M6U("421-A2M6U",false,null, A1M1U.getDescription())
	,A2M6F("421-A2M6F",false,null, A1M1F.getDescription())
	,A2M6R("421-A2M6R",false,null, A1M1R.getDescription())
	,A2M7D("421-A2M7D",false,null, A1M1D.getDescription())
	,A2M7I("421-A2M7I",false,null, A1M1I.getDescription())
	,A2M7U("421-A2M7U",false,null, A1M1U.getDescription())
	,A2M7F("421-A2M7F",false,null, A1M1F.getDescription())
	,A2M7R("421-A2M7R",false,null, A1M1R.getDescription())
	
	,A2DEV("421-A2DEV",false,"",A1DEV.getDescription())
	,A2ICT("421-A2ICT",false,"",A1ICT.getDescription())
	,A2POR("421-A2POR",false,"",A1POR.getDescription())
	,A2ING("421-A2ING",false,"",A1ING.getDescription())
	,A2SO1("421-A2SO1",false,"",A1SO1.getDescription())
	,A2SOR("421-A2SOR",false,"",A1SOR.getDescription())
	,A2SOP("421-A2SOP",false,"",A1SOP.getDescription())
	,A2RES("421-A2RES",false,"",A1RES.getDescription())
	,A2PCM("421-A2PCM",false,"",A1PCM.getDescription())
	,A2CMN("421-A2CMN",false,"",A1CMN.getDescription())
	,A2CAD("421-A2CAD",false,"",A1CAD.getDescription())

	// ACTIVIDAD 3 
	
	,A3EP1("421-A3EP1",false,null, A1EP1.getDescription())
	,A3EP2("421-A3EP2",false,null, A1EP2.getDescription())
	,A3EPD("421-A3EPD",false,null, A1EPD.getDescription())
	,A3TEM("421-A3TEM",false,null, A1TEM.getDescription())
	,A3DIA("421-A3DIA",false,null, A1DIA.getDescription())
	
	,A3M1D("421-A3M1D",false,null, A1M1D.getDescription())	
	,A3M1I("421-A3M1I",false,null, A1M1I.getDescription()) 	
	,A3M1U("421-A3M1U",false,null, A1M1U.getDescription()) 	
	,A3M1F("421-A3M1F",false,null, A1M1F.getDescription())	
	,A3M1R("421-A3M1R",false,null, A1M1R.getDescription())	
	,A3M2D("421-A3M2D",false,null, A1M1D.getDescription())
	,A3M2I("421-A3M2I",false,null, A1M1I.getDescription())
	,A3M2U("421-A3M2U",false,null, A1M1U.getDescription())
	,A3M2F("421-A3M2F",false,null, A1M1F.getDescription())
	,A3M2R("421-A3M2R",false,null, A1M1R.getDescription())
	,A3M3D("421-A3M3D",false,null, A1M1D.getDescription())
	,A3M3I("421-A3M3I",false,null, A1M1I.getDescription())
	,A3M3U("421-A3M3U",false,null, A1M1U.getDescription())
	,A3M3F("421-A3M3F",false,null, A1M1F.getDescription())
	,A3M3R("421-A3M3R",false,null, A1M1R.getDescription())
	,A3M4D("421-A3M4D",false,null, A1M1D.getDescription())
	,A3M4I("421-A3M4I",false,null, A1M1I.getDescription())
	,A3M4U("421-A3M4U",false,null, A1M1U.getDescription())
	,A3M4F("421-A3M4F",false,null, A1M1F.getDescription())
	,A3M4R("421-A3M4R",false,null, A1M1R.getDescription())
	,A3M5D("421-A3M5D",false,null, A1M1D.getDescription())
	,A3M5I("421-A3M5I",false,null, A1M1I.getDescription())
	,A3M5U("421-A3M5U",false,null, A1M1U.getDescription())
	,A3M5F("421-A3M5F",false,null, A1M1F.getDescription())
	,A3M5R("421-A3M5R",false,null, A1M1R.getDescription())
	,A3M6D("421-A3M6D",false,null, A1M1D.getDescription())
	,A3M6I("421-A3M6I",false,null, A1M1I.getDescription())
	,A3M6U("421-A3M6U",false,null, A1M1U.getDescription())
	,A3M6F("421-A3M6F",false,null, A1M1F.getDescription())
	,A3M6R("421-A3M6R",false,null, A1M1R.getDescription())
	,A3M7D("421-A3M7D",false,null, A1M1D.getDescription())
	,A3M7I("421-A3M7I",false,null, A1M1I.getDescription())
	,A3M7U("421-A3M7U",false,null, A1M1U.getDescription())
	,A3M7F("421-A3M7F",false,null, A1M1F.getDescription())
	,A3M7R("421-A3M7R",false,null, A1M1R.getDescription())
	
	,A3DEV("421-A3DEV",false,"",A1DEV.getDescription())
	,A3ICT("421-A3ICT",false,"",A1ICT.getDescription())
	,A3POR("421-A3POR",false,"",A1POR.getDescription())
	,A3ING("421-A3ING",false,"",A1ING.getDescription())
	,A3SO1("421-A3SO1",false,"",A1SO1.getDescription())
	,A3SOR("421-A3SOR",false,"",A1SOR.getDescription())
	,A3SOP("421-A3SOP",false,"",A1SOP.getDescription())
	,A3RES("421-A3RES",false,"",A1RES.getDescription())
	,A3PCM("421-A3PCM",false,"",A1PCM.getDescription())
	,A3CMN("421-A3CMN",false,"",A1CMN.getDescription())
	,A3CAD("421-A3CAD",false,"",A1CAD.getDescription())
	
	// ACTIVIDAD 4
	
	,A4EP1("421-A4EP1",false,null, A1EP1.getDescription())
	,A4EP2("421-A4EP2",false,null, A1EP2.getDescription())
	,A4EPD("421-A4EPD",false,null, A1EPD.getDescription())
	,A4TEM("421-A4TEM",false,null, A1TEM.getDescription())
	,A4DIA("421-A4DIA",false,null, A1DIA.getDescription())
	
	,A4M1D("421-A4M1D",false,null, A1M1D.getDescription())	
	,A4M1I("421-A4M1I",false,null, A1M1I.getDescription()) 	
	,A4M1U("421-A4M1U",false,null, A1M1U.getDescription()) 	
	,A4M1F("421-A4M1F",false,null, A1M1F.getDescription())	
	,A4M1R("421-A4M1R",false,null, A1M1R.getDescription())	
	,A4M2D("421-A4M2D",false,null, A1M1D.getDescription())
	,A4M2I("421-A4M2I",false,null, A1M1I.getDescription())
	,A4M2U("421-A4M2U",false,null, A1M1U.getDescription())
	,A4M2F("421-A4M2F",false,null, A1M1F.getDescription())
	,A4M2R("421-A4M2R",false,null, A1M1R.getDescription())
	,A4M3D("421-A4M3D",false,null, A1M1D.getDescription())
	,A4M3I("421-A4M3I",false,null, A1M1I.getDescription())
	,A4M3U("421-A4M3U",false,null, A1M1U.getDescription())
	,A4M3F("421-A4M3F",false,null, A1M1F.getDescription())
	,A4M3R("421-A4M3R",false,null, A1M1R.getDescription())
	,A4M4D("421-A4M4D",false,null, A1M1D.getDescription())
	,A4M4I("421-A4M4I",false,null, A1M1I.getDescription())
	,A4M4U("421-A4M4U",false,null, A1M1U.getDescription())
	,A4M4F("421-A4M4F",false,null, A1M1F.getDescription())
	,A4M4R("421-A4M4R",false,null, A1M1R.getDescription())
	,A4M5D("421-A4M5D",false,null, A1M1D.getDescription())
	,A4M5I("421-A4M5I",false,null, A1M1I.getDescription())
	,A4M5U("421-A4M5U",false,null, A1M1U.getDescription())
	,A4M5F("421-A4M5F",false,null, A1M1F.getDescription())
	,A4M5R("421-A4M5R",false,null, A1M1R.getDescription())
	,A4M6D("421-A4M6D",false,null, A1M1D.getDescription())
	,A4M6I("421-A4M6I",false,null, A1M1I.getDescription())
	,A4M6U("421-A4M6U",false,null, A1M1U.getDescription())
	,A4M6F("421-A4M6F",false,null, A1M1F.getDescription())
	,A4M6R("421-A4M6R",false,null, A1M1R.getDescription())
	,A4M7D("421-A4M7D",false,null, A1M1D.getDescription())
	,A4M7I("421-A4M7I",false,null, A1M1I.getDescription())
	,A4M7U("421-A4M7U",false,null, A1M1U.getDescription())
	,A4M7F("421-A4M7F",false,null, A1M1F.getDescription())
	,A4M7R("421-A4M7R",false,null, A1M1R.getDescription())
	
	,A4DEV("421-A4DEV",false,"",A1DEV.getDescription())
	,A4ICT("421-A4ICT",false,"",A1ICT.getDescription())
	,A4POR("421-A4POR",false,"",A1POR.getDescription())
	,A4ING("421-A4ING",false,"",A1ING.getDescription())
	,A4SO1("421-A4SO1",false,"",A1SO1.getDescription())
	,A4SOR("421-A4SOR",false,"",A1SOR.getDescription())
	,A4SOP("421-A4SOP",false,"",A1SOP.getDescription())
	,A4RES("421-A4RES",false,"",A1RES.getDescription())
	,A4PCM("421-A4PCM",false,"",A1PCM.getDescription())
	,A4CMN("421-A4CMN",false,"",A1CMN.getDescription())
	,A4CAD("421-A4CAD",false,"",A1CAD.getDescription())

//
//	// ************************************************************** [ACTIVIDAD 2]
//	,CT_S201("303-CTS201",false,null,CT_S101.getDescription())
//	,CT_S20D("303-CTS20D",false,null,CT_S10D.getDescription())
//	,CT_S202("303-CTS202",false,null,CT_S102.getDescription())
//	,CT_S2X1("303-CTS2X1",false,null,CT_S1X1.getDescription())
//	,CT_S2X2("303-CTS2X2",false,null,CT_S1X2.getDescription())
////	,CT_S2X3("303-CTS2X3",false,null,CT_S1X3.getDescription())
//	,CT_S2Y1("303-CTS2Y1",false,null,CT_S1Y1.getDescription())
////	,CT_S2Y2("303-CTS2Y2",false,null,CT_S1Y2.getDescription())
////	,CT_S2X4("303-CTS2X4",false,null,CT_S1X4.getDescription())
////	,CT_S2X5("303-CTS2X5",false,null,CT_S1X5.getDescription())
////	,CT_S2X6("303-CTS2X6",false,null,CT_S1X6.getDescription())
//	
//	,CT_S21D("303-CTS21D",false,null,CT_S11D.getDescription())
//	,CT_S21I("303-CTS21I",false,null,CT_S11I.getDescription())
//	,CT_S21U("303-CTS21U",false,null,CT_S11U.getDescription())
//	,CT_S21F("303-CTS21F",false,null,CT_S11F.getDescription())
//	,CT_S21R("303-CTS21R",false,null,CT_S11R.getDescription())
//	
//	,CT_S22D("303-CTS22D",false,null,CT_S12D.getDescription())
//	,CT_S22I("303-CTS22I",false,null,CT_S12I.getDescription())
//	,CT_S22U("303-CTS22U",false,null,CT_S12U.getDescription())
//	,CT_S22F("303-CTS22F",false,null,CT_S12F.getDescription())
//	,CT_S22R("303-CTS22R",false,null,CT_S12R.getDescription())
//	
//	,CT_S23D("303-CTS23D",false,null,CT_S13D.getDescription())
//	,CT_S23I("303-CTS23I",false,null,CT_S13I.getDescription())
//	,CT_S23U("303-CTS23U",false,null,CT_S13U.getDescription())
//	,CT_S23F("303-CTS23F",false,null,CT_S13F.getDescription())
//	,CT_S23R("303-CTS23R",false,null,CT_S13R.getDescription())
//	
//	,CT_S24D("303-CTS24D",false,null,CT_S14D.getDescription())
//	,CT_S24I("303-CTS24I",false,null,CT_S14I.getDescription())
//	,CT_S24U("303-CTS24U",false,null,CT_S14U.getDescription())
//	,CT_S24F("303-CTS24F",false,null,CT_S14F.getDescription())
//	,CT_S24R("303-CTS24R",false,null,CT_S14R.getDescription())
//	
//	,CT_S25D("303-CTS25D",false,null,CT_S15D.getDescription())
//	,CT_S25I("303-CTS25I",false,null,CT_S15I.getDescription())
//	,CT_S25U("303-CTS25U",false,null,CT_S15U.getDescription())
//	,CT_S25F("303-CTS25F",false,null,CT_S15F.getDescription())
//	,CT_S25R("303-CTS25R",false,null,CT_S15R.getDescription())
//	
//	,CT_S26D("303-CTS26D",false,null,CT_S16D.getDescription())
//	,CT_S26I("303-CTS26I",false,null,CT_S16I.getDescription())
//	,CT_S26U("303-CTS26U",false,null,CT_S16U.getDescription())
//	,CT_S26F("303-CTS26F",false,null,CT_S16F.getDescription())
//	,CT_S26R("303-CTS26R",false,null,CT_S16R.getDescription())
//	
//	,CT_S27D("303-CTS27D",false,null,CT_S17D.getDescription())
//	,CT_S27I("303-CTS27I",false,null,CT_S17I.getDescription())
//	,CT_S27U("303-CTS27U",false,null,CT_S17U.getDescription())
//	,CT_S27F("303-CTS27F",false,null,CT_S17F.getDescription())
//	,CT_S27R("303-CTS27R",false,null,CT_S17R.getDescription())
//	
////	,CT_S2P1("303-CTS2P1",false,null,CT_S1P1.getDescription())
////	,CT_S2P2("303-CTS2P2",false,null,CT_S1P2.getDescription())
////	,CT_S2P3("303-CTS2P3",false,null,CT_S1P3.getDescription())
////	,CT_S2P4("303-CTS2P4",false,null,CT_S1P4.getDescription())
////	,CT_S2E1("303-CTS2E1",false,null,CT_S1E1.getDescription())
////	,CT_S2E2("303-CTS2E2",false,null,CT_S1E2.getDescription())
////	,CT_S2E3("303-CTS2E3",false,null,CT_S1E3.getDescription())
////	,CT_S2E4("303-CTS2E4",false,null,CT_S1E4.getDescription())
////	,CT_S2C1("303-CTS2C1",false,null,CT_S1C1.getDescription())
////	,CT_S2M1("303-CTS2M1",false,null,CT_S1M1.getDescription())
////	,CT_S2D1("303-CTS2D1",false,null,CT_S1D1.getDescription())
////	,CT_S2C2("303-CTS2C2",false,null,CT_S1C2.getDescription())
////	,CT_S2M2("303-CTS2M2",false,null,CT_S1M2.getDescription())
////	,CT_S2D2("303-CTS2D2",false,null,CT_S1D2.getDescription())
////	,CT_S2C3("303-CTS2C3",false,null,CT_S1C3.getDescription())
////	,CT_S2M3("303-CTS2M3",false,null,CT_S1M3.getDescription())
////	,CT_S2D3("303-CTS2D3",false,null,CT_S1D3.getDescription())
////	,CT_S2C4("303-CTS2C4",false,null,CT_S1C4.getDescription())
////	,CT_S2M4("303-CTS2M4",false,null,CT_S1M4.getDescription())
////	,CT_S2D4("303-CTS2D4",false,null,CT_S1D4.getDescription())
////	,CT_S2H1("303-CTS2H1",false,null,CT_S1H1.getDescription())
////	,CT_S2J1("303-CTS2J1",false,null,CT_S1J1.getDescription())
////	,CT_S2H2("303-CTS2H2",false,null,CT_S1H2.getDescription())
////	,CT_S2J2("303-CTS2J2",false,null,CT_S1J2.getDescription())
////	,CT_S2H3("303-CTS2H3",false,null,CT_S1H3.getDescription())
////	,CT_S2J3("303-CTS2J3",false,null,CT_S1J3.getDescription())
////	,CT_S2H4("303-CTS2H4",false,null,CT_S1H4.getDescription())
////	,CT_S2J4("303-CTS2J4",false,null,CT_S1J4.getDescription())
//	
//	,CT_S217("303-CTS217",false,"",CT_S117.getDescription())
////	,CT_S2R1("303-CTS2R1",false,"--",CT_S1R1.getDescription())
////	,CT_S2R2("303-CTS2R2",false,"--",CT_S1R2.getDescription())
////	,CT_S218("303-CTS218",false,"D2",CT_S118.getDescription())
//	,CT_S219("303-CTS219",false,"",CT_S119.getDescription())
//	,CT_S220("303-CTS220",false,"",CT_S120.getDescription())
//	,CT_S221("303-CTS221",false,"",CT_S121.getDescription())
//	,CT_S22X("303-CTS22X",false,"",CT_S12X.getDescription())
//	,CT_S22Y("303-CTS22Y",false,"",CT_S12Y.getDescription())
//	,CT_S23Y("303-CTS23Y",false,"",CT_S13Y.getDescription())
//	,CT_S222("303-CTS222",false,"",CT_S122.getDescription())
////	,CT_S223("303-CTS223",false,"H2",CT_S123.getDescription())
//	,CT_S224("303-CTS224",false,"",CT_S124.getDescription())
//	,CT_S225("303-CTS225",false,"",CT_S125.getDescription())
////	,CT_S226("303-CTS226",false,"K2",CT_S126.getDescription())
//	,CT_S227("303-CTS227",false,"",CT_S127.getDescription())
//	,CT_S228("303-CTS228",false,"",CT_S128.getDescription())
//
//	// ************************************************************** [ACTIVIDAD 3]
//	,CT_S301("303-CTS301",false,null,CT_S101.getDescription())
//	,CT_S30D("303-CTS30D",false,null,CT_S10D.getDescription())
//	,CT_S302("303-CTS302",false,null,CT_S102.getDescription())
//	,CT_S3X1("303-CTS3X1",false,null,CT_S1X1.getDescription())
//	,CT_S3X2("303-CTS3X2",false,null,CT_S1X2.getDescription())
////	,CT_S3X3("303-CTS3X3",false,null,CT_S1X3.getDescription())
//	,CT_S3Y1("303-CTS3Y1",false,null,CT_S1Y1.getDescription())
////	,CT_S3Y2("303-CTS3Y2",false,null,CT_S1Y2.getDescription())
////	,CT_S3X4("303-CTS3X4",false,null,CT_S1X4.getDescription())
////	,CT_S3X5("303-CTS3X5",false,null,CT_S1X5.getDescription())
////	,CT_S3X6("303-CTS3X6",false,null,CT_S1X6.getDescription())
//	
//	,CT_S31D("303-CTS31D",false,null,CT_S11D.getDescription())
//	,CT_S31I("303-CTS31I",false,null,CT_S11I.getDescription())
//	,CT_S31U("303-CTS31U",false,null,CT_S11U.getDescription())
//	,CT_S31F("303-CTS31F",false,null,CT_S11F.getDescription())
//	,CT_S31R("303-CTS31R",false,null,CT_S11R.getDescription())
//	
//	,CT_S32D("303-CTS32D",false,null,CT_S12D.getDescription())
//	,CT_S32I("303-CTS32I",false,null,CT_S12I.getDescription())
//	,CT_S32U("303-CTS32U",false,null,CT_S12U.getDescription())
//	,CT_S32F("303-CTS32F",false,null,CT_S12F.getDescription())
//	,CT_S32R("303-CTS32R",false,null,CT_S12R.getDescription())
//	
//	,CT_S33D("303-CTS33D",false,null,CT_S13D.getDescription())
//	,CT_S33I("303-CTS33I",false,null,CT_S13I.getDescription())
//	,CT_S33U("303-CTS33U",false,null,CT_S13U.getDescription())
//	,CT_S33F("303-CTS33F",false,null,CT_S13F.getDescription())
//	,CT_S33R("303-CTS33R",false,null,CT_S13R.getDescription())
//	
//	,CT_S34D("303-CTS34D",false,null,CT_S14D.getDescription())
//	,CT_S34I("303-CTS34I",false,null,CT_S14I.getDescription())
//	,CT_S34U("303-CTS34U",false,null,CT_S14U.getDescription())
//	,CT_S34F("303-CTS34F",false,null,CT_S14F.getDescription())
//	,CT_S34R("303-CTS34R",false,null,CT_S14R.getDescription())
//	
//	,CT_S35D("303-CTS35D",false,null,CT_S15D.getDescription())
//	,CT_S35I("303-CTS35I",false,null,CT_S15I.getDescription())
//	,CT_S35U("303-CTS35U",false,null,CT_S15U.getDescription())
//	,CT_S35F("303-CTS35F",false,null,CT_S15F.getDescription())
//	,CT_S35R("303-CTS35R",false,null,CT_S15R.getDescription())
//	
//	,CT_S36D("303-CTS36D",false,null,CT_S16D.getDescription())
//	,CT_S36I("303-CTS36I",false,null,CT_S16I.getDescription())
//	,CT_S36U("303-CTS36U",false,null,CT_S16U.getDescription())
//	,CT_S36F("303-CTS36F",false,null,CT_S16F.getDescription())
//	,CT_S36R("303-CTS36R",false,null,CT_S16R.getDescription())
//	
//	,CT_S37D("303-CTS37D",false,null,CT_S17D.getDescription())
//	,CT_S37I("303-CTS37I",false,null,CT_S17I.getDescription())
//	,CT_S37U("303-CTS37U",false,null,CT_S17U.getDescription())
//	,CT_S37F("303-CTS37F",false,null,CT_S17F.getDescription())
//	,CT_S37R("303-CTS37R",false,null,CT_S17R.getDescription())
//	
////	,CT_S3P1("303-CTS3P1",false,null,CT_S1P1.getDescription())
////	,CT_S3P2("303-CTS3P2",false,null,CT_S1P2.getDescription())
////	,CT_S3P3("303-CTS3P3",false,null,CT_S1P3.getDescription())
////	,CT_S3P4("303-CTS3P4",false,null,CT_S1P4.getDescription())
////	,CT_S3E1("303-CTS3E1",false,null,CT_S1E1.getDescription())
////	,CT_S3E2("303-CTS3E2",false,null,CT_S1E2.getDescription())
////	,CT_S3E3("303-CTS3E3",false,null,CT_S1E3.getDescription())
////	,CT_S3E4("303-CTS3E4",false,null,CT_S1E4.getDescription())
////	,CT_S3C1("303-CTS3C1",false,null,CT_S1C1.getDescription())
////	,CT_S3M1("303-CTS3M1",false,null,CT_S1M1.getDescription())
////	,CT_S3D1("303-CTS3D1",false,null,CT_S1D1.getDescription())
////	,CT_S3C2("303-CTS3C2",false,null,CT_S1C2.getDescription())
////	,CT_S3M2("303-CTS3M2",false,null,CT_S1M2.getDescription())
////	,CT_S3D2("303-CTS3D2",false,null,CT_S1D2.getDescription())
////	,CT_S3C3("303-CTS3C3",false,null,CT_S1C3.getDescription())
////	,CT_S3M3("303-CTS3M3",false,null,CT_S1M3.getDescription())
////	,CT_S3D3("303-CTS3D3",false,null,CT_S1D3.getDescription())
////	,CT_S3C4("303-CTS3C4",false,null,CT_S1C4.getDescription())
////	,CT_S3M4("303-CTS3M4",false,null,CT_S1M4.getDescription())
////	,CT_S3D4("303-CTS3D4",false,null,CT_S1D4.getDescription())
////	,CT_S3H1("303-CTS3H1",false,null,CT_S1H1.getDescription())
////	,CT_S3J1("303-CTS3J1",false,null,CT_S1J1.getDescription())
////	,CT_S3H2("303-CTS3H2",false,null,CT_S1H2.getDescription())
////	,CT_S3J2("303-CTS3J2",false,null,CT_S1J2.getDescription())
////	,CT_S3H3("303-CTS3H3",false,null,CT_S1H3.getDescription())
////	,CT_S3J3("303-CTS3J3",false,null,CT_S1J3.getDescription())
////	,CT_S3H4("303-CTS3H4",false,null,CT_S1H4.getDescription())
////	,CT_S3J4("303-CTS3J4",false,null,CT_S1J4.getDescription())
//
//	,CT_S317("303-CTS317",false,"",CT_S117.getDescription())
////	,CT_S3R1("303-CTS3R1",false,"--",CT_S1R1.getDescription())
////	,CT_S3R2("303-CTS3R2",false,"--",CT_S1R2.getDescription())
////	,CT_S318("303-CTS318",false,"D3",CT_S118.getDescription())
//	,CT_S319("303-CTS319",false,"",CT_S119.getDescription())
//	,CT_S320("303-CTS320",false,"",CT_S120.getDescription())
//	,CT_S321("303-CTS321",false,"",CT_S121.getDescription())
//	,CT_S32X("303-CTS32X",false,"",CT_S12X.getDescription())
//	,CT_S32Y("303-CTS32Y",false,"",CT_S12Y.getDescription())
//	,CT_S33Y("303-CTS33Y",false,"",CT_S13Y.getDescription())
//	,CT_S322("303-CTS322",false,"",CT_S122.getDescription())
////	,CT_S323("303-CTS323",false,"H3",CT_S123.getDescription())
//	,CT_S324("303-CTS324",false,"",CT_S124.getDescription())
//	,CT_S325("303-CTS325",false,"",CT_S125.getDescription())
////	,CT_S326("303-CTS326",false,"K3",CT_S126.getDescription())
//	,CT_S327("303-CTS327",false,"",CT_S127.getDescription())
//	,CT_S328("303-CTS328",false,"",CT_S128.getDescription())
//	
//	// ************************************************************** [ACTIVIDAD 4]
//	,CT_S401("303-CTS401",false,null,CT_S101.getDescription())
//	,CT_S40D("303-CTS40D",false,null,CT_S10D.getDescription())
//	,CT_S402("303-CTS402",false,null,CT_S102.getDescription())
//	,CT_S4X1("303-CTS4X1",false,null,CT_S1X1.getDescription())
//	,CT_S4X2("303-CTS4X2",false,null,CT_S1X2.getDescription())
////	,CT_S4X3("303-CTS4X3",false,null,CT_S1X3.getDescription())
//	,CT_S4Y1("303-CTS4Y1",false,null,CT_S1Y1.getDescription())
////	,CT_S4Y2("303-CTS4Y2",false,null,CT_S1Y2.getDescription())
////	,CT_S4X4("303-CTS4X4",false,null,CT_S1X4.getDescription())
////	,CT_S4X5("303-CTS4X5",false,null,CT_S1X5.getDescription())
////	,CT_S4X6("303-CTS4X6",false,null,CT_S1X6.getDescription())
//	
//	,CT_S41D("303-CTS41D",false,null,CT_S11D.getDescription())
//	,CT_S41I("303-CTS41I",false,null,CT_S11I.getDescription())
//	,CT_S41U("303-CTS41U",false,null,CT_S11U.getDescription())
//	,CT_S41F("303-CTS41F",false,null,CT_S11F.getDescription())
//	,CT_S41R("303-CTS41R",false,null,CT_S11R.getDescription())
//	
//	,CT_S42D("303-CTS42D",false,null,CT_S12D.getDescription())
//	,CT_S42I("303-CTS42I",false,null,CT_S12I.getDescription())
//	,CT_S42U("303-CTS42U",false,null,CT_S12U.getDescription())
//	,CT_S42F("303-CTS42F",false,null,CT_S12F.getDescription())
//	,CT_S42R("303-CTS42R",false,null,CT_S12R.getDescription())
//	
//	,CT_S43D("303-CTS43D",false,null,CT_S13D.getDescription())
//	,CT_S43I("303-CTS43I",false,null,CT_S13I.getDescription())
//	,CT_S43U("303-CTS43U",false,null,CT_S13U.getDescription())
//	,CT_S43F("303-CTS43F",false,null,CT_S13F.getDescription())
//	,CT_S43R("303-CTS43R",false,null,CT_S13R.getDescription())
//	
//	,CT_S44D("303-CTS44D",false,null,CT_S14D.getDescription())
//	,CT_S44I("303-CTS44I",false,null,CT_S14I.getDescription())
//	,CT_S44U("303-CTS44U",false,null,CT_S14U.getDescription())
//	,CT_S44F("303-CTS44F",false,null,CT_S14F.getDescription())
//	,CT_S44R("303-CTS44R",false,null,CT_S14R.getDescription())
//	
//	,CT_S45D("303-CTS45D",false,null,CT_S15D.getDescription())
//	,CT_S45I("303-CTS45I",false,null,CT_S15I.getDescription())
//	,CT_S45U("303-CTS45U",false,null,CT_S15U.getDescription())
//	,CT_S45F("303-CTS45F",false,null,CT_S15F.getDescription())
//	,CT_S45R("303-CTS45R",false,null,CT_S15R.getDescription())
//	
//	,CT_S46D("303-CTS46D",false,null,CT_S16D.getDescription())
//	,CT_S46I("303-CTS46I",false,null,CT_S16I.getDescription())
//	,CT_S46U("303-CTS46U",false,null,CT_S16U.getDescription())
//	,CT_S46F("303-CTS46F",false,null,CT_S16F.getDescription())
//	,CT_S46R("303-CTS46R",false,null,CT_S16R.getDescription())
//	
//	,CT_S47D("303-CTS47D",false,null,CT_S17D.getDescription())
//	,CT_S47I("303-CTS47I",false,null,CT_S17I.getDescription())
//	,CT_S47U("303-CTS47U",false,null,CT_S17U.getDescription())
//	,CT_S47F("303-CTS47F",false,null,CT_S17F.getDescription())
//	,CT_S47R("303-CTS47R",false,null,CT_S17R.getDescription())
//	
////	,CT_S4P1("303-CTS4P1",false,null,CT_S1P1.getDescription())
////	,CT_S4P2("303-CTS4P2",false,null,CT_S1P2.getDescription())
////	,CT_S4P3("303-CTS4P3",false,null,CT_S1P3.getDescription())
////	,CT_S4P4("303-CTS4P4",false,null,CT_S1P4.getDescription())
////	,CT_S4E1("303-CTS4E1",false,null,CT_S1E1.getDescription())
////	,CT_S4E2("303-CTS4E2",false,null,CT_S1E2.getDescription())
////	,CT_S4E3("303-CTS4E3",false,null,CT_S1E3.getDescription())
////	,CT_S4E4("303-CTS4E4",false,null,CT_S1E4.getDescription())
////	,CT_S4C1("303-CTS4C1",false,null,CT_S1C1.getDescription())
////	,CT_S4M1("303-CTS4M1",false,null,CT_S1M1.getDescription())
////	,CT_S4D1("303-CTS4D1",false,null,CT_S1D1.getDescription())
////	,CT_S4C2("303-CTS4C2",false,null,CT_S1C2.getDescription())
////	,CT_S4M2("303-CTS4M2",false,null,CT_S1M2.getDescription())
////	,CT_S4D2("303-CTS4D2",false,null,CT_S1D2.getDescription())
////	,CT_S4C3("303-CTS4C3",false,null,CT_S1C3.getDescription())
////	,CT_S4M3("303-CTS4M3",false,null,CT_S1M3.getDescription())
////	,CT_S4D3("303-CTS4D3",false,null,CT_S1D3.getDescription())
////	,CT_S4C4("303-CTS4C4",false,null,CT_S1C4.getDescription())
////	,CT_S4M4("303-CTS4M4",false,null,CT_S1M4.getDescription())
////	,CT_S4D4("303-CTS4D4",false,null,CT_S1D4.getDescription())
////	,CT_S4H1("303-CTS4H1",false,null,CT_S1H1.getDescription())
////	,CT_S4J1("303-CTS4J1",false,null,CT_S1J1.getDescription())
////	,CT_S4H2("303-CTS4H2",false,null,CT_S1H2.getDescription())
////	,CT_S4J2("303-CTS4J2",false,null,CT_S1J2.getDescription())
////	,CT_S4H3("303-CTS4H3",false,null,CT_S1H3.getDescription())
////	,CT_S4J3("303-CTS4J3",false,null,CT_S1J3.getDescription())
////	,CT_S4H4("303-CTS4H4",false,null,CT_S1H4.getDescription())
////	,CT_S4J4("303-CTS4J4",false,null,CT_S1J4.getDescription())
//	
//	,CT_S417("303-CTS417",false,"",CT_S117.getDescription())
////	,CT_S4R1("303-CTS4R1",false,"--",CT_S1R1.getDescription())
////	,CT_S4R2("303-CTS4R2",false,"--",CT_S1R2.getDescription())
////	,CT_S418("303-CTS418",false,"D4",CT_S118.getDescription())
//	,CT_S419("303-CTS419",false,"",CT_S119.getDescription())
//	,CT_S420("303-CTS420",false,"",CT_S120.getDescription())
//	,CT_S421("303-CTS421",false,"",CT_S121.getDescription())
//	,CT_S42X("303-CTS42X",false,"",CT_S12X.getDescription())
//	,CT_S42Y("303-CTS42Y",false,"",CT_S12Y.getDescription())
//	,CT_S43Y("303-CTS43Y",false,"",CT_S13Y.getDescription())
//	,CT_S422("303-CTS422",false,"",CT_S122.getDescription())
////	,CT_S423("303-CTS423",false,"H4",CT_S123.getDescription())
//	,CT_S424("303-CTS424",false,"",CT_S124.getDescription())
//	,CT_S425("303-CTS425",false,"",CT_S125.getDescription())
////	,CT_S426("303-CTS426",false,"K4",CT_S126.getDescription())
//	,CT_S427("303-CTS427",false,"",CT_S127.getDescription())
//	,CT_S428("303-CTS428",false,"",CT_S128.getDescription())
	
	,C06("421-C06",false,"06","Cantidad a cuenta de acuerdo con los datos-base provisionales (1T/2T/3T)")
	,C07("421-C07",false,"07","Cuota anual devengada por operaciones corrientes (4T)")
	,C08("421-C08",false,"08","Cuotas soportadas o satisfechas en el ejercicio por operaciones corrientes (4T)")
	,C09("421-C09",false,"09","Cuota anual derivada del r\u00E9gimen simplificado (4T)")
	,C10T1("421-C10T1",false,"","Cantidad a cuenta autoliquidaciones trimestrales anteriores (T1) (4T)")
	,C10T2("421-C10T2",false,"","Cantidad a cuenta autoliquidaciones trimestrales anteriores (T2) (4T)")
	,C10T3("421-C10T3",false,"","Cantidad a cuenta autoliquidaciones trimestrales anteriores (T3) (4T)")
	,C10("421-C10",false,"10","Cantidad a cuenta autoliquidaciones trimestrales anteriores (4T)")
	,C11("421-C11",false,"11","Diferencia (4T)")
	
	,C12("421-C12",true,"12","Cuotas devengadas por entregas o transmisiones de activos fijos y por inversi\u00F3n del sujeto pasivo")
	,C13("421-C13",true,"13","Cuotas devengadas por arrendamiento de bienes inmuebles")
	,C14("421-C14",true,"14","Rectificaci\u00F3n de cuotas impositivas repercutidas")
	,C15("421-C15",true,"15","Cuotas deducibles por adquisiciones o importaciones de activos fijos")
	,C16("421-C16",true,"16","Cuotas deducibles correspondientes a la actividad de arrendamiento de bienes inmuebles")
	,C17("421-C17",true,"17","Cuotas del I.G.I.C. a compensar de per\u00EDodos anteriores")
	,C18("421-C18",true,"18","A deducir (exclusivamente en caso de autoliquidaci\u00F3n complementaria)")
	,C19("421-C19",true,"19","Resultado de la autoliquidaci\u00F3n")
	
//	,CT_S47("303-CTS47",false,"47","Suma de ingresos a cuenta del conjunto de actividades")
//	,CT_S48("303-CTS48",false,"48","Suma de cuotas derivadas RS del conjunto de actividades")
//	,CT_S49("303-CTS49",false,"49","(A+B) Suma de ingresos a cuenta realizados en el ejercicio")
//	,CT_S50("303-CTS50",false,"50","(A+B) Resultado")
//	,CT_S51("303-CTS51",true ,"51","Adquisiciones intracomunitarias de bienes")
//	,CT_S52("303-CTS52",true ,"52","Entregas de activos fijos")
//	,CT_S53("303-CTS53",true ,"53","IVA devengado por inversi\u00F3n del sujeto pasivo")
//	,CT_S54("303-CTS54",false,"54","Total cuota resultante")
//	,CT_S55("303-CTS55",true,"55","Adquisici\u00F3n o importaci\u00F3n de activos fijos")
//	,CT_S56("303-CTS56",false,"56","Regularizaci\u00F3n bienes de inversi\u00F3n")
//	,CT_S57("303-CTS57",false,"57","Total IVA deducible")
//	,CT_S58("303-CTS58",false,"58","Resultado R\u00E9gimen Simplificado")
//
//	,CT_C59("303-CTA59",true ,"59", "Entregas intracomunitarias de bienes y servicios")
//	,CT_C120("303-CTA120",true ,"120","Operaciones no sujetas por reglas de localizaci\u00F3n (excepto las incluidas en la casilla 123).")
//	,CT_C122("303-CTA122",true ,"122","Operaciones sujetas con inversi\u00F3n del sujeto pasivo.")
//	,CT_C123("303-CTA123",true ,"123","Operaciones no sujetas por reglas de localizaci\u00F3n acogidas a los reg\u00EDmenes especiales de ventanilla \u00FAnica.")
//	,CT_C124("303-CTA124",true ,"124","Operaciones sujetas y acogidas a los reg\u00EDmenes especiales de ventanilla \u00FAnica.")
//	,CT_C60("303-CTA60",true ,"60","Exportaciones y operaciones asimiladas")
//	,CT_C61("303-CTA61",true ,"61","Operaciones no sujetas o con inversi\u00F3n del sujeto pasivo que originan el derecho a deducci\u00F3n")
//	,CT_C62("303-CTA62",true ,"62","Criterio de Caja. Importes devengados en per\u00EDodo de liquidaci\u00F3n seg\u00FAn art. 75 LIVA. - Base Imponible")
//	,CT_C63("303-CTA63",true ,"63","Criterio de Caja. Importes devengados en per\u00EDodo de liquidaci\u00F3n seg\u00FAn art. 75 LIVA. - Cuota")
//	,CT_C74("303-CTA74",true ,"74","Criterio de Caja. Cuotas de IVA soportados conforme a la regla general de devengo seg\u00FAn art. 75 LIVA. - Base Imponible")
//	,CT_C75("303-CTA75",true ,"75","Criterio de Caja. Cuotas totales de IVA soportados conforme a la regla general de devengo seg\u00FAn art. 75 LIVA. - Cuota")
//	,CT_C76("303-CTA76",false,"76","Regularizaci\u00F3n cuotas art. 80.cinco.5\u00AA LIVA")
	
//	,CT_C64("303-CTA64",false,"64","Suma de resultados")
//	,CT_C65("303-CTA65",false,"65","% Atribuible a la Administraci\u00F3n del Estado") 
//	,CT_C66("303-CTA66",false,"66","Atribuible a la Administraci\u00F3n del Estado")
//	,CT_C77("303-CTA77",false,"77","IVA a la importaci\u00F3n liquidado por la Aduana pendiente de ingreso")  
//	
//	,CT_C67("303-CTA67",false,"67","Cuotas a compensar de periodos anteriores")  // Hasta 2020
//	
//	,CT_C110("303-CTA110",false,"110","Cuotas a compensar pendientes de periodos anteriores") // A partir de 2021
//	,CT_C78("303-CTA78",false,"78","Cuotas a compensar de periodos anteriores aplicadas en este periodo") // A partir de 2021
//	,CT_C87("303-CTA87",false,"87","Cuotas a compensar de periodos previos pendientes para periodos posteriores (No se incluyen las cuotas a compensar generadas en este periodo)") // A partir de 2021
//	
//	,CT_C68("303-CTA68",false,"68","Exclusivamente para sujetos pasivos que tributan conjuntamente a la Administraci\u00F3n del Estado y a las Diputaciones Forales Resultado de la regularizaci\u00F3n anual")
//	,CT_C108("303-CTA108",false,"108",
//		 "Exclusivamente para determinados supuestos de autoliquidaci\u00F3n rectificativa por discrepancia de criterio administrativo que no deban incluirse en otras casillas. Otros ajustes.")	
//	
//	,CT_C69("303-CTA69",false,"69",CT_S11R.getDescription()) 
//	,CT_C70("303-CTA70",false,"70","Resultados a ingresar de anteriores autoliquidaciones o liquidaciones administrativas correspondientes al ejercicio y per\u00EDodo objeto de la autoliquidaci\u00F3n")
//	,CT_C109("303-CTA109",false,"109","Devoluciones acordadas por la Agencia Tributaria como consecuencia de la tramitaci\u00F3n de anteriores autoliquidaciones correspondientes al ejercicio y per\u00EDodo objeto de la autoliquidaci\u00F3n")
//	,CT_C71("303-CTA71",false,"71","Resultado de la liquidaci\u00F3n")
//	
//	,CT_C111("303-CTA111",false,"111","Importe a devolver como consecuencia de la rectificaci\u00F3n")
//	
//	,CT_U1D("303-CTU1D",false,null,"A - Ep\u00EDgrafe IAE - Descripci\u00F3n")
//	,CT_U1C("303-CTU1C",false,null,"B - Clave - Principal")
//	,CT_U1E("303-CTU1E",false,null,"C - Ep\u00EDgrafe IAE - C\u00F3digo")
//
//	,CT_U2D("303-CTU2D",false,null,CT_U1D.getDescription())
//	,CT_U2C("303-CTU2C",false,null,"B - Clave - Otras")
//	,CT_U2E("303-CTU2E",false,null,CT_U1E.getDescription())
//
//	,CT_U3D("303-CTU3D",false,null,CT_U1D.getDescription())
//	,CT_U3C("303-CTU31C",false,null,CT_U2C.getDescription())
//	,CT_U3E("303-CTU3E",false,null,CT_U1E.getDescription())
//
//	,CT_U4D("303-CTU4D",false,null,CT_U1D.getDescription())
//	,CT_U4C("303-CTU4C",false,null,CT_U2C.getDescription())
//	,CT_U4E("303-CTU4E",false,null,CT_U1E.getDescription())
//
//	,CT_U5D("303-CTU5D",false,null,CT_U1D.getDescription())
//	,CT_U5C("303-CTU5C",false,null,CT_U2C.getDescription())
//	,CT_U5E("303-CTU5E",false,null,CT_U1E.getDescription())
//	,CT_U13("303-CTU13",false,null,"D - Marque si ha efectuado operaciones por las que tenga obligaci\u00F3n de presentar la declaraci\u00F3n anual de operaciones con terceras personas.")
//	,CT_C89("303-CTA89",false,"89","\u00C1lava/Araba")
//	,CT_C90("303-CTA90",false,"90","Guipuzcoa/Gipuzkoa")
//	,CT_C91("303-CTA91",false,"91","Vizcaya/Bizkaia")
//	,CT_C92("303-CTA92",false,"92","Navarra/Nafarroa")
//	
//	,CT_C80("303-CTA80",false,"80","Operaciones en r\u00E9gimen general")
//	,CT_C81("303-CTA81",false,"81","Operaciones en r\u00E9gimen especial del criterio de caja conforme art. 75 LIVA")
//	,CT_C82("303-CTA82",false,"82","Exportaciones, entregas intracomunitarias  y otras operaciones con derecho a deducci\u00F3n")
//	
//	,CT_C93("303-CTA93",false,"93","Entregas intracomunitarias exentas")
//	,CT_C94("303-CTA94",false,"94","Exportaciones y otras operaciones exentas con derecho a deducci\u00F3n")
//	,CT_C83("303-CTA83",false,"83","Operaciones exentas sin derecho a deducci\u00F3n")
//	,CT_C84("303-CTA84",false,"84","Operaciones no sujetas por reglas de localizaci\u00F3n o con inversi\u00F3n del sujeto pasivo") 
//	,CT_C85("303-CTA85",false,"85","Entregas de bienes objeto de instalaci\u00F3n o montaje en otros Estados miembros")
//	
//	,CT_C125("303-CTA125",false,"125","Operaciones sujetas con inversi\u00F3n del sujeto pasivo")
//	,CT_C126("303-CTA126",false,"126","Operaciones no sujetas por reglas de localizaci\u00F3n acogidas a los reg\u00EDmenes especiales de ventanilla \u00FAnica")
//	,CT_C127("303-CTA127",false,"127","Operaciones sujetas y acogidas a los reg\u00EDmenes especiales de ventanilla \u00FAnica")
//	,CT_C128("303-CTA128",false,"128","Operaciones intragrupo valoradas conforme a dispuesto en los art\u00EDculos 78 y 79 de la LIVA")
//	
//	,CT_C86("303-CTA86",false,"86","Operaciones en r\u00E9gimen simplificado")
//	,CT_C95("303-CTA95",false,"95","Operaciones en r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca") 
//	,CT_C96("303-CTA96",false,"96","Operaciones realizadas por sujetos pasivos acogidos al r\u00E9gimen especial del recargo de equivalencia")
//	,CT_C97("303-CTA97",false,"97","Operaciones en r\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n")
//	,CT_C98("303-CTA98",false,"98","Operaciones en r\u00E9gimen especial de Agencias de Viajes")
//	,CT_C79("303-CTA79",false,"79","Entregas de bienes inmuebles y operaciones financieras no habituales") 
//	,CT_C99("303-CTA99",false,"99","Entregas de bienes de inversi\u00F3n")
//
//	// Felix: Esta casilla no se utiliza desde finales de 2017 (periodos 2017 en adelante), se cambi\u00F3 por la [79]
//	//,CT_C87("303-CTA87",false,"87","Entregas de bienes inmuebles y de inversi\u00F3n y operaciones financieras no habituales")
//	
//	,CT_C88("303-CTA88",false,"88","Total volumen de operaciones")
//	,CT_C107("303-CTA107",false,"107","Territorio com\u00FAn")
//	
//	,CT_P1C("303-CTP1C",false,null,"CNAE")
//	,CT_P1I("303-CTP1I",false,null,"Imp. tot. Operaciones ")
//	,CT_P1D("303-CTP1D",false,null,"Imp. tot. Oper. con der. ded.")
//	,CT_P1T("303-CTP1T",false,null,"Tipo")
//	,CT_P1P("303-CTP1P",false,null,"% prorrata")
//
//	,CT_P2C("303-CTP2C",false,null,"CNAE")
//	,CT_P2I("303-CTP2I",false,null,CT_P1I.getDescription())
//	,CT_P2D("303-CTP2D",false,null,CT_P1D.getDescription())
//	,CT_P2T("303-CTP2T",false,null,"Tipo")
//	,CT_P2P("303-CTP2P",false,null,CT_P1P.getDescription())
//
//	,CT_P3C("303-CTP3C",false,null,"CNAE")
//	,CT_P3I("303-CTP3I",false,null,CT_P1I.getDescription())
//	,CT_P3D("303-CTP3D",false,null,CT_P1D.getDescription())
//	,CT_P3T("303-CTP3T",false,null,"Tipo")
//	,CT_P3P("303-CTP3P",false,null,CT_P1P.getDescription())
//
//	,CT_P4C("303-CTP4C",false,null,"CNAE")
//	,CT_P4I("303-CTP4I",false,null,CT_P1I.getDescription())
//	,CT_P4D("303-CTP4D",false,null,CT_P1D.getDescription())
//	,CT_P4T("303-CTP4T",false,null,"Tipo")
//	,CT_P4P("303-CTP4P",false,null,CT_P1P.getDescription())
//
//	,CT_P5C("303-CTP5C",false,null,"CNAE")
//	,CT_P5I("303-CTP5I",false,null,CT_P1I.getDescription())
//	,CT_P5D("303-CTP5D",false,null,CT_P1D.getDescription())
//	,CT_P5T("303-CTP5T",false,null,"Tipo")
//	,CT_P5P("303-CTP5P",false,null,CT_P1P.getDescription())
	;
	
	private String value;
	private boolean diffEnabled;
	private String box;
	private String description;
	
	private Mod421Key(String value,boolean diffEnabled,String box,String description) {
		this.value = value;
		this.diffEnabled = diffEnabled;
		this.box = box;
		this.description = description;
	}
	
	public boolean isDiffEnabled() {
		return this.diffEnabled;
	}

    @Override
	public String getValue() {
		return value;
	}
    @Override
	public int getBox() {
		if (AonStringUtils.isNumeric(box)) {
			return AonNumberUtils.toint(box);
		}
		return 0;
	}
	public String getBoxCode() {
		if (AonStringUtils.isNumeric(box)) {
			return AonStringUtils.leftPad(box, 3, AonStringUtils.ZERO); 
		}
		return box;
	}
	public String getDescription() {
		return description;
	}
    @Override
	public String getBoxFormatted() {
		return " [" + getBoxAsString() +"] ";
	}
	public String getBoxAsString() {
		return AonStringUtils.leftPad(Integer.toString(getBox()), 3, '0');
	}
	
	public static Mod421Key getKey(String value) {
		for (Mod421Key key : Mod421Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}

}
