package com.esferalia.aon.occam.server.accounting;

public enum AccBalanceAbbreviateKey implements IBalanceKey{
	// ACTIVO
	 BA00101(0,"ACTIVO NO CORRIENTE"
		,null
		,"BA00102+BA00111+BA00115+BA00118+BA00126+BA00134+BA00135"
	 )
 	,BA00102(1,"Inmovilizado intangible"
		,null
 		,"BA00106+BA00110"
	)
 	,BA00106(2,"Fondo de comercio"
 		,"sdb({204})"
 		,null				
	)
 	,BA00110(2,"Resto (A, P)"
		,"sdb({201,202,203,205,206,207,208,209})-sab({280,290})"
		,null				
	)
 	,BA00111(1,"Inmovilizado material"
		,"sdb({210,211,212,213,214,215,216,217,218,219,23})-sab({2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919,2810,2811,2910,2911})"
		,null
	)
 	,BA00115(1,"Inversiones inmobiliarias"
		,"sdb({22})-sab({282,292})"
		,null
	)
 	,BA00118(1,"Inversiones en empresas del grupo y asociadas a largo plazo"
		,null
		,"BA00119+BA00125"
	)
 	,BA00119(2,"Instrumentos de patrimonio"
		,"sdb({2403,2404})-sab({2493,2494,293})"
		,null				
	)
 	,BA00125(2,"Resto (A, P)"
		,"sdb({2423,2424,2413,2414})-sab({2953,2954,2943,2944})"
		,null				
	)
 	,BA00126(1,"Inversiones financieras a largo plazo"
		,null
		,"BA00127+BA00133"
	)
 	,BA00127(2,"Instrumentos de patrimonio"
		,"sdb({2405,250})-sab({2495,259})"
		,null
	)
 	,BA00133(2,"Resto (A, P)"
		,"sdb({2425,252,253,254,2415,251,258,26,257,255})-sab({2955,298,2945,297})"
		,null
	)
 	,BA00134(1,"Activos por impuesto diferido"
		,"sdb({474})"
		,null
	)
 	,BA00135(1,"Deudores comerciales no corrientes"
		,null
		,null
	)
 	,BA00136(0,"ACTIVO CORRIENTE"
		,null
		,"BA00137+BA00138+BA00149+BA00160+BA00168+BA00176+BA00177"
	)
 	,BA00137(1,"Activos no corrientes mantenidos para la venta (N, A)"
		,"sdb({580,581,582,583,584}) - sab({599})"
		,null
	)
 	,BA00138(1,"Existencias"
		,"sdb({30,31,32,33,34,35,36,407})-sab({(39)})"
		,null
	)
 	,BA00149(1,"Deudores comerciales y otras cuentas a cobrar"
		,null
		,"BA00150+BA00158+BA00159"
	)
 	,BA00150(2,"Clientes por ventas y prestaciones de servicios"
		,null
 		,"BA00151+BA00152"
	)
 	,BA00151(3,"Clientes por ventas y prestaciones de servicios a largo plazo"
		,"sdb({430,431,432,435,436})-sab({437,490,4935})"
		,null
	)
 	,BA00152(3,"Clientes por ventas y prestaciones de servicios a corto plazo"
		,null
		,null
	)
 	,BA00158(1,"Accionistas (socios) por desembolsos exigidos"
		,"sdb({5580})"
		,null
	)
 	,BA00159(1,"Otros deudores"
		,"sdb({44,460,470,471,472,473,544})"
		,null
 	)
 	,BA00160(1,"Inversiones en empresas del grupo y asociadas a corto plazo"
		,null
		,"BA00161+BA00167"
	)
 	,BA00161(2,"Instrumentos de patrimonio"
 		,"sdb({5303,5304})-sab({5393,5394,593})"
 		,null
	)
 	,BA00167(2,"Resto (A, P)"
		,"sdb({5313,5314, 5323, 5324,5333, 5334, 5343,5344, 5353, 5354,5524})+sdbPositivo(5523)-sab({5943,5944,5953,5954})"
		,null
 	)
 	,BA00168(1,"Inversiones financieras a corto plazo"
		,null
		,"BA00169+BA00175"
	)
 	,BA00169(2,"Instrumentos de patrimonio"
		,"sdb({5305,540})-sab({5395,549})"
		,null
	)
 	,BA00175(2,"Resto"
		,"sdb({5325,5345,542,543,547,5315,5335,541,546,5590,5593,5355,545,548,5525,565,566})-sab({5955,598,5945,597})+sdbPositivo({551})"
		,null
	)
 	,BA00176(1,"Periodificaciones a corto plazo"
		,"sdb({480,567})"
		,null
	)
 	,BA00177(1,"Efectivo y otros activos l\u00EDquidos equivalentes"
		,"sdb({570,571,572,573,574,575,576})"
		,null
 	)
 	,BA00180(0,"TOTAL ACTIVO"
		,null
		,"BA00101+BA00136"
	)
 	// PASIVO Y PATRIMONIO
	,BP00185(0,"PATRIMONIO NETO"
		,null
		,"BP00186+BP00209+BP00202"
	)
 	,BP00186(1,"Fondos propios"
		,null
		,"BP00187+BP00190+BP00191+BP00194+BP00195+BP00198+BP00199+BP00200+BP00201"
	)
 	,BP00187(2,"Capital"
		,null
		,"BP00188+BP00189"
 	)
 	,BP00188(3,"Capital escriturado"
		,"sab({100,101,102})"
		,null
	)
 	,BP00189(3,"(Capital no exigido)"
		,"sdb({1030,1040})"
		,null
	)
 	,BP00190(2,"Prima de emisi\u00F3n"
		,"sab({110})"
		,null
	)
 	,BP00191(2,"Reservas"
		,null
		,"BP00193+BP01001+BP01002"
	)
 	,BP00193(3,"Otras reservas)"
		,"sab({112,113,114,115,119})"
		,null
	)
 	,BP01001(3,"Reserva de capitalizaci\u00F3n"
		,null
		,null
	)
 	,BP01002(3,"Reserva de nivelaci\u00F3n"
		,null
		,null
	)
 	,BP00194(2,"(Acciones y participaciones en patrimonio propias)"
		,"sdb({108,109})"
		,null
 	)
 	,BP00195(2,"Resultados de ejercicios anteriores"
		,"sab({120})-sdb({121})"
		,null
	)
 	,BP00198(2,"Otras aportaciones de socios"
		,"sab({118})"
		,null
	)
 	,BP00199(2,"Resultado del ejercicio"
		,"sab({129,7}) - sdb({6})"
		,null
	)
 	,BP00200(2,"(Dividendo a cuenta)"
		,"sdb({557})"
		,null
	)
 	,BP00201(2,"Otros instrumentos de patrimonio neto"
		,"sab({111})"
		,null
	)
 	,BP00202(1,"Ajustes por cambio de valor"
		,"sab({1340,136,135,137})"
		,null
	)
 	,BP00209(1,"Subvenciones, donaciones y legados recibidos"
		,"sab({130,131,132})"
		,null
	)
 	,BP00210(0,"PASIVO NO CORRIENTE"
		,null
		,"BP00211+BP00216+BP00223+BP00224+BP00225+BP00226+BP00227"
 	)
 	,BP00211(1,"Provisiones a largo plazo"
		,"sab({14})"
		,null
	)
 	,BP00216(1,"Deudas a largo plazo"
		,null
		,"BP00218+BP00219+BP00222"
	)
 	,BP00218(2,"Deudas con entidades de cr\u00E9dito"
		,"sab({1605,170})"
		,null
	)
 	,BP00219(2,"Acreedores por arrendamiento financiero"
		,"sab({1625,174})"
		,null
	)
 	,BP00222(2,"Otras deudas a largo plazo"
		,"sab({1615,1635,171,172,173,175,176,177,178,179,180,185,189})"
		,null
	)
 	,BP00223(1,"Deudas con empresas del grupo y asociadas a largo plazo"
		,"sab({1603,1604,1613,1614,1623,1624,1633,1634})"
		,null
	)
 	,BP00224(1,"Pasivos por impuesto diferido"
		,"sab({479})"
		,null
	)
 	,BP00225(1,"Periodificaciones a largo plazo"
		,"sab({181})"
		,null
	)
 	,BP00226(1,"Acreedores comerciales no corrientes"
		,null
		,null
	)
 	,BP00227(1,"Deuda con caracter\u00EDsticas especiales a largo plazo"
		,"sab({15})"
		,null
 	)
 	,BP00228(0,"PASIVO CORRIENTE"
		,null
		,"BP00229+BP00230+BP00231+BP00238+BP00239+BP00250+BP00251"
	)
 	,BP00229(1,"Pasivos vinculados con activos no corr. mantenidos para la venta"
		,"sab({585,586,587,588,589})"
		,null
	)
 	,BP00230(1,"Provisiones a corto plazo"
		,"sab({499,529})"
		,null
	)
 	,BP00231(1,"Deudas a corto plazo"
		,null
		,"BP00233+BP00234+BP00237"
	)
 	,BP00232(2,"Obligaciones y otros valores negociables"
		,"sab({500,501,505,506})"
		,null
	)
 	,BP00233(2,"Deudas con entidades de cr\u00E9dito"
		,"sab({5105,520,527})"
		,null
	)
 	,BP00234(2,"Acreedores por arrendamiento financiero"
		,"sab({5125,524})"
		,null
	)
 	,BP00237(2,"Otras deudas a corto plazo"
		,"sab({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,525,555,5565,5566,5595,5598,560,561,569})+sabPositivo(551)-sdb({1034,1044,190,192})"
		,null
	)
 	,BP00238(1,"Deudas con empresas del grupo y asociadas a corto plazo"
		,"sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5524,5563,5564})+sabPositivo({5523})"
		,null
	)
 	,BP00239(1,"Acreedores comerciales y otras cuentas a pagar"
		,null
		,"BP00240+BP00249"
	)
 	,BP00240(2,"Proveedores"
		,null
		,"BP00241+BP00242"
 	)
 	,BP00241(3,"Proveedores a largo plazo"
		,null
		,null
	)
 	,BP00242(3,"Proveedores a corto plazo"
		,"sab({400,401,405})-sdb({406})"
		,null
 	)
 	,BP00249(3,"Otros acreedores"
		,"sab({41,438,465,466,475,476,477})"
		,null
	)
 	,BP00250(1,"Periodificaciones a corto plazo"
		,"sab({485,568})"
		,null
	)
 	,BP00251(1,"Deuda con caracter\u00EDsticas especiales a corto plazo"
		,"sab({502,507})"
		,null
	)
 	,BP00252(0,"TOTAL PATRIMONIO NETO Y PASIVO"
		,null
		,"BP00185+BP00210+BP00228"
 	)
 	;

	private int level;
	private String name;
	private String  initialExpressionProvider;
	private String  computeExpressionProvider;
	
	private AccBalanceAbbreviateKey(int level
			,String name
			,String initialExpressionProvider
			,String computeExpressionProvider) {
		this.level = level;
		this.name = name;
		this.initialExpressionProvider = initialExpressionProvider;
		this.computeExpressionProvider = computeExpressionProvider;
	}
	
	@Override
	public String getCode() {
		return this.toString();
	}
	@Override
	public int getLevel() {
		return level;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getComputeExpression() {
		return computeExpressionProvider;
	}
	@Override
	public String getInitialExpression() {
		return initialExpressionProvider;
	}
	public AccBalanceAbbreviateKey getBalanceKey(String keyObject) {
		return AccBalanceAbbreviateKey.valueOf(keyObject);
	}
}


