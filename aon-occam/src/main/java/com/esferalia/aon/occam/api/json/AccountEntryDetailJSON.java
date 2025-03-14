package com.esferalia.aon.occam.api.json;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class AccountEntryDetailJSON {
	
	private AccountEntryDetailJSON() {
	
	}
	
	public static AccountEntryDetail fromJSON(JSONObject json) {
		return fromJSON(json, AccountEntryDetail::new );
	}
	
	public static AccountEntryDetail fromJSON(JSONObject json, Supplier<AccountEntryDetail> supp) {
		if (JsonUtils.isEmpty(json)) return null;
		return supp.get()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setAccountEntry(JsonUtils.getInteger(json, IJsonNames.ACCOUNT_ENTRY_ID))
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
			;
	}
	
	public static Stream<AccountEntryDetail> fromJSON(JSONArray array) {
		if (JsonUtils.isEmpty(array)) return null;
		return JsonUtils.stream(array)
			.map( json -> fromJSON(json)); 
	}

	public static JSONArray toJSON(List<AccountEntryDetail> list) {
		return toJSON( AonCollectionUtils.stream(list));
	}
	
	public static JSONArray toJSON(Stream<AccountEntryDetail> stream) {
		return stream
			.filter( Objects::nonNull)
			.map( aed -> toJSON(aed))
			.collect(Collector.of(JSONArray::new, JSONArray::put, JSONArray::put));
	}
	
	
	public static JSONObject toJSON(AccountEntryDetail aed) {
		if (aed == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, aed.getId())
			.put(IJsonNames.DOMAIN, aed.getDomain())
			.put(IJsonNames.ACCOUNT_ENTRY_ID, aed.getAccountEntry())
			.put(IJsonNames.ACCOUNT, aed.getAccount())
			.put(IJsonNames.ACCOUNT_CODE, aed.getAccountCode())
			.put(IJsonNames.ACCOUNT_DESCRIPTION, aed.getAccountDescription())
			.put(IJsonNames.LINE, aed.getLine())
			.put(IJsonNames.CONCEPT, aed.getConcept())
			.put(IJsonNames.DEBIT, aed.getDebit())
			.put(IJsonNames.CREDIT, aed.getCredit())
			.put(IJsonNames.BALANCING_ACCOUNT, aed.getBalancingAccount())
			.put(IJsonNames.BALANCING_ACCOUNT_CODE, aed.getBalancingAccountCode())
			.put(IJsonNames.BALANCING_ACCOUNT_DESCRIPTION, aed.getBalancingAccountDescription())
			.put(IJsonNames.DOCUMENT_NUMBER, aed.getDocumentNumber())
			;
	}
}
