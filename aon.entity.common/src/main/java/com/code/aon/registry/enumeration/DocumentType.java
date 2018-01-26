package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DocumentType implements IResourceable {
	
	NIF,
	CIF,
	NIE,
	PASSPORT,
	WORK_PERMIT,
	COMMUNITY_CARD,
	OTHER,
	NOT_CENSUSED
	;

    private static final String MSG_KEY_PREFIX = "aon_enum_document_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
