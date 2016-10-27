package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum TaskStatus implements IResourceable {

	DELETED,
	PENDING,
	IN_PROGRESS,
	FINISHED,
	FAQ;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_task_status_";
	
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
