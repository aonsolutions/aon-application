package com.esferalia.aon.occam.api.json;

import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountTrialBalanceReportFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountTrialBalanceReportToJSON;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum AccountTrialBalanceReportJSON {

	PARAMS(
		(params, json) -> params.setParams( AccountingReportParamsJSON.fromJSON( json.optJSONObject(IJsonNames.PARAMS))),
		(params, json) -> json.putOpt(IJsonNames.PARAMS, AccountingReportParamsJSON.toJSON(  params.getParams()))
	),
	HAS_BEFORE_PERIOD_AMOUNTS(
		(params, json) -> params.setHasBeforePeriodAmounts(json.optBoolean(IJsonNames.HAS_BEFORE_PERIOD_AMOUNTS)),
		(params, json) -> json.put(IJsonNames.HAS_BEFORE_PERIOD_AMOUNTS, params.hasBeforePeriodAmounts())
	),
	HAS_OPENING_AMOUNTS(
		(params, json) -> params.setHasOpeningAmounts(json.optBoolean(IJsonNames.HAS_OPENING_AMOUNTS)),
		(params, json) -> json.put(IJsonNames.HAS_OPENING_AMOUNTS, params.hasOpeningAmounts())
	),
	HAS_IN_PERIOD_PREVIOUS_AMOUNTS(
		(params, json) -> params.setHasInPeriodPreviousAmounts(json.optBoolean(IJsonNames.HAS_IN_PERIOD_PREVIOUS_AMOUNTS)),
		(params, json) -> json.put(IJsonNames.HAS_IN_PERIOD_PREVIOUS_AMOUNTS, params.hasInPeriodPreviousAmounts())
	),
	TOTAL_BALANCE(
		(params, json) -> params.setTotalBalance( AccountTrialBalanceJSON.fromJSON( json.optJSONObject(IJsonNames.TOTAL_BALANCE))),
		(params, json) -> json.putOpt(IJsonNames.TOTAL_BALANCE, AccountTrialBalanceJSON.toJSON(  params.getTotalBalance()))
	),
	BALANCES(
		(params, json) -> {
			JSONArray balances = (JSONArray) json.opt(IJsonNames.BALANCES);
			if (balances != null && !balances.isEmpty()) {
				TreeMap<String, AccountTrialBalance> list = new TreeMap<String, AccountTrialBalance>();
				for ( int i = 0; i < balances.length(); i++) {
					JSONObject item = balances.optJSONObject(i);
					String key = JsonUtils.getString(item,IJsonNames.KEY);
					if (AonStringUtils.isNotBlank(key)) {
						AccountTrialBalance bal = AccountTrialBalanceJSON.fromJSON( item.optJSONObject(IJsonNames.BALANCE));
						list.put(key, bal);
					}
				}
				params.setBalances(list);
			}
			return params;
		},
		(params, json) -> {
			if (params.getBalances() != null && !params.getBalances().isEmpty()) {
				JSONArray balances = new JSONArray();
				int i = 0;
				for (String key : params.getBalances().keySet()) {
					JSONObject item = new JSONObject();
					item.put(IJsonNames.KEY, key);
					item.put(IJsonNames.BALANCE, AccountTrialBalanceJSON.toJSON( params.getBalances().get(key)));
					balances.put(i, item);
					i++;
				}
				json.put(IJsonNames.BALANCES, balances);
			}
			return json;
		}
	),
	;
	
	private IAonAccountTrialBalanceReportFromJSON fromJSON;
	private IAonAccountTrialBalanceReportToJSON toJSON;

	private AccountTrialBalanceReportJSON(IAonAccountTrialBalanceReportFromJSON fromJSON, IAonAccountTrialBalanceReportToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(AccountTrialBalanceReport report) {
		JSONObject json = new JSONObject();
		for (AccountTrialBalanceReportJSON p : AccountTrialBalanceReportJSON.values()) {
			p.toJSON.to(report, json);
		}
		return json;
	}
	
	public static AccountTrialBalanceReport fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static AccountTrialBalanceReport fromJSON(JSONObject json) {
		AccountTrialBalanceReport report = new AccountTrialBalanceReport();
		if (json != null) {
			for (AccountTrialBalanceReportJSON p : AccountTrialBalanceReportJSON.values()) {
				p.fromJSON.from(report, json);
			}
		}
		return report;
	}

}
