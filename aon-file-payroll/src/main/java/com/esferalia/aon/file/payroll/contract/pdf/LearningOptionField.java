package com.esferalia.aon.file.payroll.contract.pdf;

import org.apache.commons.lang.ArrayUtils;


public enum LearningOptionField implements IContractFieldName {
	
	/* Contract PAGE 3 */
	MAIN_OPT1_CHECK("Casilla de verificación53"),
	MAIN_OPT2_CHECK("Casilla de verificación54"),
	MAIN_OPT3_CHECK("Casilla de verificación55"),
	MAIN_OPT4_CHECK("Casilla de verificación56"),
	
//		FORMACIÓN Y APRENDIZAJE ( ORDINARIO ). ( pág.4 )
	OPT1_OPTION_CHECK("Casilla de verificación7"),
	OPT1_QUOTE_BONUS("Casilla de verificación8"),
	OPT1_QUOTE_NO_BONUS("Casilla de verificación9"),	
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO . ( pág.5 )
	OPT2_OPTION_CHECK("Casilla de verificación10"),
	// TODO: complete this option
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. ( pág.6 )
	OPT3_OPTION_CHECK("Casilla de verificación57"),
	// TODO: complete this option
//		DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.7 )
	OPT4_OPTION_CHECK("Casilla de verificación64"),
	// TODO: complete this option
	;
	
	private String value;
	private boolean overridable;
	private boolean check;
	
	private LearningOptionField(String value, boolean... values) {
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
	
	