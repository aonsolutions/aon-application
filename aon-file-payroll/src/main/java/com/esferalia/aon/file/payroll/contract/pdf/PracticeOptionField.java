package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;


public enum PracticeOptionField implements IContractFieldName {

	/* Contract PAGE 3 */
	MAIN_OPT1_CHECK(""),
	MAIN_OPT2_CHECK(""),
	MAIN_OPT3_CHECK(""),
	MAIN_OPT4_CHECK(""),
	MAIN_OPT5_CHECK(""),
	
//		PRÁCTICAS ( ORDINARIO ). (pag. 4)
	OPT1_OPTION_CHECK(""),		
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMA DE TERRORISMO .(pag.5)
	OPT2_OPTION_CHECK(""),		
//		DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO (pag.6)
	OPT3_OPTION_CHECK(""),		
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pag.7)
	OPT4_OPTION_CHECK(""),		
//		DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO. (pag.8)
	OPT5_OPTION_CHECK(""),		
	
	;
	
	private String value;
	private boolean overridable;
	private boolean check;
	
	private PracticeOptionField(String value, boolean... values) {
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
	
	