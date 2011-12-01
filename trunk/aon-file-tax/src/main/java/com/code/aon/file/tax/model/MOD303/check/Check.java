package com.code.aon.file.tax.model.MOD303.check;

import java.util.ResourceBundle;

public class Check {

    private static final String BASE_NAME = "com.code.aon.file.tax.model.MOD303.i18n.messages";

    public static String getMessage(String msg) {
	    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
		return bundle.getString(msg);
    }
}
