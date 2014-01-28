package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;


public enum TemporaryOptionField implements IContractFieldName {

	/* Contract PAGE 3 */
	MAIN_OPT1_CHECK("Casilla de verificación47"),
	MAIN_OPT2_CHECK("Casilla de verificación47q"),
	MAIN_OPT3_CHECK("Casilla de verificación47w"),
	MAIN_OPT4_CHECK("Casilla de verificación47e"),
	MAIN_OPT5_CHECK("Casilla de verificación47r"),
	MAIN_OPT6_CHECK("Casilla de verificación47t"),
	MAIN_OPT7_CHECK("Casilla de verificación47y"),
	MAIN_OPT8_CHECK("Casilla de verificación47u"),
	MAIN_OPT9_CHECK("Casilla de verificación47i"),
	MAIN_OPT10_CHECK("Casilla de verificación47o"),
	MAIN_OPT11_CHECK("Casilla de verificación47p"),
	MAIN_OPT12_CHECK("Casilla de verificación47a"),
	MAIN_OPT13_CHECK("Casilla de verificación47s"),
	MAIN_OPT14_CHECK("Casilla de verificación47d"),
	MAIN_OPT15_CHECK("Casilla de verificación47f"),
	MAIN_OPT16_CHECK("Casilla de verificación47g"),
	MAIN_OPT17_CHECK("Casilla de verificación47h"),
	MAIN_OPT18_CHECK("Casilla de verificación47j"),
	
//		OBRA O SERVICIO DETERMINADO. ( pág.4 )
	OPT1_OPTION_CHECK("Casilla de verificación48"),
	OPT1_TC2_401("Casilla de verificación481"),
	OPT1_TC2_501("Casilla de verificación482"),
	OPT1_WORK_DESCRIPTION1("Texto49",Boolean.TRUE),
	OPT1_WORK_DESCRIPTION2("Texto50",Boolean.TRUE),
//		EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCIÓN. (pág.5 )
	OPT2_OPTION_CHECK("Casilla de verificación51"),
	OPT2_TC2_402("Casilla de verificación51a"),
	OPT2_TC2_502("Casilla de verificación51s"),
	OPT2_WORK_DESCRIPTION1("Texto52",Boolean.TRUE),
	OPT2_WORK_DESCRIPTION2("Texto53",Boolean.TRUE),
//		INTERINIDAD. ( pág.6 )
	OPT3_OPTION_CHECK("Casilla de verificación55"),
	OPT3_TC2_410("Casilla de verificación551"),
	OPT3_TC2_510("Casilla de verificación552"),
	OPT3_REPLACED_WORKER_NAME("Texto56",Boolean.TRUE),
	OPT3_CAUSE1("Casilla de verificación553"),
	OPT3_CAUSE2("Casilla de verificación554"),
	OPT3_CAUSE3("Casilla de verificación555"),
	OPT3_CAUSE4("Casilla de verificación556"),
	OPT3_CAUSE5("Casilla de verificación557"),
	OPT3_CAUSE6("Casilla de verificación558"),
	OPT3_CAUSE7("Casilla de verificación559"),
	OPT3_CAUSE7_OPT1("Casilla de verificación5510"),
	OPT3_CAUSE7_OPT2("Casilla de verificación5511"),
	OPT3_CAUSE1_BONUS("Casilla de verificación57"),
	OPT3_CAUSE2_BONUS("Casilla de verificación571"),
	OPT3_CAUSE3_BONUS("Casilla de verificación572"),
	OPT3_CAUSE4_BONUS("Casilla de verificación573"),
	OPT3_CAUSE5_BONUS("Casilla de verificación574"),
	OPT3_CAUSE6_BONUS("Casilla de verificación575"),
//		PRIMER EMPLEO JOVEN. ( pág.7 )
	OPT4_OPTION_CHECK(""),
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO. ( pág.8 )
	OPT5_OPTION_CHECK(""),
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL POR EMPRESA DE INSERCIÓN. ( pág.9 )
	OPT6_OPTION_CHECK(""),
//		DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO. ( pág.10 )
	OPT7_OPTION_CHECK(""),
//		SITUACIÓN DE JUBILACIÓN PARCIAL. ( pág.11 )
	OPT8_OPTION_CHECK("Casilla de verificación70"),
	OPT8_REDUCTION_PERCENT("Texto71"),
//		RELEVO. ( pág.12 )
	OPT9_OPTION_CHECK(""),
//		A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA. ( pág.13 )
	OPT10_OPTION_CHECK(""),
//		DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.14 )
	OPT11_OPTION_CHECK("Casilla de verificación3"),
	OPT11_FULL_TIME("Casilla de verificación28"),
	OPT11_TC2_401("Casilla de verificación28a"),
	OPT11_TC2_402("Casilla de verificación28z"),
	OPT11_TC2_410("Casilla de verificación28w"),
	OPT11_TC2_450("Casilla de verificación28s"),
	OPT11_TC2_990_FULL_TIME("Casilla de verificación28x"),
	OPT11_PARTIALLY_TIME("Casilla de verificación28q"),
	OPT11_TC2_501("Casilla de verificación28e"),
	OPT11_TC2_502("Casilla de verificación28d"),
	OPT11_TC2_510("Casilla de verificación28c"),
	OPT11_TC2_550("Casilla de verificación28r"),
	OPT11_TC2_990_PARTIALLY_TIME("Casilla de verificación28f"),
	OPT11_SOCIAL_INTERES_CHECK("Casilla de verificación28v"),
	OPT11_AGRICULTURAL_PROMOTION_CHECK("Casilla de verificación28t"),
	OPT11_EMPLOYER_LOCAL_CORPORATION_CHECK("Casilla de verificación28g"),
	OPT11_EMPLOYER_STATE_ADMINISTRATION_CHECK("Casilla de verificación28b"),
	OPT11_EMPLOYER_COMMUNITY_CHECK("Casilla de verificación28y"),
	OPT11_EMPLOYER_NONPROFIT_ENTITY_CHECK("Casilla de verificación28h"),
	OPT11_EMPLOYER_UNIVERSITY_CHECK("Casilla de verificación28n"),
	OPT11_COLLECTIVE_AGREEMENT("Texto31"),
//		DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR. (pág.15 )
	OPT12_OPTION_CHECK("Casilla de verificación4"),
	OPT12_FULL_TIME("Casilla de verificación12"),
	OPT12_TC2_401("Casilla de verificación32q"),
	OPT12_TC2_410("Casilla de verificación32"),
	OPT12_PARTIALLY_TIME("Casilla de verificación13"),
	OPT12_TC2_501("Casilla de verificación32e"),
	OPT12_TC2_510("Casilla de verificación32w"),
	OPT12_ONSITE_HOURS_YES("Casilla de verificación32r"),
	OPT12_ONSITE_HOURS_NO("Casilla de verificación32t"),
	OPT12_ONSITE_HOURS(OPT12_ONSITE_HOURS_YES, OPT12_ONSITE_HOURS_NO),
	OPT12_ONSITE_WEEK_HOURS("Texto33",Boolean.TRUE),
	OPT12_ONSITE_HOURS_DISTRIBUTION("Texto34",Boolean.TRUE),
	OPT12_SALARY_OPT1("Casilla de verificación32y"),
	OPT12_SALARY_OPT2("Casilla de verificación32u"),
	OPT12_SALARY_OPT3("Casilla de verificación32i"),
	OPT12_SALARY_OPT(OPT12_SALARY_OPT1, OPT12_SALARY_OPT2, OPT12_SALARY_OPT3),
	OPT12_OVERNIGHT_YES("Casilla de verificación32o"),
	OPT12_OVERNIGHT_NO("Casilla de verificación32p"),
	OPT12_OVERNIGHT(OPT12_OVERNIGHT_YES, OPT12_OVERNIGHT_NO),
	OPT12_OVERNIGHT_WEEK_DAYS("Texto35",Boolean.TRUE),
//		DE PERSONAS CON DISCAPACIDAD. (pág.16 )
	OPT13_OPTION_CHECK(""),
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pág.17 )
	OPT14_OPTION_CHECK("Casilla de verificación6"),
	OPT14_FULL_TIME("Casilla de verificación43"),
	OPT14_TC2_401("Casilla de verificación43w"),
	OPT14_TC2_402("Casilla de verificación43s"),
	OPT14_TC2_410("Casilla de verificación43x"),
	OPT14_TC2_430("Casilla de verificación43e"),
	OPT14_TC2_441("Casilla de verificación43d"),
	OPT14_TC2_990_FULL_TIME("Casilla de verificación43c"),
	OPT14_PARTIALLY_TIME("Casilla de verificación43q"),
	OPT14_TC2_501("Casilla de verificación43r"),
	OPT14_TC2_502("Casilla de verificación43f"),
	OPT14_TC2_510("Casilla de verificación43v"),
	OPT14_TC2_530("Casilla de verificación43t"),
	OPT14_TC2_540("Casilla de verificación43g"),
	OPT14_TC2_541("Casilla de verificación43b"),
	OPT14_TC2_990_PARTIALLY_TIME("Casilla de verificación43y"),
	OPT14_TRIAL_PERIOD("Texto47",Boolean.TRUE),
	OPT14_TRIAL_TERMS("Texto48",Boolean.TRUE),
	OPT14_PROFESSION("Texto51",Boolean.TRUE),
	OPT14_DISTANCE_ADJUSTMENT("Texto54",Boolean.TRUE),
	OPT14_DISTANCE_ADJUSTMENT_MORE("Texto55",Boolean.TRUE),
	OPT14_COLLECTIVE_AGREEMENT("Texto57",Boolean.TRUE),
//		DE INVESTIGADORES. ( pág.18 )
	OPT15_OPTION_CHECK(""),
//		DE TRABAJADOES/AS PENADOS EN INSTITUCIONES PENITENCIARIAS. (pág.19 )
	OPT16_OPTION_CHECK(""),
//		DE MENORES Y JÓVENES EN CENTROS DE MENORES. ( SOMETIDOS A MEDIDADAS DE INTERNAMIENTO PREVISTAS EN LA LEY ORGÁNICA 5/2000 DE 21 DE ENERO ). ( pág.20 )
	OPT17_OPTION_CHECK(""),
//		OTRAS SITUACIONES. ( pág.21 )
	OPT18_OPTION_CHECK(""),
	
	;
	
	private String value;
	private boolean overridable;
	private boolean check;
	private TemporaryOptionField[] compositeValues;
	
	private TemporaryOptionField(TemporaryOptionField... compositeValues) {
		this.compositeValues = compositeValues;
	}
	private TemporaryOptionField(String value, boolean... values) {
		this.value = value;
		this.overridable = (ArrayUtils.getLength(values)>0)?values[0]:false;
		this.check= (ArrayUtils.getLength(values)>1)?values[1]:false;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	@Override
	public boolean isOverridable(){
		return overridable || compositeValues!=null;
	}
	@Override
	public boolean isCheck(){
		return check || compositeValues!=null;
	}
	@Override
	public IContractFieldName[] getCompositeValues(){
		return compositeValues;
	}
	
}
	
	