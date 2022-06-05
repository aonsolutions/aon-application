package com.esferalia.aon.occam.api.model.attachment;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum RegistryAttachmentType {

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
	CRETA_BASES,
	DOCUMENTAL_ASESOR,
	DOCUMENTAL_EMPLOYEE
	;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getName(){
		return this.toString();
	}
	

	public static RegistryAttachmentType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RegistryAttachmentType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RegistryAttachmentType.values().length) return null;
		return RegistryAttachmentType.values()[i];
	}

	public static RegistryAttachmentType safeValueOf( String str) {
		if(AonStringUtils.isBlank(str)) return null;
		for (RegistryAttachmentType rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getName().equalsIgnoreCase(str))
				return rs;
		}
		return null;
	}

	
	public static Byte[] drive(){
		return new Byte[]{
			ADDITIONAL_IMAGE.value(), BANNER.value(),
			CORPORATE_IDENTITY.value(), DIGITAL_CERTIFICATE.value(),
			DOCUMENT.value(), FISCAL_TEMPLATES.value(),
			FISCAL_REPORTS.value(), SIGNATURE.value(),
			INVOICE_FOOTER_TEXT.value(), MARKETING_TEMPLATE.value(),
			ENTERPRISE_CONTRACT_CLAUSES.value(), POS_INVOICE_FOOTER_TEXT.value(),
			REPORT_BACKGROUND.value(), SYSTEM_MESSAGE.value()
		};
	}
	
}