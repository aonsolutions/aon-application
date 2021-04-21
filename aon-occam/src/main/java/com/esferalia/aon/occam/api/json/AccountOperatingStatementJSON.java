package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountOperatingStatementFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountOperatingStatementToJSON;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;

public enum AccountOperatingStatementJSON {
	
	ACCOUNT(
		(params, json) -> params.setAccount(AccountOperatingAccountJSON.fromJSON( (JSONObject) json.opt(IJsonNames.ACCOUNT))),
		(params, json) -> json.put(IJsonNames.ACCOUNT, AccountOperatingAccountJSON.toJSON(params.getAccount()))
	),
	MONTH(
		(params, json) -> params.setMonth(JsonUtils.getString(json, IJsonNames.MONTH)),
		(params, json) -> json.put(IJsonNames.MONTH, params.getMonth())
	),
	DEBIT(
		(params, json) -> params.setDebit(JsonUtils.getdouble(json, IJsonNames.DEBIT)),
		(params, json) -> json.put(IJsonNames.DEBIT, params.getDebit())
	),
	CREDIT(
		(params, json) -> params.setCredit(JsonUtils.getdouble(json, IJsonNames.CREDIT)),
		(params, json) -> json.put(IJsonNames.CREDIT, params.getCredit())
	),
	SALES_RATIO(
		(params, json) -> params.setSalesRatio(JsonUtils.getdouble(json, IJsonNames.SALES_RATIO)),
		(params, json) -> json.put(IJsonNames.SALES_RATIO, params.getSalesRatio())
	),
	PURCHASES_RATIO(
		(params, json) -> params.setPurchasesRatio(JsonUtils.getdouble(json, IJsonNames.PURCHASES_RATIO)),
		(params, json) -> json.put(IJsonNames.PURCHASES_RATIO, params.getPurchasesRatio())
	),
	EXPENSES_RATIO(
		(params, json) -> params.setExpensesRatio(JsonUtils.getdouble(json, IJsonNames.EXPENSES_RATIO)),
		(params, json) -> json.put(IJsonNames.EXPENSES_RATIO, params.getExpensesRatio())
	),
	INCREASE_PERCENT(
		(params, json) -> params.setIncreasePercent(JsonUtils.getdouble(json, IJsonNames.INCREASE_PERCENT)),
		(params, json) -> json.put(IJsonNames.INCREASE_PERCENT, params.getIncreasePercent())
	);
	
	private IAonAccountOperatingStatementFromJSON fromJSON;
	private IAonAccountOperatingStatementToJSON toJSON;
	
	private AccountOperatingStatementJSON (IAonAccountOperatingStatementFromJSON fromJSON, IAonAccountOperatingStatementToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(AccountOperatingStatement statement) {
		if (statement != null) {
			JSONObject json = new JSONObject();
			for (AccountOperatingStatementJSON p : AccountOperatingStatementJSON.values()) {
				p.toJSON.to(statement, json);
			}
			return json;
		}
		return null;
	}
	
	public static AccountOperatingStatement fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static AccountOperatingStatement fromJSON(JSONObject json) {
		if (json != null) {
			AccountOperatingStatement account = new AccountOperatingStatement();
			for (AccountOperatingStatementJSON p : AccountOperatingStatementJSON.values()) {
				p.fromJSON.from(account, json);
			}
			return account;
		}
		return null;
	}
}
