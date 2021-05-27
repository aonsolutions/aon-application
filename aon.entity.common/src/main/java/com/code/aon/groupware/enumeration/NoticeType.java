package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum NoticeType implements IResourceable {

	CALL,
	VISIT,
	MESSAGE,
    COMMUNICATION,
    ISSUE,
    TICKET,
    AVISO, //warning = aviso
    NOTA,
    COMENTARIO
    ;

    private static final String MSG_KEY_PREFIX = "aon_enum_notice_type_";
	
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}