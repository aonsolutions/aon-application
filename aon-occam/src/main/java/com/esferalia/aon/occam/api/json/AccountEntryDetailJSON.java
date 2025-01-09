package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountEntryDetailJSON {
	
	private AccountEntryDetailJSON() {
	
	}
	
	public static List<AccountEntryDetail> fromJSON(JSONArray json) {
		LinkedList<AccountEntryDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static AccountEntryDetail fromJSON(JSONObject json) {		
		return new AccountEntryDetail()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setAccount(JsonUtils.getInteger(json, IJsonNames.ACCOUNT))
				.setAccountCode(JsonUtils.getString(json, IJsonNames.ACCOUNT_CODE))
				.setAccountDescription(JsonUtils.getString(json, IJsonNames.ACCOUNT_DESCRIPTION))
				.setLine(JsonUtils.getInteger(json, IJsonNames.LINE))
				.setConcept(JsonUtils.getString(json, IJsonNames.CONCEPT))
				.setDebit(JsonUtils.getdouble(json, IJsonNames.DEBIT))
				.setCredit(JsonUtils.getdouble(json, IJsonNames.CREDIT))
				.setBalancingAccount(JsonUtils.getInteger(json, IJsonNames.BALANCING_ACCOUNT))
				.setBalancingAccountCode(JsonUtils.getString(json, IJsonNames.BALANCING_ACCOUNT_CODE))
				.setBalancingAccountDescription(JsonUtils.getString(json, IJsonNames.BALANCING_ACCOUNT_DESCRIPTION))
				.setDocumentNumber(JsonUtils.getString(json, IJsonNames.DOCUMENT_NUMBER))
				
				.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
				;
	}
	
	public static JSONArray toJSON(List<AccountEntryDetail> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<AccountEntryDetail> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(AccountEntryDetail object) {
		return new JSONObject()
				.put(IJsonNames.ID, object.getId())
				.put(IJsonNames.DOMAIN, object.getDomain())
				.put(IJsonNames.ACCOUNT, object.getAccount())
				.put(IJsonNames.ACCOUNT_CODE, object.getAccountCode())
				.put(IJsonNames.ACCOUNT_DESCRIPTION, object.getAccountDescription())
				.put(IJsonNames.LINE, object.getLine())
				.put(IJsonNames.CONCEPT, object.getConcept())
				.put(IJsonNames.DEBIT, object.getDebit())
				.put(IJsonNames.CREDIT, object.getCredit())
				.put(IJsonNames.BALANCING_ACCOUNT, object.getBalancingAccount())
				.put(IJsonNames.BALANCING_ACCOUNT_CODE, object.getBalancingAccountCode())
				.put(IJsonNames.BALANCING_ACCOUNT_DESCRIPTION, object.getBalancingAccountDescription())
				
				.put(IJsonNames.DIRTY, object.isDirty())
				;
	}
}
