package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;

public class D2DepositInitialization {
	
	public static Map<String,String> INITIALIZE_EXPRESSION_MAP = new LinkedHashMap<String,String>();
	public static Map<String,String> INITIALIZE_EXPRESSION_MAP_D2 = new LinkedHashMap<String,String>();

	static { // BALANCE: ACTIVO
		
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA111100.toString(), "sdb({20})-sab({280,290})");//Inmovilizado intangible
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA111200.toString(), "sdb({21,23})-sab({281,291})");//Inmovilizado material
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA111300.toString(), "sdb({22})-sab({282,292})");//Inversiones inmobiliarias
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA111400.toString(), "sdb({2403,2404,2413,2414,2423,2424})-sab({2493,2494,2933,2934,2943,2944,2953,2954})");//Inversiones en empresas del grupo y asociadas a largo plazo
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA111500.toString(), "ABREVIADO?sdb({2405,2415,2425,250,251,252,253,254,255,257,258,26})-sab({2495,2935,259,2945,2955,297,298})"
																				+ ":sdb({2405,2415,2425,250,251,252,253,254,255,258,26})-sab({2495,259,2935,2945,2955,296,297,298})");//Inversiones financieras a largo plazo
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA111600.toString(), "sdb({474})");//Activos por impuesto diferido
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA111700.toString(), "sdb({45})"); // TODO NECA 6º .8 (abreviado)//Deudores comerciales no corrientes
																				//+ ":0.0"); // TODO NECA 5.ª 5 (pymes)

		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112100.toString(), "ABREVIADO?sdb({580,581,582,583,584})-sab({599})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112200.toString(), "sdb({30,31,32,33,34,35,36,407})-sab({39})");
	
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112380.toString(), "sdb({430,431,432,433,434,435,436})-sab({437,490,493})");		
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112381.toString(), "sdb({43001})");	// TODO NECA 6.ª 8 (abreviado)
																						// TODO NECA 5.ª 5 (pymes)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112382.toString(), "sdb({430,431,432,435,436})-sab({437,490,4935})");	
																						// TODO NECA 6.ª 8 (abreviado)
																						// TODO NECA 5.ª 5 (pymes)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112370.toString(), "sdb({5580})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112390.toString(), "sdb({44,460,470,471,472,544})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112400.toString(), "ABREVIADO?sdb({5303,5304,5313,5314,5323,5324,5333,5334,5343,5344,5353,5354,5523,5524})-sab({5393,5394,593,5943,5944,5953,5954})"
																				+ ":sdb({5303,5304,5313,5314,5323,5324,5333,5334,5343,5344,5353,5354,5523,5524})-sab({5393,5394,5933,5934,5943,5944,5953,5954})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112500.toString(), "ABREVIADO?sdb({5305,5315,5325,5335,5345,5355,540,541,542,543,545,546,547,548,551,5525,5590,5593,565,566})-sab({5395,549,5945,5955,597,598})"
																				+ ":sdb({5305,5315,5325,5335,5345,5355,540,541,542,543,545,546,547,548,551,5525,5590,565,566})-sab({5395,549,5935,5945,5955,596,597,598})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112600.toString(), "sdb({480,567})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA112700.toString(), "sdb({57})");
	}
	
	static { // BALANCE: PATRIMONIO NETO Y PASIVO
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121110.toString(),"sab({100,101,102})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121120.toString(),"sdb({1030,1040})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121200.toString(),"sab({110})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121300.toString(),"ABREVIADO?sab({112,113,114,115,119}):sab({112,113,114,119})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121350.toString(),"ABREVIADO?sab({}):0.0");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121360.toString(),"ABREVIADO?sab({112,113,114,115,119}):sab({112,113,114,119})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121400.toString(),"sdb({108,109})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121500.toString(),"sab({120})-sdb({121})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121600.toString(),"sab({118})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121700.toString(),"sab({129})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121800.toString(),"sdb({557})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2121900.toString(),"ABREVIADO?sab({111})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2122000.toString(),"ABREVIADO?sab({133,1340,1341,137})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BP2122000.toString(),"0.0"); // TODO NECA 5.ª 9 (PYMES)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2123000.toString(),"sab({130,131,132})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131100.toString(),"sab({14})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131220.toString(),"sab({1605,170})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131230.toString(),"sab({1625,174})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131290.toString(),"ABREVIADO?sab({1615,1635,171,172,173,175,176,177,178,179,180,185,189})"
																				+ ":sab({1615,1635,171,172,173,175,176,177,179,180,185})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131300.toString(),"sab({1603,1604,1613,1614,1623,1624,1633,1634})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131400.toString(),"sab({479})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131500.toString(),"sab({181})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131600.toString(),"sab({42})");	// TODO NECA 6.ª 16 (ABREVIADO)
																						// TODO NECA 5.ª 11 (PYMES)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2131700.toString(),"sab({150,153,154})"); 
			//***** la formula la he cogido de Mod2002014Initialization *****//"0.0"); 	// TODO 15; (NECA 6.ª 17) (ABREVIADO)
																						// TODO 15;NECA 5.ª 12 (PYMES)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232100.toString(),"ABREVIADO?sab({585,586,587,588,589})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232200.toString(),"sab({499,529})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232320.toString(),"sab({5105,520,527})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232330.toString(),"sab({5125,524})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232390.toString(),"ABREVIADO?sabPositivo({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,551,5525,555,5565,5566,5595,5598,560,561,569})-sdb({1034,1044,190,192})"
																				+ ":sabPositivo({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,551,5525,555,5565,5566,5595,560,561})-sdb({1034,1044,190,192})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232400.toString(),"sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5523,5524,5563,5564})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232580.toString(),"sab({400,401,403,404,405})-sdb({406})"); // TODO ¿¿??
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232581.toString(),"sab({40001})"); 	// TODO NECA 6.ª 16 (ABREVIADO)
																								// TODO NECA 5.ª 11 (PYMES)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232582.toString(),"sab({400,401,403,404,405})-sdb({406,40001})"); 
																						// TODO NECA 6.ª 16 (ABREVIADO)
																						// TODO NECA 5.ª 11 (PYMES)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232590.toString(),"ABREVIADO?sab({41,438,465,466,475,476,477})"
																				+ ":sab({41,438,465,475,476,477})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232600.toString(),"sab({485,568})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.BA2232700.toString(),"sab({502,507})");//  ,558})"); 	
																						//TODO 502,507; NECA 6.ª 17 (ABREVIADO)
																						// TODO 502,507;NECA 5.ª 12 (PYMES)
	}
	static { // CUENTA DE PERDIDAS Y GANANCIAS 
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40100.toString(), "sap({700,701,702,703,704,705})-sdp({706,708,709})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40200.toString(), "sap({71,7930})-sdp({6930})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40300.toString(), "sap({73})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40400.toString(), "sap({606,608,609,61,7931,7932,7933})-sdp({600,601,602,607,6931,6932,6933})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40500.toString(), "sap({740,747,75})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40600.toString(), "ABREVIADO?sap({7950,7957})-sdp({64})"
																				+ ":sap({64})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40700.toString(), "sap({636,639,794,7954})-sdp({62,631,634,65,694,695})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40800.toString(), "sap({68})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA40900.toString(), "sap({746})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41000.toString(), "ABREVIADO?sap({7951,7952,7955,7956})"
																				+ ": sap({7951,7952,7955})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41100.toString(), "sap({770,771,772,790,791,792})-sdp({670,671,672,690,691,692})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41200.toString(), "sap({774})"); // TODO 774; (NECA 7.ª 6)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41300.toString(), "sap({778})-sdp({678})"); // TODO (678),778; (NECA 7.ª 9) (ABREVIADO)
																					 // TODO (678),778;NECA 6.ª 6 (PYMES)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41430.toString(), "sap({746})"); // TODO 746; (NECA 7.ª 4) (ABREVIADO)
																					 // TODO 746;NECA 6.ª 4 (PYMES)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41490.toString(), "ABREVIADO?sap({760,761,762,767,769})"
																				+ ": sap({760,761,762,769})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41500.toString(), "sap({660,661,662,664,665,669})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41600.toString(), "sap({763})-sdp({663})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41700.toString(), "sap({768})-sdp({668})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41800.toString(), "sap({766,773,775,796,797,798,799})-sdp({666,667,673,675,696,697,698,699})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA41900.toString(), "sap({6301,638})-sdp({6300,633})"); 
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA42110.toString(), "0.0"); // TODO -
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA42120.toString(), "0.0"); // TODO -
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PA42130.toString(), "0.0"); // TODO -
 	}
	
	static { //ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150010.toString(), "ABREVIADO?sab({900,991,992})+sdb({800,89})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150020.toString(), "ABREVIADO?sab({910})+sdb({810})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150030.toString(), "ABREVIADO?sab({94})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150040.toString(), "ABREVIADO?sab({95})+sdb({85})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150050.toString(), "(sab({900})+sdb({860}))"); // TODO (860),900; (NECA 8.ª 1.2)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150060.toString(), "(sab({920})+sdb({820}))"); // TODO (820),920; (NECA 8.ª 1.3)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150070.toString(), "ABREVIADO?sab({8301,834,835,838})+sdb({8300,833})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150080.toString(), "ABREVIADO?sab({902,993,994})+sdb({802})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150090.toString(), "ABREVIADO?sab({912})+sdb({812})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150100.toString(), "ABREVIADO?sdb({84})");
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150110.toString(), "(sab({902})+sdb({862}))"); // TODO (862),902; (NECA 8.ª 1.2)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150120.toString(), "(sab({921})+sdb({821}))"); // TODO (821),921; (NECA 8.ª 1.3)
		INITIALIZE_EXPRESSION_MAP.put(D2DepositHeaderKey.PNA150130.toString(), "ABREVIADO?sab({8301})+sdb({836,837})");
	}


	static { //AP3 APLICACION DE RESULTADOS
		// BASE DE REPARTO
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391000.toString(),"sab({129})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391001.toString(),"sab({120})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391002.toString(),"sab({113})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391003.toString(),"0.0");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391004.toString(),"sab({129,120,113})");

		//APLICACIÓN A
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391005.toString(),"sab({112})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391006.toString(),"0.0");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391007.toString(),"sab({114})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391008.toString(),"sab({113})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391009.toString(),"0.0");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391010.toString(),"sab({120})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391011.toString(),"sab({121})");
		INITIALIZE_EXPRESSION_MAP_D2.put(D2DepositKey.MA391012.toString(),"sab({112,114,113,121,120})");
	}

	static { //AP5 INMOVILIZADO MATERIAL, INTANGIBLE E INVERSIONES INMOBILIARIAS
	
	}

	static { //AP6 ACTIVOS FINANCIEROS
		
	}
	
	static { //AP7 PASIVOS FINANCIEROS
		
	}

	static { //AP10 INGRESOS Y GASTOS
		
	}
	
	static { //AP11 SUBVENCIONES, DONACIONES Y LEGADOS
		
	}
	
	static { //AP12 OPERACIONES CON PARTES VINCULANTES
		
	}
	
	static { //AP13 OTRA INFORMACIÓN
		
	}

	static { //AP14 INFORMACIÓN SOBRE MEDIOAMBIENTE
		
	}
	
	static { //AP15 INFORMACIÓN SOBRE APLAZAMIENTOS DE PAGOS EFECTUADOS
		
	}
}

