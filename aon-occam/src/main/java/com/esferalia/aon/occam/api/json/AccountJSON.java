package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountToJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.watson.util.AonNumberUtils;

public enum AccountJSON {

	ID(
		(account, json) -> account.setId(AonNumberUtils.toInteger(  json.optNumber(IJsonNames.ID, null) )),
		(account, json) -> json.put(IJsonNames.ID, account.getId())
	),
	DOMAIN(
		(account, json) -> account.setDomain(AonNumberUtils.toInteger(  json.optNumber(IJsonNames.DOMAIN, null) )),
		(account, json) -> json.put(IJsonNames.DOMAIN, account.getDomain())
	),
	CODE(
		(account, json) -> account.setCode(json.optString(IJsonNames.CODE,null)),
		(account, json) -> json.put(IJsonNames.CODE, account.getCode())
	),
	DESCRIPTION(
		(account, json) -> account.setDescription(json.optString(IJsonNames.DESCRIPTION,null)),
		(account, json) -> json.put(IJsonNames.DESCRIPTION, account.getDescription())
	),
	ALIAS(
			(account, json) -> account.setAlias(json.optString(IJsonNames.ALIAS,null)),
			(account, json) -> json.put(IJsonNames.ALIAS, account.getAlias())
	),
	ENTRY_ENABLED(
			(account, json) -> account.setEntryEnabled(json.optBoolean(IJsonNames.ENTRY_ENABLED)),
			(account, json) -> json.put(IJsonNames.ENTRY_ENABLED, account.isEntryEnabled())
	),
	LEVEL(
			(account, json) -> account.setLevel((byte) json.optInt(IJsonNames.LEVEL)),
			(account, json) -> json.put(IJsonNames.LEVEL, account.getLevel())
	),
	ACTIVE(
			(account, json) -> account.setActive(json.optBoolean(IJsonNames.ACTIVE)),
			(account, json) -> json.put(IJsonNames.ACTIVE, account.isActive())
	),
	COST_CENTER(
			(account, json) -> account.setCostCenter(json.optString(IJsonNames.COST_CENTER,null)),
			(account, json) -> json.put(IJsonNames.COST_CENTER, account.getCostCenter())
	),
	;

	private IAonAccountFromJSON fromJSON;
	private IAonAccountToJSON toJSON;

	private AccountJSON(IAonAccountFromJSON fromJSON, IAonAccountToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(Account account) {
		if (account != null) {
			JSONObject json = new JSONObject();
			for (AccountJSON p : AccountJSON.values()) {
				p.toJSON.to(account, json);
			}
			return json;
		}
		return null;
	}
	
	public static Account fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static Account fromJSON(JSONObject json) {
		if (json != null) {
			Account account = new Account();
			for (AccountJSON p : AccountJSON.values()) {
				p.fromJSON.from(account, json);
			}
			return account;
		}
		return null;
	}

}
