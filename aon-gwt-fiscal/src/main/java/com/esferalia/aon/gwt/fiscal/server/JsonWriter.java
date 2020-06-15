package com.esferalia.aon.gwt.fiscal.server;

import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.watson.error.AonCoreException;

public class JsonWriter {
	
	public static JSONObject writeToJSON(Account account) {
		try {
			JSONObject json = new JSONObject()
				.put("id", account.getId())
				.put("domain", account.getDomain())
				.put("code", account.getCode())
				.put("description", account.getDescription())
				.put("alias", account.getAlias())
				.put("level", account.getLevel())
				.put("active", account.isActive())
				.put("costCenter", account.getCostCenter())
			;
			return json;
		} catch (JSONException e) {
			throw new AonCoreException(e);
		}
	}

	public static JSONObject writeToJSON(FlatAccountEntryDetail entry) {
		try {
			JSONObject json = new JSONObject()
				.put("entryId", entry.getEntryId())
				.put("entryDomain", entry.getEntryDomain())
				.put("entryPeriod", entry.getEntryPeriodName())
				.put("entryPeriodName", entry.getEntryPeriodName())
				.put("entryDate", entry.getEntryDate())
				.put("entryType", entry.getEntryType().ordinal())
				.put("activity", entry.getActivity())
				.put("activityName", entry.getActivityName())
				.put("journal", entry.getJournal())
				.put("securityLevel", entry.getEntrySecurityLevel().ordinal())
				.put("comments", entry.getComments())
				
				.put("detailId", entry.getDetailId())
				.put("account", entry.getAccount())
				.put("accountCode", entry.getAccountCode())
				.put("accountDescription", entry.getAccountDescription())
				.put("concept", entry.getConcept())
				.put("line", entry.getLine())
				.put("debit", entry.getDebit())
				.put("credit", entry.getCredit())
				.put("debitBalance", entry.getDebitBalance())
				.put("unpaidBalance", entry.getUnpaidBalance())
				.put("initialDebitBalance", entry.getInitialDebitBalance())
				.put("initialUnpaidBalance", entry.getInitialUnpaidBalance())
				.put("balancingAccount", entry.getBalancingAccount())
				.put("balancingAccountCode", entry.getBalancingAccountCode())
				.put("balancingAccountDescription", entry.getBalancingAccountDescription())
				.put("documentNumber", entry.getDocumentNumber())
			;
			return json;
		} catch (JSONException e) {
			throw new AonCoreException(e);
		}
	}

	
	public static JSONObject writeToJSON(AccountEntry entry) {
		try {
			JSONObject json = new JSONObject()
				.put("domain", entry.getDomain())
				.put("id", entry.getId())
				.put("period", entry.getPeriod())
				.put("periodName", entry.getPeriodName())
				.put("periodStatus", entry.getPeriodStatus().ordinal())
				.put("entryDate", entry.getEntryDate())
				.put("entryType", entry.getEntryType().ordinal())
				.put("activity", entry.getActivity())
				.put("activityDescription", entry.getActivityDescription())
				.put("journal", entry.getJournal())
				.put("securityLevel", entry.getSecurityLevel().ordinal())
				.put("comments", entry.getComments())
				.put("creationUser", entry.getCreationUser())
				.put("creationDate", entry.getCreationDate())
				.put("modificationUser", entry.getModificationUser())
				.put("modificationDate", entry.getModificationDate())
			;
			if (entry.getDetails() != null && !entry.getDetails().isEmpty()) {
				JSONArray details = new JSONArray();
				int i = 0;
				for (AccountEntryDetail detail : entry.getDetails()) {
					details.put(i,writeToJSON(detail));
					i++;
				}
				json.put("details", details);
			}
			return json;
		} catch (JSONException e) {
			throw new AonCoreException(e);
		}
	}
	
	public static JSONObject writeToJSON(AccountEntryDetail detail) {
		try {
			JSONObject json = new JSONObject()
				.put("domain", detail.getDomain())
				.put("id", detail.getId())
				.put("accountEntry", detail.getAccountEntry())
				.put("account", detail.getAccount())
				.put("accountCode", detail.getAccountCode())
				.put("accountDescription", detail.getAccountDescription())
				.put("line", detail.getLine())
				.put("concept", detail.getConcept())
				.put("debit", detail.getDebit())
				.put("credit", detail.getCredit())
				.put("balancingAccount", detail.getBalancingAccount())
				.put("balancingAccountCode", detail.getBalancingAccountCode())
				.put("balancingAccountDescription", detail.getBalancingAccountDescription())
				.put("documentNumber", detail.getDocumentNumber())
				.put("creationUser", detail.getCreationUser())
				.put("creationDate", detail.getCreationDate())
				.put("modificationUser", detail.getModificationUser())
				.put("modificationDate", detail.getModificationDate())
			;
			return json;
		} catch (JSONException e) {
			throw new AonCoreException(e);
		}
	}

	public static void write(PrintWriter out, JSONArray array) throws JSONException {
		if (array != null) {
			out.write('[');
			boolean first = true;
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (first) {
					first = false;	
				} else {
					out.write(',');		
				}
				obj.write(out);
				out.write('\n');
			}
			out.write(']');
		}
		
	}

}
