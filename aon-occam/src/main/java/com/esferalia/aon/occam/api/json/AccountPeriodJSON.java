package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountPeriodFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountPeriodToJSON;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.util.AonNumberUtils;

public enum AccountPeriodJSON {

	ID(
		(account, json) -> account.setId(AonNumberUtils.toInteger(  json.optNumber(IJsonNames.ID, null) )),
		(account, json) -> json.put(IJsonNames.ID, account.getId())
	),
	DOMAIN(
		(account, json) -> account.setDomain(AonNumberUtils.toInteger(  json.optNumber(IJsonNames.DOMAIN, null) )),
		(account, json) -> json.put(IJsonNames.DOMAIN, account.getDomain())
	),
	NAME(
		(account, json) -> account.setName(json.optString(IJsonNames.NAME,null)),
		(account, json) -> json.put(IJsonNames.NAME, account.getName())
	),
	INITIATION_DATE(
		(params, json) -> params.setInitiationDate(JsonUtils.getDate(json, IJsonNames.INITIATION_DATE)),
		(params, json) -> JsonUtils.putDate(json, IJsonNames.INITIATION_DATE, params.getInitiationDate())
	),
	DEADLINE(
		(params, json) -> params.setDeadline(JsonUtils.getDate(json, IJsonNames.DEADLINE)),
		(params, json) -> JsonUtils.putDate(json, IJsonNames.DEADLINE, params.getDeadline())
	),
	STATUS(
		(params, json) -> params.setStatus( AccountPeriodStatus.safeValueOf( JsonUtils.getInteger(json,IJsonNames.STATUS) )),
		(params, json) -> JsonUtils.putEnum(json, IJsonNames.STATUS, params.getStatus())
	),
	;
//	private AccountPeriodStatus status;

	private IAonAccountPeriodFromJSON fromJSON;
	private IAonAccountPeriodToJSON toJSON;

	private AccountPeriodJSON(IAonAccountPeriodFromJSON fromJSON, IAonAccountPeriodToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(AccountPeriod period) {
		if (period != null) {
			JSONObject json = new JSONObject();
			for (AccountPeriodJSON p : AccountPeriodJSON.values()) {
				p.toJSON.to(period, json);
			}
			return json;
		}
		return null;
	}
	
	public static AccountPeriod fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static AccountPeriod fromJSON(JSONObject json) {
		if (json != null) {
			AccountPeriod period = new AccountPeriod();
			for (AccountPeriodJSON p : AccountPeriodJSON.values()) {
				p.fromJSON.from(period, json);
			}
			return period;
		}
		return null;
	}

}
