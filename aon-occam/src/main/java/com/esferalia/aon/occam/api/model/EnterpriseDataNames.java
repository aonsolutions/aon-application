package com.esferalia.aon.occam.api.model;

import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum EnterpriseDataNames {


	// INVOICE COMMUNICATION CONFIGURATION
	ICC_ADMINISTRATION,
	
	ICC_LROE ("TicketBai/LROE" ),
	ICC_SIF ("SIF", "Sistema Inform\u00E1tico de Facturaci\u00F3n"),
	ICC_NO_SIF ("No SIF", "Sin sistema inform\u00E1tico de facturaci\u00F3n"),
	ICC_SII ("SII", "Suministro Inmediato de Informaci\u00F3n"),
	ICC_TBAI ("TicketBai" ),
	ICC_VERIFACTU ("Verifactu" ),
	ICC_NO_VERIFACTU ("No Verifactu" ),
	ICC_SERES,
	ICC_EMAIL,
	ICC_FACTURAE, 
	
	// INVOFOX COUNTERS
	INVOFOX, 
	
	// PAYROLL
	agreement, 
	PAY_authorization_key_PAY, 
	PAY_REPORT_salary_PAY, 
	PAY_REPORT_enterpriseSalary_PAY, 
	PAY_salarySendingMethod_PAY, 
	PAY_salarySending_email_PAY, 
	PAY_ss_pension_plan_mutual_PAY
	;

	private String label;
	private String description;
	
	private EnterpriseDataNames() {
		this("", "");
	}
	private EnterpriseDataNames(String label) {
		this(label, "");
	}
	private EnterpriseDataNames(String label, String description) {
		this.label = label;
		this.description = description;
	}
	public String getLabel() {
		return label;
	}
	public String getDescription() {
		return description;
	}

	public static Optional<EnterpriseDataNames> safeValueOf(String name) {
		if (name == null) return Optional.empty();
		return AonCollectionUtils.stream(EnterpriseDataNames.values())
			.filter(e -> AonStringUtils.equalsIgnoreCase( e.name() , name))
			.findFirst();
	}
	
	public static EnterpriseDataNames[] getInvoiceCommunicationTypesNames() {
		return new EnterpriseDataNames[] {
			ICC_LROE,
			ICC_SIF,
			ICC_NO_SIF,
			ICC_SII,
			ICC_TBAI,
			ICC_VERIFACTU,
			ICC_NO_VERIFACTU,
		};
	}
}
