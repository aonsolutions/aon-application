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

public class BOEBalanceNormalScript extends BalanceScript {
	
	private IAccMiningKeyAccept accepter;
	
	private static enum AccBOEBalanceNormalKey implements IBalanceKey {
		// ACTIVO
		 ANC	(0,HEADER0, "A)", "ACTIVO NO CORRIENTE", null, "ANCI+ANCII+ANCIII+ANCIV+ANCV+ANCVI")
			,ANCI	(1, HEADER1, "I", "Inmovilizado intangible", null, "ANCI01+ANCI02+ANCI03+ANCI04+ANCI05+ANCI06+ANCI07")
				,ANCI01	(2, LEAF , "1", "Desarrollo", "sdb({201})-sab({2801,2901})", null)
				,ANCI02	(2, LEAF , "2", "Concesiones", "sdb({202})-sab({2802,2902})", null)
				,ANCI03	(2, LEAF , "3", "Patentes, licencias, marcas y similares", "sdb({203})-sab({2803,2903})", null)
				,ANCI04	(2, LEAF , "4", "Fondo de comercio", "sdb({204}) -sab({2804})", null)
				,ANCI05	(2, LEAF , "5", "Aplicaciones inform\u00E1ticas", "sdb({206})-sab({2806,2906})", null)
				,ANCI06	(2, LEAF , "6", "Investigaci\u00F3n", "sdb({200})-sab({2800})", null)
				,ANCI07	(2, LEAF , "7", "Otro inmovilizado intangible", "sdb({205,209})-sab({2805,2905})", null)
			,ANCII	(1, HEADER1, "II", "Inmovilizado material", null, "ANCII1+ANCII2+ANCII3")
				,ANCII1	(2, LEAF , "1", "Terrenos y construcciones", "sdb({210,211})-sab({2810,2811,2910,2911})", null)
				,ANCII2	(2, LEAF , "2", "Instalaciones t\u00E9cnicas y otro inmovilizado material","sdb({212,213,214,215,216,217,218,219})-sab({2812,2813,2814,2815,2816,2817,2818,2819,2912,2913,2914,2915,2916,2917,2918,2919})",null)
				,ANCII3	(2, LEAF , "3", "Inmovilizado en curso y anticipos", "sdb({23})", null)
			,ANCIII	(1, HEADER1, "III", "Inversiones inmobiliarias", null, "ANCIII1+ANCIII2")
				,ANCIII1(2, LEAF , "1", "Terrenos", "sdb({220})-sab({2920})", null)
				,ANCIII2(2, LEAF , "2", "Construcciones", "sdb({221})-sab({282,2921})", null)
			,ANCIV	(1, HEADER1, "IV", "Inversiones en empresas del grupo y asociadas a largo plazo", null,"ANCIV1+ANCIV2+ANCIV3+ANCIV4+ANCIV5")
				,ANCIV1	(2, LEAF , "1", "Instrumentos de patrimonio", "sdb({2403,2404})-sab({2493,2494,293})", null)
				,ANCIV2	(2, LEAF , "2", "Cr\u00E9ditos a empresas", "sdb({2423,2424})-sab({2953,2954})", null)
				,ANCIV3	(2, LEAF , "3", "Valores representativos de deuda", "sdb({2413,2414})-sab({2943,2944})", null)
				,ANCIV4	(2, LEAF , "4", "Derivados", null, null)
				,ANCIV5 (2, LEAF , "5", "Otros activos financieros", null, null)
			,ANCV	(1, HEADER1, "V", "Inversiones financieras a largo plazo", null, "ANCV1+ANCV2+ANCV3+ANCV4+ANCV5")
				,ANCV1	(2, LEAF , "1", "Instrumentos de patrimonio", "sdb({2405,250})-sab({2495,259})", null)
				,ANCV2	(2, LEAF , "2", "Cr\u00E9ditos a terceros", "sdb({2425,252,253,254})-sab({2955,298})", null)
				,ANCV3	(2, LEAF , "3", "Valores representativos de deuda", "sdb({2415,251})-sab({2945,297})", null)
				,ANCV4	(2, LEAF , "4", "Derivados", "sdb({255})", null)
				,ANCV5	(2, LEAF , "5", "Otros activos financieros", "sdb({257,258,26})", null)
			,ANCVI	(1, LEAF, "VI", "Activos por impuesto diferido", "sdb({474})", null)
		
		,AC		(0,HEADER0, "B", "ACTIVO CORRIENTE", null, "ACI+ACII+ACIII+ACIV+ACV+ACVI+ACVII")
			,ACI	(1, LEAF, "I", "Activos no corrientes mantenidos para la venta", "sdb({580,581,582,583,584}) - sab({599})",null)
			,ACII	(1, HEADER1, "I1", "Existencias", null, "ACII1+ACII2+ACII3+ACII4+ACII5+ACII6")
				,ACII1	(2, LEAF, "1", "Comerciales", "sdb({30})-sab({390})", null)
				,ACII2	(2, LEAF, "2", "Materias primas y otros aprovisionamientos", "sdb({31,32})-sab({391,392})", null)
				,ACII3	(2, LEAF, "3", "Productos en curso", "sdb({33,34})-sab({393,394})", null)
				,ACII4	(2, LEAF, "4", "Productos terminados", "sdb({35})-sab({395})", null)
				,ACII5	(2, LEAF, "5", "Subproductos, residuos y materiales recuperados", "sdb({36})-sab({396})", null)
				,ACII6	(2, LEAF, "6", "Anticipos a proveedores", "sdb({407})", null)
			,ACIII	(1, HEADER1, "III", "Deudores comerciales y otras cuentas a cobrar", null,"ACIII1+ACIII2+ACIII3+ACIII4+ACIII5+ACIII6+ACIII7")
				,ACIII1	(2, LEAF, "1", "Clientes por ventas y prestaciones de servicios","sdb({430,431,432,435,436})-sab({437,490,4935})", null)
				,ACIII2	(2, LEAF, "2", "Clientes empresas del grupo y asociadas", "sdb({433,434})-sab({4933,4934})", null)
				,ACIII3	(2, LEAF, "3", "Deudores varios", "sdb({44})", null)
				,ACIII4	(2, LEAF, "4", "Personal", "sdb({460,544})", null)
				,ACIII5	(2, LEAF, "5", "Activos por impuesto corriente", "sdb({4709})", null)
				,ACIII6	(2, LEAF, "6", "Otros cr\u00E9ditos con las Administraciones p\u00FAblicas","sdb({4700,4708,471,472,473})", null)
				,ACIII7	(2, LEAF, "7", "Accionistas (socios) por desembolsos exigidos", "sdb({5580})", null)
			,ACIV	(1, HEADER1, "IV", "Inversiones en empresas del grupo y asociadas a corto plazo", null,"ACIV1+ACIV2+ACIV3+ACIV4+ACIV5")
				,ACIV1	(2, LEAF, "1", "Instrumentos de patrimonio", "sdb({5303,5304})-sab({5393,5394,593})", null)
				,ACIV2	(2, LEAF, "2", "Cr\u00E9ditos a empresas", "sdb({5323,5324,5343,5344})-sab({5953,5954})", null)
				,ACIV3	(2, LEAF, "3", "Valores representativos de deuda", "sdb({5313,5314,5333,5334})-sab({5943,5944})", null)
				,ACIV4	(2, LEAF, "4", "Derivados", null, null)
				,ACIV5	(2, LEAF, "5", "Otros activos financieros", "sdb({5353,5354})+sdbPositivo({5523})+sdbPositivo({5524})", null)
			,ACV	(1, HEADER1, "V", "Inversiones financieras a corto plazo", null, "ACV1+ACV2+ACV3+ACV4+ACV5")
				,ACV1	(2, LEAF, "1", "Instrumentos de patrimonio", "sdb({5305,540})-sab({5395,549})", null)
				,ACV2	(2, LEAF, "2", "Cr\u00E9ditos a empresas", "sdb({5325,5345,542,543,547})-sab({5955,598})", null)
				,ACV3	(2, LEAF, "3", "Valores representativos de deuda", "sdb({5315,5335,541,546})-sab({5945,597})", null)
				,ACV4	(2, LEAF, "4", "Derivados", "sdb({5590,5593})", null)
				,ACV5	(2, LEAF, "5", "Otros activos financieros", "sdb({5355,545,548,554,565,566})+sdbPositivo({551})+sdbPositivo({5525})", null)
			,ACVI	(1, LEAF, "VI", "Periodificaciones a corto plazo", "sdb({480,567})", null)
			,ACVII	(1, HEADER1, "VII", "Efectivo y otros activos l\u00EDquidos equivalentes", null, "ACVII1+ACVII2")
				,ACVII1	(2, LEAF, "1", "Tesorer\u00EDa", "sdb({570,571,572,573,574,575})", null)
				,ACVII2	(2, LEAF, "2", "Otros activos l\u00EDquidos equivalentes", "sdb({576})", null)
			
		,TA		(0,TOTAL0, "(A+B)", "TOTAL ACTIVO", null, "ANC+AC")

		// PASIVO Y PATRIMONIO
		,PN			(0,HEADER0, "A)", "PATRIMONIO NETO", null, "PNA1+PNA2+PNA3")
			,PNA1		(1, HEADER1, "A-1)", "Fondos propios", null,"PNA1I+PNA1II+PNA1III+PNA1IV+PNA1V+PNA1VI+PNA1VII+PNA1VIII+PNA1IX")
				,PNA1I		(2, HEADER2, "I", "Capital", null, "PNA1I1+PNA1I2")
					,PNA1I1		(3, LEAF, "1", "Capital escriturado", "sab({100,101,102})", null)
					,PNA1I2		(3, LEAF, "2", "(Capital no exigido)", "sab({1030,1040})", null)
				,PNA1II		(2, LEAF, "II", "Prima de emisi\u00F3n", "sab({110})", null)
				,PNA1III	(2, HEADER2, "III", "Reservas", null, "PNA1III1+PNA1III2")
					,PNA1III1	(3, LEAF, "1", "Legal y estatutarias", "sab({112,1141})", null)
					,PNA1III2	(3, LEAF, "2", "Otras reservas)", "sab({113,1140,1142,1143,1144,115,119})", null)
				,PNA1IV		(2, LEAF, "IV", "(Acciones y participaciones en patrimonio propias)", "sab({108,109})", null)
				,PNA1V		(2, HEADER2, "V", "Resultados de ejercicios anteriores", null, "PNA1V1+PNA1V2")
					,PNA1V1		(3, LEAF, "1", "Remanente", "sab({120})", null)
					,PNA1V2		(3, LEAF, "2", "(Resultados negativos de ejercicios anteriores)", "sab({121})", null)
				,PNA1VI		(2, LEAF, "VI", "Otras aportaciones de socios", "sab({118})", null)
				,PNA1VII	(2, LEAF, "VII", "Resultado del ejercicio", "sab({129,7}) - sdb({6})", null)
				,PNA1VIII	(2, LEAF, "VIII", "(Dividendo a cuenta)", "sab({557})", null)
				,PNA1IX		(2, LEAF, "IX", "Otros instrumentos de patrimonio neto", "sab({111})", null)
			,PNA2		(1, HEADER1, "A-2)", "Ajustes por cambio de valor", null, "PNA2I+PNA2II+PNA2III+PNA2IV+PNA2V")
				,PNA2I		(2, LEAF, "I", "Activos financieros disponibles para la venta", "sab({133})", null)
				,PNA2II		(2, LEAF, "II", "Operaciones de cobertura", "sab({1340})", null)
				,PNA2III	(2, LEAF, "III", "Activos no corrientes y pasivos vinculados, mantenidos para la venta", "sab({136})",null)
				,PNA2IV		(2, LEAF, "IV", "Diferencia de conversi\u00F3n", "sab({135})", null)
				,PNA2V		(2, LEAF, "V", "Otros", "sab({137})", null)
			,PNA3		(1, LEAF, "A-3)", "Subvenciones, donaciones y legados recibidos", "sab({130,131,132})", null)
		
		,PNC		(0,HEADER0, "B)", "PASIVO NO CORRIENTE", null, "PNCI+PNCII+PNCIII+PNCIV+PNCV+PNCVI")
			,PNCI		(1, HEADER1, "I", "Provisiones a largo plazo", null, "PNCI1+PNCI2+PNCI3+PNCI4")
				,PNCI1		(2, LEAF, "1", "Obligaciones por prestaciones a largo plazo al personal", "sab({140})", null)
				,PNCI2		(2, LEAF, "2", "Actuaciones medioambientales", "sab({145})", null)
				,PNCI3		(2, LEAF, "3", "Provisiones por reestructuraci\u00F3n", "sab({146})", null)
				,PNCI4		(2, LEAF, "4", "Otras provisiones", "sab({141,142,143,147})", null)
			,PNCII		(1, HEADER1, "II", "Deudas a largo plazo", null, "PNCII1+PNCII2+PNCII3+PNCII4+PNCII5")
				,PNCII1		(2, LEAF, "1", "Obligaciones y otros valores negociables", "sab({177,178,179})", null)
				,PNCII2		(2, LEAF, "2", "Deudas con entidades de cr\u00E9dito", "sab({1605,170})", null)
				,PNCII3		(2, LEAF, "3", "Acreedores por arrendamiento financiero", "sab({1625,174})", null)
				,PNCII4		(2, LEAF, "4", "Derivados", "sab({176})", null)
				,PNCII5		(2, LEAF, "5", "Otros pasivos financieros", "sab({1615,1635,171,172,173,175,180,185,189})", null)
			,PNCIII		(1, HEADER1, "III", "Deudas con empresas del grupo y asociadas a largo plazo","sab({1603,1604,1613,1614,1623,1624,1633,1634})", null)
			,PNCIV		(1, LEAF, "IV", "Pasivos por impuesto diferido", "sab({479})", null)
			,PNCV		(1, LEAF, "V", "Periodificaciones a largo plazo", null, null)
			,PNCVI		(1, LEAF, "VI", "Deudas con caracter\u00EDsticas especiales a largo plazo", "sab({150})", null)
		
		,PC			(0,HEADER0, "C)", "PASIVO CORRIENTE", null, "PCI+PCII+PCIII+PCIV+PCV+PCVI+PCVII")
			,PCI		(1, LEAF, "I", "Pasivos vinculados con activos no corr. mantenidos para la venta","sab({585,586,587,588,589})", null)
			,PCII		(1, LEAF, "II", "Provisiones a corto plazo", "sab({499,529})", null)
			,PCIII		(1, HEADER1, "III", "Deudas a corto plazo", null, "PCIII1+PCIII2+PCIII3+PCIII4+PCIII5")
				,PCIII1		(2, LEAF, "1", "Obligaciones y otros valores negociables", "sab({500,501,505,506})", null)
				,PCIII2		(2, LEAF, "2", "Deudas con entidades de cr\u00E9dito", "sab({5105,520,527})", null)
				,PCIII3		(2, LEAF, "3", "Acreedores por arrendamiento financiero", "sab({5125,524})", null)
				,PCIII4		(2, LEAF, "4", "Derivados", "sab({5595,5598})", null)
				,PCIII5		(2, LEAF, "5", "Otros pasivos financieros","sab({194,509,5115,5135,5145,521,522,523,525,526,528,555,5565,5566,560,561,569})+sabPositivo({551})+sabPositivo({5525})-sdb({1034,1044,190,192})",null)
			,PCIV		(1, LEAF, "IV", "Deudas con empresas del grupo y asociadas a corto plazo","sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5563,5564})+sabPositivo({5523})+sabPositivo({5524})", null)
			,PCV		(1, HEADER1, "V", "Acreedores comerciales y otras cuentas a pagar", null,"PCV1+PCV2+PCV3+PCV4+PCV5+PCV6+PCV7")
				,PCV1		(2, LEAF, "1", "Proveedores", "sab({400,401,405})-sdb({406})", null)
				,PCV2		(2, LEAF, "2", "Proveedores, empresas del grupo y asociadas", "sab({403,404})", null)
				,PCV3		(2, LEAF, "3", "Acreedores varios", "sab({41})", null)
				,PCV4		(2, LEAF, "4", "Personal (remuneraciones pendientes de pago)", "sab({465,466})", null)
				,PCV5		(2, LEAF, "5", "Pasivos por impuesto corriente", "sab({4752})", null)
				,PCV6		(2, LEAF, "6", "Otras deudas con las Administraciones p\u00FAblicas", "sab({4750,4751,4758,476,477})",null)
				,PCV7		(2, LEAF, "7", "Anticipos de clientes", "sab({438})", null)
			,PCVI		(1, LEAF, "VI" ,"Periodificaciones a corto plazo", "sab({485,568})", null)
			,PCVII		(1, LEAF, "VII","Deuda con caracter\u00EDsticas especiales a corto plazo","sab({502,507,199})",null)
		
		,TOTAL		(0,TOTAL0, "(A+B+C)", "TOTAL PATRIMONIO NETO Y PASIVO", null, "PN+PNC+PC")
		;

		private int level;
		private AccountBalanceLineStyle type;
		private String prefix;
		private String name;
		private String  initialExpressionProvider;
		private String  computeExpressionProvider;
		
		private AccBOEBalanceNormalKey(int level
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
							return (AccBOEBalanceNormalKey.valueOf((String) key) != null);
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				};
		}
		return accepter;
	}

	@Override
	public List<AccBOEBalanceNormalKey> getKeyList() {
		return new LinkedList<AccBOEBalanceNormalKey>( Arrays.asList(AccBOEBalanceNormalKey.values()));
	}

}
