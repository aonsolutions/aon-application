package com.esferalia.aon.occam.api.json;

import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class AccountJSON {
	
	private AccountJSON() {
		
	}
	/**
	 * @deprecated Use Optional<JSONObject> to(Optional<Account> account)
	 */
	@Deprecated
	public static JSONObject toJSON(Account account) {
		if (account == null) return null;
		return to( account ).orElse( null );
	}
	
	/**
	 * @deprecated Will be deleted
	 */
	@Deprecated
	public static Account fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json); 
	}
	
	/**
	 * @deprecated Use Optional<JSONObject> from(Optional<Account> account)
	 */
	@Deprecated
	public static Account fromJSON(JSONObject json) {
		if(json == null) return null;
		return from( json ).orElse( new Account() ) ;
	}

	public static Optional<Account> from(JSONObject json) {
		return from(json, Account::new );
	}
	public static Optional<Account> from(JSONObject json, Supplier<Account> account) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( 
			account.get()
				.setId(JsonUtils.getInteger( json, IJsonNames.ID ))
				.setDomain(JsonUtils.getInteger( json, IJsonNames.DOMAIN ))
				.setCode(JsonUtils.getString(json,IJsonNames.CODE))
				.setDescription(JsonUtils.getString(json,IJsonNames.DESCRIPTION))
				.setAlias(JsonUtils.getString(json,IJsonNames.ALIAS))
				.setEntryEnabled(JsonUtils.getboolean(json, IJsonNames.ENTRY_ENABLED))
				.setLevel(JsonUtils.getbyte(json,IJsonNames.LEVEL))
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
				.setCostCenter(JsonUtils.getString(json,IJsonNames.COST_CENTER))
		);
	}
	
	public static Optional<JSONObject> to(Optional<Account> account) {
		return account.flatMap( a -> to( a) );
	}
	public static Optional<JSONObject> to(Account account) {
		if (account == null) return Optional.empty();
		return Optional.of(
			new JSONObject()
				.put(IJsonNames.ID, account.getId())
				.put(IJsonNames.DOMAIN, account.getDomain())
				.put(IJsonNames.CODE, account.getCode())
				.put(IJsonNames.DESCRIPTION, account.getDescription())
				.put(IJsonNames.ALIAS, account.getAlias())
				.put(IJsonNames.ENTRY_ENABLED, account.isEntryEnabled())
				.put(IJsonNames.LEVEL, account.getLevel())
				.put(IJsonNames.ACTIVE, account.isActive())
				.put(IJsonNames.COST_CENTER, account.getCostCenter())
		);
	}

}


	
	
	
