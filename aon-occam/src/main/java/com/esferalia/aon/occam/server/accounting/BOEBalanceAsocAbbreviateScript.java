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

public class BOEBalanceAsocAbbreviateScript extends BalanceScript {
	
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	private IAccMiningKeyAccept accepter;
	
	private static enum AccBOEBalanceAsocAbbreviateKey implements IBalanceKey{
		// ACTIVO
		 ANC(0,HEADER0,"A)","ACTIVO NO CORRIENTE",null,"ANCI+ANCII+ANCIII+ANCIV+ANCV+ANCVI+ANCVII")
			,ANCI	(1,LEAF,"I"	 ,"Inmovilizado intangible","sdb({20})-sab({280,2830,290})",null)
			,ANCII	(1,LEAF,"II" ,"Bienes del Patrimonio Hist\u00F3rico","sdb({24})-sab({299})",null)
			,ANCIII	(1,LEAF,"III","Inmovilizado material","sdb({21,23})-sab({281,2831,291})",null)
			,ANCIV	(1,LEAF,"IV" ,"Inversiones inmobiliarias","sdb({22})-sab({282,2832,292})",null)
			,ANCV	(1,LEAF,"V"	 ,"Inversiones en entidades del grupo y asociadas a largo plazo","sdb({2503,2504,2513,2514,2523,2524})-sab({2593,2594,2933,2934,2943,2944,2953,2954})",null)
			,ANCVI	(1,LEAF,"VI" ,"Inversiones financieras a largo plazo","sdb({2505,2515,2525,260,261,262,263,264,265,268,27})-sab({2595,269,2935,2945,2955,296,297,298})",null)
			,ANCVII	(1,LEAF,"VII","Activos por impuesto diferido","sdb({474})",null)
		
		,AC(0,HEADER0,"B)","ACTIVO CORRIENTE",null,"ACI+ACII+ACIII+ACIV+ACV+ACVI+ACVII")
			,ACI(1,LEAF,"I","Existencias","sdb({30,31,32,33,34,35,36,407})-sab({(39)})",null)
			,ACII(1,LEAF,"II","Usuarios y otros deudores de la actividad propia","sdb({447,448}) - sab({495})",null)
			,ACIII(1,HEADER1,"III","Deudores comerciales y otras cuentas a cobrar","sdb({430,431,432,433,434,435,436})-sab({437,490,493,440,441,446,449,460,464,470,471,472,473,558,544,558})",null)
			,ACIV(1,LEAF,"IV","Inversiones en empresas del grupo y asociadas a corto plazo","sdb({5303,5304,5313,5314, 5323, 5324,5333, 5334, 5343,5344, 5353, 5354})+sdbPositivo({5523,5524})-sab({5393,5394,593,5943,5944,5953,5954})",null)
			
			,ACV(1,LEAF,"V","Inversiones financieras a corto plazo"
					,"sdb({5305,5315,5325,5345,5355,540,541,542,543,545,546,547,548,5315,5335,541,546,5590,5593,565,566})-sab({5395,549,5945,5955,597,598})+sdbPositivo({551,5525})",null)
			,ACVI(1,LEAF,"VI","Periodificaciones a corto plazo","sdb({480,567})",null)
			,ACVII(1,LEAF,"VII","Efectivo y otros activos l\u00EDquidos equivalentes","sdb({57})",null)
		,TC(0,TOTAL0,"(A+B)","TOTAL ACTIVO",null,"ANC + AC")
	
	// PASIVO Y PATRIMONIO
		,PN(0,HEADER0,"A)","PATRIMONIO NETO",null,"PNA1+PNA2+PNA3")
			,PNA1(1,HEADER1,"A-1)","Fondos propios",null,"PNA1I+PNA1II+PNA1III+PNA1IV")
				,PNA1I(2,HEADER2,"I","Dotación fundacional / Fondo social",null,"PNA1I1+PNA1I2")
					,PNA1I1(3,LEAF,"1","Dotación fundacional / Fondo social","sab({100,101})",null)
					,PNA1I2(3,LEAF,"2","(Dotación fundacional no exigido / Fondo social no exigido)","sab({103,104})",null)
				,PNA1II(2,LEAF,"II","Reservas","sab({111,113,114,115})",null)
				,PNA1III(2,LEAF,"III","Excedentes de ejercicios anteriores","sab({120})-sdb({121})"	,null)
				,PNA1IV(2,LEAF,"IV","Excedentes del ejercicio","sab({129,7}) - sdb({6})",null)
			,PNA2(1,LEAF,"A-2)","Ajustes por cambio de valor","sab({133,1340,137})",null)
			,PNA3(1,LEAF,"A-3)","Subvenciones, donaciones y legados recibidos","sab({130,131,132})",null)
			
		,PNC(0,HEADER0,"B)","PASIVO NO CORRIENTE",null,"PNCI+PNCII+PNCIII+PNCIV+PNCV")
			,PNCI(1,LEAF,"I","Provisiones a largo plazo","sab({14})",null)
			,PNCII(1,HEADER1,"II","Deudas a largo plazo",null,"PNCII1+PNCII2+PNCII3")
				,PNCII1(2,LEAF,"1","Deudas con entidades de cr\u00E9dito","sab({1605,170})",null)
				,PNCII2(2,LEAF,"2","Acreedores por arrendamiento financiero","sab({1625,174})",null)
				,PNCII3(2,LEAF,"3","Otras deudas a largo plazo","sab({1615,1635,171,172,173,175,176,177,179,180,185,189})",null)
			,PNCIII(1,LEAF,"III","Deudas con entidades del grupo y asociadas a largo plazo","sab({1603,1604,1613,1614,1623,1624,1633,1634})",null)
			,PNCIV(1,LEAF,"IV","Pasivos por impuesto diferido","sab({479})",null)
			,PNCV(1,LEAF,"V","Periodificaciones a largo plazo","sab({181})",null)
			
		,PC(0,HEADER0,"C)","PASIVO CORRIENTE",null,"PCI+PCII+PCIII+PCIV+PCV+PCVI")
			,PCI(1,LEAF,"I","Provisiones a corto plazo","sab({499,529})",null)
			,PCII(1,HEADER1,"II","Deudas a corto plazo",null,"PCII1+PCII2+PCII3")
				,PCII1(2,LEAF,"1","Deudas con entidades de cr\u00E9dito","sab({5105,520,527})",null)
				,PCII2(2,LEAF,"2","Acreedores por arrendamiento financiero","sab({5125,524})",null)
				,PCII3(2,LEAF,"3","Otras deudas a corto plazo","sab({500,505,506,509,5115,5135,5145,521,522,523,525,528,5530,5532,555,5565,5566,5595,5598,560,561,569})+sabPositivo({551,5525})",null)
			,PCIII(1,LEAF,"III","Deudas con entidadesdel grupo y asociadas a corto plazo","sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5563,5564})+sabPositivo({5523,5524})",null)
			,PCIV(1,LEAF,"IV","Beneficiarios - Acreedores","sab({412})",null)
			,PCV(1,HEADER1,"V","Acreedores comerciales y otras cuentas a pagar",null,"PCV1+PCV2")
				,PCV1(2,LEAF,"1","Proveedores","sab({400,401,403,404,405})-sdb({406})",null)
				,PCV2(2,LEAF,"2","Otros acreedores","sab({410,411,419,438,465,466,475,476,477})",null)
			,PCVI(1,LEAF,"VI","Periodificaciones a corto plazo","sab({485,568})",null)
			
		,BP00252(0,TOTAL0,"(A+B+C)","TOTAL PATRIMONIO NETO Y PASIVO",null,"PN+PNC+PC")
	 	;
	
		private int level;
		private AccountBalanceLineStyle type;
		private String prefix;
		private String name;
		private String  initialExpressionProvider;
		private String  computeExpressionProvider;
		
		private AccBOEBalanceAsocAbbreviateKey(int level
				,AccountBalanceLineStyle type
				,String prefix
				,String name
				,String initialExpressionProvider
				,String computeExpressionProvider) {
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
			return this.type;
		}
	}
	
	@Override
	public IAccMiningKeyAccept getAccepter() {
		if (accepter == null) {
			accepter = new IAccMiningKeyAccept() {
	
					@Override
					public boolean acceptKey(Object key) {
						try {
							return (AccBOEBalanceAsocAbbreviateKey.valueOf((String) key) != null);
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				};
		}
		return accepter;
	}
	
	@Override
	public List<AccBOEBalanceAsocAbbreviateKey> getKeyList() {
		return new LinkedList<AccBOEBalanceAsocAbbreviateKey>( Arrays.asList(AccBOEBalanceAsocAbbreviateKey.values()));
	}

}
