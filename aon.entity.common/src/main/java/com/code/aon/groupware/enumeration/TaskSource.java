package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TaskSource implements IResourceable {

	MANUAL,
	ASSIGNED,
	PROCESS,
	CAU;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_task_source_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
