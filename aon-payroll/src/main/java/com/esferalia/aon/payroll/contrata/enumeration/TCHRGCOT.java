package com.esferalia.aon.payroll.contrata.enumeration;

import com.code.aon.common.enumeration.IStringEnum;

/** 
 * Enumeration for represent Contrata (S.E.P.E.) TCHRGCOT table codes.
 * Generation main class: com.esferalia.aon.payroll.contrata.ContrataCodeTablesWriter.
 *  ------------------------------------------------------------------------
 *  TABLA      	DESCRIPCION						FECHA ÚLTIMA ACTUALIZACIÓN.
 *  TCHRGCOT	RÉGIMEN DE COTIZACIÓN						10-01-2012						
 *  ------------------------------------------------------------------------
 */ 
public enum TCHRGCOT implements IStringEnum {

	TCHRGCOT_0111( "0111", "RÉGIMEN GENERAL", null, null ),
	TCHRGCOT_0112( "0112", "ARTISTAS (R.G.)", null, null ),
	TCHRGCOT_0113( "0113", "JUGADORES PROFESIONALES DE FÚTBOL", null, null ),
	TCHRGCOT_0114( "0114", "PROFESIONALES TAURINOS", null, null ),
	TCHRGCOT_0115( "0115", "FERROVIARIOS", null, null ),
	TCHRGCOT_0121( "0121", "REPRESENTANTES COMERCIALES", null, null ),
	TCHRGCOT_0131( "0131", "SISTEMA ESPECIAL RESINA", null, null ),
	TCHRGCOT_0132( "0132", "SISTEMA ESPECIAL INDUSTRIAS DE CONSERVAS VEGETALES", null, null ),
	TCHRGCOT_0133( "0133", "SISTEMA ESPECIAL FRUTAS Y HORTALIZAS", null, null ),
	TCHRGCOT_0134( "0134", "SISTEMA ESPECIAL EMPAQUETADO Y MANIPULADO DE TOMATE FRESCO", null, null ),
	TCHRGCOT_0135( "0135", "SISTEMA ESPECIAL HOSTELERÍA", null, null ),
	TCHRGCOT_0136( "0136", "SISTEMA ESPECIAL EXHIBICIÓN CINEMATOGRÁFICA, SALAS DE BAILE, DISCOTECAS Y SALAS DE FIESTAS", null, null ),
	TCHRGCOT_0137( "0137", "SISTEMA ESPECIAL ESTUDIOS DE MERCADO Y OPINIÓN PÚBLICA", null, null ),
	TCHRGCOT_0138( "0138", "REG.GRAL. (SIST.ESP. EMPLEADOS HOGAR)", null, null ),
	TCHRGCOT_0140( "0140", "RÉGIMEN GENERAL ( CONVENIO ESPECIAL )", null, null ),
	TCHRGCOT_0150( "0150", "REG.GRAL.CCC.RESP.SOLID.,SUBS.,MORTIS C", null, null ),
	TCHRGCOT_0151( "0151", "C.C. CONVENCIONALES DEL R.E.T.A.", null, null ),
	TCHRGCOT_0152( "0152", "C.C. CONVENCIONALES DEL R.E.T.A. (ESC. DE LIBROS)", null, null ),
	TCHRGCOT_0160( "0160", "C.C. CONVENCIONALES DEL R.E.A. C/A", null, null ),
	TCHRGCOT_0161( "0161", "REG.GRAL.(SIST.ESP.AGRARIO.INACTIV)", null, null ),
	TCHRGCOT_0163( "0163", "REG.GRAL.(SIST.ESP.AGRARIO CCC)", null, null ),
	TCHRGCOT_0170( "0170", "C.C. CONVENCIONALES DEL R.E.A. C/P", null, null ),
	TCHRGCOT_0180( "0180", "C.C. CONVENCIONALES DEL R.E.E.H.", null, null ),
	TCHRGCOT_0521( "0521", "TRABAJADORES AUTÓNOMOS", null, null ),
	TCHRGCOT_0522( "0522", "ESCRITORES DE LIBROS (RÉGIMEN ESPECIAL DE TRABAJADORES AUTÓNOMOS)", null, null ),
	TCHRGCOT_0531( "0531", "REGIMEN ESP. AUTONOMOS CESE ACTIVIDAD", null, null ),
	TCHRGCOT_0540( "0540", "REG.ESP.AUTONOMOS(CONVENIO ESPECIAL)", null, null ),
	TCHRGCOT_0611( "0611", "TRABAJADOR POR CUENTA AJENA", null, null ),
	TCHRGCOT_0613( "0613", "RÉGIMEN ESPECIAL AGRARIO", null, null ),
	TCHRGCOT_0640( "0640", "REG.ESP.AGRARIO CUENTA AJ.(CONVENIO ESP)", null, null ),
	TCHRGCOT_0650( "0650", "REA C/A.CCC.RESP.SOLID.,SUBS.,MORTIS C", null, null ),
	TCHRGCOT_0721( "0721", "TRABAJADOR CUENTA PROPIA (R.E.A.)", null, null ),
	TCHRGCOT_0740( "0740", "REG.ESP.AGRARIO CUENTA PR.(CONVENIO ESP)", null, null ),
	TCHRGCOT_0800( "0800", "RÉGIMEN ESPECIAL DE TRABAJADORES DEL MAR", null, null ),
	TCHRGCOT_0811( "0811", "TRABAJADOR POR CUENTA AJENA GRUPO 1 (RE. MAR)", null, null ),
	TCHRGCOT_0812( "0812", "TRABAJADOR POR CUENTA AJENA GRUPO 2ª (RE. MAR)", null, null ),
	TCHRGCOT_0813( "0813", "TRABAJADOR POR CUENTA AJENA GRUPO 2B(RE. MAR)", null, null ),
	TCHRGCOT_0814( "0814", "TRABAJADOR POR CUENTA AJENA GRUPO 3 (RE. MAR)", null, null ),
	TCHRGCOT_0821( "0821", "TRABAJADOR POR CUENTA PROPIA GRUPO 1 ASIM. C.AJENA (R.E.M.)", null, null ),
	TCHRGCOT_0822( "0822", "TRABAJADOR POR CUENTA PROPIA GRUPO 2.A ASIMI. C.AJENA (R.E.M)", null, null ),
	TCHRGCOT_0823( "0823", "TRABAJADOR POR CUENTA PROPIA GRUPO 2.B ASIM. C. AJENA (R.E.M)", null, null ),
	TCHRGCOT_0825( "0825", "TRABAJADOR POR CUENTA PROPIA GRUPO 3 C. INDIVIDUAL (R.E.M)", null, null ),
	TCHRGCOT_0831( "0831", "REGIMEN ESP.MAR.AUTONOMOS CESE ACTIVIDAD", null, null ),
	TCHRGCOT_0840( "0840", "REG.ESPECIAL DEL MAR (CONVENIO ESPECIAL)", null, null ),
	TCHRGCOT_0850( "0850", "R.E.MAR.C/A.RESP.SOLID.,SUBS.,MORTIS C", null, null ),
	TCHRGCOT_0899( "0899", "RÉGIMEN ESPECIAL MAR DESEMPLEO", null, null ),
	TCHRGCOT_0911( "0911", "MINERÍA DEL CARBÓN", null, null ),
	TCHRGCOT_0940( "0940", "REG.ESP.MINERIA CARBON (CONVENIO ESP.)", null, null ),
	TCHRGCOT_0950( "0950", "R.E.CARSEPEON.RESP.SOLIDAR.,SUSEPES.,MORTIS C", null, null ),
	TCHRGCOT_1211( "1211", "RÉGIMEN ESPECIAL HOGAR (FIJOS)", null, null ),
	TCHRGCOT_1221( "1221", "RÉGIMEN ESPECIAL HOGAR (DISCONTINUOS)", null, null ),
	TCHRGCOT_1240( "1240", "REG.ESP.EMPLEADOS HOGAR (CONVENIO ESP.)", null, null ),
	TCHRGCOT_1250( "1250", "R.E.H.FIJOS RESP.SOLIDAR.,SUBS,MORTIS C", null, null ),
	TCHRGCOT_1911( "1911", "SEGURO ESCOLAR", null, null ),
	TCHRGCOT_2300( "2300", "RECURSOS DIVERSOS", null, null ),
	TCHRGCOT_2311( "2311", "RESPONS. DE RECURSOS DISTINTOS DE CUOTAS", null, null ),
	TCHRGCOT_3011( "3011", "CONCIERTO ASISTENCIA SANITARIA", null, null ),
	TCHRGCOT_3040( "3040", "ASISTENCIA SANITARIA(CONVENIO ESPECIAL)", null, null ),
	TCHRGCOT_4008( "4008", "CONVENIOS ESPECIALES DE R.E.M.", null, null ),
	;
	private String value;
	private String label;
	private String startDate;
	private String endDate;

	TCHRGCOT( String value, String label, String startDate, String endDate ) {
		this.value = value;
		this.label = label;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public static TCHRGCOT getEnumByValue(String expression) {
		for( TCHRGCOT o : TCHRGCOT.values() ) {
			if ( o.getValue().equals(expression) ) {
				return o;
			}
		}
		return null;
	}

}