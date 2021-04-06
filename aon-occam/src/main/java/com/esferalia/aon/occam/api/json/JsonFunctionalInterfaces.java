package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Domain;

public class JsonFunctionalInterfaces {

	// ------------------------------------------ [DOMAIN]
	@FunctionalInterface
	public static interface IAonDomainFromJSON {
		Domain from(Domain t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonDomainToJSON {
		JSONObject to(Domain t, JSONObject json);
	}
	// ------------------------------------------ [ACCOUNT]
	@FunctionalInterface
	public static interface IAonAccountFromJSON {
		Account from(Account t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAccountToJSON {
		JSONObject to(Account t, JSONObject json);
	}
	// ---------------------------------- [ACCOUNT PERIOD]
	@FunctionalInterface
	public static interface IAonAccountPeriodFromJSON {
		AccountPeriod from(AccountPeriod t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAccountPeriodToJSON {
		JSONObject to(AccountPeriod t, JSONObject json);
	}
	// ------------------------- [ACCOUNTING_REPORT_PARAMS]
	@FunctionalInterface
	public static interface IAonAccountingReportParamsFromJSON {
		AccountingReportParams from(AccountingReportParams t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAccountingReportParamsToJSON {
		JSONObject to(AccountingReportParams t, JSONObject json);
	}
	// ------------------------- [ACCOUNT TRIAL BALANCE]
	@FunctionalInterface
	public static interface IAonAccountTrialBalanceFromJSON {
		AccountTrialBalance from(AccountTrialBalance t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAccountTrialBalanceToJSON {
		JSONObject to(AccountTrialBalance t, JSONObject json);
	}
	// ------------------------- [ACCOUNT TRIAL BALANCE REPORT]
	@FunctionalInterface
	public static interface IAonAccountTrialBalanceReportFromJSON {
		AccountTrialBalanceReport from(AccountTrialBalanceReport t, JSONObject json);
	}

	@FunctionalInterface
	public static interface IAonAccountTrialBalanceReportToJSON {
		JSONObject to(AccountTrialBalanceReport t, JSONObject json);
	}
	
	
}
