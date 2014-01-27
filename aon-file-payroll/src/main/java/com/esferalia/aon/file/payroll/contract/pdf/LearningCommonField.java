package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;



public enum LearningCommonField implements IContractFieldName{
	/* 
	 * Contract enterprise fields
	 */
	ENTERPRISE_CIF("Textoasddfgghhjjkhfgf"),
	ENTERPRISE_DIR_STAFF_NAME("Texto2"),
	ENTERPRISE_DIR_STAFF_NIF("Texto3"),
	ENTERPRISE_DIR_STAFF_CHARGE("Texto4"),
	ENTERPRISE_NAME("Texto5"),
	ENTERPRISE_ADDRESS("Texto6"),
	ENTERPRISE_COUNTRY("Texto7"),
	ENTERPRISE_COUNTRY_CODE1("Cifra1"),
	ENTERPRISE_COUNTRY_CODE2("Cifra2"),
	ENTERPRISE_COUNTRY_CODE3("Cifra3"),
	ENTERPRISE_MUNICIPALITY("Texto8"),
	ENTERPRISE_MUNICIPALITY_CODE1("Cifra4"),
	ENTERPRISE_MUNICIPALITY_CODE2("Cifra5"),
	ENTERPRISE_MUNICIPALITY_CODE3("Cifra6"),
	ENTERPRISE_MUNICIPALITY_CODE4("Cifra7"),
	ENTERPRISE_MUNICIPALITY_CODE5("Cifra8"),
	ENTERPRISE_ZIP1("Cifra9"),
	ENTERPRISE_ZIP2("Cifra10"),
	ENTERPRISE_ZIP3("Cifra11"),
	ENTERPRISE_ZIP4("Cifra12"),
	ENTERPRISE_ZIP5("Cifra13"),
	
	/* 
	 * Contract ccc fields
	 */
	CCC_REG1("Cifra14"),
	CCC_REG2("Cifra15"),
	CCC_REG3("Cifra16"),
	CCC_REG4("Cifra17"),
	CCC_PROV1("Cifra18"),
	CCC_PROV2("Cifra19"),
	CCC_NISS("Texto9"),
	CCC_CONTROL_DIGIT1("Cifra20"),
	CCC_CONTROL_DIGIT2("Cifra21"),
	CCC_ACTIVITY("Texto10"),
	CCC_ACTIVITY_CODE1("Cifra22"),
	CCC_ACTIVITY_CODE2("Cifra23"),
	
	/* 
	 * Contract workplace fields
	 */
	WORKPLACE_COUNTRY("Texto11"),
	WORKPLACE_COUNTRY_CODE1("Cifra24"),
	WORKPLACE_COUNTRY_CODE2("Cifra25"),
	WORKPLACE_COUNTRY_CODE3("Cifra26"),
	WORKPLACE_MUNICIPALITY("Texto12"),
	WORKPLACE_MUNICIPALITY_CODE1("Cifra27"),
	WORKPLACE_MUNICIPALITY_CODE2("Cifra28"),
	WORKPLACE_MUNICIPALITY_CODE3("Cifra29"),
	WORKPLACE_MUNICIPALITY_CODE4("Cifra30"),
	WORKPLACE_MUNICIPALITY_CODE5("Cifra31"),
	
	/*
	 * Contract employee fields
	 */
	EMPLOYEE_NAME("Texto13"),
	EMPLOYEE_NIF("Texto14"),
	EMPLOYEE_BIRTH_DATE("Texto15"),
	EMPLOYEE_NSS("Texto16"),
	EMPLOYEE_FORMATION_LEVEL("Texto17"),
	EMPLOYEE_FORMATION_CODE1("Cifra32"),
	EMPLOYEE_FORMATION_CODE2("Cifra33"),
	EMPLOYEE_COUNTRY("Texto18"),
	EMPLOYEE_COUNTRY_CODE1("Cifra34"),
	EMPLOYEE_COUNTRY_CODE2("Cifra35"),
	EMPLOYEE_COUNTRY_CODE3("Cifra36"),
	EMPLOYEE_ADDRESS_MUNICIPALITY("Texto19"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1("Cifra37"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2("Cifra38"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3("Cifra39"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4("Cifra40"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5("Cifra41"),
	EMPLOYEE_ADDRESS_COUNTRY("Texto20"),
	EMPLOYEE_ADDRESS_COUNTRY_CODE1("Cifra42"),
	EMPLOYEE_ADDRESS_COUNTRY_CODE2("Cifra43"),
	EMPLOYEE_ADDRESS_COUNTRY_CODE3("Cifra44"),
	
	/*
	 * LEGAL REPRESENTATION
	 */
	LEGAL_REPRESENTATIVE_NAME("Renglon1"),
	LEGAL_REPRESENTATIVE_NIF("Renglon2"),
	LEGAL_REPRESENTATIVE_CHARGE("Renglon3"),
	
	/*
	 * Contract page 1
	 */
	QUOTE_BONUS_YES("Casilla de verificación1"),
	QUOTE_BONUS_NO("Casilla de verificación2"),
	EMPLOYEE_OPT1("Casilla de verificación3"),
	EMPLOYEE_OPT2("Casilla de verificación4"),
	EMPLOYEE_OPT3("Casilla de verificación5"),
	EMPLOYEE_OPT4("Casilla de verificación6"),
	
	ACTIVITY_EMPLOYEE_PROFFESION("Texto21"),
	CNO1("Texto26"),
	CNO2("Texto27"),
	CNO3("Texto28"),
	CNO4("Texto29"),
	ACTIVITY_EMPLOYEE_CATEGORY("Texto22"),
	ACTIVITY_WORKPLACE_ADDRESS1("Texto23"),
	ACTIVITY_WORKPLACE_ADDRESS2("Texto24"),
	
	/*
	 * Contract page 2
	 */
	CONTRACT_WORKPLACE_ADDRESS("Texto30"),
	CONTRACT_EMPLOYEE_PROFFESION("Texto31"),
	CONTRACT_EMPLOYEE_CATEGORY("Texto32"),
	FORMATION_TEACHER("Texto33"),
	FORMATION_TEACHER_QUALIFICATION("Texto34"),
	
	JOURNAL_HOURS("Texto35"),
	TOTAL_HOURS("Texto36"),
	JOURNAL_PERCENT("Texto37"),
	JOURNAL_COLLECTIVE_AGREEMENT("Texto38"),
//		EFFECTIVE_JOURNAL_SCHEDULE("Texto39",Boolean.TRUE),
	HORARIO_LABORAL("Texto39",Boolean.TRUE),
//		FORMATION_JOURNAL_SCHEDULE("Texto40",Boolean.TRUE),
	HORARIO_LECTIVO("Texto40",Boolean.TRUE),
//		FORMATION_JOURNAL_SCHEDULE2("Texto41"),
	HORARIO_LECTIVO2("Texto41"),
	
	CONTRACT_DURATION("Texto42"),
	START_DATE("Texto43"),
	END_DATE("Texto44"),
	TRIAL_DURATION("Texto45",Boolean.TRUE),
	TRIAL_DURATION_INCREASE("Casilla de verificación46"),
	
	SALARY_AMOUNT("Texto47",Boolean.TRUE),
	SALARY_PERIOD("Texto48",Boolean.TRUE),
	
	HOLIDAYS("Texto49",Boolean.TRUE),
	
	ANNEX_I_CHECK("Casilla de verificación59"),
	ANNEX_II_CHECK("Casilla de verificación60"),
	
	COLLECTIVE_AGREEMENT("Texto54g"),
	
	SEPE_TOWN_FOR_CONTRACT_START("Texto51"),
	SEPE_TOWN_FOR_CONTRACT_END("Texto52"),
	
	// SPECIFIC fields: because its length, it is defined forward
	
	// ANEX I fields: because its length, it is defined forward
	
	// ANEX II fields: because its length, it is defined forward
	
	SIGN_TOWN("Texto74"),
	SIGN_DAY("Texto75"),
	SIGN_MONTH("Texto76"),
	SIGN_YEAR("Texto77"),
	
	;
	
	private String value;
	private boolean overridable;
	private boolean check;
	
	private LearningCommonField(String value, boolean... values) {
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
	