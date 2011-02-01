package com.code.aon.file.bank.model.CSB34.checks;

import java.util.ResourceBundle;

/**
 * Controls the messages of checks
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Check {

    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.file.bank.model.CSB34.i18n.messages";

    
    /**
     * Returns the message for this key
     * 
     * @param msg the key
     * @return the message
     */
    public static String getMessage(String msg) {
	    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
		return bundle.getString(msg);
    }
}
