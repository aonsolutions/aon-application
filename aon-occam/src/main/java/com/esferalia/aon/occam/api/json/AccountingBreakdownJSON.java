package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class AccountingBreakdownJSON {
	
	private AccountingBreakdownJSON() {
		
	}
	public static List<AccountingBreakdown> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<AccountingBreakdown> fromJSON(JSONArray json) {
		LinkedList<AccountingBreakdown> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static AccountingBreakdown fromJSON(JSONObject json) {
		return new AccountingBreakdown()
			.setEntryId(JsonUtils.getInteger(json, IJsonNames.ENTRY_ID))
			.setJournal(JsonUtils.getInteger(json, IJsonNames.JOURNAL))
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE))
			.setActivity(JsonUtils.optInteger(json, IJsonNames.ACTIVITY))
			.setActivityDescription(JsonUtils.getString(json, IJsonNames.ACTIVITY_DESCRIPTION))
			.setEpigraphSection( JsonUtils.getString(json, IJsonNames.EPIGRAPH))
			.setEpigraph( JsonUtils.getString(json, IJsonNames.EPIGRAPH_SECTION))
			.setRegime(IRPFRegime.safeValueOf(JsonUtils.optInteger(json,IJsonNames.IRPF_REGIME)))
			.setAccount(JsonUtils.getInteger(json, IJsonNames.ACCOUNT))
			.setAccountCode(JsonUtils.optString(json, IJsonNames.ACCOUNT_CODE))
			.setAccountDescription(JsonUtils.optString(json, IJsonNames.ACCOUNT_DESCRIPTION))
			.setDebit( json.optDouble(IJsonNames.DEBIT))
			.setCredit( json.optDouble(IJsonNames.CREDIT))
			;
	}
	
	public static JSONArray toJSON(List<AccountingBreakdown> tasks) {
		return toJSON(tasks.stream());
	}
	
	public static JSONArray toJSON(Stream<AccountingBreakdown> vatContexts) {
		JSONArray array = new JSONArray();
		vatContexts.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(AccountingBreakdown irpf) {
		return new JSONObject()
				.putOpt(IJsonNames.ENTRY_ID, irpf.getEntryId())
				.putOpt(IJsonNames.JOURNAL, irpf.getJournal())
				.putOpt(IJsonNames.ISSUE_DATE, irpf.getIssueDate() ==null?null:AonNumberUtils.toString(irpf.getIssueDate().getTime()))
				.putOpt(IJsonNames.ACTIVITY, irpf.getActivity())
				.putOpt(IJsonNames.ACTIVITY_DESCRIPTION, irpf.getActivityDescription())
				.putOpt(IJsonNames.EPIGRAPH, irpf.getEpigraph())
				.putOpt(IJsonNames.EPIGRAPH_SECTION, irpf.getEpigraphSection())
				.put(IJsonNames.IRPF_REGIME, irpf.getRegime()==null?null:irpf.getRegime().ordinal() )
				.putOpt(IJsonNames.ACCOUNT, irpf.getAccount())
				.putOpt(IJsonNames.ACCOUNT_CODE, irpf.getAccountCode())
				.putOpt(IJsonNames.ACCOUNT_DESCRIPTION, irpf.getAccountDescription())
				.putOpt(IJsonNames.DEBIT, irpf.getDebit() )
				.putOpt(IJsonNames.CREDIT, irpf.getCredit() )
			;
	}

}
