package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountEntryJSON {
	
	private AccountEntryJSON() {
	
	}
	
	public static List<AccountEntry> fromJSON(JSONArray json) {
		LinkedList<AccountEntry> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static AccountEntry fromJSON(JSONObject json) {
		return new AccountEntry()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setPeriod(JsonUtils.getInteger(json, IJsonNames.PERIOD))
			.setPeriodName(JsonUtils.getString(json, IJsonNames.PERIOD_NAME))
			.setPeriodStatus(AccountPeriodStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.PERIOD_STATUS)))
			.setEntryDate(JsonUtils.getDate(json, IJsonNames.ENTRY_DATE))
			.setEntryType(AccountEntryType.safeValueOf(JsonUtils.getString(json, IJsonNames.ENTRY_TYPE)))
			.setActivity(JsonUtils.getInteger(json, IJsonNames.ACTIVITY))
			.setActivityDescription(JsonUtils.getString(json, IJsonNames.ACTIVITY_DESCRIPTION))
			.setJournal(JsonUtils.getInteger(json, IJsonNames.JOURNAL))
			.setSecurityLevel(SecurityLevel.safeValueOf(JsonUtils.getString(json, IJsonNames.SECURITY_LEVEL)))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setDetails((LinkedList) AccountEntryDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DETAILS)))
			
			.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
			.setUndeductible(JsonUtils.getboolean(json, IJsonNames.UNDEDUCTIBLE))
		;
	}
	
	public static JSONArray toJSON(List<AccountEntry> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<AccountEntry> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(AccountEntry object) {
		return new JSONObject()
				.put(IJsonNames.ID, object.getId())
				.put(IJsonNames.DOMAIN, object.getDomain())
				.put(IJsonNames.PERIOD, object.getPeriod())
				.put(IJsonNames.PERIOD_NAME, object.getPeriodName())
				.put(IJsonNames.PERIOD_STATUS, object.getPeriodStatus().name())
				.put(IJsonNames.ENTRY_DATE, object.getEntryDate())
				.put(IJsonNames.ENTRY_TYPE, object.getEntryType().name())
				.put(IJsonNames.ACTIVITY, object.getActivity())
				.put(IJsonNames.ACTIVITY_DESCRIPTION, object.getActivityDescription())
				.put(IJsonNames.JOURNAL, object.getJournal())
				.put(IJsonNames.SECURITY_LEVEL, object.getSecurityLevel().name())
				.put(IJsonNames.COMMENTS, object.getComments())
				.put(IJsonNames.DETAILS , AccountEntryDetailJSON.toJSON(object.getDetails()))
				
				.put(IJsonNames.DIRTY, object.isDirty())
				.put(IJsonNames.UNDEDUCTIBLE, object.isUndeductible());	
	}
}
