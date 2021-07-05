package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class RegistryBankJSON {

	public static LinkedList<RegistryBank> fromJSON(JSONArray json) {
		LinkedList<RegistryBank> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistryBank fromJSON(JSONObject json) {
		return new RegistryBank()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setBankAccount(new BankAccount(JsonUtils.getString(json, IJsonNames.BANK_ACCOUNT)))
				.setBic(JsonUtils.getString(json, IJsonNames.BIC))
//				.setSuffix(JsonUtils.getString(json, IJsonNames.SUFIX))
				.setAlias(JsonUtils.getString(json, IJsonNames.ALIAS))
				.setActive(JsonUtils.getBoolean(json, IJsonNames.ACTIVE))
				.setAccount(JsonUtils.getInteger(json, IJsonNames.ACCOUNT));
	}
	
	public static JSONArray toJSON(LinkedList<RegistryBank> rbanks) {
		return toJSON(rbanks.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryBank> rbanks) {
		JSONArray array = new JSONArray();
		rbanks.forEach(rbank -> array.put(toJSON(rbank)));
		return array;
	}
	
	public static JSONObject toJSON(RegistryBank rbank) {
		return new JSONObject()
				.put(IJsonNames.ID, rbank.getId())
				.put(IJsonNames.DOMAIN, rbank.getDomain())
				.put(IJsonNames.REGISTRY, rbank.getRegistry())
				.put(IJsonNames.BANK_ACCOUNT, rbank.getBankAccount().getIban())
				.put(IJsonNames.BIC, rbank.getBic())
//				.put(IJsonNames.SUFIX, rbank.getSuffix());
				.put(IJsonNames.ALIAS, rbank.getAlias())
				.put(IJsonNames.ACTIVE, rbank.isActive())
				.put(IJsonNames.ACCOUNT, rbank.getAccount());
	}
}
