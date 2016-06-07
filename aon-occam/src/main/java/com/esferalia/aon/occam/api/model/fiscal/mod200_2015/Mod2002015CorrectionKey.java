package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;


public enum Mod2002015CorrectionKey implements Serializable  {

	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(Mod2002015Key.I0355,Mod2002015Key.D0356,"Cambio de criterios contables (art. 11.3.2\u00BA LIS)")
	,C02(Mod2002015Key.I0357,Mod2002015Key.D0358,"Operaciones a plazos (art. 11.4 LIS)")
	,C03(Mod2002015Key.I0359,Mod2002015Key.D0360,"Reversi\u00F3n del deterioro del valor de los elementos patrimoniales (art. 11.6 LIS)")
	,C04(Mod2002015Key.I0225,Mod2002015Key.D0226,"Rentas negativas art. 11.9, 10 y 11 LIS")
	,C05(Mod2002015Key.I0545,Mod2002015Key.D0272,"Ajustes por rentas derivadas de operaciones con quita o espera (art. 11.13 LIS)")
	,C06(Mod2002015Key.I0361,Mod2002015Key.D0362,"Otras diferencias de imputaci\u00F3n temporal de ingresos y gastos (art. 11 LIS)")
	,C07(Mod2002015Key.I0303,Mod2002015Key.D0304,"Diferencias entre amortizaci\u00F3n contable y fiscal (arts. 12.1 LIS)")
	,C08(null            	,Mod2002015Key.D0505,"Reversi\u00F3n del 30% importe gastos de amortiz. contable (excluidas emp. reducida dimensi\u00F3n) (art. 7 Ley 16/2012)")
	,C09(Mod2002015Key.I0305,Mod2002015Key.D0306,"Amortizaci\u00F3n de inmovilizado afecto a actividades de investigaci\u00F3n y desarrollo (art. 12.3.b) LIS)")
	,C10(Mod2002015Key.I0307,Mod2002015Key.D0308,"Libertad de amortizaci\u00F3n de gastos de investigaci\u00F3n y desarrollo (art. 12.3.c) LIS)")
	,C11(Mod2002015Key.I1003,Mod2002015Key.D1004,"Libertad de amortizaci\u00F3n inmovilizado material nuevo (arts. 12.3 e) LIS)")
	,C12(Mod2002015Key.I0309,Mod2002015Key.D0310,"Otros supuestos de libertad de amortizaci\u00F3n (arts. 12.3 a) y d) LIS)")
	,C13(Mod2002015Key.I1005,Mod2002015Key.D1006,"Amortizaci\u00F3n del inmovilizado intangible con vida \u00FAtil definida (DT 13\u00AA.1 LIS) y amortizaci\u00F3n del art. 12.2 LIS")
	,C14(Mod2002015Key.I0514,Mod2002015Key.D0509,"Libertad de amortizaci\u00F3n con mantenimiento de empleo (RDL 6/2010 y DT 13\u00AA.2)")
	,C15(Mod2002015Key.I0516,Mod2002015Key.D0551,"Libertad de amortizaci\u00F3n sin mantenimiento de empleo (RDL 13/2010 y DT 13\u00AA.2)")
	,C16(Mod2002015Key.I0321,Mod2002015Key.D0322,"P\u00E9rdidas por deterioro del art. 13.1 LIS no afectada por el art. 11.12 LIS")
	,C17(Mod2002015Key.I0415,Mod2002015Key.D0211,"P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2 LIS a los que se refiere el art. 11.12 LIS.")
	,C18(Mod2002015Key.I0331,Mod2002015Key.D0332,"P\u00E9rdidas por deterioro de IM, inversiones inmobiliarias e II, incluido el fondo de comercio (art. 13.2 a) y DT 15 LIS)")
	,C19(Mod2002015Key.I0325,Mod2002015Key.D0326,"Ajustes por deterioro de valores repr. de partic. en el capital o fondos propios (art. 13.2 b) L.I.S. y DT 16a.1 y 2 LIS)" )
	,C20(Mod2002015Key.I0327,Mod2002015Key.D0328,"P\u00E9rdidas por deterioro de valores representativos de deuda (art. 13.2 c) LIS y DT 15\u00AA LIS)")
	,C21(Mod2002015Key.I0333,Mod2002015Key.D0334,"Deducci\u00F3n del intangible de vida \u00FAtil indefinida (art. 13.3 LIS)")
	,C22(Mod2002015Key.I0416,Mod2002015Key.D0543,"Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (art. 14.1 y 14.2 LIS)")
	,C23(Mod2002015Key.I0335,Mod2002015Key.D0336,"Provisiones y gastos por pensiones no afectados por el art. 11.12 LIS (arts. 14.1, 14.6 y 14.8 LIS)")
	,C24(Mod2002015Key.I0337,Mod2002015Key.D0338,"Otras provisiones no deducibles fiscalmente (art. 14 LIS) no afectadas por el art. 11.12 LIS")
	,C25(null				,Mod2002015Key.D0368,"Subvenciones p\u00FAblicas incluidas en el resultado del ejercicio, no integrables en la base imponible (art. 14.8 LIS)")
	,C26(Mod2002015Key.I0339,null			    ,"Gastos por donativos y liberalidades (art. 15 e) LIS)")
	,C27(Mod2002015Key.I0341,Mod2002015Key.D0342,"Operaciones realizadas con para\u00EDsos fiscales (art. 15 g) LIS)")
	,C28(Mod2002015Key.I0508,null			    ,"Gastos financieros derivados de deudas y operaciones con entidades del grupo (art. 15 h) LIS)")
	,C29(Mod2002015Key.I1009,Mod2002015Key.D1010,"Gastos correspondientes a operaciones realizadas con personas o entidades vinculadas (art. 15 j) LIS)")
	,C30(Mod2002015Key.I0343,null			    ,"Otros gastos no deducibles (arts. 15 a), c), d), f) e i) LIS)")
	,C31(Mod2002015Key.I0363,Mod2002015Key.D0364,"Ajustes por la limitaci\u00F3n en la deducibilidad en gastos financieros (art. 16 LIS)")
	,C32(Mod2002015Key.I0345,Mod2002015Key.D0346,"Revalorizaciones contables (art. 17.1 LIS)")
	,C33(Mod2002015Key.I0371,null			    ,"Reducciones de capital y distribuci\u00F3n de la prima de emisi\u00F3n (art. 17.6 LIS)") 
	,C34(Mod2002015Key.I0347,Mod2002015Key.D0348,"Transmisiones lucrativas y societarias: aplicaci\u00F3n del valor normal de mercado (art. 17.4 LIS)")
	,C35(Mod2002015Key.I1011,Mod2002015Key.D1012,"Operaciones vinculadas: aplicaci\u00F3n del valor normal de mercado (art. 18 LIS)")
	,C36(Mod2002015Key.I1013,Mod2002015Key.D1014,"Cambios de residencia y otras operaciones del art. 19 LIS: aplicaci\u00F3n del valor normal de mercado")
	,C37(Mod2002015Key.I1015,Mod2002015Key.D1016,"Efectos de la valoraci\u00F3n contable diferente a la fiscal (art. 20 LIS)")
	,C38(Mod2002015Key.I0369,Mod2002015Key.D0370,"Exenci\u00F3n por doble imposici\u00F3n sobre dividendos y rentas derivadas de transmisi\u00F3n de valores ent. resid. y no resid. (art. 21 LIS)") 
	,C39(Mod2002015Key.I0256,Mod2002015Key.D0278,"Exenci\u00F3n de rentas en el extranjero (art. 22 LIS)") 
	,C40(null			   	,Mod2002015Key.D0372,"Reducci\u00F3n de rentas procedentes de determinados activos intangibles (art. 23 y DT 20a LIS)")
	,C41(Mod2002015Key.I0373,Mod2002015Key.D0374,"Obra ben\u00E9fico-social de las cajas de ahorro y fundaciones bancarias (art. 24 LIS)")
	,C42(Mod2002015Key.I0340,null			    ,"Impuesto extranjero soportado por el contribuyente, no deducible por afectar a rentas con deducci\u00F3n por doble imposici\u00F3n (art. 31.2 LIS)")
	,C43(Mod2002015Key.I0351,null			    ,"Impuesto extranjero sobre los beneficios con cargo a los cuales se pagan los dividendos objeto de deducci\u00F3n por doble imposici\u00F3n internacional (art. 32.1 LIS)")
	,C44(Mod2002015Key.I0375,Mod2002015Key.D0376,"Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico (Cap. II, T\u00EDt. VII LIS)") 
	,C45(Mod2002015Key.I1320,Mod2002015Key.D1321,"Uni\u00F3n temporal de empresas, ajustes del art. 45.1 LIS")
	,C46(Mod2002015Key.I0184,Mod2002015Key.D0544,"Uni\u00F3n temporal de empresas, ajustes por rentas exentas de UTE que opera en el extranjero (art. 45.2 LIS)") 
	,C47(Mod2002015Key.I1022,Mod2002015Key.D1023,"Uni\u00F3n temporal de empresas, ajustes por participar en el extranjero en f\u00F3rmulas de colaboraci\u00F3n an\u00E1logas a las UTE (art. 45.2 LIS)") 
	,C48(Mod2002015Key.I1018,Mod2002015Key.D1019,"Uni\u00F3n temporal de empresas, ajustes por criterios de imputaci\u00F3n temporal (art. 46.2 LIS)")
	,C49(Mod2002015Key.I1275,Mod2002015Key.D1276,"Bases imp. negativas generadas dentro del grupo fi scal por la ent. transmitida y que hayan sido compensadas (art. 62.2 LIS)") 
	,C50(Mod2002015Key.I0377,Mod2002015Key.D0378,"Sociedades y fondos de capital-riesgo y sociedades de desarrollo industrial regional (cap\u00EDtulo IV, t\u00EDtulo VII LIS)")
	,C51(Mod2002015Key.I0379,Mod2002015Key.D0380,"Valoraci\u00F3n de bienes y derechos. R\u00E9gimen especial operaciones reestructuraci\u00F3n (cap\u00EDtulo VII, t\u00EDtulo VII LIS)")
	,C52(Mod2002015Key.I0381,Mod2002015Key.D0382,"Miner\u00EDa e hidrocarburos: factor agotamiento (arts. 91 y 96 LIS)")
	,C53(Mod2002015Key.I0383,Mod2002015Key.D0384,"Hidrocarburos: Amortizaci\u00F3n de inversiones intangibles y gastos de investigaci\u00F3n (art. 99 LIS)")
	,C54(Mod2002015Key.I0387,Mod2002015Key.D0388,"Transparencia fiscal internacional (art. 100 LIS)")
	,C55(Mod2002015Key.I0311,Mod2002015Key.D0312,"Empresas de reducida dimensi\u00F3n: libertad de amortizaci\u00F3n (art. 102 LIS)")
	,C56(Mod2002015Key.I0313,Mod2002015Key.D0314,"Empresas de reducida dimensi\u00F3n: amortizaci\u00F3n acelerada (art. 103 LIS)")
	,C57(Mod2002015Key.I0323,Mod2002015Key.D0324,"Empresas de reducida dimensi\u00F3n: p\u00E9rdidas por deterioro cr\u00E9ditos insolvencias (art. 104 LIS)")
	,C58(Mod2002015Key.I0317,Mod2002015Key.D0318,"Arrendamiento financiero: r\u00E9gimen especial (art. 106 LIS)")
	,C59(Mod2002015Key.I0385,Mod2002015Key.D0386,"R\u00E9gimen fiscal entidades de tenencia de valores extranjeros (cap\u00EDtulo XIII, t\u00EDtulo VII LIS)")
	,C60(Mod2002015Key.I0389,Mod2002015Key.D0390,"R\u00E9gimen de entidades parcialmente exentas (cap\u00EDtulo XIV, t\u00EDtulo VII LIS)") 
	,C61(null			   	,Mod2002015Key.D0396,"Montes vecinales en mano com\u00FAn (cap\u00EDtulo XV del t\u00EDtulo VII LIS)")
	,C62(Mod2002015Key.I0397,Mod2002015Key.D0398,"R\u00E9gimen de entidades navieras en funci\u00F3n del tonelaje (cap\u00EDtulo XVI del t\u00EDtulo VII LIS)")
	,C63(Mod2002015Key.I0250,Mod2002015Key.D0251,"Aportaciones y colaboraci\u00F3n a favor de entidades sin fi nes lucrativos")
	,C64(Mod2002015Key.I0391,Mod2002015Key.D0392,"R\u00E9gimen fiscal entidades sin fi nes lucrativos (Ley 49/2002)")
	,C65(null			   	,Mod2002015Key.D0400,"Cooperativas: Fondo de reserva obligatorio (Ley 20/1990)")
	,C66(Mod2002015Key.I0403,Mod2002015Key.D0404,"Reserva para inversiones en Canarias (Ley 19/1994)")
	,C67(Mod2002015Key.I0518,Mod2002015Key.D0519,"Exenci\u00F3n transmisi\u00F3n bienes inmuebles (DA 6\u00AA LIS)")
	,C68(Mod2002015Key.I0510,Mod2002015Key.D0512,"Operaciones a plazos (DT 1\u00AA LIS)")
	,C69(Mod2002015Key.I0329,Mod2002015Key.D0330,"Adquisici\u00F3n de participaciones en entidades no residentes (DT 14\u00AA LIS)")
	,C70(Mod2002015Key.I0365,Mod2002015Key.D1026,"Reinversi\u00F3n de beneficios extraordinarios (DT 24\u00AA LIS)")
	,C71(Mod2002015Key.I0409,Mod2002015Key.D0410,"Entidades en r\u00E9g. de atribuci\u00F3n de rentas const. en el extranj. con presencia en territ. espa\u00F1ol (art. 38 LIRNR)")
	,C72(Mod2002015Key.I0411,Mod2002015Key.D0412,"Correcciones espec\u00EDficas de entidades sometidas a la normativa foral")
	,C73(Mod2002015Key.I1027,Mod2002015Key.D1028,"Eliminaciones pendiente de incorporar de sociedades que dejen de pertenecer a un grupo")
	,C74(Mod2002015Key.I0413,Mod2002015Key.D0414,"Otras correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias")
	;
	 
    private String description;
    private Mod2002015Key increase;
    private Mod2002015Key decrease;

	private Mod2002015CorrectionKey(Mod2002015Key increase, Mod2002015Key decrease, String description) {
		this.increase = increase;
		this.decrease = decrease;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public Mod2002015Key getIncrease() {
		return increase;
	}
	public Mod2002015Key getDecrease() {
		return decrease;
	}
	public boolean isIncreaseEnabled() {
		return (getIncrease() != null);
	}
	public boolean isDecreaseEnabled() {
		return (getDecrease() != null);
	}
	
	public static void main(String[] args) {
		
		for (Mod2002015CorrectionKey k : Mod2002015CorrectionKey.values()) {
			if (k.getIncrease() != null) {				
				System.out.print( k.getIncrease().toString());				
			}
			else System.out.print("     ");
			System.out.print("  ");
			
			if (k.getDecrease() != null) {				
				System.out.print( k.getDecrease().toString());				
			}
			else System.out.print("     ");
			
			System.out.print("  ");
			
			System.out.print(k.getDescription());			
			
			System.out.println();
		}
		
	}

}

