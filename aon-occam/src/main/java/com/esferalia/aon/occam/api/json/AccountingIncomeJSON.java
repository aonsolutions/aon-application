package com.esferalia.aon.occam.api.json;

import java.util.Optional;
import java.util.function.Supplier;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AccountingIncomeJSON {
	
	private AccountingIncomeJSON() {
	}
	
	public static Optional<AccountingIncome> from(JSONObject json) {
		return from(json, AccountingIncome::new );
	}
	public static Optional<AccountingIncome> from(JSONObject json, Supplier<AccountingIncome> income) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( 
			income.get()
				.setDomain(JsonUtils.getInt(json, IJsonNames.DOMAIN))
				.setCustomer(CustomerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CUSTOMER)))
				.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setActivity(JsonUtils.getInteger(json, IJsonNames.ACTIVITY))
				.setExpAccount(AccountJSON.from(JsonUtils.getJSONObject(json, IJsonNames.EXP_ACCOUNT)).orElse(null))
				.setConcept(JsonUtils.optString(json,IJsonNames.CONCEPT))
				.setReferenceCode(JsonUtils.optString(json,IJsonNames.REFERENCE_CODE))
				.setAmount(JsonUtils.getdouble(json, IJsonNames.AMOUNT))
				.setBank(RegistryBankJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.BANK)))
				.setCashAccount(AccountJSON.from(JsonUtils.getJSONObject(json, IJsonNames.CASH_ACCOUNT)).orElse(null))
				.setComments(JsonUtils.optString(json, IJsonNames.COMMENTS))
				.setAccountEntry(AccountEntryJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ACCOUNT_ENTRY)).orElse(null))
		);
	}
	
	public static Optional<JSONObject> to(AccountingIncome a) {
		if (a == null) return Optional.empty();
		return Optional.of(
			new JSONObject()
				.put(IJsonNames.DOMAIN, a.getDomain())
				.put(IJsonNames.CUSTOMER, CustomerJSON.toJSON(a.getCustomer().orElse(null)))
				.put(IJsonNames.DATE, AonDateUtils.format(a.getDate(), AonDateUtils.SIMPLE_DATE_FORMAT))
				.put(IJsonNames.ACTIVITY, a.getActivity().orElse(null)) 
				.put(IJsonNames.EXP_ACCOUNT, AccountJSON.to(a.getExpAccount()).orElse(null))
				.put(IJsonNames.CONCEPT, a.getConcept())
				.put(IJsonNames.REFERENCE_CODE, a.getReferenceCode())
				.put(IJsonNames.AMOUNT, a.getAmount())
				.put(IJsonNames.BANK, RegistryBankJSON.to(a.getBank()).orElse(null))
				.put(IJsonNames.CASH_ACCOUNT, AccountJSON.to(a.getCashAccount()).orElse(null))
				.put(IJsonNames.COMMENTS, a.getComments())
				.put(IJsonNames.ACCOUNT_ENTRY, AccountEntryJSON.toJSON(a.getAccountEntry().orElse(null)))
		);
	}
}
