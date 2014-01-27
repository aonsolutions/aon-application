package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;


public enum IndefiniteCommonField implements IContractFieldName{
		
	/* 
	 * Contract enterprise fields
	 */
	ENTERPRISE_CIF("Texto3"),
	ENTERPRISE_DIR_STAFF_NAME("Texto4"),
	ENTERPRISE_DIR_STAFF_NIF("Texto5"),
	ENTERPRISE_DIR_STAFF_CHARGE("Texto6"),
	ENTERPRISE_NAME("Texto7"),
	ENTERPRISE_ADDRESS("Texto8"),
	ENTERPRISE_COUNTRY("Texto9"),
	ENTERPRISE_COUNTRY_CODE1("Texto10"),
	ENTERPRISE_COUNTRY_CODE2("Texto11"),
	ENTERPRISE_COUNTRY_CODE3("Texto12"),
	ENTERPRISE_MUNICIPALITY("Texto13"),
	ENTERPRISE_MUNICIPALITY_CODE1("Texto14"),
	ENTERPRISE_MUNICIPALITY_CODE2("Texto15"),
	ENTERPRISE_MUNICIPALITY_CODE3("Texto16"),
	ENTERPRISE_MUNICIPALITY_CODE4("Texto17"),
	ENTERPRISE_MUNICIPALITY_CODE5("Texto18"),
	ENTERPRISE_ZIP1("Texto19"),
	ENTERPRISE_ZIP2("Texto20"),
	ENTERPRISE_ZIP3("Texto21"),
	ENTERPRISE_ZIP4("Texto22"),
	ENTERPRISE_ZIP5("Texto23"),
	
	/* 
	 * Contract ccc fields
	 */
	CCC_REG1("Texto24"),
	CCC_REG2("Texto25"),
	CCC_REG3("Texto26"),
	CCC_REG4("Texto27"),
	CCC_PROV1("Texto28"),
	CCC_PROV2("Texto29"),
	CCC_NISS("Texto30"),
	CCC_CONTROL_DIGIT1("Texto31"),
	CCC_CONTROL_DIGIT2("Texto32"),
	CCC_ACTIVITY("Texto33"),
	CCC_ACTIVITY_CODE1("Texto34"),
	CCC_ACTIVITY_CODE2("Texto35"),
	
	/* 
	 * Contract workplace fields
	 */
	WORKPLACE_COUNTRY("Texto36"),
	WORKPLACE_COUNTRY_CODE1("Texto37"),
	WORKPLACE_COUNTRY_CODE2("Texto38"),
	WORKPLACE_COUNTRY_CODE3("Texto39"),
	WORKPLACE_MUNICIPALITY("Texto40"),
	WORKPLACE_MUNICIPALITY_CODE1("Texto41"),
	WORKPLACE_MUNICIPALITY_CODE2("Texto42"),
	WORKPLACE_MUNICIPALITY_CODE3("Texto43"),
	WORKPLACE_MUNICIPALITY_CODE4("Texto44"),
	WORKPLACE_MUNICIPALITY_CODE5("Texto45"),
	
	/*
	 * Contract employee fields
	 */
	EMPLOYEE_NAME("Texto46"),
	EMPLOYEE_NIF("Texto47"),
	EMPLOYEE_BIRTH_DATE("Texto48"),
	EMPLOYEE_NSS("Texto49"),
	EMPLOYEE_FORMATION_LEVEL("Texto50"),
	EMPLOYEE_FORMATION_CODE1("Texto51"),
	EMPLOYEE_FORMATION_CODE2("Texto52"),
	EMPLOYEE_COUNTRY("Texto53"),
	EMPLOYEE_COUNTRY_CODE1("Texto54"),
	EMPLOYEE_COUNTRY_CODE2("Texto55"),
	EMPLOYEE_COUNTRY_CODE3("Texto56"),
	EMPLOYEE_ADDRESS_MUNICIPALITY("Texto57"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1("Texto58"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2("Texto59"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3("Texto60"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4("Texto61"),
	EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5("Texto62"),
	EMPLOYEE_ADDRESS_COUNTRY("Texto63"),
	EMPLOYEE_ADDRESS_COUNTRY_CODE1("Texto64"),
	EMPLOYEE_ADDRESS_COUNTRY_CODE2("Texto65"),
	EMPLOYEE_ADDRESS_COUNTRY_CODE3("Texto66"),

	/*
	 * LEGAL REPRESENTATION
	 */
	LEGAL_REPRESENTATIVE_NAME("Texto67"),
	LEGAL_REPRESENTATIVE_NIF("Texto68"),
	LEGAL_REPRESENTATIVE_CHARGE("Texto69"),
	
	/*
	 * Contract page 1
	 */
	PROFESSION("Texto71"),
	CATEGORY("Texto72"),
	// FUNCTIONS
	WORKPLACE_FULL_ADDRESS("Texto75"),
	WORKPLACE_FULL_ADDRESS_MORE("Texto73"),
	// DISTANCE_WORKING
	// DISTANCE_WORKING_ADDRESS
	
	// DISCONTONUOUS_WORK_DESCRIPTION
	// DISCONTONUOUS_WORK_ACTIVITY
	// DISCONTONUOUS_WORK_DURATION
	// DISCONTONUOUS_WORK_ESTIMATED_DURATION
	// DISCONTONUOUS_WORK_AGREEMENT_COLLECTIVE
	// DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_HOURS
	// DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_PERIOD
	// DISCONTONUOUS_WORK_ESTIMATED_SCHEDULE
	// DISCONTINUOUS_AGREEMENT_COLLECTIVE_YES
	// DISCONTINUOUS_AGREEMENT_COLLECTIVE_NO
	// **UNKNOWN**
	
	FULL_TIME("Casilla de verificación82"),
	FULL_TIME_WEEK_HOURS("Texto8696",Boolean.TRUE),
	FULL_TIME_START_TIME("Texto8620",Boolean.TRUE),
	FULL_TIME_END_TIME("Texto86",Boolean.TRUE),
	PARTIALLY_TIME("Casilla de verificación83"),
	PARTIALLY_TIME_HOURS("Texto86ññññ"),
	PARTIALLY_TIME_DAYLY("Casilla de verificación84879"),
	PARTIALLY_TIME_WEEKLY("Casilla de verificación84236"),
	PARTIALLY_TIME_MONTHLY("Casilla de verificación84opi"),
	PARTIALLY_TIME_YEARLY("Casilla de verificación85"),
	// HOURS
	// COMPLEMENTARY_HOURS_YES
	// COMPLEMENTARY_HOURS_NO
	
	/*
	 * Contract page 2
	 */
	START_DATE("Texto92"),
	TRIAL_DURATION("Text9107",Boolean.TRUE),

	SALARY_AMOUNT("Texto1",Boolean.TRUE),
	SALARY_PERIOD("Texto2",Boolean.TRUE),
	SALARY_CONCEPT("Texto70",Boolean.TRUE),

	HOLIDAYS("Texto91005500",Boolean.TRUE),

	AGREEMENT_COLLECTIVE("Texto82"),

	RELIEF_CONTRACT_YES("Casilla de verificación8423"),
	RELIEF_CONTRACT_NO("Casilla de verificación84mk"),
	// RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY1
	// RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY2
	// RELIEF_CONTRACT_PARTIALLY_CONTRACT_SEPE_MUNICIPALITY
	// RELIEF_CONTRACT_PARTIALLY_CONTRACT_NUMBER
	// RELIEF_CONTRACT_PARTIALLY_CONTRACT_DATE
	
	SEPE_MUNICIPALITY("oecomu",Boolean.TRUE),
	
	;
	
	private String value;
	private boolean overridable;
	private boolean check;
	
	private IndefiniteCommonField(String value, boolean... values) {
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
	
	