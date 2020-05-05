package net.aonsolutions.aon.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonFinanceFromJSON;
import net.aonsolutions.aon.api.json.FunctionalInterfaces.IAonFinanceToJSON;

public enum AonFinanceJSON {

	DUE_DATE(
		(finance, json) -> finance.setDueDate(AonDateUtils.dateTimeParse(json.optString(IConstants.DUE_DATE))),
		(finance, json) -> json.put(IConstants.DUE_DATE, AonDateUtils.dateTimeFormat(finance.getDueDate()))
	),
	AMOUNT(
		(finance, json) -> finance.setAmount(json.optDouble(IConstants.AMOUNT)),
		(finance, json) -> json.put(IConstants.AMOUNT, finance.getAmount())
	),
	PAY_METHOD(
		(finance, json) -> finance.setPayMethodType(json.optEnum(PayMethodType.class, IConstants.PAY_METHOD)),
		(finance, json) -> json.put(IConstants.PAY_METHOD, finance.getPayMethodType().name())
	),
	IBAN(
		(finance, json) -> finance.setBankAccount(new BankAccount(json.optString(IConstants.IBAN))),
		(finance, json) -> json.put(IConstants.IBAN, finance.getBankAccount().getBankCode())
	);

	private IAonFinanceFromJSON fromJSON;
	private IAonFinanceToJSON toJSON;

	private AonFinanceJSON(IAonFinanceFromJSON fromJSON, IAonFinanceToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	public static JSONObject toJSON(Finance t) {
		JSONObject json = new JSONObject();
		for (AonFinanceJSON p : AonFinanceJSON.values()) {
			p.toJSON.to(t, json);
		}
		return json;
	}

	public static Finance fromJSON(JSONObject json) {
		Finance finance = new Finance();
		if (json != null) {
			for (AonFinanceJSON p : AonFinanceJSON.values()) {
				p.fromJSON.from(finance, json);
			}
		}
		return finance;
	}

}
