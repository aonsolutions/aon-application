package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractAttachmentType implements IResourceable {
	
	CONTRACT_DOC_DRAFT,
	CONTRACT_DOC,
	
	BASIC_COPY_DRAFT,
	BASIC_COPY,
	
	SEPE_CONTRACT_FILE,
	SEPE_CONTRACT_COMMUNICATION_ID,
	SEPE_CONTRACT_RESPONSE,
	
	TRAINING_CENTER_DIRECT_DEBIT,
	
	TRAINING_ANNEX_I,
	TRAINING_ANNEX_II,

	EXTENSION_DOC_DRAFT,
	EXTENSION_DOC,
	
	CONTRACT_CLAUSES,
	
	SEPE_EXTENSION_FILE,
	SEPE_EXTENSION_COMMUNICATION_ID,
	SEPE_EXTENSION_RESPONSE,

	SEPE_CERTIFICADOS_FILE,
	SEPE_CERTIFICADOS_COMMUNICATION_ID,
	SEPE_CERTIFICADOS_RESPONSE,
	
	SEPE_TRANSFORM_FILE,
	
	;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_attachment_type_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
	
}
