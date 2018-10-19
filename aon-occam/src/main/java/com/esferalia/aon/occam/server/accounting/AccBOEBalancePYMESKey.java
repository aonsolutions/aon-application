package com.esferalia.aon.occam.server.accounting;

public enum AccBOEBalancePYMESKey implements IBalanceKey{
	// ACTIVO
	 ANC(0,false,"A)","ACTIVO NO CORRIENTE",null,"ANCI+ANCII+ANCIII+ANCIV+ANCV+ANCVI")
		,ANCI(1,false,"I","Inmovilizado intangible","sdb({20})-sab({280,290})",null)
		,ANCII(1,false,"II","Inmovilizado material","sdb({21,23})-sab({281,291})",null)
		,ANCIII(1,false,"III","Inversiones inmobiliarias","sdb({22})-sab({282,292})",null)
		,ANCIV(1,false,"IV","Inversiones en empresas del grupo y asociadas a largo plazo","sdb({2403,2404,2413,2414,2423,2424})-sab({2493,2494,293,2943,2944,2953,2954})",null)
		,ANCV(1,false,"V","Inversiones financieras a largo plazo","sdb({2405,2415,2425,250,251,252,253,254,255,257,258,26})-sab({2495,259,2945,2955,297,298})",null)
		,ANCVI(1,false,"VI","Activos por impuesto diferido","sdb({474})",null)
	
	,AC(0,false,"B)","ACTIVO CORRIENTE",null,"ACI+ACII+ACIII+ACIV+ACV+ACVI")
		,ACI(1,false,"I","Existencias","sdb({30,31,32,33,34,35,36,407})-sab({(39)})",null)
		,ACII(1,false,"II","Deudores comerciales y otras cuentas a cobrar",null,"ACII1+ACII2+ACII3")
			,ACII1(2,true,"1","Clientes por ventas y prestaciones de servicios","sdb({430,431,432,435,436})-sab({437,490,493})",null)
			,ACII2(2,true,"2","Accionistas (socios) por desembolsos exigidos","sdb({5580})",null)
			,ACII3(2,true,"3","Otros deudores","sdb({44,460,470,471,472,473,544})",null)
		,ACIII(1,false,"III","Inversiones en empresas del grupo y asociadas a corto plazo","sdb({5303,5304,5313,5314, 5323, 5324,5333, 5334, 5343,5344, 5353, 5354,5524})+sdbPositivo(5523)-sab({5393,5394,593,5943,5944,5953,5954})",null)
		,ACIV(1,false,"IV","Inversiones financieras a corto plazo","sdb({5305,540,5325,5345,542,543,547,5315,5335,541,546,5590,5593,5355,545,548,5525,565,566})-sab({5955,598,5945,597,5395,549})+sdbPositivo({551})",null)
		,ACV(1,false,"V","Periodificaciones a corto plazo","sdb({480,567})",null)
		,ACVI(1,false,"VI","Efectivo y otros activos l\u00EDquidos equivalentes","sdb({57})",null)
	,TC(0,false,"(A+B)","TOTAL ACTIVO",null,"ANC + AC")

//PASIVO Y PATRIMONIO
	,PN(0,false,"A)","PATRIMONIO NETO",null,"PNA1+PNA2")
		,PNA1(1,false,"A-1)","Fondos propios",null,"PNA1I+PNA1II+PNA1III+PNA1IV+PNA1V+PNA1VI+PNA1VII+PNA1VIII")
			,PNA1I(2,false,"I","Capital",null,"PNA1I1+PNA1I2")
				,PNA1I1(3,true,"1","Capital escriturado","sab({100,101,102})",null)
				,PNA1I2(3,true,"2","(Capital no exigido)","sdb({1030,1040})",null)
			,PNA1II(2,false,"II","Prima de emisi\u00F3n","sab({110})"	,null)
			,PNA1III(2,false,"III","Reservas","sab({112,113,114,115,119})",null)
			,PNA1IV(2,false,"IV","(Acciones y participaciones en patrimonio propias)","sdb({108,109})",null)
			,PNA1V(2,false,"V","Resultados de ejercicios anteriores","sab({120})-sdb({121})",null)
			,PNA1VI(2,false,"VI","Otras aportaciones de socios","sab({118})",null)
			,PNA1VII(2,false,"VII","Resultado del ejercicio","sab({129,7}) - sdb({6})",null)
			,PNA1VIII(2,false,"VIII","(Dividendo a cuenta)","sdb({557})",null)
		,PNA2(1,false,"A-2)","Subvenciones, donaciones y legados recibidos","sab({130,131,132})",null)
		
	,PNC(0,false,"B)","PASIVO NO CORRIENTE",null,"PNCI+PNCII+PNCIII+PNCIV+PNCV")
		,PNCI(1,false,"I","Provisiones a largo plazo","sab({14})",null)
		,PNCII(1,false,"II","Deudas a largo plazo",null,"PNCII1+PNCII2+PNCII3")
			,PNCII1(2,true,"1","Deudas con entidades de cr\u00E9dito","sab({1605,170})",null)
			,PNCII2(2,true,"2","Acreedores por arrendamiento financiero","sab({1625,174})",null)
			,PNCII3(2,true,"3","Otras deudas a largo plazo","sab({1615,1635,171,172,173,175,176,177,178,179,180,185,189})",null)
		,PNCIII(1,false,"III","Deudas con empresas del grupo y asociadas a largo plazo","sab({1603,1604,1613,1614,1623,1624,1633,1634})",null)
		,PNCIV(1,false,"IV","Pasivos por impuesto diferido","sab({479})",null)
		,PNCV(1,false,"V","Periodificaciones a largo plazo","sab({181})",null)
		
	,PC(0,false,"C)","PASIVO CORRIENTE",null,"PCI+PCII+PCIII+PCIV+PCV")
		,PCI(1,false,"II","Provisiones a corto plazo","sab({499,529})",null)
		,PCII(1,false,"III","Deudas a corto plazo",null,"PCII1+PCII2+PCII3")
			,PCII1(2,true,"1","Deudas con entidades de cr\u00E9dito","sab({5105,520,527})",null)
			,PCII2(2,true,"2","Acreedores por arrendamiento financiero","sab({5125,524})",null)
			,PCII3(2,true,"3","Otras deudas a corto plazo","sab({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,525,555,5565,5566,5595,5598,560,561,569})+sabPositivo(551)-sdb({1034,1044,190,192})",null)
		,PCIII(1,false,"IV","Deudas con empresas del grupo y asociadas a corto plazo","sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5524,5563,5564})+sabPositivo({5523})",null)
		,PCIV(1,false,"V","Acreedores comerciales y otras cuentas a pagar",null,"PCIV1+PCIV2")
			,PCIV1(2,true,"1","Proveedores","sab({400,401,405})-sdb({406})",null)
			,PCIV2(2,true,"2","Otros acreedores","sab({41,438,465,466,475,476,477})",null)
		,PCV(1,false,"VI","Periodificaciones a corto plazo","sab({485,568})",null)
	,BP00252(0,false,"(A+B+C)","TOTAL PATRIMONIO NETO Y PASIVO",null,"PN+PNC+PC")
	;
 	;

	private int level;
	private boolean leaf;
	private String prefix;
	private String name;
	private String  initialExpressionProvider;
	private String  computeExpressionProvider;
	
	private AccBOEBalancePYMESKey(int level
			,boolean leaf
			,String prefix
			,String name
			,String initialExpressionProvider
			,String computeExpressionProvider) {
		this.level = level;
		this.leaf = leaf;
		this.name = name;
		this.prefix = prefix;
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
	public String getPrefix() {
		return prefix;
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
	public AccBOEBalancePYMESKey getBalanceKey(String keyObject) {
		return AccBOEBalancePYMESKey.valueOf(keyObject);
	}
	public static int getMaxLevel(){
		return 2;
	}
	@Override
	public boolean isLeaf() {
		return this.leaf;
	}
}


