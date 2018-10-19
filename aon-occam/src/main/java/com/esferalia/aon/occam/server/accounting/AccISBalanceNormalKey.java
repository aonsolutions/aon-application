package com.esferalia.aon.occam.server.accounting;

public enum AccBalanceNormalKey implements IBalanceKey{
	// ACTIVO
	 BA00101(0,"ACTIVO NO CORRIENTE"
		,null
		,"BA00102+BA00111+BA00115+BA00118+BA00126+BA00134+BA00135"
	 )
 	,BA00102(1,"Inmovilizado intangible"
		,null
 		,"BA00103+BA00104+BA00105+BA00106+BA00107+BA00108+BA00700+BA00109"
	)
 	,BA00103(2,"Desarrollo"
 		,"sdb({201})-sab({2801,2901})"
		,null
	)
 	,BA00104(2,"Concesiones"
		,"sdb({202})-sab({2802,2902})"
		,null
	)
 	,BA00105(2,"Patentes, licencias, marcas y similares"
		,"sdb({203})-sab({2803,2903})"
		,null
	)
 	,BA00106(2,"Fondo de comercio"
 		,"sdb({204})"
 		,null				
	)
 	,BA00107(2,"Aplicaciones inform\u00E1ticas"
		,"sdb({206})-sab({2806,2906})"
		,null				
	)
 	,BA00108(2,"Investigaci\u00F3n"
		,"sdb({200})-sab({2800})"
		,null				
 	)
 	,BA00700(2,"Propiedad intelectual"
 		,null
 		,null
	)
 	,BA00109(2,"Otro inmovilizado intangible"
		,"sdb({205,209})-sab({2805,2905})"
		,null				
	)
 	,BA00111(1,"Inmovilizado material"
		,null
		,"BA00112+BA00113+BA00114"
	)
 	,BA00112(2,"Terrenos y construcciones"
		,"sdb({210,211})-sab({2810,2811,2910,2911})"
		,null				
	)
 	,BA00113(2,"Instalaciones t\u00E9cnicas y otro inmovilizado material"
		,"sdb({212,213,214,215,216,217,218,219})-sab({2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919})"
		,null				
	)
 	,BA00114(2,"Inmovilizado en curso y anticipos"
		,"sdb({23})"
		,null				
 	)
 	,BA00115(1,"Inversiones inmobiliarias"
		,null
		,"BA00116+BA00117"
	)
 	,BA00116(2,"Terrenos"
		,"sdb({220})-sab({2920})"
		,null				
	)
 	,BA00117(2,"Construcciones"
		,"sdb({221})-sab({282,2921})"
		,null				
 	)
 	,BA00118(1,"Inversiones en empresas del grupo y asociadas a largo plazo"
		,null
		,"BA00119+BA00120+BA00121+BA00122+BA00123+BA00124"
	)
 	,BA00119(2,"Instrumentos de patrimonio"
		,"sdb({2403,2404})-sab({2493,2494,293})"
		,null				
	)
 	,BA00120(2,"Cr\u00E9ditos a empresas"
		,"sdb({2423,2424})-sab({2953,2954})"
		,null
	)
 	,BA00121(2,"Valores representativos de deuda"
		,"sdb({2413,2414})-sab({2943,2944})"
		,null
	)
 	,BA00122(2,"Derivados"
		,null
		,null
	)
 	,BA00123(2,"Otros activos financieros"
		,null
		,null
	)
 	,BA00124(2,"Otras inversiones"
		,null
		,null
	)
 	,BA00126(1,"Inversiones financieras a largo plazo"
		,null
		,"BA00127+BA00128+BA00129+BA00130+BA00131+BA00132"
	)
 	,BA00127(2,"Instrumentos de patrimonio"
		,"sdb({2405,250})-sab({2495,259})"
		,null
	)
 	,BA00128(2,"Cr\u00E9ditos a terceros"
		,"sdb({2425,252,253,254})-sab({2955,298})"
		,null
	)
 	,BA00129(2,"Valores representativos de deuda"
		,"sdb({2415,251})-sab({2945,297})"
		,null
	)
 	,BA00130(2,"Derivados"
		,"sdb({255})"
		,null
 	)
 	,BA00131(2,"Otros activos financieros"
		,"sdb({258,26})"
		,null
	)
 	,BA00132(2,"Otras inversiones"
		,"sdb({257})"
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
 	,BA00137(1,"Activos no corrientes mantenidos para la venta"
		,"sdb({580,581,582,583,584}) - sab({599})"
		,null
	)
 	,BA00138(1,"Existencias"
		,null
		,"BA00139+BA00140+BA00141+BA00144+BA00147+BA00148+BA00701"
	)
 	,BA00139(2,"Comerciales"
		,"sdb({30})-sab({390})"
		,null
	)
 	,BA00140(2,"Materias primas y otros aprovisionamientos"
		,"sdb({31,32})-sab({391,392})"
		,null
	)
 	,BA00141(2,"Productos en curso"
		,null
 		,"BA00142+BA00143"
	)
 	,BA00142(3,"De ciclo largo de producci\u00F3n"
		,null
		,null
	)
 	,BA00143(3,"De ciclo corto de producci\u00F3n"
		,"sdb({33,34})-sab({393,394})"
		,null
	)
 	,BA00144(2,"Productos terminados"
		,null
		,"BA00145+BA00146"
	)
 	,BA00145(3,"De ciclo largo de producci\u00F3n"
		,null
		,null
	)
 	,BA00146(3,"De ciclo corto de producci\u00F3n"
		,"sdb({35})-sab({395})"
		,null
	)
 	,BA00147(2,"Subproductos, residuos y materiales recuperados"
		,"sdb({36})-sab({396})"
		,null
	)
 	,BA00148(2,"Anticipos a proveedores"
		,"sdb({407})"
		,null
	)
 	,BA00701(2,"Derechos de emisi\u00F3n de gases de efecto invernadero"
		,"sdb({207})"
		,null
	)
 	,BA00149(1,"Deudores comerciales y otras cuentas a cobrar"
		,null
		,"BA00150+BA00153+BA00154+BA00155+BA00156+BA00157+BA00158"
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
 	,BA00153(2,"Clientes empresas del grupo y asociadas"
		,"sdb({433,434})-sab({4933,4934})"
		,null
	)
 	,BA00154(2,"Deudores varios"
		,"sdb({44})"
		,null
	)
 	,BA00155(2,"Personal"
		,"sdb({460,544})"
		,null
	)
 	,BA00156(2,"Activos por impuesto corriente"
		,"sdb({4709})"
		,null
	)
 	,BA00157(2,"Otros cr\u00E9ditos con las Administraciones p\u00FAblicas"
		,"sdb({4700,4708,471,472,473})"
		,null
	)
 	,BA00158(2,"Accionistas (socios) por desembolsos exigidos"
		,"sdb({5580})"
		,null
	)
 	,BA00160(1,"Inversiones en empresas del grupo y asociadas a corto plazo"
		,null
		,"BA00161+BA00162+BA00163+BA00164+BA00165+BA00166"
	)
 	,BA00161(2,"Instrumentos de patrimonio"
 		,"sdb({5303,5304})-sab({5393,5394,593})"
 		,null
	)
 	,BA00162(2,"Cr\u00E9ditos a empresas"
		,"sdb({5323,5324,5343,5344})-sab({5953,5954})"
		,null
	)
 	,BA00163(2,"Valores representativos de deuda"
		,"sdb({5313,5314,5333,5334})-sab({5943,5944})"
		,null
	)
 	,BA00164(2,"Derivados"
		,null
		,null
	)
 	,BA00165(2,"Otros activos financieros"
		,"sdb({5353,5354,5524})+sdbPositivo(5523)"
		,null
	)
 	,BA00166(2,"Otras inversiones"
		,null
		,null
	)
 	,BA00168(1,"Inversiones financieras a corto plazo"
		,null
		,"BA00169+BA00170+BA00171+BA00172+BA00173+BA00174"
 	)
 	,BA00169(2,"Instrumentos de patrimonio"
		,"sdb({5305,540})-sab({5395,549})"
		,null
	)
 	,BA00170(2,"Cr\u00E9ditos a empresas"
		,"sdb({5325,5345,542,543,547})-sab({5955,598})"
		,null
	)
 	,BA00171(2,"Valores representativos de deuda"
		,"sdb({5315,5335,541,546})-sab({5945,597})"
		,null
	)
 	,BA00172(2,"Derivados"
		,"sdb({5590,5593})"
		,null
	)
 	,BA00173(2,"Otros activos financieros"
		,"sdb({5355,545,548,5525,565,566})+sdbPositivo({551})"
		,null
	)
 	,BA00174(2,"Otras inversiones"
		,null
		,null
	)
 	,BA00176(1,"Periodificaciones a corto plazo"
		,"sdb({480,567})"
		,null
	)
 	,BA00177(1,"Efectivo y otros activos l\u00EDquidos equivalentes"
		,null
		,"BA00178+BA00179"
	)
 	,BA00178(2,"Tesorer\u00EDa"
		,"sdb({570,571,572,573,574,575})"
		,null
	)
 	,BA00179(2,"Otros activos l\u00EDquidos equivalentes"
		,"sdb({576})"
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
 	,BP00191(1,"Reservas"
		,null
		,"BP00192+BP00193+BP00702+BP01001+BP01002"
	)
 	,BP00192(2,"Legal y estatutarias"
		,"sab({112,1141})"
		,null
	)
 	,BP00193(2,"Otras reservas)"
		,"sab({113,1140,1142,1143,1144,115,119})"
		,null
	)
 	,BP00702(2,"Reserva de revalorizaci\u00F3n (Ley 16/2012 de 27 de diciembre)"
		,null
		,null
	)
 	,BP01001(2,"Reserva de capitalizaci\u00F3n"
		,null
		,null
	)
 	,BP01002(2,"Reserva de nivelaci\u00F3n"
		,null
		,null
	)
 	,BP00194(1,"(Acciones y participaciones en patrimonio propias)"
		,"sdb({108,109})"
		,null
 	)
 	,BP00195(1,"Resultados de ejercicios anteriores"
		,null 
		,"BP00196+BP00197"
	)
 	,BP00196(2,"Remanente"
		,"sab({120})"
		,null
	)
 	,BP00197(2,"(Resultados negativos de ejercicios anteriores)"
		,"sab({121})"
		,null
	)
 	,BP00198(1,"Otras aportaciones de socios"
		,"sab({118})"
		,null
	)
 	,BP00199(1,"Resultado del ejercicio"
		,"sab({129,7}) - sdb({6})"
		,null
	)
 	,BP00200(1,"(Dividendo a cuenta)"
		,"sdb({557})"
		,null
	)
 	,BP00201(1,"Otros instrumentos de patrimonio neto"
		,"sab({111})"
		,null
	)
 	,BP00202(1,"Ajustes por cambio de valor"
		,null
		,"BP00203+BP00204+BP00205+BP00206+BP00207"
	)
 	,BP00203(2,"Activos financieros disponibles para la venta"
		,"sab({133})"
		,null
	)
 	,BP00204(2,"Operaciones de cobertura"
		,"sab({1340})"
		,null
	)
 	,BP00205(2,"Activos no corrientes y pasivos vinculados, mantenidos para la venta"
		,"sab({136})"
		,null
	)
 	,BP00206(2,"Diferencia de conversi\u00F3n"
		,"sab({135})"
		,null
	)
 	,BP00207(2,"Otros"
		,"sab({137})"
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
		,null
		,"BP00212+BP00213+BP00214+BP00215"
	)
 	,BP00212(2,"Obligaciones por prestaciones a largo plazo al personal"
		,"sab({140})"
		,null
	)
 	,BP00213(2,"Actuaciones medioambientales"
		,"sab({145})"
		,null
	)
 	,BP00214(2,"Provisiones por reestructuraci\u00F3n"
		,"sab({146})"
		,null
	)
 	,BP00215(2,"Otras provisiones"
		,"sab({141,142,143,147})"
		,null
	)
 	,BP00216(1,"Deudas a largo plazo"
		,null
		,"BP00217+BP00218+BP00219+BP00220+BP00221"
	)
 	,BP00217(2,"Obligaciones y otros valores negociables"
		,"sab({177,178,179})"
		,null
	)
 	,BP00218(2,"Deudas con entidades de cr\u00E9dito"
		,"sab({1605,170})"
		,null
	)
 	,BP00219(2,"Acreedores por arrendamiento financiero"
		,"sab({1625,174})"
		,null
	)
 	,BP00220(2,"Derivados"
		,"sab({176})"
		,null
	)
 	,BP00221(2,"Otros pasivos financieros"
		,"sab({1615,1635,171,172,173,175,180,185,189})"
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
		,null
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
 	,BP00229(1,"Pasivos vinculados con activos no corr. mantenidos para la venta(N, A)"
		,"sab({585,586,587,588,589})"
		,null
	)
 	,BP00230(1,"Provisiones a corto plazo"
		,null
		,"BP00703+BP00704"
	)
 	,BP00703(2,"Provisiones por derechos de emisi\u00F3n de gases de efecto invernadero"
		,"sab({529})"
		,null
	)
 	,BP00704(2,"Otras provisiones"
		,null
		,null
	)
 	,BP00231(1,"Deudas a corto plazo"
		,null
		,"BP00232+BP00233+BP00234+BP00235+BP00236"
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
 	,BP00235(2,"Derivados"
		,"sab({5595,5598})"
		,null
	)
 	,BP00236(2,"Otros pasivos financieros"
		,"sab({194,509,5115,5135,5145,521,522,523,525,526,528,5525,555,5565,5566,560,561,569})+sabPositivo(551)-sdb({1034,1044,190,192})"
		,null
	)
 	,BP00238(1,"Deudas con empresas del grupo y asociadas a corto plazo"
		,"sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5524,5563,5564})+sabPositivo({5523})"
		,null
	)
 	,BP00239(1,"Acreedores comerciales y otras cuentas a pagar"
		,null
		,"BP00240+BP00243+BP00244+BP00245+BP00246+BP00247+BP00248"
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
 	,BP00243(2,"Proveedores, empresas del grupo y asociadas"
		,"sab({403,404})"
		,null
	)
 	,BP00244(2,"Acreedores varios"
		,"sab({41})"
		,null
	)
 	,BP00245(2,"Personal (remuneraciones pendientes de pago)"
		,"sab({465,466})"
		,null
	)
 	,BP00246(2,"Pasivos por impuesto corriente"
		,"sab({4752})"
		,null
	)
 	,BP00247(2,"Otras deudas con las Administraciones p\u00FAblicas"
		,"sab({4750,4751,4758,476,477})"
		,null
	)
 	,BP00248(2,"Anticipos de clientes"
		,"sab({438})"
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
	
	private AccBalanceNormalKey(int level
			,String name
			,String  initialExpressionProvider
			,String  computeExpressionProvider) {
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
	public AccBalanceNormalKey getBalanceKey(String keyObject) {
		return AccBalanceNormalKey.valueOf(keyObject);
	}
}


