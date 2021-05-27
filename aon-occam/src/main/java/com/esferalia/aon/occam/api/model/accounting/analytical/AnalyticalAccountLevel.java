package com.esferalia.aon.occam.api.model.accounting.analytical;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AnalyticalAccountLevel  {
	ACCOUNT_LEVEL_9 (9),
	ACCOUNT_LEVEL_4 (4),
	ACCOUNT_LEVEL_3 (3),
	ACCOUNT_LEVEL_2 (2),
	ACCOUNT_LEVEL_1 (1),
	COST_CENTER		(0)
	;
	private int accountCodeLength;
	
	private AnalyticalAccountLevel(int accountCodeLength ) {
		this.accountCodeLength = accountCodeLength;
	}
	
	public int getAccountCodeLength() {
		return accountCodeLength;
	}
	
	public static AnalyticalAccountLevel safeValueOf( String accountCode) {
		int length = AonStringUtils.length(accountCode);
		for (AnalyticalAccountLevel l : AnalyticalAccountLevel.values()) {
			if (length == l.getAccountCodeLength()) return l;
		}
		return COST_CENTER;
	}
}
