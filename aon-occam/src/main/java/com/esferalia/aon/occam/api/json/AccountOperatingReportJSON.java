package com.esferalia.aon.occam.api.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DateInterval;

public class AccountOperatingReportJSON {

	public static BiConsumer<AccountOperatingReport, JSONObject> PARAMS_TOJSON = (report, json) -> {
		JSONObject paramsJson = AccountingReportParamsJSON.toJSON(report.getParams());
		json.put(IJsonNames.PARAMS, paramsJson);
	};
	public static BiConsumer<AccountOperatingReport, JSONObject> PARAMS_FROMJSON = (report, json) -> {
		AccountingReportParams params = AccountingReportParamsJSON.fromJSON(json);
		report.setParams(params);
	};
	public static BiConsumer<AccountOperatingReport, JSONObject> ACCOUNTS_TOJSON = (report, json) -> {
		TreeSet<AccountOperatingAccount> accounts = report.getAccounts();
		if (accounts != null && !accounts.isEmpty()) {
			JSONArray accountsArray = new JSONArray();
			int i = 0;
			for (AccountOperatingAccount account : accounts) {
				accountsArray.put(i++, AccountOperatingAccountJSON.toJSON(account));
			}
			json.put(IJsonNames.ACCOUNTS, accountsArray);
		}
	};
	public static BiConsumer<AccountOperatingReport, JSONObject> ACCOUNTS_FROMJSON = (report, json) -> {
		JSONArray accountsArray = json.optJSONArray(IJsonNames.ACCOUNTS);
		if (accountsArray != null && !accountsArray.isEmpty()) {
			TreeSet<AccountOperatingAccount> list = new TreeSet<AccountOperatingAccount>();
			for (int i=0; i< accountsArray.length(); i++) {
				list.add(AccountOperatingAccountJSON.fromJSON(accountsArray.optJSONObject(i)));
			}
			report.setAccounts(list);
		}
	};
	
	
	//All the information related to accounts in conctrete interval
	public static BiConsumer<AccountOperatingReport, JSONObject> INTERVALS_TOJSON = (report, json) -> {
		JSONArray intervalsJson = new JSONArray();
		if (report.getIntervals() != null && !report.getIntervals().isEmpty()) {
			int i = 0;	
			
			TreeSet<AccountOperatingAccount> accounts = report.getAccounts();
			
			for (DateInterval interval : report.getIntervals()) {
				JSONObject intervalJson= new JSONObject();
				intervalJson.put(IJsonNames.INTERVAL, DateIntervalJSON.toJSON(interval));
				
				JSONArray statementArray = new JSONArray();
				int j = 0;
				for (AccountOperatingAccount account : accounts) {
					AccountOperatingStatement statement = report.get(account.getCode(), interval);
					if (statement != null) {
						statementArray.put(j++, AccountOperatingStatementJSON.toJSON(statement));
					}
				}
				intervalJson.put(IJsonNames.STATEMENTS, statementArray);
				intervalsJson.put(i++, intervalJson);
			}
			json.put(IJsonNames.INTERVALS, intervalsJson);
		}
	};
	
	public static BiConsumer<AccountOperatingReport, JSONObject> INTERVALS_FROMJSON = (report, json) -> {
		JSONArray intervals = (JSONArray) json.opt(IJsonNames.INTERVALS);
		if (intervals != null && !intervals.isEmpty()) {
			TreeSet<DateInterval> list = new TreeSet<DateInterval>();
			for (int i=0; i<intervals.length(); i++) {
				JSONObject item = intervals.optJSONObject(i);
				JSONObject intervalJson = item.optJSONObject(IJsonNames.INTERVAL);
				list.add(DateIntervalJSON.fromJSON(intervalJson));
			}
			report.setIntervals(list);
		}
	};
	
	public static BiConsumer<AccountOperatingReport, JSONObject> STATEMENTS_FROMJSON = (report, json) -> {
		Collection<String> excludedCodes = new ArrayList<String>();
		AccountOperatingStatementType[] values = AccountOperatingStatementType.values();
		Arrays.stream(values).forEach( v -> excludedCodes.add(v.name()));
		
		JSONArray intervals = (JSONArray) json.opt(IJsonNames.INTERVALS);
		if (intervals != null && !intervals.isEmpty()) {
			for (int i=0; i<intervals.length(); i++) {
				JSONObject item = intervals.optJSONObject(i);
				JSONObject intervalJson = item.optJSONObject(IJsonNames.INTERVAL);
				JSONArray statementArray = item.optJSONArray(IJsonNames.STATEMENTS);
				if (statementArray != null) {
					for (int j=0; j<statementArray.length(); j++) {
						JSONObject account = null;
						if (statementArray.optJSONObject(j) != null) {
							account = statementArray.getJSONObject(j).optJSONObject(IJsonNames.ACCOUNT);
						}
						String code = null;
						if (account != null)
							code = JsonUtils.getString(account, IJsonNames.CODE);
						
						
						if (!excludedCodes.contains(code))
							report.put(DateIntervalJSON.fromJSON(intervalJson), AccountOperatingStatementJSON.fromJSON(statementArray.getJSONObject(j)));
					}
				}
			}
		}
	};
	

	public static JSONObject toJSON(AccountOperatingReport report) {
		JSONObject json = new JSONObject();
		
		PARAMS_TOJSON
		.andThen(ACCOUNTS_TOJSON)
		.andThen(INTERVALS_TOJSON)
		.accept(report, json);
			
		return json;
	}
	
	public static AccountOperatingReport fromJSON(JSONObject json) {
		AccountOperatingReport report = new AccountOperatingReport();
		PARAMS_FROMJSON
		.andThen(ACCOUNTS_FROMJSON)
		.andThen(INTERVALS_FROMJSON)
		.andThen(STATEMENTS_FROMJSON)
		.accept(report, json);
		
		return report;
	}

	public static AccountOperatingReport fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

}
