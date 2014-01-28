package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;


public enum TemporaryCommonField implements IContractFieldName{
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
	PROFESSION("Texto1"),
	CATEGORY("Texto21"),
	FUNCTIONS("Texto22",Boolean.TRUE),
	WORKPLACE_FULL_ADDRESS("Texto23"),
	WORKPLACE_FULL_ADDRESS_MORE("Texto24"),
	EMPLOYEE_CONTRACT_DISTANCE("Casilla de verificación25",Boolean.TRUE,Boolean.TRUE),
	EMPLOYEE_CONTRACT_DISTANCE_ADDR("Texto26", Boolean.TRUE),
	FULL_TIME("Casilla de verificación27"),
	FULL_TIME_WEEK_HOURS("Texto28",Boolean.TRUE),
	FULL_TIME_START_TIME("Texto29",Boolean.TRUE),
	FULL_TIME_END_TIME("Texto30",Boolean.TRUE),
	PARTIALLY_TIME("Casilla de verificación31"),
	PARTIALLY_TIME_HOURS("Texto32"),
	PARTIALLY_TIME_DAYLY("Casilla de verificación33"),
	PARTIALLY_TIME_WEEKLY("Casilla de verificación34"),
	PARTIALLY_TIME_MONTHLY("Casilla de verificación35"),
	PARTIALLY_TIME_YEARLY("Casilla de verificación36"),
	PARTIALLY_TIME_JOB_LOWER_THAN("Texto321",Boolean.TRUE),
	PARTIALLY_TIME_JOB_DISTRIBUTION("Texto322",Boolean.TRUE),
	START_DATE("Texto37"),
	END_DATE("Texto38"),
	TRIAL_DURATION("Texto39",Boolean.TRUE),
	GREATER_DURATION_AGREEMENT_COL("Casilla de verificación40",Boolean.TRUE,Boolean.TRUE),
	SALARY_AMOUNT("Texto42",Boolean.TRUE),
	SALARY_PERIOD("Texto43"),
	SALARY_CONCEPT("Texto44",Boolean.TRUE),
	
	/*
	 * Contract page 2
	 */
	HOLIDAYS("Texto45",Boolean.TRUE),
	SEPE_MUNICIPALITY("Texto46",Boolean.TRUE),
	
	;
	
	private String value;
	private boolean overridable;
	private boolean check;
	
	private TemporaryCommonField(String value, boolean... values) {
		this.value = value;
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
	public String getValue() {
		return value;
	}
	
	public TemporaryCommonField[] getCompositeValues(){
		return null;
	}
}	
