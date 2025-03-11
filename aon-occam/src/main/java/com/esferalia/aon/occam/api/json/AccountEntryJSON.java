package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AccountEntryJSON {
	
	private AccountEntryJSON() {
	
	}
	public static Optional<AccountEntry> fromJSON(JSONObject json) {
		return fromJSON(json, AccountEntry::new );
	}
	
	public static Optional<AccountEntry> fromJSON(JSONObject json, Supplier<AccountEntry> supp) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( supp.get()
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
			.setConfidential( JsonUtils.getboolean(json,IJsonNames.CONFIDENTIAL)) 
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setDetails(AccountEntryDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DETAILS))
					.collect(Collectors.toCollection(LinkedList::new)))
		);
	}
	
	public static Optional<JSONObject> toJSON(AccountEntry ae) {
		if (ae == null) return Optional.empty();
		return Optional.of( new JSONObject()
			.put(IJsonNames.ID, ae.getId())
			.put(IJsonNames.DOMAIN, ae.getDomain())
			.put(IJsonNames.PERIOD, ae.getPeriod())
			.put(IJsonNames.PERIOD_NAME, ae.getPeriodName())
			.put(IJsonNames.PERIOD_STATUS, ae.getPeriodStatus() == null ? null : ae.getPeriodStatus().name())
			.put(IJsonNames.ENTRY_DATE, AonDateUtils.format(ae.getEntryDate(), AonDateUtils.SIMPLE_DATE_FORMAT))
			.put(IJsonNames.ENTRY_TYPE, ae.getEntryType() == null? null : ae.getEntryType().name())
			.put(IJsonNames.ACTIVITY, ae.getActivity())
			.put(IJsonNames.ACTIVITY_DESCRIPTION, ae.getActivityDescription())
			.put(IJsonNames.JOURNAL, ae.getJournal())
			.put(IJsonNames.CONFIDENTIAL, ae.isConfidential())
			.put(IJsonNames.COMMENTS, ae.getComments())
			.put(IJsonNames.DETAILS , AccountEntryDetailJSON.toJSON(ae.getDetails())))
		;
	}
}
