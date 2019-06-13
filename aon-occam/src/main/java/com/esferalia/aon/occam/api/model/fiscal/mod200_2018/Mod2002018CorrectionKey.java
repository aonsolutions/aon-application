package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod2002018CorrectionKey implements Serializable, IMod200KeysProvider  {

	 C01(Mod2002018Key.I0355,Mod2002018Key.D0356,"Cambio de criterios contables (art. 11.3.2o LIS)") 
	,C02(Mod2002018Key.I0357,Mod2002018Key.D0358,"Operaciones a plazos (art. 11.4 LIS)")
	,C03(Mod2002018Key.I0359,Mod2002018Key.D0360,"Reversi\u00F3n del deterioro del valor de los elementos patrimoniales (art. 11.6 LIS)") 
	,C04(Mod2002018Key.I0225,Mod2002018Key.D0226,"Rentas negativas arts. 11.9, 10 y 11 LIS")
	,C05(Mod2002018Key.I1514,Mod2002018Key.D0272,"Ajustes por rentas derivadas de operaciones con quita o espera (art. 11.13 LIS) ")
	,C06(Mod2002018Key.I0361,Mod2002018Key.D0362,"Otras diferencias de imputaci\u00F3n temporal de ingresos y gastos (art. 11 LIS)")
	,C07(Mod2002018Key.I0303,Mod2002018Key.D0304,"Diferencias entre amortizaci\u00F3n contable y fiscal (arts. 12.1 LIS)")
	,C08(null 				,Mod2002018Key.D0505,"Deducci\u00F3n del 30% importe gastos de amortiz. contable (excluidas emp. reducida dimensi\u00F3n) (art. 7 Ley 16/2012)")
	,C09(Mod2002018Key.I1005,Mod2002018Key.D1006,"Amortizaci\u00F3n del inmovilizado intangible (art. 12.2 LIS) y amortizaci\u00F3n de la DT 13a.1 LIS") 
	,C10(Mod2002018Key.I0305,Mod2002018Key.D0306,"Amortizaci\u00F3n de inmovilizado afecto a actividades de investigaci\u00F3n y desarrollo (art. 12.3 b) LIS)")
	,C11(Mod2002018Key.I0307,Mod2002018Key.D0308,"Libertad de amortizaci\u00F3n de gastos de investigaci\u00F3n y desarrollo (art. 12.3 c) LIS)")
	,C12(Mod2002018Key.I1003,Mod2002018Key.D1004,"Libertad de amortizaci\u00F3n inmovilizado material nuevo (arts. 12.3 e) LIS)")
	,C13(Mod2002018Key.I0309,Mod2002018Key.D0310,"Otros supuestos de libertad de amortizaci\u00F3n (arts. 12.3 a) y d) LIS)")
	,C14(Mod2002018Key.I0514,Mod2002018Key.D0509,"Libertad de amortizaci\u00F3n con mantenimiento de empleo (RDL 6/2010 y DT 13a.2)") 
	,C15(Mod2002018Key.I0516,Mod2002018Key.D0551,"Libertad de amortizaci\u00F3n sin mantenimiento de empleo (RDL 13/2010 y DT 13a.2)")
	,C16(Mod2002018Key.I0321,Mod2002018Key.D0322,"P\u00E9rdidas por deterioro del art. 13.1 LIS no afectada por el art. 11.12 ni por DT 33a.1 LIS")
	,C17(Mod2002018Key.I0415,Mod2002018Key.D0211,"P\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (arts. 14.1 y 14.2 LIS) a los que se refi ere el art. 11.12 y DT 33a.1 LIS.") 
	,C18(Mod2002018Key.I0331,Mod2002018Key.D0332,"P\u00E9rdidas por deterioro de IM, inversiones inmobiliarias e II, incluido el fondo de comercio (art. 13.2 a) y DT 15 LIS)")
	,C19(Mod2002018Key.I0325,Mod2002018Key.D0326,"Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (art. 13.2 b) LIS)")
	,C20(Mod2002018Key.I1518,Mod2002018Key.D0394,"Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (DT 16a.1 y 2 LIS)")
	,C21(Mod2002018Key.I0333,Mod2002018Key.D0334,"Ajustes por p\u00E9rdidas por deterioro de valores repr. de partic. en el capital o fondos propios (DT 16a.3 LIS)") 
	,C22(Mod2002018Key.I0327,Mod2002018Key.D0328,"P\u00E9rdidas por deterioro de valores representativos de deuda (art. 13.2 c) LIS y DT 15a LIS)")
	,C23(Mod2002018Key.I0416,Mod2002018Key.D0543,"Aplicaci\u00F3n del l\u00EDmite del art. 11.12 LIS a las p\u00E9rdidas por deterioro del art. 13.1 LIS y provisiones y gastos (arts. 14.1 y 14.2 LIS)")
	,C24(Mod2002018Key.I0335,Mod2002018Key.D0336,"Gastos y provisiones por pensiones no afectados por el art. 11.12 LIS (arts. 14.1, 14.6 y 14.8 LIS)")
	,C25(Mod2002018Key.I0337,Mod2002018Key.D0338,"Otras provisiones no deducibles fiscalmente (art. 14 LIS) no afectadas por el art. 11.12 LIS")
	,C26(null				,Mod2002018Key.D0368,"Subvenciones p\u00FAblicas incluidas en el resultado del ejercicio, no integrables en la base imponible (art. 14.8 LIS)")
	,C27(Mod2002018Key.I1002,null				,"Gastos no deducibles por considerarse retribuci\u00F3n de fondos propios (art. 15 a) LIS)")
	,C28(Mod2002018Key.I1815,null               ,"Multas, sanciones y otros (art. 15 c) LIS)")
	,C29(Mod2002018Key.I0343,null				,"P\u00E9rdidas del juego (art. 15 d) LIS)")
	,C30(Mod2002018Key.I0339,null				,"Gastos por donativos y liberalidades (art. 15 e) LIS)")
	,C31(Mod2002018Key.I1816,null               ,"Gastos de actuaciones contrarias al ordenamiento jur\u00EDdico (art. 15 f) LIS)")
	,C32(Mod2002018Key.I0341,Mod2002018Key.D0342,"Operaciones realizadas con para\u00EDsos fiscales (art. 15 g) LIS)")
	,C33(Mod2002018Key.I0508,null				,"Gastos financieros derivados de deudas con entidades del grupo (art. 15 h) LIS)")
	,C34(Mod2002018Key.I1817,null               ,"Gastos derivados de la extinci\u00F3n de la relaci\u00F3n laboral o mercantil (art. 15 i) LIS)")	
	,C35(Mod2002018Key.I1009,Mod2002018Key.D1010,"Gastos correspondientes a operaciones realizadas con personas o entidades vinculadas (art. 15 j) LIS)")
	,C36(Mod2002018Key.I1807,Mod2002018Key.D1811,"P\u00E9rdidas por deterioro de valorres repr. de partic. en el capital o fondos propios (art. 15 k) LIS)")
	,C37(Mod2002018Key.I1808,Mod2002018Key.D1812,"Disminuci\u00F3n de valor originada por criterio de valor razonable (art. 15 l) LIS)")
	,C38(Mod2002018Key.I1813,Mod2002018Key.D1814,"Deuda tributaria de actos jur\u00EDdicos documentados (ITP y AJD) (art. 15 m) LIS)")
	,C39(Mod2002018Key.I0363,Mod2002018Key.D0364,"Ajustes por la limitaci\u00F3n en la deducibilidad en gastos financieros (art. 16 LIS)")
	,C40(Mod2002018Key.I0345,Mod2002018Key.D0346,"Revalorizaciones contables (art. 17.1 LIS)")
	,C41(Mod2002018Key.I1818,Mod2002018Key.D1819,"Operaciones de aumento de capital o fondos propios por compensaci\u00F3n de cr\u00E9ditos (art. 17.2 LIS)")	
	,C42(Mod2002018Key.I0371,null				,"SICAV: Reducciones de capital y distribuci\u00F3n de la prima de emisi\u00F3n (art. 17.6 LIS)")	
	,C43(Mod2002018Key.I0347,Mod2002018Key.D0348,"Transmisiones lucrativas y societarias: aplicaci\u00F3n del valor de mercado (art. 17.4 LIS)")
	,C44(Mod2002018Key.I1011,Mod2002018Key.D1012,"Operaciones vinculadas: aplicaci\u00F3n del valor de mercado (art. 18 LIS )")
	,C45(Mod2002018Key.I1013,Mod2002018Key.D1014,"Cambios de residencia y otras operaciones del art. 19 LIS")
	,C46(Mod2002018Key.I1015,Mod2002018Key.D1016,"Efectos de la valoraci\u00F3n contable diferente a la fiscal (art. 20 LIS)")
	,C47(null				,Mod2002018Key.D0370,"Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades residentes (art. 21.1 LIS)")
	,C48(null				,Mod2002018Key.D2181,"Exenci\u00F3n sobre dividendos o participaciones en beneficios de entidades no residentes (art. 21.1 LIS)")
	,C49(Mod2002018Key.I2182,Mod2002018Key.D2183,"Exenci\u00F3n sobre la renta obtenida en la transmisi\u00F3n de valores entidades residentes (art. 21.3 LIS)")
	,C50(Mod2002018Key.I2184,Mod2002018Key.D2185,"Exenci\u00F3n sobre la renta obtenida en la transmisi\u00F3n de valores entidades no residentes (art. 21.3 LIS)")
	,C51(Mod2002018Key.I2186,Mod2002018Key.D2187,"Exenci\u00F3n sobre la renta obtenida en los supuestos del art. 21.3 LIS distintos a transmisiones de valores de entidades, entidades residentes (art. 21.3 LIS)")
	,C52(Mod2002018Key.I2188,Mod2002018Key.D2189,"Exenci\u00F3n sobre la renta obtenida en los supuestos del art. 21.3 LIS distintos a transmisiones de valores de entidades, entidades no residentes (art. 21.3 LIS)")
	,C53(Mod2002018Key.I0256,Mod2002018Key.D0278,"Exenci\u00F3n de rentas en el extranjero (art. 22 LIS)")
	,C54(Mod2002018Key.I1822,Mod2002018Key.D0372,"Reducci\u00F3n de rentas procedentes de determinados activos intangibles (art. 23 y DT 20a LIS)")
	,C55(Mod2002018Key.I0373,Mod2002018Key.D0374,"Obra ben\u00E9fico-social de las cajas de ahorro y fundaciones bancarias (art. 24 LIS)")
	,C56(Mod2002018Key.I0340,Mod2002018Key.D1589,"Impuesto extranjero soportado por el contribuyente, no deducible por afectar a rentas con deducci\u00F3n por doble imposici\u00F3n (art. 31.2 LIS)")
	,C57(Mod2002018Key.I0351,null 				,"Impuesto extranjero sobre los beneficios con cargo a los cuales se pagan los dividendos objeto de deducci\u00F3n por doble imposici\u00F3n internacional (art. 32.1 LIS)")
	,C58(Mod2002018Key.I0375,Mod2002018Key.D0376,"Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico (Cap. II, T\u00EDt. VII LIS)")
	,C59(Mod2002018Key.I1320,Mod2002018Key.D1321,"Uni\u00F3n temporal de empresas, ajustes del art. 45.1 LIS")
	,C60(Mod2002018Key.I0184,Mod2002018Key.D0544,"Uni\u00F3n temporal de empresas, ajustes por rentas exentas de UTE que opera en el extranjero (art. 45.2 LIS)")
	,C61(Mod2002018Key.I1022,Mod2002018Key.D1023,"Uni\u00F3n temporal de empresas, ajustes por rentas exentas por participar en el extranjero en f\u00F3rmulas de colaboraci\u00F3n an\u00E1logas a las UTE (art. 45.2 LIS)")
	,C62(Mod2002018Key.I1018,Mod2002018Key.D1019,"Uni\u00F3n temporal de empresas, ajustes por criterios de imputaci\u00F3n temporal (art. 46.2 LIS)")
	,C63(Mod2002018Key.I1275,Mod2002018Key.D1276,"Bases imp. negativas generadas dentro del grupo fi scal por la ent. transmitida y que hayan sido compensadas (art. 62.2 LIS)")
	,C64(Mod2002018Key.I0377,Mod2002018Key.D0378,"Sociedades y fondos de capital-riesgo y sociedades de desarrollo industrial regional (cap\u00EDtulo IV del t\u00EDtulo VII LIS)")
	,C65(Mod2002018Key.I0379,Mod2002018Key.D0380,"Valoraci\u00F3n de bienes y derechos. R\u00E9gimen especial operaciones reestructuraci\u00F3n (cap\u00EDtulo VII del t\u00EDtulo VII LIS)")
	,C66(Mod2002018Key.I0381,Mod2002018Key.D0382,"Miner\u00EDa e hidrocarburos: factor agotamiento (arts. 91 y 95 LIS)")
	,C67(Mod2002018Key.I0383,Mod2002018Key.D0384,"Hidrocarburos: Amortizaci\u00F3n de inversiones intangibles y gastos de investigaci\u00F3n (art. 99 LIS)") 
	,C68(Mod2002018Key.I0387,Mod2002018Key.D0388,"Transparencia fiscal internacional (art. 100 LIS)")
	,C69(Mod2002018Key.I0311,Mod2002018Key.D0312,"Empresas de reducida dimensi\u00F3n: libertad de amortizaci\u00F3n (art. 102 LIS)")  
	,C70(Mod2002018Key.I0313,Mod2002018Key.D0314,"Empresas de reducida dimensi\u00F3n: amortizaci\u00F3n acelerada (art. 103 LIS)")
	,C71(Mod2002018Key.I0323,Mod2002018Key.D0324,"Empresas de reducida dimensi\u00F3n: p\u00E9rdidas por deterioro cr\u00E9ditos insolvencias (art. 104 LIS)") 
	,C72(Mod2002018Key.I0317,Mod2002018Key.D0318,"Arrendamiento financiero: r\u00E9gimen especial (art. 106 LIS)")
	,C73(Mod2002018Key.I0385,Mod2002018Key.D0386,"R\u00E9gimen fiscal entidades de tenencia de valores extranjeros (cap\u00EDtulo XIII del t\u00EDtulo VII LIS)")
	,C74(Mod2002018Key.I0389,Mod2002018Key.D0390,"R\u00E9gimen de entidades parcialmente exentas (cap\u00EDtulo XIV del t\u00EDtulo VII LIS)")
	,C75(null				,Mod2002018Key.D0396,"Montes vecinales en mano com\u00FAn (cap\u00EDtulo XV del t\u00EDtulo VII LIS)")
	,C76(Mod2002018Key.I0397,Mod2002018Key.D0398,"R\u00E9gimen de entidades navieras en funci\u00F3n del tonelaje (cap\u00EDtulo XVI del t\u00EDtulo VII LIS)")
	,C77(Mod2002018Key.I0250,Mod2002018Key.D0251,"Aportaciones y colaboraci\u00F3n a favor de entidades sin fines lucrativos")
	,C78(Mod2002018Key.I0391,Mod2002018Key.D0392,"R\u00E9gimen fiscal entidades sin fi nes lucrativos (Ley 49/2002)")
	,C79(null				,Mod2002018Key.D0400,"Cooperativas: Fondo de reserva obligatorio (Ley 20/1990)")
	,C80(Mod2002018Key.I0403,Mod2002018Key.D0404,"Reserva para inversiones en Canarias (Ley 19/1994)")
	,C81(Mod2002018Key.I0518,Mod2002018Key.D0519,"Exenci\u00F3n transmisi\u00F3n bienes inmuebles (DA 6a LIS)")
	,C82(null				,Mod2002018Key.D1824,"Rentas procedentes de transmisi\u00F3n de inmovilizado obtenidas por las Autoridades Portuarias (DA 68\u00AA Ley 6/2018)")
	,C83(Mod2002018Key.I0510,Mod2002018Key.D0512,"Operaciones a plazos (DT 1a LIS)")
	,C84(Mod2002018Key.I0329,Mod2002018Key.D0330,"Adquisici\u00F3n de participaciones en entidades no residentes (DT 14a LIS) (hasta el 21/12/07)")
	,C85(Mod2002018Key.I0365,Mod2002018Key.D1026,"Reinversi\u00F3n de beneficios extraordinarios (DT 24a LIS)")
	,C86(Mod2002018Key.I2129,Mod2002018Key.D2130,"Ajustes por la primera aplicaci\u00F3n de la Circular 4/2017 del Banco de Espa\u00F1a, a entidades de cr\u00E9dito")	
	,C87(Mod2002018Key.I0409,Mod2002018Key.D0410,"Entidades en r\u00E9g. de atribuci\u00F3n de rentas const. en el extranj. con presencia en territ. espa\u00F1ol (art. 38 TRLIRNR)") 
	,C88(Mod2002018Key.I0411,Mod2002018Key.D0412,"Correcciones espec\u00EDficas de entidades sometidas a la normativa foral")
	,C89(Mod2002018Key.I1027,Mod2002018Key.D1028,"Eliminaciones pendientes de incorporar de sociedades que dejen de pertenecer a un grupo")
	,C90(Mod2002018Key.I0413,Mod2002018Key.D0414,"Otras correcciones al resultado de la cuenta de p\u00E9rdidas y ganancias")
	;
		 
    private String description;
    private Mod2002018Key increase;
    private Mod2002018Key decrease;
    private Mod2002018Key[] keys;
    private CorrectionDetail[] detail; 

	private Mod2002018CorrectionKey(Mod2002018Key increase, Mod2002018Key decrease, String description) {
		this.increase = increase;
		this.decrease = decrease;
		this.description = description;
		keys = new Mod2002018Key[]{increase,decrease};
		
		// Añadimos las casillas de detalle, de determinadas casillas de correcciones al resultado contable
		detail = null;
		for (int i=1;i<=13;i++) {
			
			Mod2002018Key ik = null;
			Mod2002018Key dk = null;

			if (increase != null ) {
				String s = increase.toString() + AonStringUtils.leftPad(Integer.toString(i), 2, '0');
				try {
					ik = Mod2002018Key.valueOf(s);
				} catch (Exception e) {
					ik = null;
				}
			}
			
			if (decrease != null ) {
				String s = decrease.toString() + AonStringUtils.leftPad(Integer.toString(i), 2, '0');
				try {
					dk = Mod2002018Key.valueOf(s);
				} catch (Exception e) {
					dk = null;
				}
			}
			
			if (ik!=null && dk!=null) {			
				if (detail == null) 
			    	detail = new CorrectionDetail[13];
				
				detail[i-1] = new CorrectionDetail();
				detail[i-1].setIncrease(ik);
				detail[i-1].setDecrease(dk);					
				detail[i-1].setDescription(CorrectionDetail.DETAIL_DESCRIPTIONS[i-1]);				
			}
		}
	}
	
	public CorrectionDetail[] getDetail() {
		return detail;
	}		
	
	public String getDescription() {
		return description;
	}
	public Mod2002018Key getIncrease() {
		return increase;
	}
	public Mod2002018Key getDecrease() {
		return decrease;
	}
	public boolean isIncreaseEnabled() {
		return (getIncrease() != null);
	}
	public boolean isDecreaseEnabled() {
		return (getDecrease() != null);
	}
	
	@Override
	public Mod2002018Key[] getKeys() {
		return keys; 
	}
	
	public static void main(String[] args) {
		int i = 0;
		int d = 0;
		for (Mod2002018CorrectionKey k : Mod2002018CorrectionKey.values()) {
			System.out.println(k.getIncrease()+"   "+k.getDecrease());
			
			CorrectionDetail[] p = k.getDetail();
			if (p!=null)
			for (int j=0;j<p.length;j++) {
				if (p[j]!=null)
				  System.out.println(p[j].getKeys()[0]+ " " +p[j].getKeys()[1]+ " "+ p[j].getDescription());
			}
			
			if (k.getIncrease() != null) i++;
			if (k.getDecrease() != null) d++;
		}
		System.out.println( "I ..: " + i);
		System.out.println( "D ..: " + d);
	}

}

final class CorrectionDetail implements Serializable, IMod200KeysProvider {
	
	private static final long serialVersionUID = 791258471554632660L;
	
	public static String[] DETAIL_DESCRIPTIONS = new String[] {
			 "Correcciones Permanentes"
			,"Correcciones temporarias con origen en el ejercicio"
			,"Por amortizaciones"
			,"Por deterioros de valor"
			,"Por pensiones"
			,"Por fondo de comercio"
			,"Resto"
			,"Correcc. temporarias con origen en ejerc. anteriores"
			,"Por amortizaciones"
			,"Por deterioros de valor"
			,"Por pensiones"
			,"Por fondo de comercio"
			,"Resto"
		};	
	
	private Mod2002018Key increase;
	private Mod2002018Key decrease;
	private String description;

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public Mod2002018Key[] getKeys() {
		return new Mod2002018Key[]{increase,decrease};
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setIncrease(Mod2002018Key increase) {
		this.increase = increase;
	}

	public void setDecrease(Mod2002018Key decrease) {
		this.decrease = decrease;
	}
	
}


