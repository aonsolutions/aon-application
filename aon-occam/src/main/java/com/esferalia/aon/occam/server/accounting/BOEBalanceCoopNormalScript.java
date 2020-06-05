package com.esferalia.aon.occam.server.accounting;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER0;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER1;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER2;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.TOTAL0;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.LEAF;

import com.esferalia.aon.occam.api.model.AccountBalanceLineStyle;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;

public class BOEBalanceCoopNormalScript extends BalanceScript {
	
	private IAccMiningKeyAccept accepter;
	
	private static enum AccBOEBalanceCoopNormalKey implements IBalanceKey{
		// ACTIVO
		 ANC(0,HEADER0,"A)","ACTIVO NO CORRIENTE",null,"ANCI+ANCII+ANCIII+ANCIV+ANCV+ANCVI")
		 	,ANCI(1,HEADER1,"I","Inmovilizado intangible",null,"ANCI01+ANCI02+ANCI03+ANCI04+ANCI05+ANCI06+ANCI07")
			 	,ANCI01(2,LEAF,"1","Desarrollo","sdb({201})-sab({2801,2901})",null)
			 	,ANCI02(2,LEAF,"2","Concesiones","sdb({202})-sab({2802,2902})",null)
			 	,ANCI03(2,LEAF,"3","Patentes, licencias, marcas y similares","sdb({203})-sab({2803,2903})",null)
			 	,ANCI04(2,LEAF,"4","Fondo de comercio","sdb({204})-sab({2804})",null)
			 	,ANCI05(2,LEAF,"5","Aplicaciones inform\u00E1ticas","sdb({206})-sab({2806,2906})",null)
			 	,ANCI06(2,LEAF,"6","Investigaci\u00F3n","sdb({200})-sab({2800})",null)
			 	,ANCI07(2,LEAF,"7","Otro inmovilizado intangible","sdb({205,209})-sab({2805,2905})",null)
		 	,ANCII(1,HEADER1,"II","Inmovilizado material",null,"ANCII1+ANCII2+ANCII3")
			 	,ANCII1(2,LEAF,"1","Terrenos y construcciones","sdb({210,211})-sab({2810,2811,2910,2911})",null)
			 	,ANCII2(2,LEAF,"2","Instalaciones t\u00E9cnicas y otro inmovilizado material","sdb({212,213,214,215,216,217,218,219})-sab({2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919})",null)
			 	,ANCII3(2,LEAF,"3","Inmovilizado en curso y anticipos","sdb({23})",null)
			,ANCIII(1,HEADER1,"III","Inversiones inmobiliarias",null,"ANCIII1+ANCIII2")
			 	,ANCIII1(2,LEAF,"1","Terrenos","sdb({220})-sab({2920})",null)
			 	,ANCIII2(2,LEAF,"2","Construcciones","sdb({221})-sab({282,2921})",null)
		 	,ANCIV(1,HEADER1,"IV","Inversiones a largo plazo en empresas del grupo, asociadas y socios",null,"ANCIV1+ANCIV2+ANCIV3+ANCIV4+ANCIV5+ANCIV6")
			 	,ANCIV1(2,LEAF,"1","Instrumentos de patrimonio","sdb({2403,2404})-sab({2493,2494,293})",null)
			 	,ANCIV2(2,HEADER2,"2","Cr\u00E9ditos a empresas",null,"ANCIV21+ANCIV22")
			 		,ANCIV21(3,LEAF,"a","Del grupo","sdb({2423})-sab({2953})",null)
			 		,ANCIV22(3,LEAF,"b","Asociadas","sdb({2424})-sab({2954})",null)
			 	,ANCIV3(2,LEAF,"3","Valores representativos de deuda","sdb({2413,2414})-sab({2943,2944})",null)
			 	,ANCIV4(2,LEAF,"4","Derivados",null,null)
			 	,ANCIV5(2,LEAF,"5","Otros activos financieros",null,null)
			 	,ANCIV6(2,LEAF,"6","Cr\u00E9ditos a socios","sdb({2527})",null)
		 	,ANCV(1,HEADER1,"V","Inversiones financieras a largo plazo",null,"ANCV1+ANCV2+ANCV3+ANCV4+ANCV5")
			 	,ANCV1(2,LEAF,"1","Instrumentos de patrimonio","sdb({2405,250})-sab({2495,259})",null)
			 	,ANCV2(2,LEAF,"2","Cr\u00E9ditos a terceros","sdb({2425,252,253,254})-sab({2527,2955,298})",null)
			 	,ANCV3(2,LEAF,"3","Valores representativos de deuda","sdb({2415,251})-sab({2945,297})",null)
			 	,ANCV4(2,LEAF,"4","Derivados","sdb({255})",null)
			 	,ANCV5(2,LEAF,"5","Otros activos financieros","sdb({257,258,26})",null)
		 	,ANCVI(1,LEAF,"VI","Activos por impuesto diferido","sdb({474})",null)
		 	
	 	,AC(0,HEADER0,"B","ACTIVO CORRIENTE",null,"ACI+ACII+ACIII+ACIV+ACV+ACVI+ACVII")
		 	,ACI (1,LEAF,"I","Activos no corrientes mantenidos para la venta","sdb({580,581,582,583,584}) - sab({599})",null)
	 		,ACII(1,HEADER1,"I1","Existencias",null,"ACII1+ACII2+ACII3+ACII4+ACII5+ACII6")
	 			,ACII1(2,LEAF,"1","Comerciales","sdb({30})-sab({390})",null)
	 			,ACII2(2,LEAF,"2","Materias primas y otros aprovisionamientos","sdb({31,32})-sab({391,392})",null)
	 			,ACII3(2,LEAF,"3","Productos en curso","sdb({33,34})-sab({393,394})",null)
	 			,ACII4(2,LEAF,"4","Productos terminados","sdb({35})-sab({395})",null)
	 			,ACII5(2,LEAF,"5","Subproductos, residuos y materiales recuperados","sdb({36})-sab({396})",null)
	 			,ACII6(2,LEAF,"6","Anticipos a proveedores","sdb({407})",null)
			,ACIII(1,HEADER1,"III","Deudores comerciales y otras cuentas a cobrar",null,"ACIII1+ACIII2+ACIII3+ACIII4+ACIII5+ACIII6+ACIII7")
				,ACIII1(2,LEAF,"1","Clientes por ventas y prestaciones de servicios","sdb({430,431,432,435,436})-sab({437,490,4935})",null)
				,ACIII2(2,HEADER2,"2","Clientes empresas del grupo, asociadas y socios deudores",null,"ACIII21+ACIII22+ACIII23")
					,ACIII21(3,LEAF,"a","Empresas del grupo","sdb({433})-sab({4933})",null)
					,ACIII22(3,LEAF,"b","Empresas asociadas","sdb({434})-sab({4934})",null)
					,ACIII23(3,LEAF,"c","Socios deudores","sdb({447})",null)
			 	,ACIII3(2,LEAF,"3","Deudores varios","sdb({44,5531,5533})-sab({447})",null)
			 	,ACIII4(2,LEAF,"4","Personal","sdb({460,544})",null)
			 	,ACIII5(2,LEAF,"5","Activos por impuesto corriente","sdb({4709})",null)
			 	,ACIII6(2,LEAF,"6","Otros cr\u00E9ditos con las Administraciones p\u00FAblicas","sdb({4700,4708,471,472,473})",null)
			 	,ACIII7(2,LEAF,"7","Socios por desembolsos exigidos","sdb({5580})",null)
		 	,ACIV(1,HEADER1,"IV","Inversiones a corto plazo en empresas del grupo, asociadas y socios",null,"ACIV1+ACIV2+ACIV3+ACIV4+ACIV5+ACIV6")
			 	,ACIV1(2,LEAF,"1","Instrumentos de patrimonio","sdb({5303,5304})-sab({5393,5394,593})",null)
			 	,ACIV2(2,HEADER2,"2","Cr\u00E9ditos a empresas",null,"ACIV21+ACIV22")
			 		,ACIV21(2,LEAF,"a","Empresas del grupo","sdb({5323,5343})-sab({5953})",null)
			 		,ACIV22(2,LEAF,"b","Empresas asociadas","sdb({5324,5344})-sab({5954})",null)
			 	,ACIV3(2,LEAF,"3","Valores representativos de deuda","sdb({5313,5314,5333,5334})-sab({5943,5944})",null)
			 	,ACIV4(2,LEAF,"4","Derivados",null,null)
			 	,ACIV5(2,LEAF,"5","Otros activos financieros","sdb({5353,5354})+sdbPositivo({5523,5524})",null)
			 	,ACIV6(2,LEAF,"6","Cr\u00E9ditos a socios","sdb({5427})",null)
	 	
		 	,ACV(1,HEADER1,"V","Inversiones financieras a corto plazo",null,"ACV1+ACV2+ACV3+ACV4+ACV5")
			 	,ACV1(2,LEAF,"1","Instrumentos de patrimonio","sdb({5305,540})-sab({5395,549})",null)
			 	,ACV2(2,LEAF,"2","Cr\u00E9ditos a empresas","sdb({5325,5345,542,543,547})-sab({5427,5955,598})",null)
			 	,ACV3(2,LEAF,"3","Valores representativos de deuda","sdb({5315,5335,541,546})-sab({5945,597})",null)
			 	,ACV4(2,LEAF,"4","Derivados","sdb({5590,5593})",null)
			 	,ACV5(2,LEAF,"5","Otros activos financieros","sdb({5355,545,548,5525,565,566})+sdbPositivo({551})",null)
	 	 	,ACVI (1,LEAF,"VI","Periodificaciones a corto plazo","sdb({480,567})",null)
		 	,ACVII(1,HEADER1,"VII","Efectivo y otros activos l\u00EDquidos equivalentes",null,"ACVII1+ACVII2")
			 	,ACVII1(2,LEAF,"1","Tesorer\u00EDa"	,"sdb({570,571,572,573,574,575})",null)
			 	,ACVII2(2,LEAF,"2","Otros activos l\u00EDquidos equivalentes","sdb({576})",null)
	 	,TA(0,TOTAL0,"(A+B)","TOTAL ACTIVO",null,"ANC+AC")
	 	
	 	// PASIVO Y PATRIMONIO
		,PN(0,HEADER0,"A)","PATRIMONIO NETO",null,"PNA1+PNA2+PNA3")
		 	,PNA1(1,HEADER1,"A-1)","Fondos propios",null,"PNA1I+PNA1II+PNA1III+PNA1IV+PNA1V+PNA1VI+PNA1VII+PNA1VIII")
			 	,PNA1I(2,HEADER2,"I","Capital",null,"PNA1I1+PNA1I2")	
				 	,PNA1I1(3,LEAF,"1","Capital cooperativo suscrito","sab({100})",null)
				 	,PNA1I2(3,LEAF,"2","(Capital cooperativo no exigido)","sab({1030,1040})",null)
				 ,PNA1II(2,HEADER2,"II","Reservas",null,"PNA1II1+PNA1II2+PNA1II3+PNA1II4+PNA1II5")
			 		,PNA1II1(3,LEAF,"1","Fondo de Reserva Obligatorio","sab({112})",null)
			 		,PNA1II2(3,LEAF,"2","Fondo de Reembolso o Actualización","sab({11450,11451})",null)
			 		,PNA1II3(3,LEAF,"3","Fondo de Reserva Voluntario","sab({113})",null)
			 		,PNA1II4(3,LEAF,"4","Reservas estatutarias","sab({1141})",null)
			 		,PNA1II5(3,LEAF,"5","Otras reservas","sab({1143,115,119})",null)
		 		,PNA1III(2,HEADER2,"III","Resultados de ejercicios anteriores",null,"PNA1III1+PNA1III2")
		 			,PNA1III1(3,LEAF,"1","Remanente","sab({120})",null)
		 			,PNA1III2(3,LEAF,"2","(Resultados negativos de ejercicios anteriores)","sab({121})",null)
	 			,PNA1IV(2,LEAF,"IV","Otras aportaciones de socios","sab({118})",null)
	 			,PNA1V(2,LEAF,"V","Resultado de la cooperativa","sab({129,7}) - sdb({6})",null)
	 			,PNA1VI(2,LEAF,"VI","(Retorno cooperativo y remuneración discrecional a cuenta entregada en el ejercicio)","sab({557})",null)
	 			,PNA1VII(2,LEAF,"VII","Fondos capitalizados","sab({1070,1071})",null)
	 			,PNA1VIII(2,LEAF,"VIII","Otros instrumentos de patrimonio neto","sab({111})",null)
		 	,PNA2(1,HEADER1,"A-2)","Ajustes por cambio de valor",null,"PNA2I+PNA2II+PNA2III")
		 		,PNA2I(2,LEAF,"I","Activos financieros disponibles para la venta","sab({133})",null)
		 		,PNA2II(2,LEAF,"II","Operaciones de cobertura","sab({1340})",null)
		 		,PNA2III(2,LEAF,"III","Otros","sab({137})",null)
		 	,PNA3(1,LEAF,"A-3)","Subvenciones, donaciones y legados recibidos","sab({130,131,132})",null)
	
		 	,PNC(0,HEADER0,"B)","PASIVO NO CORRIENTE",null,"PNCI+PNCII+PNCIII+PNCIV+PNCV+PNCVI+PNCVII")
	 			,PNCI(1,LEAF,"I","Fondo de Educación, Formación y Promoción a largo plazo","sab({148})",null)
	 			,PNCII(1,HEADER1,"II","Deudas con características especiales a largo plazo",null,"PNCII1+PNCII2+PNCII3")
	 				,PNCII1(2,LEAF,"1","\"Capital\" reembolsable exigible","sab({150,1540})-sdb({1530,55850})",null)
	 				,PNCII2(2,LEAF,"2","Fondos especiales calificados como pasivo","sab({1711,1712,1713})",null)
	 				,PNCII3(2,LEAF,"3","Acreedores por fondos capitalizados a largo plazo","sab({1714})",null)
		 		,PNCIII(1,HEADER1,"III","Provisiones a largo plazo",null,"PNCIII1+PNCIII2+PNCIII3+PNCIII4")
				 	,PNCIII1(2,LEAF,"1","Obligaciones por prestaciones a largo plazo al personal","sab({140})",null)
				 	,PNCIII2(2,LEAF,"2","Actuaciones medioambientales","sab({145})",null)
				 	,PNCIII3(2,LEAF,"3","Provisiones por reestructuraci\u00F3n","sab({146})",null)
				 	,PNCIII4(2,LEAF,"4","Otras provisiones","sab({141,142,143,147})",null)
				 ,PNCIV(1,HEADER1,"IV","Deudas a largo plazo",null,"PNCIV1+PNCIV2+PNCIV3+PNCIV4+PNCIV5")
				 	,PNCIV1(2,LEAF,"1","Obligaciones y otros valores negociables","sab({177,178,179})",null)
				 	,PNCIV2(2,LEAF,"2","Deudas con entidades de cr\u00E9dito","sab({1605,170})",null)
				 	,PNCIV3(2,LEAF,"3","Acreedores por arrendamiento financiero","sab({1625,174})",null)
				 	,PNCIV4(2,LEAF,"4","Derivados","sab({176})",null)
				 	,PNCIV5(2,LEAF,"5","Otros pasivos financieros","sab({1615,1635,171,172,173,175,180,185,189})-sab({1710,1711,1712,1713,1715})",null)
				,PNCV(1,HEADER1,"V","Deudas a largo plazo con empresas del grupo, asociadas y socios",null,"PNCV1+PNCV2+PNCV3")
			 		,PNCV1(2,LEAF,"1","Deudas con empresas del grupo","sab({1603,1613,1623,1633})",null)
			 		,PNCV2(2,LEAF,"2","Deudas con empresas asociadas","sab({1604,1614,1624,1634})",null)
			 		,PNCV3(2,LEAF,"3","Deudas con socios","sab({1710,1715})",null)
			 	,PNCVI(1,LEAF,"VI","Pasivos por impuesto diferido","sab({479})",null)
			 	,PNCVII(1,LEAF,"VII","Periodificaciones a largo plazo","sab({181})",null)
	
		 	,PC(0,HEADER0,"C)","PASIVO CORRIENTE",null,"PCI+PCII+PCIII+PCIV+PCV+PCVI+PCVII+PCVIII")
				,PCI(1,LEAF,"I","Fondo de Educación, Formación y Promoción a corto plazo","sab({5298})",null)
	 			,PCII(1,HEADER1,"II","Deudas con características especiales a corto plazo",null,"PCII1+PCII2+PCII3")
					,PCII1(2,LEAF,"1","\"Capital\" reembolsable exigible","sab({5020})",null)
					,PCII2(2,LEAF,"2","Fondos especiales calificados como pasivo","sab({5211,5212,5213})",null)
					,PCII3(2,LEAF,"3","Acreedores por fondos capitalizados a largo plazo","sab({5214})",null)
		 		,PCIII(1,LEAF,"III","Pasivos vinculados con activos no corr. mantenidos para la venta","sab({585,586,587,588,589})",null)
		 		,PCIV(1,LEAF,"IV","Provisiones a corto plazo","sab({499,529})-sab({5298})",null)
		 		,PCV(1,HEADER1,"V","Deudas a corto plazo",null,"PCV1+PCV2+PCV3+PCV4+PCV5")
				 	,PCV1(2,LEAF,"1","Obligaciones y otros valores negociables","sab({500,501,505,506})",null)
					,PCV2(2,LEAF,"2","Deudas con entidades de cr\u00E9dito","sab({5105,520,527})",null)
					,PCV3(2,LEAF,"3","Acreedores por arrendamiento financiero","sab({5125,524})",null)
					,PCV4(2,LEAF,"4","Derivados","sab({5595,5598})",null)
					,PCV5(2,LEAF,"5","Otros pasivos financieros","sab({194,509,5115,5135,5145,521,522,523,525,528,5525,5530,5532,555,5565,5566,560,561,569})+sabPositivo(551)-sdb({1034,1044,190,192,5210,5211,5212,5213})",null)
				,PCVI(1,HEADER1,"VI","Deudas a corto plazo con empresas del grupo, asociadas y socios",null,"PCVI1+PCVI2+PCVI3")
			 		,PCVI1(2,LEAF,"1","Deudas con empresas del grupo","sab({5103,5113,5123,5133,5143,5563})+sabPositivo({5523})",null)
			 		,PCVI2(2,LEAF,"2","Deudas con empresas asociadas","sab({5104,5114,5124,5134,5144,5564})+sabPositivo({5524})",null)
			 		,PCVI3(2,LEAF,"3","Deudas con socios","sab({507,5210,526})",null)
				,PCVII(1,HEADER1,"VII","Acreedores comerciales y otras cuentas a pagar",null,"PCVII1+PCVII2+PCVII3+PCVII4+PCVII5+PCVII6+PCVII7")
					,PCVII1(2,LEAF,"1","Proveedores","sab({400,401,405})-sdb({406})",null)
					,PCVII2(2,HEADER2,"2","Proveedores, empresas del grupo, asociadas y socios",null,"PCVII21+PCVII22")
						,PCVII21(3,LEAF,"a","Socios proveedores","sab({4007})",null)
						,PCVII22(3,LEAF,"b","Proveedores, empresas del grupo y asociadas","sab({403,404})",null)
					,PCVII3(2,LEAF,"3","Acreedores varios","sab({41})",null)
					,PCVII4(2,LEAF,"4","Personal (remuneraciones pendientes de pago)","sab({465,466})",null)
					,PCVII5(2,LEAF,"5","Pasivos por impuesto corriente","sab({4752})",null)
					,PCVII6(2,LEAF,"6","Otras deudas con las Administraciones p\u00FAblicas","sab({4750,4751,4758,476,477})",null)
					,PCVII7(2,LEAF,"7","Anticipos de clientes","sab({438})",null)
				,PCVIII(1,LEAF,"VIII","Periodificaciones a corto plazo","sab({485,568})",null)
			,TOTAL(0,TOTAL0,"(A+B+C)","TOTAL PATRIMONIO NETO Y PASIVO",null,"PN+PNC+PC")
	 	;
	
		private int level;
		private AccountBalanceLineStyle type;
		private String prefix;
		private String name;
		private String  initialExpressionProvider;
		private String  computeExpressionProvider;
		
		private AccBOEBalanceCoopNormalKey(int level
				,AccountBalanceLineStyle type
				,String prefix
				,String name
				,String  initialExpressionProvider
				,String  computeExpressionProvider) {
			this.level = level;
			this.type = type;
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
		@Override
		public AccountBalanceLineStyle getType() {
			return type;
		}
	}

	@Override
	public IAccMiningKeyAccept getAccepter() {
		if (accepter == null) {
			accepter = new IAccMiningKeyAccept() {
	
					@Override
					public boolean acceptKey(Object key) {
						try {
							return (AccBOEBalanceCoopNormalKey.valueOf((String) key) != null);
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				};
		}
		return accepter;
	}
	
	@Override
	public List<AccBOEBalanceCoopNormalKey> getKeyList() {
		return new LinkedList<AccBOEBalanceCoopNormalKey>( Arrays.asList(AccBOEBalanceCoopNormalKey.values()));
	}

}
