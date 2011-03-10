package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DeductionType implements IResourceable {

	
	COMMON_CONTINGENCY(true),
	PROFESSIONAL_CONTINGENCY(true),
	UNEMPLOYMENT(true),
	JOB_TRAINING(true),
	STRUCTURAL_OVERTIME(true),
	NON_STRUCTURAL_OVERTIME(true),
	IRPF(false),
	ADVANCE_PAYMENT(false),
	IN_KIND(false),
	OTHER(false)
	;
	
	
	private boolean ssDeduction = false;
	
	
	private DeductionType(boolean ssDeduction) {
		this.ssDeduction = ssDeduction;
	}
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.salary.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_deduction_type_";
    
    
    
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }



	public boolean isSsDeduction() {
		return ssDeduction;
	}
	
}
