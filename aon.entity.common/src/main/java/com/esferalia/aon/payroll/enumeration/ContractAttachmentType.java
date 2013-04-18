package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractAttachmentType implements IResourceable {
	
	CONTRACT_DOCUMENT_DRAFT,
	
	CONTRACT_DOCUMENT,
	
	BASIC_COPY_DRAFT,
	
	BASIC_COPY,
	
	SPEE_CONTRATA_FILE,
	
	SPEE_CONTRATA_RESPONSE,
	
	SPEE_CONTRATA_STATUS,
	
	TRAINING_CENTER_DIRECT_DEBIT,
	
	TRAINING_ANNEX_I,

	TRAINING_ANNEX_II
	
	;
	
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_attachment_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	
}
