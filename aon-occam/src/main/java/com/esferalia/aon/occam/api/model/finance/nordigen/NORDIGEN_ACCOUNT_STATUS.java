package com.esferalia.aon.occam.api.model.finance.nordigen;

public enum NORDIGEN_ACCOUNT_STATUS {
	DISCOVERED("User has successfully authenticated and account is discovered"),
	PROCESSING("Account is being processed by the Institution"),
	ERROR("An error was encountered when processing account"),
	EXPIRED("Access to account has expired as set in End User Agreement"),
	READY("Account has been successfully processed"),
	SUSPENDED("Account has been suspended (more than 10 consecutive failed attempts to access the account)")
	;
	
	private String description;
	
	private NORDIGEN_ACCOUNT_STATUS(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public static NORDIGEN_ACCOUNT_STATUS safeValueOf(String description) {
		try {
			return NORDIGEN_ACCOUNT_STATUS.valueOf(description);
		} catch (NullPointerException | IllegalArgumentException e) {
			return null;
		}
	}
	
}
