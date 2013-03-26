package com.esferalia.aon.gwt.payroll.shared;


public class Deduction extends Item<Deduction.Type> {

	public static enum Type {
		COMMON_CONTINGENCY, PROFESSIONAL_CONTINGENCY, UNEMPLOYMENT, JOB_TRAINING, STRUCTURAL_OVERTIME, NON_STRUCTURAL_OVERTIME, IRPF, ADVANCE_PAYMENT, IN_KIND, OTHER, FOGASA // TODO:
		;

	}


}