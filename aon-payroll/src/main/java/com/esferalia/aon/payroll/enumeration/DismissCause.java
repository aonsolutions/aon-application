package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DismissCause implements IResourceable {
	
	
	DC1(33, 24),
	DC2(45, 42),
	DC3(20, 12), 
	DC4(45, 42), 
	DC5(20, 12),
	;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_dismiss_cause_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    private Integer compensationDaysPerYear;
    private Integer maximunMonths;
    
    DismissCause( Integer compensationDaysPerYear, Integer maximunMonths ) {
      	this.compensationDaysPerYear = compensationDaysPerYear;
      	this.maximunMonths = maximunMonths;
  	}
    
    public Integer getCompensationDaysPerYear() {
    	return compensationDaysPerYear;
    }
    
    public Integer getMaximunMonths() {
    	return maximunMonths;
    }
    
}
