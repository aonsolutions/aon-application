package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SepeBatchAttachmentType implements IResourceable {
	
	GENERATED_FILE,
	
	COMMUNICATION_ID,
	
	RESPONSE_FILE,
	
	;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_sepe_batch_attachment_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	
}
