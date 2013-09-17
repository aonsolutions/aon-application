package com.code.aon.accounting.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum AccountEntryType.
 */
public enum AmortizationPeriod implements IResourceable {
	
        YEARLY(1),
        MONTHLY(12),
        BI_MONTHLY(6),
        QUARTERLY(4),
        FOUR_MONTHLY(3),
        HALF_YEARLY(2);
       
    private static final String MSG_KEY_PREFIX = "aon_enum_amortization_period_";

    private int yearFraction;
   
    private AmortizationPeriod(int yearFraction) {
        this.yearFraction = yearFraction;
    }
   
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
                return bundle.getString(MSG_KEY_PREFIX + toString());
    }

    public double getYearFraction() {
        return yearFraction;
    }
}
