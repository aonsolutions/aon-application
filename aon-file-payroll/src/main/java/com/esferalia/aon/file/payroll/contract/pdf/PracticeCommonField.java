package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;


public enum PracticeCommonField implements IContractFieldName{
	/*
	 * Contract page 1
	 */
	TC2_100("tipocontrato_100"),
	TC2_150("tipocontrato_150"),
	LEGAL_REPRESENTATIVE_NAME("Texto65"),
	LEGAL_REPRESENTATIVE_NIF("Texto66"),
	LEGAL_REPRESENTATIVE_CHARGE("Texto67"),
	ART4_RDL_3_2012_YES("Casilla de verificación78464"),
	ART4_RDL_3_2012_NO("Casilla de verificación71016430"),
	PROFESSION("profetraba"),
	CATEGORY("catetraba"),
	FUNCTION("funciontraba"),
	WORKPLACE_FULL_ADDRESS("calletrab"),
	CONTRACT_START_DATE("fechaini"),
	WEEK_HOURS("horasjorna1",Boolean.TRUE),
	START_TIME("horainicio",Boolean.TRUE),
	END_TIME("horafin",Boolean.TRUE),
	
	/*
	 * Contract page 2
	 */
	SALARY_AMOUNT("retribu",Boolean.TRUE),
	SALARY_PERIOD("perioretri"),
	SALARY_CONCEPT("concepsala",Boolean.TRUE),
	HOLIDAYS("vacaciones",Boolean.TRUE),
	RELIEF_CONTRACT_YES("Casilla de verificación7"),
	RELIEF_CONTRACT_NO("Casilla de verificación8"),
	ART4_RDL_3_2012_BT_16_30_UNEMPLOYED("Casilla de verificación11"),
	ART4_RDL_3_2012_BT_16_30_YOUNG("Casilla de verificación9"),
	ART4_RDL_3_2012_BT_16_30_WOMAN("Casilla de verificación10"),
	ART4_RDL_3_2012_GT_45_UNEMPLOYED("Casilla de verificación13"),
	ART4_RDL_3_2012_GT_45("Casilla de verificación12"),
	ART4_RDL_3_2012_GT_45_WOMAN("Casilla de verificación14"),
	UNEMPLOYED_WITH_3_BENEFIT("Casilla de verificación15"),
	FIRST_EMPLOYEE_LT_30("Casilla de verificación16"),
	AGREEMENT_COLLECTIVE("convcole"),
	SEPE_MUNICIPALITY("oecomu",Boolean.TRUE),
	ADDITIONAL_CLAUSES("T25"),
	
	// OVERRIDES FIELDS
	ENTERPRISE_COUNTRY1("Texto1pas1"),
	ENTERPRISE_MUNICIPALITY1("Texto2mun1"),
	WORKPLACE_COUNTRY1("Texto34"),
	WORKPLACE_MUNICIPALITY1("Texto38"),
	EMPLOYEE_COUNTRY1("Texto51"),
	EMPLOYEE_ADDRESS_MUNICIPALITY1("Texto55"),
	EMPLOYEE_ADDRESS_COUNTRY1("Texto61"),
	;
	
	private String value;
	private boolean overridable;
	private boolean check;
	
	private PracticeCommonField(String value, boolean... values) {
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
	