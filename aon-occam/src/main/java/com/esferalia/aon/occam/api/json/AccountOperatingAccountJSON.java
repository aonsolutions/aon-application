package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountOperatingAccountFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonAccountOperatingAccountToJSON;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.watson.util.AonNumberUtils;

public enum AccountOperatingAccountJSON {
	
	TYPE(
		(params, json) -> params.setType(AccountOperatingStatementType.valueOf(JsonUtils.getString(json, IJsonNames.TYPE))),
		(params, json) -> json.put(IJsonNames.TYPE, params.getType())
	),
	ID(
		(params, json) -> params.setId(AonNumberUtils.toInteger(json.optNumber(IJsonNames.ID, null))),
		(params, json) -> json.put(IJsonNames.ID, params.getId())
	),
	CODE(
		(params, json) -> params.setCode(JsonUtils.getString(json, IJsonNames.CODE)),
		(params, json) -> json.put(IJsonNames.CODE, params.getCode())
	),
	DESCRIPTION(
		(params, json) -> params.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION)),
		(params, json) -> json.put(IJsonNames.DESCRIPTION, params.getDescription())
	);
	
	private IAonAccountOperatingAccountFromJSON fromJSON;
	private IAonAccountOperatingAccountToJSON toJSON;
	
	private AccountOperatingAccountJSON (IAonAccountOperatingAccountFromJSON fromJSON, IAonAccountOperatingAccountToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(AccountOperatingAccount account) {
		if (account != null) {
			JSONObject json = new JSONObject();
			for (AccountOperatingAccountJSON p : AccountOperatingAccountJSON.values()) {
				p.toJSON.to(account, json);
			}
			return json;
		}
		return null;
	}
	
	public static AccountOperatingAccount fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}

	public static AccountOperatingAccount fromJSON(JSONObject json) {
		if (json != null) {
			AccountOperatingAccount account = new AccountOperatingAccount();
			for (AccountOperatingAccountJSON p : AccountOperatingAccountJSON.values()) {
				p.fromJSON.from(account, json);
			}
			return account;
		}
		return null;
	}
}
