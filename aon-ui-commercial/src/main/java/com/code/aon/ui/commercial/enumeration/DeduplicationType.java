package com.code.aon.ui.commercial.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

public enum DeduplicationType implements IResourceable {

	NAME(ICommonMessages.COMPANY_NAME),
	
	DOCUMENT(ICommonMessages.DOCUMENT),
	
	TELEPHONE(ICommonMessages.TELEPHONE2),
	
	EMAIL(ICommonMessages.EMAIL);	
	
	private String messageKey;
	
	private DeduplicationType(String messageKey) {
		this.messageKey = messageKey;
	}	
	
    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
    	ResourceBundle bundle = ResourceBundle.getBundle(ICommonMessages.BUNDLE_RESOURCE, locale);
    	return bundle.getString(messageKey);
    }
 
    public String getName() {
    	return AonUtil.getMessage(messageKey);
    }
    
}
