package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum NoticeStatus implements IResourceable {

	ATTENDED,
	WILL_CALL,
	CALL;
	
	private static final String BASE_NAME = "com.code.aon.groupware.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_notice_status_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}