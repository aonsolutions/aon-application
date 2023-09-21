package es.translogia.tedi.json;

import java.text.SimpleDateFormat;

import org.json.JSONObject;

import es.translogia.tedi.ewok.IConstants;
import es.translogia.tedi.ewok.TediFinance;
import es.translogia.tedi.ewok.TediPayMethod;
import es.translogia.tedi.json.FunctionalInterfaces.ITediFinanceFromJSON;
import es.translogia.tedi.json.FunctionalInterfaces.ITediFinanceToJSON;

public enum TediFinanceJSON {

	DUE_DATE(
		(finance, json) -> finance.setDueDate(TediJSONUtils.parseDate(json.optString(IConstants.DUE_DATE), new SimpleDateFormat("yyyy-MM-dd"))),
		(finance, json) -> TediJSONUtils.put(json, IConstants.DUE_DATE, finance.getDueDate(), new SimpleDateFormat("yyyy-MM-dd"))
	),
	AMOUNT(
		(finance, json) -> finance.setAmount(TediJSONUtils.optDouble(json, IConstants.AMOUNT)),
		(finance, json) -> json.put(IConstants.AMOUNT, finance.getAmount())
	),
	PAY_METHOD(
		(finance, json) -> finance.setPayMethod(json.optEnum(TediPayMethod.class, IConstants.PAY_METHOD)),
		(finance, json) -> json.put(IConstants.PAY_METHOD, finance.getPayMethod())
	),
	IBAN(
		(finance, json) -> finance.setIban(json.optString(IConstants.IBAN)),
		(finance, json) -> json.put(IConstants.IBAN, finance.getIban())
	),
	PENDING(
		(finance, json) -> finance.setPending(json.optBoolean(IConstants.PENDING)),
		(finance, json) -> json.put(IConstants.PENDING, finance.getPending())
	);

	private ITediFinanceFromJSON fromJSON;
	private ITediFinanceToJSON toJSON;

	private TediFinanceJSON(ITediFinanceFromJSON fromJSON, ITediFinanceToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	public static JSONObject toJSON(TediFinance t) {
		JSONObject json = new JSONObject();
		for (TediFinanceJSON p : TediFinanceJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static TediFinance fromJSON(JSONObject json) {
		TediFinance emailInfo = new TediFinance();
		if (json != null) {
			for (TediFinanceJSON p : TediFinanceJSON.values()) {
				p.fromJSON.from(emailInfo, json);
			}
		}
		return emailInfo;
	}
}
