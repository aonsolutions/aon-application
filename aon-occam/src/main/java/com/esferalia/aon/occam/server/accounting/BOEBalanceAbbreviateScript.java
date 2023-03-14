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

public class BOEBalanceAbbreviateScript extends BalanceScript {
	
	private static final long serialVersionUID = 4273014640210249421L;
	private IAccMiningKeyAccept accepter;
	
	private static enum AccBOEBalanceAbbreviateKey implements IBalanceKey{
		// ACTIVO
		 ANC(0,HEADER0,"A)","ACTIVO NO CORRIENTE",null,"ANCI+ANCII+ANCIII+ANCIV+ANCV+ANCVI")
			,ANCI(1,LEAF,"I","Inmovilizado intangible","sdb({20})-sab({280,290})",null)
			,ANCII(1,LEAF,"II","Inmovilizado material","sdb({21,23})-sab({281,291})",null)
			,ANCIII(1,LEAF,"III","Inversiones inmobiliarias","sdb({22})-sab({282,292})",null)
			,ANCIV(1,LEAF,"IV","Inversiones en empresas del grupo y asociadas a largo plazo","sdb({2403,2404,2413,2414,2423,2424})-sab({2493,2494,293,2943,2944,2953,2954})",null)
			,ANCV(1,LEAF,"V","Inversiones financieras a largo plazo","sdb({2405,2415,2425,250,251,252,253,254,255,257,258,26})-sab({2495,259,2945,2955,297,298})",null)
			,ANCVI(1,LEAF,"VI","Activos por impuesto diferido","sdb({474})",null)
		
		,AC(0,HEADER0,"B)","ACTIVO CORRIENTE",null,"ACI+ACII+ACIII+ACIV+ACV+ACVI+ACVII")
			,ACI(1,LEAF,"I","Activos no corrientes mantenidos para la venta","sdb({580,581,582,583,584}) - sab({599})",null)
			,ACII(1,LEAF,"II","Existencias","sdb({30,31,32,33,34,35,36,407})-sab({(39)})",null)
			,ACIII(1,HEADER1,"III","Deudores comerciales y otras cuentas a cobrar",null,"ACIII1+ACIII2+ACIII3")
				,ACIII1(2,LEAF,"1","Clientes por ventas y prestaciones de servicios","sdb({430,431,432,435,434,435,436})-sab({437,490,493})",null)
				,ACIII2(2,LEAF,"2","Accionistas (socios) por desembolsos exigidos","sdb({5580})",null)
				,ACIII3(2,LEAF,"3","Otros deudores","sdb({44,460,470,471,472,473,544})",null)
			,ACIV(1,LEAF,"IV","Inversiones en empresas del grupo y asociadas a corto plazo","sdb({5303,5304,5313,5314,5323,5324,5333,5334,5343,5344,5353,5354})+sdbPositivo({5523})+sdbPositivo({5524})-sab({5393,5394,593,5943,5944,5953,5954})",null)
			,ACV(1,LEAF,"V","Inversiones financieras a corto plazo"
				,"sdb({5305,5315,5325,5335,5345,5355,540,541,542,543,545,546,547,548,5590,5593,565,566})-sab({5955,598,5945,597,5395,549})"
				+ "+sdbPositivo({551})"
				+ "+sdbPositivo({5525})"
				+ "+sdbPositivo({554})"
				,null)
			,ACVI(1,LEAF,"VI","Periodificaciones a corto plazo","sdb({480,567})",null)
			,ACVII(1,LEAF,"VII","Efectivo y otros activos l\u00EDquidos equivalentes","sdb({57})",null)
		,TC(0,TOTAL0,"(A+B)","TOTAL ACTIVO",null,"ANC + AC")

	// PASIVO Y PATRIMONIO
		,PN(0,HEADER0,"A)","PATRIMONIO NETO",null,"PNA1+PNA2+PNA3")
			,PNA1(1,HEADER1,"A-1)","Fondos propios",null,"PNA1I+PNA1II+PNA1III+PNA1IV+PNA1V+PNA1VI+PNA1VII+PNA1VIII+PNA1IX")
				,PNA1I(2,HEADER2,"I","Capital",null,"PNA1I1+PNA1I2")
					,PNA1I1(3,LEAF,"1","Capital escriturado","sab({100,101,102})",null)
					,PNA1I2(3,LEAF,"2","(Capital no exigido)","sab({1030,1040})",null)
				,PNA1II(2,LEAF,"II","Prima de emisi\u00F3n","sab({110})"	,null)
				,PNA1III(2,LEAF,"III","Reservas","sab({112,113,114,115,119})",null)
				,PNA1IV(2,LEAF,"IV","(Acciones y participaciones en patrimonio propias)","sab({108,109})",null)
				,PNA1V(2,LEAF,"V","Resultados de ejercicios anteriores","sab({120,121})",null)
				,PNA1VI(2,LEAF,"VI","Otras aportaciones de socios","sab({118})",null)
				,PNA1VII(2,LEAF,"VII","Resultado del ejercicio","sab({129,7}) - sdb({6})",null)
				,PNA1VIII(2,LEAF,"VIII","(Dividendo a cuenta)","sab({557})",null)
				,PNA1IX(2,LEAF,"IX","Otros instrumentos de patrimonio neto","sab({111})",null)
			,PNA2(1,LEAF,"A-2)","Ajustes por cambio de valor","sab({133,1340,137})",null)
			,PNA3(1,LEAF,"A-3)","Subvenciones, donaciones y legados recibidos","sab({130,131,132})",null)
			
		,PNC(0,HEADER0,"B)","PASIVO NO CORRIENTE",null,"PNCI+PNCII+PNCIII+PNCIV+PNCV+PNCVI")
			,PNCI(1,LEAF,"I","Provisiones a largo plazo","sab({14})",null)
			,PNCII(1,HEADER1,"II","Deudas a largo plazo",null,"PNCII1+PNCII2+PNCII3")
				,PNCII1(2,LEAF,"1","Deudas con entidades de cr\u00E9dito","sab({1605,170})",null)
				,PNCII2(2,LEAF,"2","Acreedores por arrendamiento financiero","sab({1625,174})",null)
				,PNCII3(2,LEAF,"3","Otras deudas a largo plazo","sab({1615,1635,171,172,173,175,176,177,178,179,180,185,189})",null)
			,PNCIII(1,LEAF,"III","Deudas con empresas del grupo y asociadas a largo plazo","sab({1603,1604,1613,1614,1623,1624,1633,1634})",null)
			,PNCIV(1,LEAF,"IV","Pasivos por impuesto diferido","sab({479})",null)
			,PNCV(1,LEAF,"V","Periodificaciones a largo plazo","sab({181})",null)
			,PNCVI(1, LEAF, "VI", "Deudas con caracter\u00EDsticas especiales a largo plazo", "sab({150})", null)

			
		,PC(0,HEADER0,"C)","PASIVO CORRIENTE",null,"PCI+PCII+PCIII+PCIV+PCV+PCVI+PCVII")
			,PCI(1,LEAF,"I","Pasivos vinculados con activos no corr. mantenidos para la venta","sab({585,586,587,588,589})",null)
			,PCII(1,LEAF,"II","Provisiones a corto plazo","sab({499,529})",null)
			,PCIII(1,HEADER1,"III","Deudas a corto plazo",null,"PCIII1+PCIII2+PCIII3")
				,PCIII1(2,LEAF,"1","Deudas con entidades de cr\u00E9dito","sab({5105,520,527})",null)
				,PCIII2(2,LEAF,"2","Acreedores por arrendamiento financiero","sab({5125,524})",null)
				,PCIII3(2,LEAF,"3","Otras deudas a corto plazo"
					,"sab({194,500,501,505,506,509,5115,5135,5145,521,522,523,525,526,528,555,5565,5566,5595,5598,560,561,569})"
					+ "+sabPositivo({551})"
					+ "+sabPositivo({5525})"
					+ "+sabPositivo({554})"
					+ "-sdb({1034,1044,190,192})"
					,null)
			,PCIV(1,LEAF,"IV","Deudas con empresas del grupo y asociadas a corto plazo","sab({5103,5104,5113,5114,5123,5124,5133,5134,5143,5144,5563,5564})+sabPositivo({5523})+sabPositivo({5524})",null)
			,PCV(1,HEADER1,"V","Acreedores comerciales y otras cuentas a pagar",null,"PCV1+PCV2")
				,PCV1(2,LEAF,"1","Proveedores","sab({400,401,403,404,405})-sdb({406})",null)
				,PCV2(2,LEAF,"2","Otros acreedores","sab({41,438,465,466,475,476,477})",null)
			,PCVI(1,LEAF,"VI","Periodificaciones a corto plazo","sab({485,568})",null)
			,PCVII(1,LEAF,"VII","Deuda con caracter\u00EDsticas especiales a corto plazo","sab({502,507,199})",null)
		,BP00252(0,TOTAL0,"(A+B+C)","TOTAL PATRIMONIO NETO Y PASIVO",null,"PN+PNC+PC")
	 	;
	
		private int level;
		private AccountBalanceLineStyle type;
		private String prefix;
		private String name;
		private String  initialExpressionProvider;
		private String  computeExpressionProvider;
		
		private AccBOEBalanceAbbreviateKey(int level
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
							return (AccBOEBalanceAbbreviateKey.valueOf((String) key) != null);
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				};
		}
		return accepter;
	}
	
	@Override
	public List<AccBOEBalanceAbbreviateKey> getKeyList() {
		return new LinkedList<AccBOEBalanceAbbreviateKey>( Arrays.asList(AccBOEBalanceAbbreviateKey.values()));
	}

}
