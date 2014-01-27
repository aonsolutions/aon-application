package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;


public enum IndefiniteOptionField implements IContractFieldName {
	
	/* Contract PAGE 3 */
	MAIN_OPT1_CHECK("Casilla de verificación871"),
	MAIN_OPT2_CHECK("Casilla de verificación872"),
	MAIN_OPT3_CHECK("Casilla de verificación873"),
	MAIN_OPT4_CHECK("Casilla de verificación874"),
	MAIN_OPT5_CHECK("Casilla de verificación875"),
	MAIN_OPT6_CHECK("Casilla de verificación876"),
	MAIN_OPT7_CHECK("Casilla de verificación877"),
	MAIN_OPT8_CHECK("Casilla de verificación878"),
	MAIN_OPT9_CHECK("Casilla de verificación879"),
	MAIN_OPT10_CHECK("Casilla de verificación8710"),
	MAIN_OPT11_CHECK("Casilla de verificación8711"),
	MAIN_OPT12_CHECK("Casilla de verificación8712"),
	MAIN_OPT13_CHECK("Casilla de verificación8713"),
	MAIN_OPT14_CHECK("Casilla de verificación8714"),
	MAIN_OPT15_CHECK("Casilla de verificación8715"),
	MAIN_OPT16_CHECK("Casilla de verificación7"),
	MAIN_OPT17_CHECK("Casilla de verificación14ddddddddd"),
	

	
//		INDEFINIDO ORDINARIO (pag. 4)
	OPT1_OPTION_CHECK("Casilla de verificación8766"),
	OPT1_TC2_100("Casilla de verificación8oooo"),
	OPT1_TC2_200("Casilla de verificación87333"),
	OPT1_TC2_300("Casilla de verificación872369"),
//		DE PERSONAS CON DISCAPACIDAD (pag. 5)
	OPT2_OPTION_CHECK("Casilla de verificación48"),
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO (pag.6)
	OPT3_OPTION_CHECK("Casilla de verificación48"),
//		DE PERSONAS CON DISCAPACIDAD PROCEDENTES DE ENCLAVES LABORALES (pag.7)
	OPT4_OPTION_CHECK("Casilla de verificación48"),
//		DE APOYO A LOS EMPRENDEDORES (pag.8)
	OPT5_OPTION_CHECK("Casilla de verificación84opi369612369"),
	OPT5_FULL_TIME("Casilla de verificación8"),
	OPT5_TC2_100("Casilla de verificación11"),
	OPT5_TC2_150("Casilla de verificación12"),
	OPT5_PARTIALLY_TIME("Casilla de verificación9"),
	OPT5_TC2_200("Casilla de verificación84opi36ñpoi"),
	OPT5_TC2_250("Casilla de verificación84opi3632548"),
	OPT5_DISCONTINUOUS_TIME("Casilla de verificación10"),
	OPT5_TC2_300("Casilla de verificación84opi895"),
	OPT5_TC2_350("Casilla de verificación84opi3692369666"),
	OPT5_BONUS_ART4_RDL3_2012_YES("Casilla de verificación84opi3692668889"),
	OPT5_BONUS_ART4_RDL3_2012_NO("Casilla de verificación84opi698"),
	OPT5_REGISTERED_IN_EMPLOYMENT_OFFICE("Casilla de verificación84opi3pñoium"),
	OPT5_UNEMPLOYED_BT_16_30("Casilla de verificación84opi36998756"),
	OPT5_UNEMPLOYED_BT_16_30_JUNIOR("Casilla de verificación84opi3698715"),
	OPT5_UNEMPLOYED_BT_16_30_FEMALE("Casilla de verificación84opi301258963185"),
	OPT5_UNEMPLOYED_GT_45("Casilla de verificación84opi3987562489"),
	OPT5_UNEMPLOYED_GT_45_MALE("Casilla de verificación84opi3698888887"),
	OPT5_UNEMPLOYED_GT_45_FEMALE("Casilla de verificación84opi3669875658"),
	OPT5_UNEMPLOYED_WITH_3_MONTH_BENEFIT("Casilla de verificación84opi99634582"),
	OPT5_FIRST_EMPLOYEE_AND_LT_30("Casilla de verificación84opi36926897"),
//		DE UN JÓVEN POR MICROEMPRESAS Y EMPRESARIOS AUTÓNOMOS (pag.9)
	OPT6_OPTION_CHECK("Casilla de verificación84opi987"),
	OPT6_TC2_100("Casilla de verificación84opi37523"),
	OPT6_TC2_200("Casilla de verificación84opi3692664"),
	OPT6_LT_30_EMPLOYEE("Casilla de verificación84opi369569"),
	OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33("Casilla de verificación84opi3663781"),
	OPT6_AGREEMENT_COLLECTIVE1("Texto101"),
	OPT6_AGREEMENT_COLLECTIVE2("Texto102"),
//		DE NUEVO PROYECTO DE EMPRENDIMIENTO JOVEN (pag.10)
	OPT7_OPTION_CHECK("Casilla de verificación84opi36302"),
	OPT7_TC2_100("Casilla de verificación84opi3692874"),
	OPT7_TC2_200("Casilla de verificación84opi369236"),
	OPT7_TC2_300("Casilla de verificación84opi36921234"),
	OPT7_UNEMPLOYED_DURING_12_MONTH("129266666"),
	OPT7_PROFFESIONAL_RECUALIFICATION("Casilla de verificación84opi3692623"),
	OPT7_AGREEMENT_COLLECTIVE1("Texto103"),
	OPT7_AGREEMENT_COLLECTIVE2("Texto104"),
//		A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA (pag.11)
	OPT8_OPTION_CHECK("Casilla de verificación48"),
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMAS DE TERRORISMO (pag.12)
	OPT9_OPTION_CHECK("Casilla de verificación48"),
//		DE EXCLUIDOS EN EMPRESAS DE INSERCIÓN (pag.13)
	OPT10_OPTION_CHECK("Casilla de verificación48"),
//		DE MAYORES DE 52 AÑOS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO (pag.14)
	OPT11_OPTION_CHECK("Casilla de verificación48"),
//		PROCENTE DE PRIMER EMPLEO JOVEN DE ETT. (pag.15)
	OPT12_OPTION_CHECK("Casilla de verificación48"),
//		PROCEDENTE DE UN CONTRATO PARA LA FORMACIÓN Y EL APRENDIZAJE DE ETT (pag.16)
	OPT13_OPTION_CHECK("Casilla de verificación48"),
//		PROCEDENTE DE UN CONTRATO EN PRÁCTICAS DE ETT. ( pág 17)
	OPT14_OPTION_CHECK("Casilla de verificación48"),
//		DEL SERVICIO DEL HOGAR FAMILIAR (pag.18)
	OPT15_OPTION_CHECK("Casilla de verificación112"),
	OPT15_TC2_100("Casilla de verificación180"),
	OPT15_TC2_200("Casilla de verificación181"),
	OPT15_ONSITE_HOURS_YES("Casilla de verificación178"),
	OPT15_ONSITE_HOURS_NO("Casilla de verificación179"),
	OPT15_ONSITE_WEEK_HOURS("Texto187"),
	OPT15_ONSITE_HOURS_DISTRIBUTION("Texto110"),
	OPT15_SALARY_OPT1("Casilla de verificación182"),
	OPT15_SALARY_OPT2("Casilla de verificación183"),
	OPT15_SALARY_OPT3("Casilla de verificación184"),
	OPT15_OVERNIGHT_YES("Casilla de verificación185"),
	OPT15_OVERNIGHT_NO("Casilla de verificación186"),
	OPT15_OVERNIGHT_WEEK_DAYS("Texto188"),
//		OTRAS SITUACIONES (pág19)
	OPT16_OPTION_CHECK("Casilla de verificación48"),
//		CONVERSIÓN DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO (pag.20)
	OPT17_OPTION_CHECK("Casilla de verificación48"),
	OPT17_FULL_TIME("Casilla de verificación190"),
	OPT17_TC2_139("Casilla de verificación193"),
	OPT17_TC2_109("Casilla de verificación1931"),
	OPT17_TC2_189("Casilla de verificación19321"),
	OPT17_FULL_TIME_QUOTE_BONUS_YES("Casilla de verificación3"),
	OPT17_FULL_TIME_QUOTE_BONUS_NO("Casilla de verificación4"),
	OPT17_PARTIALLY_TIME("Casilla de verificación191"),
	OPT17_TC2_239("Casilla de verificación1933"),
	OPT17_TC2_209("Casilla de verificación1946"),
	OPT17_TC2_289("Casilla de verificación19569"),
	OPT17_PARTIALLY_TIME_QUOTE_BONUS_YES("Casilla de verificación5"),
	OPT17_PARTIALLY_TIME_QUOTE_BONUS_NO("Casilla de verificación6"),
	OPT17_DISCONTINUOUS_TIME("Casilla de verificación192"),
	OPT17_TC2_339("Casilla de verificación19398"),
	OPT17_TC2_309("Casilla de verificación1937892"),
	OPT17_TC2_389("Casilla de verificación19371"),
	OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_YES("Casilla de verificación1"),
	OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_NO("Casilla de verificación2"),
	OPT17_SEPE_MUNICIPALITY("Texto197"),
	OPT17_TRANSFORM_DATE("Texto198"),
	OPT17_IS_FULL_TIME("Casilla de verificación1938888"),
	OPT17_IS_FULL_TIME_DISCONTINUOUS("Casilla de verificación19333332"),
	OPT17_SOURCE_CONTRACT("Texto199"),
	OPT17_SOURCE_CONTRACT_START_DATE("Texto200"),
	OPT17_SOURCE_CONTRACT_SEPE_MUNICIPALITY("Texto202"),
	OPT17_SOURCE_CONTRACT_SEPE_DATE("Texto203"),
	OPT17_SOURCE_CONTRACT_SEPE_ID("Texto204"),
	;
	
	
	private String value;
	private boolean overridable;
	private boolean check;
	
	private IndefiniteOptionField(String value, boolean... values) {
		this.value = value;
		if(ArrayUtils.getLength(values)>0){
			this.overridable = values[0];
		}
		if(ArrayUtils.getLength(values)>1){
			this.check = values[1];
		}
	}
	
	@Override
	public boolean isOverridable(){
		return overridable;
	}
	@Override
	public boolean isCheck(){
		return check;
	}
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public IContractFieldName[] getCompositeValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
	
	