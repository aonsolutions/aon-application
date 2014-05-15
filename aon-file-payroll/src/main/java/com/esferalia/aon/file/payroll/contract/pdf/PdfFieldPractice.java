package com.esferalia.aon.file.payroll.contract.pdf;

import java.util.ResourceBundle;

import org.apache.commons.lang.ArrayUtils;


public enum PdfFieldPractice implements IContractFieldName{
	
	/*
	 * Contract page 1
	 */
	TC2_100,
	TC2_150,
	LEGAL_REPRESENTATIVE_NAME,
	LEGAL_REPRESENTATIVE_NIF,
	LEGAL_REPRESENTATIVE_CHARGE,
	ART4_RDL_3_2012_YES,
	ART4_RDL_3_2012_NO,
	PROFESSION,
	CATEGORY,
	FUNCTION,
	WORKPLACE_FULL_ADDRESS,
	CONTRACT_START_DATE,
	WEEK_HOURS(Boolean.TRUE),
	START_TIME(Boolean.TRUE),
	END_TIME(Boolean.TRUE),
	
	/*
	 * Contract page 2
	 */
	SALARY_AMOUNT(Boolean.TRUE),
	SALARY_PERIOD,
	SALARY_CONCEPT(Boolean.TRUE),
	HOLIDAYS(Boolean.TRUE),
	RELIEF_CONTRACT_YES,
	RELIEF_CONTRACT_NO,
	ART4_RDL_3_2012_BT_16_30_UNEMPLOYED,
	ART4_RDL_3_2012_BT_16_30_YOUNG,
	ART4_RDL_3_2012_BT_16_30_WOMAN,
	ART4_RDL_3_2012_GT_45_UNEMPLOYED,
	ART4_RDL_3_2012_GT_45,
	ART4_RDL_3_2012_GT_45_WOMAN,
	UNEMPLOYED_WITH_3_BENEFIT,
	FIRST_EMPLOYEE_LT_30,
	AGREEMENT_COLLECTIVE,
	SEPE_MUNICIPALITY(Boolean.TRUE),
	ADDITIONAL_CLAUSES,
	
	// OVERRIDES FIELDS
	ENTERPRISE_COUNTRY1,
	ENTERPRISE_MUNICIPALITY1,
	WORKPLACE_COUNTRY1,
	WORKPLACE_MUNICIPALITY1,
	EMPLOYEE_COUNTRY1,
	EMPLOYEE_ADDRESS_MUNICIPALITY1,
	EMPLOYEE_ADDRESS_COUNTRY1,
	
	/* Contract PAGE 3 */
	MAIN_OPT1_CHECK,
	MAIN_OPT2_CHECK,
	MAIN_OPT3_CHECK,
	MAIN_OPT4_CHECK,
	MAIN_OPT5_CHECK,
	
//		PRÁCTICAS ( ORDINARIO ). (pag. 4)
	OPT1_OPTION_CHECK,		
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMA DE TERRORISMO .(pag.5)
	OPT2_OPTION_CHECK,		
//		DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO (pag.6)
	OPT3_OPTION_CHECK,		
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pag.7)
	OPT4_OPTION_CHECK,		
//		DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO. (pag.8)
	OPT5_OPTION_CHECK,	
	
	;
	
	private String BASE_NAME = "com.esferalia.aon.file.payroll.i18n.PdfFieldsPractice";
	
	private boolean overridable;
	private boolean check;
	
	private PdfFieldPractice(boolean... values) {
		this.overridable = (ArrayUtils.getLength(values)>0)?values[0]:false;		
		this.check= (ArrayUtils.getLength(values)>1)?values[1]:false;
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
	public boolean isCommonValue(){
		return !this.toString().matches("OPT\\d+_\\w+");
	}
	@Override
	public String getValue(){
		return ResourceBundle.getBundle(BASE_NAME).getString(toString());
	}
	@Override
	public IContractFieldName[] getCompositeValues(){
		return null;
	}
	
}
	