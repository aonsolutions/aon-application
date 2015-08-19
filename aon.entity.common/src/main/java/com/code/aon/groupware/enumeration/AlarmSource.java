package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum AlarmSource implements IResourceable {
	
	NOTICE,
	TASK,
	EXTERNAL,
	COMMERCIAL_TRACKING,
	OFFICE;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_alarm_source_";
	
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}
