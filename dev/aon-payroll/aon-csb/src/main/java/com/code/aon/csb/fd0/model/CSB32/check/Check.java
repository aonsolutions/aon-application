package com.code.aon.csb.fd0.model.CSB32.check;

import java.util.ResourceBundle;

public class Check {

    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.csb.fd0.model.CSB32.i18n.messages";

    public static String getMessage(String msg) {
	    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
		return bundle.getString(msg);
    }
}
