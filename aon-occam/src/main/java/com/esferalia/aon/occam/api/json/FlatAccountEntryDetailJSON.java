package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class FlatAccountEntryDetailJSON {
	
	private FlatAccountEntryDetailJSON() {
	
	}
	
	public static List<FlatAccountEntryDetail> fromJSON(JSONArray json) {
		LinkedList<FlatAccountEntryDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static FlatAccountEntryDetail fromJSON(JSONObject json) {		
		return new FlatAccountEntryDetail()
			.setEntryId(JsonUtils.getInteger(json, IJsonNames.ENTRY_ID))
			.setEntryDomain(JsonUtils.getInteger(json, IJsonNames.ENTRY_DOMAIN))
			.setEntryPperiod(JsonUtils.getInteger(json, IJsonNames.ENTRY_PERIOD))
			.setEntryPeriodName(JsonUtils.getString(json, IJsonNames.ENTRY_PERIOD_NAME))
			.setEntryDate(JsonUtils.getDate(json, IJsonNames.ENTRY_DATE))
			.setEntryType(AccountEntryType.safeValueOf(JsonUtils.getString(json, IJsonNames.ENTRY_TYPE)))
			.setActivity(JsonUtils.getInteger(json, IJsonNames.ACTIVITY))
			.setActivityName(JsonUtils.getString(json, IJsonNames.ACTIVITY_NAME))
			.setJournal(JsonUtils.getInteger(json, IJsonNames.JOURNAL))
			.setEntrySecurityLevel(SecurityLevel.safeValueOf(JsonUtils.getString(json, IJsonNames.ENTRY_SECURITY_LEVEL)))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setEntryCreationUser(JsonUtils.getString(json, IJsonNames.ENTRY_CREATION_USER))
			.setEntryCreationDate(JsonUtils.getDate(json, IJsonNames.ENTRY_CREATION_DATE))
			.setEntryModificationUser(JsonUtils.getString(json, IJsonNames.ENTRY_CREATION_USER))
			.setEntryModificationDate(JsonUtils.getDate(json, IJsonNames.ENTRY_MODIFICATION_DATE))

			.setDetailId(JsonUtils.getInteger(json, IJsonNames.DETAIL_ID))
			.setLine(JsonUtils.getInteger(json, IJsonNames.LINE))
			.setAccount(JsonUtils.getInteger(json, IJsonNames.ACCOUNT))
			.setAccountCode(JsonUtils.getString(json, IJsonNames.ACCOUNT_CODE))
			.setAccountDescription(JsonUtils.getString(json, IJsonNames.ACCOUNT_DESCRIPTION))
			.setConcept(JsonUtils.getString(json, IJsonNames.CONCEPT))
			.setDebit(JsonUtils.getdouble(json, IJsonNames.DEBIT))
			.setCredit(JsonUtils.getdouble(json, IJsonNames.CREDIT))
			.setDebitBalance(JsonUtils.getdouble(json, IJsonNames.DEBIT_BALANCE))
			.setUnpaidBalance(JsonUtils.getdouble(json, IJsonNames.UNPAID_BALANCE))
			.setInitialDebitBalance(JsonUtils.getdouble(json, IJsonNames.INITIAL_DEBIT_BALANCE))
			.setInitialUnpaidBalance(JsonUtils.getdouble(json, IJsonNames.INITIAL_UNPAID_BALANCE))
			
			.setBalancingAccount(JsonUtils.getInteger(json, IJsonNames.BALANCING_ACCOUNT))
			.setBalancingAccountCode(JsonUtils.getString(json, IJsonNames.BALANCING_ACCOUNT_CODE))
			.setBalancingAccountDescription(JsonUtils.getString(json, IJsonNames.BALANCING_ACCOUNT_DESCRIPTION))
			.setDocumentNumber(JsonUtils.getString(json, IJsonNames.DOCUMENT_NUMBER))
			;
	}
	
	public static JSONArray toJSON(List<FlatAccountEntryDetail> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<FlatAccountEntryDetail> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(FlatAccountEntryDetail object) {
		return new JSONObject()
			.put(IJsonNames.ENTRY_ID, object.getEntryId())
			.put(IJsonNames.ENTRY_DOMAIN, object.getEntryDomain())
			.put(IJsonNames.ENTRY_PERIOD, object.getEntryPperiod())
			.put(IJsonNames.ENTRY_PERIOD_NAME, object.getEntryPeriodName())
			.put(IJsonNames.ENTRY_DATE, object.getEntryDate())
			.put(IJsonNames.ENTRY_TYPE,	object.getEntryType().name())
			.put(IJsonNames.ACTIVITY, object.getActivity())
			.put(IJsonNames.ACTIVITY_NAME, object.getActivityName())
			.put(IJsonNames.JOURNAL, object.getJournal())
			.put(IJsonNames.ENTRY_SECURITY_LEVEL, object.getEntrySecurityLevel().name())
			.put(IJsonNames.COMMENTS, object.getComments())
			.put(IJsonNames.ENTRY_CREATION_USER, object.getEntryCreationUser())
			.put(IJsonNames.ENTRY_CREATION_DATE, object.getEntryCreationDate())
			.put(IJsonNames.ENTRY_MODIFICATION_USER, object.getEntryModificationUser())
			.put(IJsonNames.ENTRY_MODIFICATION_DATE, object.getEntryModificationDate())
			
			.put(IJsonNames.DETAIL_ID, object.getDetailId())
			.put(IJsonNames.LINE, object.getLine())
			.put(IJsonNames.ACCOUNT, object.getAccount())
			.put(IJsonNames.ACCOUNT_CODE, object.getAccountCode())
			.put(IJsonNames.ACCOUNT_DESCRIPTION, object.getAccountDescription())
			.put(IJsonNames.CONCEPT, object.getConcept())
			.put(IJsonNames.DEBIT, object.getDebit())
			.put(IJsonNames.CREDIT, object.getCredit())
			.put(IJsonNames.DEBIT_BALANCE, object.getDebitBalance())
			.put(IJsonNames.UNPAID_BALANCE, object.getUnpaidBalance())
			.put(IJsonNames.INITIAL_DEBIT_BALANCE, object.getInitialDebitBalance())
			.put(IJsonNames.INITIAL_UNPAID_BALANCE, object.getUnpaidBalance())
			.put(IJsonNames.BALANCING_ACCOUNT, object.getBalancingAccount())
			.put(IJsonNames.BALANCING_ACCOUNT_CODE, object.getBalancingAccountCode())
			.put(IJsonNames.BALANCING_ACCOUNT_DESCRIPTION, object.getBalancingAccountDescription())
			.put(IJsonNames.DOCUMENT_NUMBER, object.getDocumentNumber())
			;
	}
}
