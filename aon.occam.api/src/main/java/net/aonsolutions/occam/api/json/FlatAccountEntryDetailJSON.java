package net.aonsolutions.occam.api.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.FlatAccountEntryDetail;

public class FlatAccountEntryDetailJSON {
	
	private FlatAccountEntryDetailJSON() {
		
	}
	
	public static JSONObject toJSON(FlatAccountEntryDetail flat) {
		if (flat == null) return null;
		return new JSONObject()
			.put(IJsonNames.ENTRY_ID, flat.getEntryId())
			.put(IJsonNames.ENTRY_DOMAIN, flat.getEntryDomain())
			.put(IJsonNames.ENTRY_PERIOD, flat.getEntryPeriodName())
			.put(IJsonNames.ENTRY_PERIOD_NAME, flat.getEntryPeriodName())
			.put(IJsonNames.ENTRY_DATE, flat.getEntryDate())
			.put(IJsonNames.ENTRY_TYPE, flat.getEntryType().ordinal())
			.put(IJsonNames.ACTIVITY, flat.getActivity())
			.put(IJsonNames.ACTIVITY_DESCRIPTION, flat.getActivityDescription())
			.put(IJsonNames.JOURNAL, flat.getJournal())
			.put(IJsonNames.SECURITY_LEVEL, flat.getEntrySecurityLevel().ordinal())
			.put(IJsonNames.COMMENTS, flat.getComments())
			.put(IJsonNames.DETAIL_ID, flat.getDetailId())
			.put(IJsonNames.ACCOUNT, flat.getAccount())
			.put(IJsonNames.ACCOUNT_CODE, flat.getAccountCode())
			.put(IJsonNames.ACCOUNT_DESCRIPTION, flat.getAccountDescription())
			.put(IJsonNames.CONCEPT, flat.getConcept())
			.put(IJsonNames.LINE, flat.getLine())
			.put(IJsonNames.DEBIT, flat.getDebit())
			.put(IJsonNames.CREDIT, flat.getCredit())
			.put(IJsonNames.DEBIT_BALANCE, flat.getDebitBalance())
			.put(IJsonNames.UNPAID_BALANCE, flat.getUnpaidBalance())
			.put(IJsonNames.INITIAL_DEBIT_BALANCE, flat.getInitialDebitBalance())
			.put(IJsonNames.INITIAL_UNPAID_BALANCE, flat.getInitialUnpaidBalance())
			.put(IJsonNames.BALANCING_ACCOUNT, flat.getBalancingAccount())
			.put(IJsonNames.BALANCING_ACCOUNT_CODE, flat.getBalancingAccountCode())
			.put(IJsonNames.BALANCING_ACCOUNT_DESCRIPTION, flat.getBalancingAccountDescription())
			.put(IJsonNames.DOCUMENT_NUMBER, flat.getDocumentNumber())
		;
	}


}
