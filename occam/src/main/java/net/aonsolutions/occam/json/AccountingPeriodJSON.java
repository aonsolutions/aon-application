package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.accounting.AccountingPeriod;
import net.aonsolutions.occam.api.constants.AccountingPeriodStatus;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class AccountingPeriodJSON {
	
	private AccountingPeriodJSON() {
	}
	
	public static List<AccountingPeriod> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(AccountingPeriodJSON::from)
			.toList();		
	}
	
	public static AccountingPeriod from(JSONObject json) {
		if (json == null) return null; 
		return new AccountingPeriod()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setName(AonJSONUtils.getString(json, AonNames.NAME))
			.setStartDate(AonJSONUtils.getDate(json, AonNames.START_DATE))
			.setEndingDate(AonJSONUtils.getDate(json, AonNames.END_DATE))
			.setStatus(AccountingPeriodStatus.safeValueOf(AonJSONUtils.getString(json, AonNames.STATUS)).orElse(null) )
			.setDefaultPeriod( AonJSONUtils.getBoolean(json, AonNames.DEFAULT_PERIOD))
			.setAudit( AuditJSON.from(AonJSONUtils.getObject(json, AonNames.AUDIT))) 
		;
	}
	
	public static JSONArray to(List<AccountingPeriod> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<AccountingPeriod> stream) {
		return stream
			.map(AccountingPeriodJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(AccountingPeriod ap) {
		if (ap == null) return null;
		return new JSONObject()
			.put(AonNames.ID, ap.getId())
			.put(AonNames.DOMAIN, ap.getDomain())
			.putOpt(AonNames.NAME, ap.getName())
			.putOpt(AonNames.START_DATE, AonJSONUtils.formatDate( ap.getStartDate() ))
			.putOpt(AonNames.END_DATE, AonJSONUtils.formatDate( ap.getEndingDate() ))
			.putOpt(AonNames.STATUS, AonObjectUtils.ifNotNullDo(ap.getStatus(), Object::toString ))
			.putOpt(AonNames.DEFAULT_PERIOD, ap.isDefaultPeriod())
			.putOpt(AonNames.AUDIT, AuditJSON.to(ap.getAudit().orElse(null)))
			;
	}
}
