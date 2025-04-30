package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public enum AgreementIntegrityFix implements Serializable {
	
	OTHER_DOMAIN_AGREEMENT_PAYMENTS("Devengos de convenio en otro dominio", "\u00bfDesea arreglar devengos de convenio en otro dominio\u003f"),
	AGREEMENT_PAYMENTS_WITHOUT_PAYMENT_CONCEPT("Devengos de convenio sin concepto", "\u00bfDesea arreglar devengos de convenio sin concepto\u003f"),
	AGREEMENT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT("Conceptos de convenio en otro dominio", "\u00bfDesea arreglar conceptos de convenio en otro dominio\u003f"),
	NO_CODE_WRONG_CODE_PAYMENT_CONCEPT("Conceptos del convenio sin c\u00f3digo / C\u00f3digo err\u00f3neo", "\u00bfDesea arreglar conceptos del convenio sin c\u00f3digo / c\u00f3digo err\u00f3neo\u003f"),
	PAYMENT_CONCEPT_CODE_AS_VAR_IN_EXPRESSION("C\u00f3digo de concepto usado en expresiones", "\u00bfDesea arreglar c\u00f3digo concepto usado en expresiones\u003f"),
	AGREEMENT_VARIABLES_AS_PAYMENT_CONCEPT_CODE("Variables no usadas en expresi\u00f3n, iguales a c\u00f3digos de conceptos", "\u00bfDesea arreglar variables no usadas en expres\u00f3n iguales a c\u00f3digos conceptos\u003f"),
	AGREEMENT_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE("C\u00f3digo de concepto iguales a variables de contexto", "\u00bfDesea arreglar c\u00f3digo concepto / variables iguales a variables de contexto\u003f"),
	
	AGREEMENT_PAYMENT_WRONG_EXPRESSION("Expresiones con formato err\u00f3neo", "\u00bfDesea arreglar las expresiones con formato err\u00f3neo\u003f"),
	
	AGREEMENT_PAYMENT_DUPLICATE_VARIABLES("Expresiones con variables duplicadas", "\u00bfDesea arreglar las expresiones con variables duplicadas\u003f"),
	PAYMENT_CONCEPT_DUPLICATE("C\u00f3digos de concepto duplicados", "\u00bfDesea arreglar los c\u00f3digos de concepto duplicados\u003f"),
	
	CONTRACT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT("Devengos de contrato con conceptos en otro dominio", "\u00bfDesea arreglar devengos contrato con conceptos en otro dominio\u003f"),
	CONTRACT_PAYMENTS_PAYMENT_CONCEPTS_WITHOUT_CODE("Devengos de contrato con conceptos sin c\u00f3digo", "\u00bfDesea arreglar devengos contrato con conceptos sin c\u00f3digo\u003f"),
	
	PAYMENT_CONCEPT_NO_REFERENCE("Conceptos del dominio sin referencia a convenios o contratos", "\u00bfDesea arreglar conceptos del dominio sin referencia al convenio o contrato\u003f"),
	
	AGREEMENT_EXTRA_WRONG_FORMAT_PERIOD("Extras con formato err\u00f3neo en las fechas / Fechas > 12 meses", "\u00bfDesea arreglar extras con formato err\u00f3neo en las fechas / fechas > 12 meses\u003f"),
	AGREEMENT_EXTRA_START_END("Extras error dia fecha incio o fecha fin", "\u00bfDesea arreglar extras con error en las fechas de incio o fin\u003f"),
	
	AGREEMENT_EXTRA_PAIR_SW("Pareja extras (verano y navidad)", "\u00bfDesea crear pareja de extras (verano y naviad)\u003f"),
	AGREEMENT_EXTRA_SW_SAME_PERIOD("Extras (verano y naviad) no comparten la misma periodicidad", "\u00bfDesea arreglar extras (verano y naviad) que no comparten la misma periocidad\u003f"),
	AGREEMENT_EXTRA_SW_CODE("Extras (verano y naviad) no comparten el mismo c\u00f3digo de concepto", "\u00bfDesea arreglar extras (verano y naviad) que no comparten lel mismo c\u00f3digo de concepto\u003f"),
	
	AGREEMENT_DATA_INHERIT("Inherit en el nivel 0 del convenio", "\u00bfDesea eliminar 'inherit' de las expresiones del nivel 0\u003f"),
	AGREEMENT_LEVEL_DATA_INHERIT("Inherit en niveles retributivos del convenio", "\u00bfDesea eliminar 'inherit' de los niveles retributivos del convenio\u003f"),
	AGREEMENT_PAYMENT_INHERIT("Inherit en devengos del convenio", "\u00bfDesea eliminar 'inherit' de los devengos del convenio\u003f"),
	
	CONTRACT_PAYMENT_INHERIT("Inherit en devengos de contratos asociados al convenio", "\u00bfDesea eliminar 'inherit' de los devengos de los contratos asociados al convenio\u003f"),
	
	AGREEMENT_PAYMENT_PAYMENT_CONCEPT_SAME_EXPR("Devengos con la misma expresi\u00f3n que el concepto asociado", "\u00bfDesea dejar unicamente la expresi\u00f3n del concepto\u003f")
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
