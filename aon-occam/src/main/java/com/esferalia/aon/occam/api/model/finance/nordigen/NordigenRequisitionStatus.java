package com.esferalia.aon.occam.api.model.finance.nordigen;


import java.util.Arrays;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum NordigenRequisitionStatus {
	CREATED("CR", "CREATED", "Requisition has been successfully created"),
	LINKED("LN", "LINKED", "Account has been successfully linked to requisition"),
	EXPIRED("EX", "EXPIRED", "Access to account has expired as set in End User Agreement"),
	REJECTED("RJ", "REJECTED", "SSN verification has failed"),
	UNDERGOING_AUTHENTICATION("UA", "UNDERGOING_AUTHENTICATION", "End-user is redirected to the financial institution for authentication"),
	GRANTING_ACCESS("GA", "GRANTING_ACCESS", "End-user is granting access to their account information"),
	SELECTING_ACCOUNTS("SA", "SELECTING_ACCOUNTS", "End-user is selecting accounts"),
	GIVING_CONSENT("GC", "GIVING_CONSENT", "End-user is giving consent at Nordigen's consent screen")
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
		if(AonStringUtils.isBlank(shortName)) return null;
		return Arrays.stream(values())
			.filter(dt -> shortName.equalsIgnoreCase(dt.getShortName()))
			.findFirst()
			.orElse(null);
	}
}
