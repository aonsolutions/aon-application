package net.aonsolutions.aon.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonAccountFromJSON;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonAccountToJSON;

public enum AonAccountJSON {

	ID(
		(account, json) -> account.setId(json.optInt(IConstants.ID)),
		(account, json) -> json.put(IConstants.ID, account.getId())
	),
	CODE(
		(account, json) -> account.setCode(json.optString(IConstants.CODE)),
		(account, json) -> json.put(IConstants.CODE, account.getCode())
	),
	DESCRIPTION(
		(account, json) -> account.setDescription(json.optString(IConstants.DESCRIPTION)),
		(account, json) -> json.put(IConstants.DESCRIPTION, account.getDescription())
	);

	private IAonAccountFromJSON fromJSON;
	private IAonAccountToJSON toJSON;

	private AonAccountJSON(IAonAccountFromJSON fromJSON, IAonAccountToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	public static JSONObject toJSON(Account t) {
		JSONObject json = new JSONObject();
		for (AonAccountJSON p : AonAccountJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static Account fromJSON(JSONObject json) {
		Account account = new Account();
		if (json != null) {
			for (AonAccountJSON p : AonAccountJSON.values()) {
				p.fromJSON.from(account, json);
			}
		}
		return account;
	}

}
