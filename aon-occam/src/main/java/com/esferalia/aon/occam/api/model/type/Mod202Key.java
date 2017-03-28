package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

// Á --> \u00C1 á --> \u00E1 
// É --> \u00C9 é --> \u00E9 
// Í --> \u00CD í --> \u00ED 
// Ó --> \u00D3 ó --> \u00F3 
// Ú --> \u00DA ú --> \u00FA ... acento
// Ü --> \u00DC ü --> \u00fc ... diéresis
// Ñ --> \u00D1 ñ --> \u00F1
// º --> \u00BA ª --> \u00AA 
// ¿ --> \u00BF


public enum Mod202Key implements IFiscalModelKey{
	 P00("202-P00",0,"IBAN")
	,P01("202-P01",0,"Tipo de declaraci\u00F3n")
	,P02("202-P02",0,"Fecha de inicio del per\u00EDodo impositivo")
	,P03("202-P03",0,"C.N.A.E. actividad principal")
	
	,X01("202-X01",0,"Entidad que aplica el r\u00E9gimen de la Ley 49/2002 de 23 de diciembre")
	,X02("202-X02",0,"Entidad que aplica el r\u00E9gimen de la Ley 11/2009 de 26 de octubre")
	,X03("202-X03",0,"Volumen de operaciones superior a 6.010.121 euros")
	,X04("202-X04",0,"Entidad que aplica el r\u00E9gimen de las entidades navieras en funci\u00F3n del tonelaje")
	
	,X05("202-X05",0,"Entidades que aplican incentivos de empresa de reducida dimensi\u00F3n")
	,X06("202-X06",0,"Cifra de negocios de los 12 meses anteriores a la fecha de inicio del per\u00EDodo impositivo > 6.000.000  \u20AC")
	,X07("202-X07",0,"Cooperativa fiscalmente protegida u Otras entidades con posibilidad de aplicar dos tipos impositivos (ej. entidades ZEC)")
	,X08("202-X08",0,"Tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso")
	,X09("202-X09",0,"Importe neto de la cifra de negocios")
	,X10("202-X10",0,"Entidades en las que al menos el 85% de ingresos del periodo impositivo")
	,X11("202-X11",0,"Marque esta casilla si concurre ALGUNA de las siguientes circunstancias:")
	,X00("202-X00",0,"Modalidad de c\u00E1lculo")
	,X12("202-X12",0,"Entidad que cumpla los requisitos del art. 101 LIS y apliquen tipo gravamen art. 29.1, 1 er p\u00E1rrafo LIS.")
	,X13("202-X13",0,"Cooperativa fiscalmente protegida.")
	,X14("202-X14",0,"Otras entidades con posibilidad de aplicar dos tipos impositivos.")
	
	
	,C01("202-C01", 1,"Base del pago fraccionado")
	,C02("202-C02", 2,"Resultado de la declaraci\u00F3n anterior (complementarias)")
	,C03("202-C03", 3,"A Ingresar")
	
	,C04("202-C04", 4,"Resultado contable despu\u00E9s del IS")
	,C05("202-C05", 5,"Correcciones al resultado contable - por Impuesto sobre Sociedades - Aumentos")
	,C06("202-C06", 6,"Correcciones al resultado contable - por Impuesto sobre Sociedades - Disminuciones")
	,C36("202-C36",36,"30% gastos amortiz (exc.  emp. reducidas) - Aumentos")
	,C37("202-C37",37,"Reversi\u00F3n del 30% del importe de los gastos de amortiz. contable (art. 7 Ley 16/2012)")
	,C07("202-C07", 7,"Resto correcciones al resultado contable, excepto comp. - Aumentos")
	,C08("202-C08", 8,"Resto correcciones al resultado contable, excepto comp. - Disminuciones")
	,C38("202-C38",38,"TOTAL. - Aumentos")
	,C39("202-C39",39,"TOTAL - Disminuciones")
	,C09("202-C09", 9,"25% del importe de los dividendos y rentas devengadas de fuente extranjera")
	,C43("202-C43",43,"100% del importe de los dividendos y rentas devengadas de entidades residentes")
	,C13("202-C13",13,"Base imponible previa")
	,C44("202-C44",44,"Remanente reserva de capitalizaci\u00F3n no aplicada por insuficiencia de base")
	,C14("202-C14",14,"Compensaci\u00F3n de bases negativas de ejercicios anteriores")
	,C45("202-C45",45,"Reserva de nivelaci\u00F3n (art. 105 LIS) (Solo entidades del art. 101 LIS) - Aumentos")
	,C46("202-C46",46,"Reserva de nivelaci\u00F3n (art. 105 LIS) (Solo entidades del art. 101 LIS) - Disminuciones")
	
	,C16("202-C16",16,"Base pago fraccionado")
	,C17("202-C17",17,"Porcentaje")
	,C47("202-C47",47,"Dotaciones del art. 11.12 LIS (DF 4\u00BA LIS)")
	,C40("202-C40",40,"Compensaci\u00F3n de cuotas negativas ejer. anteriores (s\u00F3lo cooperativas)")
	,C48("202-C48",48,"Reserva de nivelaci\u00F3n (105 LIS) convertido en cuotas - Aumentos")
	,C49("202-C49",49,"Reserva de nivelaci\u00F3n (105 LIS) convertido en cuotas - Disminuciones") 
	,C18("202-C18",18,"Caso general - Resultado previo (clave ([16] x [17]) + [47]-[40]+[48]-[49])")
	
	,C19("202-C19",19,"Base del pago fraccionado")
	,C20("202-C20",20,"Base a tipo 1")
	,C21("202-C21",21,"Porcentaje")
	,C22("202-C22",22,"Importe pago fraccionado") 
	,C23("202-C23",23,"Base a tipo 2")
	,C24("202-C24",24,"Porcentaje")
	,C25("202-C25",25,"Importe pago fraccionado") 
	,C50("202-C50",50,"Dotaciones del art. 11.12 de la LIS (s\u00F3lo cooperativas) (DF 4\u00BA LIS)") 
	,C42("202-C42",42,"Compensaci\u00F3n de cuotas negativas de per\u00EDodos anteriores (s\u00F3lo cooperativas)") 
	,C51("202-C51",51,"Reserva de nivelaci\u00F3n (art. 105 LIS) (s\u00F3lo entidades del art. 101 LIS). Aumentos") 
	,C52("202-C52",52,"Reserva de nivelaci\u00F3n (art. 105 LIS) (s\u00F3lo entidades del art. 101 LIS). Disminuciones") 
	,C26("202-C26",26,"Resultado previo(claves [22]+[25]-[50]-[42]+[51]-[52])")
	,C27("202-C27",27,"Bonificaciones correspondientes al periodo computado (total)") 
	,C28("202-C28",28,"Retenciones e ingresos a cuenta practicados sobre ingresos periodo computado") 
	,C29("202-C29",29,"Volumen operaciones en Territorio Com\u00FAn (%)") 
	,C30("202-C30",30,"Pagos fraccionados de periodos anteriores en Territorio Com\u00FAn") 
	,C31("202-C31",31,"Resultado de la declaraci\u00F3n anterior (exclusivamente si \u00E9sta es complementaria)") 
	,C32("202-C32",32,"Resultado")
	,C33("202-C33",33,"M\u00EDnimo a ingresar (s\u00F3lo para empresas con CN igual o superior a 10 millones euros)")
	,C34("202-C34",34,"Cantidad a ingresar")
	
	,A01("202-A01",0,"Comunicaci\u00F3n de datos adicionales a la declaraci\u00F3n")
	,A02("202-A02",0,"Numero de Referencia de Sociedades (NRS)")
	,A03("202-A03",0,"Importe excluido por operaciones de quita o espera")
	,A04("202-A04",0,"Parte integrada en la base imponible por operaciones de quita o espera")
	,A05("202-A05",0,"Parte integrada en la base imponible por operaciones de quita o espera (s\u00F3lo cooperativas)")
	,A06("202-A06",0,"Rentas de reversi\u00F3n de deterioros que se integran en la base imponible")
	,A07("202-A07",0,"Importe correspondiente a la reserva para inversiones en Canarias")
	,A08("202-A08",0,"Importe correspondiente a la bonificaci\u00F3n prevista en el art. 26 de la Ley 19/1994")
	,A09("202-A09",0,"Importe no computable por aplicaci\u00F3n del r\u00E9gimen fiscal de la ZEC")
	,A10("202-A10",0,"Importe de la minoraci\u00F3n correspondiente a las rentas que tengan derecho a la bonificaci\u00F3n prevista en el art. 33 LIS")
	,A11("202-A11",0,"Importe excluido por operaciones de aumento de capital o fondos propios por compensaci\u00F3n de cr\u00E9ditos que no se integren en la base imponible por aplicaci\u00F3n del art. 17.2 LIS")
	,A12("202-A12",0,"Importe renta exenta de las entidades que aplican el r\u00E9gimen fiscal especial del Cap\u00EDtulo XIV del T\u00EDtulo VII LIS")
	,A13("202-A13",0,"Importe de la bonificaci\u00F3n prevista en el art. 34 LIS")	
	;

	private String value;
	private int box;
	private String description;
	
	public static Mod202Key[] MOD_A_KEYS = new Mod202Key[]{Mod202Key.C01,Mod202Key.C02,Mod202Key.C03};
	public static Mod202Key[] MOD_B_KEYS = new Mod202Key[]{Mod202Key.C04,Mod202Key.C05,Mod202Key.C06
			,Mod202Key.C37,Mod202Key.C07,Mod202Key.C08,Mod202Key.C38,Mod202Key.C39,Mod202Key.C09
			,Mod202Key.C43,Mod202Key.C13,Mod202Key.C44,Mod202Key.C14,Mod202Key.C45,Mod202Key.C46
			,Mod202Key.C27,Mod202Key.C28,Mod202Key.C29,Mod202Key.C30,Mod202Key.C31,Mod202Key.C32
			,Mod202Key.C33,Mod202Key.C34};
	public static Mod202Key[] MOD_B1_KEYS = new Mod202Key[]{Mod202Key.C16,Mod202Key.C17,Mod202Key.C47
			,Mod202Key.C40,Mod202Key.C48,Mod202Key.C49,Mod202Key.C18};
	public static Mod202Key[] MOD_B2_KEYS = new Mod202Key[]{Mod202Key.C19,Mod202Key.C20,Mod202Key.C21
		,Mod202Key.C22,Mod202Key.C23,Mod202Key.C24,Mod202Key.C25,Mod202Key.C50,Mod202Key.C42 
		,Mod202Key.C51,Mod202Key.C52,Mod202Key.C26
	};
	
	private Mod202Key(String value,int box,String description) {
    	this.value = value;
    	this.box = box;
    	this.description = description;
	}
	@Override
	public String getValue() {
		return value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getBox() {
		return box;
	}
	public boolean isEnabled() {
		// TODO disable computed keys
		return true;
	}
	
	public static Mod202Key getKey(String value) {
		for (Mod202Key key : Mod202Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
	
}
