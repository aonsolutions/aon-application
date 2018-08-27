package com.code.aon.registry.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum RegistryAttachmentType implements IResourceable {

	LOGO,
	ADDITIONAL_IMAGE,
	BANNER,
	CORPORATE_IDENTITY,
	DIGITAL_CERTIFICATE,
	DOCUMENT,
	FISCAL_TEMPLATES,
	FISCAL_REPORTS,
	SIGNATURE,
	INVOICE_FOOTER_TEXT,
	MARKETING_TEMPLATE,
	ENTERPRISE_CONTRACT_CLAUSES,
	DOMAIN_BOOK_HISTORY,
	DOMAIN_INSERT_HISTORY,
	DOMAIN_REMOVE_HISTORY,
	AON_TEMPLATES,
	POS_INVOICE_FOOTER_TEXT,
	D2_DEPOSIT,
	ECOMMERCE_PRODUCT_TEMPLATES,
	REPORT_BACKGROUND,
	CRETA_RESPUESTA,
	CRETA_TRABAJADORES_Y_TRAMOS,
	SYSTEM_MESSAGE,
	CRETA_BASES;
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_registry_attachment_type_";

    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}