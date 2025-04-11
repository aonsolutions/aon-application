package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public enum AgreementIntegrityFix implements Serializable {
	
	OTHER_DOMAIN_AGREEMENT_PAYMENTS("Devengos de convenio en otro dominio", "\u00bfDesea arreglar devengos de convenio en otro dominio\u003f"),
	AGREEMENT_PAYMENTS_WITHOUT_PAYMENT_CONCEPT("Devengos de convenio sin concepto", "\u00bfDesea arreglar devengos de convenio sin concepto\u003f"),
	AGREEMENT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT("Conceptos de convenio en otro dominio", "\u00bfDesea arreglar conceptos de convenio en otro dominio\u003f"),
	NO_CODE_WRONG_CODE_PAYMENT_CONCEPT("Conceptos sin c\u00f3digo / C\u00f3digo err\u00f3neo", "\u00bfDesea arreglar conceptos sin c\u00f3digo / c\u00f3digo err\u00f3neo\u003f"),
	PAYMENT_CONCEPT_CODE_AS_VAR_IN_EXPRESSION("C\u00f3digo concepto usado en expresiones", "\u00bfDesea arreglar c\u00f3digo concepto usado en expresiones\u003f"),
	AGREEMENT_VARIABLES_AS_PAYMENT_CONCEPT_CODE("Variables no usadas en expresi\u00f3n iguales a c\u00f3digos conceptos", "\u00bfDesea arreglar variables no usadas en expres\u00f3n iguales a c\u00f3digos conceptos\u003f"),
	AGREEMENT_VARIABLES_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE("C\u00f3digo concepto / Variables iguales a variables de contexto", "\u00bfDesea arreglar c\u00f3digo concepto / variables iguales a variables de contexto\u003f"),
	CONTRACT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT("Devengos contrato con conceptos en otro dominio", "\u00bfDesea arreglar devengos contrato con conceptos en otro dominio\u003f"),
	CONTRACT_PAYMENTS_PAYMENT_CONCEPTS_WITHOUT_CODE("Devengos contrato con conceptos sin c\u00f3digo", "\u00bfDesea arreglar devengos contrato con conceptos sin c\u00f3digo\u003f"),
	PAYMENT_CONCEPT_NO_REFERENCE("Conceptos del dominio sin referencia al convenio o contrato", "\u00bfDesea arreglar conceptos del dominio sin referencia al convenio o contrato\u003f"),
	AGREEMENT_EXTRA_WRONG_FORMAT_PERIOD("Extras con formato err\u00f3neo en las fechas / Fechas > 12 meses", "\u00bfDesea arreglar extras con formato err\u00f3neo en las fechas / fechas > 12 meses\u003f")
	;
	
	private String title;
	private String deleteMessage;
	
	private AgreementIntegrityFix(String title,String deleteMessage ) {
		this.title = title;
		this.deleteMessage = deleteMessage;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDeleteMessage() {
		return this.deleteMessage;
	}
	
}
