package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.watson.client.util.AonCollectionUtils;

public class AccountJSON {
	
	private AccountJSON() {
	}
	
	public static List<Account> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(AccountJSON::from)
			.toList();		
	}
	
	public static Account from(JSONObject json) {
		if (json == null) return null; 
		return new Account()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setCode(AonJSONUtils.getString(json, AonNames.CODE))
			.setDescription(AonJSONUtils.getString(json, AonNames.DESCRIPTION))
			.setAlias(AonJSONUtils.getString(json, AonNames.ALIAS))
			.setActive(AonJSONUtils.getBoolean(json, AonNames.ACTIVE))
		;
	}
	
	public static JSONArray to(List<Account> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Account> stream) {
		return stream
			.map(AccountJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Account account) {
		if (account == null) return null;
		return new JSONObject()
			.put(AonNames.ID, account.getId())
			.put(AonNames.DOMAIN, account.getDomain())
			.putOpt(AonNames.CODE, account.getCode())
			.putOpt(AonNames.DESCRIPTION, account.getDescription())
			.putOpt(AonNames.ALIAS, account.getAlias())
			.putOpt(AonNames.ACTIVE, account.isActive())
			;
	}
}
