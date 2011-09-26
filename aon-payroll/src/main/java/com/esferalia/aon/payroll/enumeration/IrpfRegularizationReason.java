package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;

public enum IrpfRegularizationReason implements IResourceable {

	BASE_IRPF_CHANGE,
	MIN_PERSONAL_CHANGE,
	SPOUSAL_SUPPORT_IN,
	FOOD_ANNUITY_IN,
	FAMILY_STATUS_2_3,
	CEUTA_MELILLA_OUT,
	CEUTA_MELILLA_IN,
	CEUTA_MELILLA_OUT_WORK,
	DEDUCT_HOME_LOAN_IN,
	DEDUCT_HOME_LOAN_OUT,
	OTHER;

	@Override
	public String getName(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Integer getCausa() {
		return ordinal() +1;
	}
	
}
