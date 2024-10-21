package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Account;

public class AccountJSON {
	
	private AccountJSON() {
		
	}

	public static Account fromJSON(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return null;
		return new Account()
			.setId(JsonUtils.getInteger( json, IJsonNames.ID ))
			.setDomain(JsonUtils.getInteger( json, IJsonNames.DOMAIN ))
			.setCode(JsonUtils.getString(json,IJsonNames.CODE))
			.setDescription(JsonUtils.getString(json,IJsonNames.DESCRIPTION))
			.setAlias(JsonUtils.getString(json,IJsonNames.ALIAS))
			.setEntryEnabled(JsonUtils.getboolean(json, IJsonNames.ENTRY_ENABLED))
			.setLevel(JsonUtils.getbyte(json,IJsonNames.LEVEL))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			.setCostCenter(JsonUtils.getString(json,IJsonNames.COST_CENTER))
			.setSelected(JsonUtils.getboolean(json,IJsonNames.SELECTED))
		;
	}
	
	public static JSONObject toJSON(Account account) {
		if (account == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, account.getId())
			.put(IJsonNames.DOMAIN, account.getDomain())
			.put(IJsonNames.CODE, account.getCode())
			.put(IJsonNames.DESCRIPTION, account.getDescription())
			.put(IJsonNames.ALIAS, account.getAlias())
			.put(IJsonNames.ENTRY_ENABLED, account.isEntryEnabled())
			.put(IJsonNames.LEVEL, account.getLevel())
			.put(IJsonNames.ACTIVE, account.isActive())
			.put(IJsonNames.COST_CENTER, account.getCostCenter())
			.put(IJsonNames.SELECTED, account.isSelected())
		;
	}


}
