package com.code.aon.csb.fd0.model.CSB19.check;

import java.util.ResourceBundle;

public class Check {

    /**
     * Message file base path.
     */
    private static final String BASE_NAME = "com.code.aon.csb.fd0.model.CSB19.i18n.messages";

   	//java.util.Locale.setDefault( new java.util.Locale((String)entornoDataSet.getAttribute("idioma"),(String)entornoDataSet.getAttribute("pais")) );
    
    public static String getMessage(String msg) {
	    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
		return bundle.getString(msg);
    }
}
