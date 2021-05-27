package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountTrialBalanceFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountTrialBalanceToJSON;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;

public enum AccountTrialBalanceJSON {

	ID(
		(params, json) -> params.setId( JsonUtils.getInteger(json,IJsonNames.ID)),
		(params, json) -> json.put(IJsonNames.ID, params.getId())
	),
	CODE(
		(params, json) -> params.setCode( JsonUtils.getString(json,IJsonNames.CODE)),
		(params, json) -> json.put(IJsonNames.CODE, params.getCode())
	),
	DESCRIPTION(
		(params, json) -> params.setDescription( JsonUtils.getString(json,IJsonNames.DESCRIPTION)),
		(params, json) -> json.put(IJsonNames.DESCRIPTION, params.getDescription())
	),
	BEFORE_PERIOD_DEBIT(
		(params, json) -> params.setBeforePeriodDebit( json.optDouble(IJsonNames.BEFORE_PERIOD_DEBIT)),
		(params, json) -> json.put(IJsonNames.BEFORE_PERIOD_DEBIT, params.getBeforePeriodDebit())
	),
	BEFORE_PERIOD_CREDIT(
		(params, json) -> params.setBeforePeriodCredit( json.optDouble(IJsonNames.BEFORE_PERIOD_CREDIT)),
		(params, json) -> json.put(IJsonNames.BEFORE_PERIOD_CREDIT, params.getBeforePeriodCredit())
	),
	IN_PERIOD_OPENING_DEBIT(
		(params, json) -> params.setInPeriodOpeningDebit( json.optDouble(IJsonNames.IN_PERIOD_OPENING_DEBIT)),
		(params, json) -> json.put(IJsonNames.IN_PERIOD_OPENING_DEBIT, params.getInPeriodOpeningDebit())
	),
	IN_PERIOD_OPENING_CREDIT(
		(params, json) -> params.setInPeriodOpeningCredit( json.optDouble(IJsonNames.IN_PERIOD_OPENING_CREDIT)),
		(params, json) -> json.put(IJsonNames.IN_PERIOD_OPENING_CREDIT, params.getInPeriodOpeningCredit())
	),
	IN_PERIOD_BEFORE_DEBIT(
		(params, json) -> params.setInPeriodBeforeDebit( json.optDouble(IJsonNames.IN_PERIOD_BEFORE_DEBIT)),
		(params, json) -> json.put(IJsonNames.IN_PERIOD_BEFORE_DEBIT, params.getInPeriodBeforeDebit())
	),
	IN_PERIOD_BEFORE_CREDIT(
		(params, json) -> params.setInPeriodBeforeCredit( json.optDouble(IJsonNames.IN_PERIOD_BEFORE_CREDIT)),
		(params, json) -> json.put(IJsonNames.IN_PERIOD_BEFORE_CREDIT, params.getInPeriodBeforeCredit())
	),
	IN_PERIOD_DEBIT(
		(params, json) -> params.setInPeriodDebit( json.optDouble(IJsonNames.IN_PERIOD_DEBIT)),
		(params, json) -> json.put(IJsonNames.IN_PERIOD_DEBIT, params.getInPeriodDebit())
	),
	IN_PERIOD_CREDIT(
		(params, json) -> params.setInPeriodCredit( json.optDouble(IJsonNames.IN_PERIOD_CREDIT)),
		(params, json) -> json.put(IJsonNames.IN_PERIOD_CREDIT, params.getInPeriodCredit())
	),
	;

	private IAonAccountTrialBalanceFromJSON fromJSON;
	private IAonAccountTrialBalanceToJSON toJSON;

	private AccountTrialBalanceJSON(IAonAccountTrialBalanceFromJSON fromJSON, IAonAccountTrialBalanceToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(AccountTrialBalance balance) {
		JSONObject json = new JSONObject();
		for (AccountTrialBalanceJSON p : AccountTrialBalanceJSON.values()) {
			p.toJSON.to(balance, json);
		}
		return json;
	}
	
	public static AccountTrialBalance fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static AccountTrialBalance fromJSON(JSONObject json) {
		AccountTrialBalance balance = new AccountTrialBalance();
		if (json != null) {
			for (AccountTrialBalanceJSON p : AccountTrialBalanceJSON.values()) {
				p.fromJSON.from(balance, json);
			}
		}
		return balance;
	}

}
