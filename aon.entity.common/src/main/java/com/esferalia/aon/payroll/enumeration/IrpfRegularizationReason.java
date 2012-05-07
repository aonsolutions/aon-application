package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

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

	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_irpf_regularization_reason_";

	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	public Integer getCausa() {
		return ordinal() +1;
	}
	
	public static IrpfRegularizationReason valueof(int causa) {
		IrpfRegularizationReason reasons [] = values();
		return causa > 0 && causa <= reasons.length ?  reasons[causa-1] : null;
	}
	
}
