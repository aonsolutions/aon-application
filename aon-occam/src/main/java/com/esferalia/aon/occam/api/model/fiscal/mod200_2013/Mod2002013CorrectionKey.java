package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013CorrectionKey implements Serializable  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(Mod2002013Key.I0303,Mod2002013Key.D0304,"Diferencias entre amortizaci\u00F3n contable y fiscal (arts. 11.1 y 11.4 L.I.S.)")
	,C0002(Mod2002013Key.I0504,Mod2002013Key.D0505,"30% importe gastos de amortiz. contable (excluidas emp. reducida dimensi\u00F3n) (art. 7 Ley 16/2012)")
	,C0003(Mod2002013Key.I0305,Mod2002013Key.D0306,"Amortizaci\u00F3n de inmovilizado afecto a actividades de investigaci\u00F3n y desarrollo (art. 11.2.c) L.I.S.)")
	,C0004(Mod2002013Key.I0307,Mod2002013Key.D0308,"Libertad de amortizaci\u00F3n de gastos de investigaci\u00F3n y desarrollo (art. 11.2.d) L.I.S.)")
	,C0005(Mod2002013Key.I0514,Mod2002013Key.D0509,"Libertad de amortizaci\u00F3n con mantenimiento de empleo (D.T. 37a y D.A. 11a L.I.S.-RDL 6/2010)")
	,C0006(Mod2002013Key.I0516,Mod2002013Key.D0551,"Libertad de amortizaci\u00F3n sin matenimiento de empleo (D.T. 37a y D.A. 11a L.I.S.-RDL 13/2010)")
	,C0007(Mod2002013Key.I0309,Mod2002013Key.D0310,"Otros supuestos de libertad de amortizaci\u00F3n (arts. 11.2, a), b) y e) L.I.S.)")
	,C0008(Mod2002013Key.I0311,Mod2002013Key.D0312,"Empresas de reducida dimensi\u00F3n: libertad de amortizaci\u00F3n (arts. 109 y 110 L.I.S.)")
	,C0009(Mod2002013Key.I0313,Mod2002013Key.D0314,"Empresas de reducida dimensi\u00F3n: amortizaci\u00F3n acelerada (arts. 111 y 113 L.I.S.)")
	,C0010(Mod2002013Key.I0315,Mod2002013Key.D0316,"Cesi\u00F3n de bienes con opci\u00F3n de compra (art. 11.3 L.I.S.)")
	,C0011(Mod2002013Key.I0317,Mod2002013Key.D0318,"Arrendamiento financiero: r\u00E9gimen especial (art. 115 L.I.S.)")
	,C0012(Mod2002013Key.I0319,Mod2002013Key.D0320,"P\u00E9rdidas por deterioro no justificadas de valor de fondos editoriales, fonogr\u00E1ficos y audiovisuales (art. 12.1 L.I.S.)")
	,C0013(Mod2002013Key.I0321,Mod2002013Key.D0322,"P\u00E9rdidas por deterioro de valor de cr\u00E9ditos derivadas de insolvencia de deudores (art. 12.2 L.I.S.)")
	,C0014(Mod2002013Key.I0323,Mod2002013Key.D0324,"Empresas de reducida dimensi\u00F3n: p\u00E9rdidas por deterioro cr\u00E9ditos insolvencias (art. 112 L.I.S.)")
	,C0015(Mod2002013Key.I0325,Mod2002013Key.D0326,"Ajustes por deterioro de valores representativos de partic. en el capital o fondos propios (D.T. 41a. 1 y 2 L.I.S.)") 
	,C0016(Mod2002013Key.I0327,Mod2002013Key.D0328,"P\u00E9rdidas por deterioro de valores representativos de deuda (art. 12.4 L.I.S.)")
	,C0017(Mod2002013Key.I0329,Mod2002013Key.D0330,"Adquisici\u00F3n de participaciones en entidades no residentes (art. 12.5 L.I.S.) (*)")
	,C0018(Mod2002013Key.I0331,Mod2002013Key.D0332,"Deducci\u00F3n del fondo de comercio (art. 12.6 L.I.S.)")
	,C0019(Mod2002013Key.I0333,Mod2002013Key.D0334,"Deducci\u00F3n del intangible de vida \u00FAtil indefinida (art. 12.7 L.I.S.)")
	,C0020(Mod2002013Key.I0335,Mod2002013Key.D0336,"Provisiones y gastos por pensiones (arts. 13.3, 14.1.f) y 19.5 L.I.S.)")
	,C0021(Mod2002013Key.I0337,Mod2002013Key.D0338,"Otras provisiones no deducibles fiscalmente (art. 13 L.I.S.)")
	,C0022(Mod2002013Key.I0339,null           ,"Gastos por donativos y liberalidades (art. 14.1.e) L.I.S.)")
	,C0023(Mod2002013Key.I0341,Mod2002013Key.D0342,"Operaciones realizadas con para\u00EDsos fiscales (arts. 12.3 y 14.1.g) L.I.S.)")
	,C0024(Mod2002013Key.I0508,null           ,"Gastos financieros derivados de deudas con entidades del grupo (art. 14.1.h))")
	,C0025(Mod2002013Key.I0510,Mod2002013Key.D0511,"P\u00E9rdidas por deterioro valores representativos de partic. en el capital o fondos propios (art. 14.1. j) L.I.S.)")
	,C0026(Mod2002013Key.I0512,Mod2002013Key.D0513,"Rentas negativas obtenidas en el extranjero a trav\u00E9s de E.P. (art. 14.1. k) L.I.S.)")
	,C0027(Mod2002013Key.I0343,null           ,"Otros gastos no deducibles (arts. 14.1.c), d) e i) L.I.S.)")
	,C0028(Mod2002013Key.I0184,null           ,"Rentas negativas obtenidas por miembros de una UTE que opere en el extranjero (art. 14.1 l) L.I.S.)")
	,C0029(Mod2002013Key.I0345,Mod2002013Key.D0346,"Revalorizaciones contables (art. 15.1 L.I.S.)")
	,C0030(Mod2002013Key.I0347,Mod2002013Key.D0348,"Aplicaci\u00F3n del valor normal de mercado (arts. 15.2, 16, 17 y 18 L.I.S.)")
	,C0031(Mod2002013Key.I0349,Mod2002013Key.D0350,"Ingresos por donaciones y legados otorgados por terceros (art. 15.3 L.I.S.)")
	,C0032(null           ,Mod2002013Key.D0352,"Correcci\u00F3n de rentas por efecto de la depreciaci\u00F3n monetaria (art. 15.9 L.I.S.)")
	,C0033(null           ,Mod2002013Key.D0354,"Gastos por operaciones con acciones propias, como gastos de emisi\u00F3n, honorarios, comisiones, etc. (art. 19.3 L.I.S.)")
	,C0034(Mod2002013Key.I0355,Mod2002013Key.D0356,"Errores contables (art. 19.3 L.I.S.)")
	,C0035(Mod2002013Key.I0357,Mod2002013Key.D0358,"Operaciones a plazos (art. 19.4 L.I.S.)")
	,C0036(Mod2002013Key.I0359,Mod2002013Key.D0360,"Reversi\u00F3n del deterioro del valor de los elementos patrimoniales (art. 19.6 L.I.S.)")
	,C0037(Mod2002013Key.I0225,Mod2002013Key.D0226,"Rentas negativas art. 19.11 y 12 L.I.S.")
	,C0038(Mod2002013Key.I0415,Mod2002013Key.D0416,"Ajustes art. 19.13 L.I.S.")
	,C0039(Mod2002013Key.I0361,Mod2002013Key.D0362,"Otras diferencias de imputaci\u00F3n temporal de ingresos y gastos (art. 19 L.I.S.)")
	,C0040(Mod2002013Key.I0363,Mod2002013Key.D0364,"Ajustes por la limitaci\u00F3n en la deducibilidad en gastos financieros (art. 20 L.I.S.)")
	,C0041(Mod2002013Key.I0365,null           ,"Reinversi\u00F3n de beneficios extraordinarios (D. T. 3a L.I.S.)")
	,C0042(Mod2002013Key.I0367,null           ,"Gastos no deducibles por incompatibilidad con la deducci\u00F3n por reinversi\u00F3n (art. 42 L.I.S.)")
	,C0043(Mod2002013Key.I0369,Mod2002013Key.D0370,"Exenci\u00F3n por doble imposici\u00F3n internacional (art. 21 L.I.S.)")
	,C0044(Mod2002013Key.I0256,Mod2002013Key.D0278,"Exenci\u00F3n por doble imposici\u00F3n internacional (arts. 22 y D.T. 41a. 3 y 4 L.I.S.)")
	,C0045(null           ,Mod2002013Key.D0372,"Reducci\u00F3n de ingresos procedentes de determinados activos intangibles (art. 23 y D.T. 40a L.I.S.)")
	,C0046(Mod2002013Key.I0373,Mod2002013Key.D0374,"Obra ben\u00E9fico-social de las cajas de ahorro y fundaciones bancarias (art. 24 L.I.S.)")
	,C0047(Mod2002013Key.I0375,Mod2002013Key.D0376,"Agrupaciones de inter\u00E9s econ\u00F3mico y uniones temporales de empresas (cap\u00EDtulo II, t\u00EDtulo VII L.I.S.)")
	,C0048(Mod2002013Key.I0377,Mod2002013Key.D0378,"Sociedades y fondos de capital-riesgo y sociedades de desarrollo industrial regional (cap\u00EDtulo IV, t\u00EDtulo VII L.I.S.)")
	,C0049(Mod2002013Key.I0379,Mod2002013Key.D0380,"Valoraci\u00F3n de bienes y derechos. R\u00E9gimen especial operaciones reestructuraci\u00F3n (cap\u00EDtulo VIII, t\u00EDtulo VII L.I.S.)")
	,C0050(Mod2002013Key.I0381,Mod2002013Key.D0382,"Miner\u00EDa e hidrocarburos: factor agotamiento (arts. 98 y 102 L.I.S.)")
	,C0051(Mod2002013Key.I0383,Mod2002013Key.D0384,"Hidrocarburos: Amortizaci\u00F3n de inversiones intangibles y gastos de investigaci\u00F3n (art. 106 L.I.S.)")
	,C0052(Mod2002013Key.I0385,Mod2002013Key.D0386,"R\u00E9gimen fiscal entidades de tenencia de valores extranjeros (cap\u00EDtulo XIV, t\u00EDtulo VII L.I.S.)")
	,C0053(Mod2002013Key.I0387,Mod2002013Key.D0388,"Transparencia fiscal internacional (art. 107 L.I.S.)")
	,C0054(Mod2002013Key.I0389,Mod2002013Key.D0390,"R\u00E9gimen de entidades parcialmente exentas (cap\u00EDtulo XV, t\u00EDtulo VII L.I.S.)")
	,C0055(Mod2002013Key.I0250,Mod2002013Key.D0251,"Aportaciones y colaboraci\u00F3n a favor de entidades sin fines lucrativos")
	,C0056(Mod2002013Key.I0391,Mod2002013Key.D0392,"R\u00E9gimen fiscal entidades sin fines lucrativos (Ley 49/2002)")
	,C0057(null           ,Mod2002013Key.D0396,"Montes vecinales en mano com\u00FAn (cap\u00EDtulo XVI del t\u00EDtulo VII L.I.S.)")
	,C0058(Mod2002013Key.I0397,Mod2002013Key.D0398,"R\u00E9gimen de entidades navieras en funci\u00F3n del tonelaje (cap\u00EDtulo XVII del t\u00EDtulo VII L.I.S.)")
	,C0059(null           ,Mod2002013Key.D0400,"Cooperativas: Fondo de reserva obligatorio (Ley 20/1990)")
	,C0060(Mod2002013Key.I0403,Mod2002013Key.D0404,"Reserva para inversiones en Canarias (Ley 19/1994)")
	,C0061(Mod2002013Key.I0405,Mod2002013Key.D0406,"Diferimiento plusval\u00EDas procesos de concentraci\u00F3n empresarial (D. A. 4a L.I.S.)")
	,C0062(Mod2002013Key.I0409,Mod2002013Key.D0410,"Entidades en r\u00E9g. de atribuci\u00F3n de rentas const. en el extranj. con presencia en territ. espa\u00F1ol (art. 38 LIRNR)")
	,C0063(Mod2002013Key.I0411,Mod2002013Key.D0412,"Correcciones espec\u00EDficas de entidades sometidas a la normativa foral")
	,C0064(Mod2002013Key.I0518,Mod2002013Key.D0519,"Exenci\u00F3n transmisi\u00F3n bienes inmuebles (D.A. 16a L.I.S.)")
	,C0065(Mod2002013Key.I0340,null           ,"Impuesto extranjero sobre los beneficios con cargo a los cuales se pagan los dividendos objeto de deducci\u00F3n por doble imposici\u00F3n internacional (art. 32.1 LIS)")
	,C0066(Mod2002013Key.I0351,null           ,"Impuesto extranjero soportado por el sujeto pasivo, no deducible por afectar a rentas con deducci\u00F3n por doble imposici\u00F3n (art. 31.2 LIS)")
	,C0067(null           ,Mod2002013Key.D0368,"Subvenciones p\u00FAblicas incluidas en el resultado del ejercicio, no integrables en la base imponible")
	,C0068(Mod2002013Key.I0371,null           ,"SICAV: Reducciones de capital y distribuci\u00F3n de la prima de emisi\u00F3n")
	,C0069(Mod2002013Key.I0413,Mod2002013Key.D0414,"Otras correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias")
	
	;
	 
    private String description;
    private Mod2002013Key increase;
    private Mod2002013Key decrease;

	private Mod2002013CorrectionKey(Mod2002013Key increase, Mod2002013Key decrease, String description) {
		this.increase = increase;
		this.decrease = decrease;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public Mod2002013Key getIncrease() {
		return increase;
	}
	public Mod2002013Key getDecrease() {
		return decrease;
	}
	public boolean isIncreaseEnabled() {
		return (getIncrease() != null);
	}
	public boolean isDecreaseEnabled() {
		return (getDecrease() != null);
	}

}

