package com.esferalia.aon.occam.api.model.finance.nordigen;

public enum NordigenRequisitionStatus {
	CR("CR", "CREATED", "Requisition has been successfully created"),
	LN("LN", "LINKED", "Account has been successfully linked to requisition"),
	EX("EX", "EXPIRED", "Access to account has expired as set in End User Agreement"),
	RJ("RJ", "REJECTED", "SSN verification has failed"),
	UA("UA", "UNDERGOING_AUTHENTICATION", "End-user is redirected to the financial institution for authentication"),
	GA("GA", "GRANTING_ACCESS", "End-user is granting access to their account information"),
	SA("SA", "SELECTING_ACCOUNTS", "End-user is selecting accounts"),
	GC("GC", "GIVING_CONSENT", "End-user is giving consent at Nordigen's consent screen")
	;
	
	
	private String shortName;
	private String longName;
	private String description;
	
	private NordigenRequisitionStatus(String shortName, String longName, String description) {
		this.shortName = shortName;
		this.longName = longName;
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLongName() {
		return longName;
	}

	public String getDescription() {
		return description;
	}
	
	public static NordigenRequisitionStatus safeValueOf(String shortName) {
		try {
			return NordigenRequisitionStatus.valueOf(shortName);
		} catch (NullPointerException | IllegalArgumentException e) {
			return null;
		}
	}
}
