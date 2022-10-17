package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountTransactionFromJSON;
import com.esferalia.aon.occam.api.json.nordigen.NordigenJSONFunctionalInterfaces.INordigenAccountTransactionToJSON;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransaction;
import com.esferalia.aon.watson.server.AonDateUtils;

public enum NordigenAccountTransactionJSON {
	TRANSACTION_ID(
			(transaction, json) -> transaction.setTransactionId(json.optString("transactionId", null)),
			(transaction, json) -> json.put("transactionId", transaction.getTransactionId())
	),
	ENTRY_REFERENCE(
			(transaction, json) -> transaction.setEntryReference(json.optString("entryReference", null)),
			(transaction, json) -> json.put("entryReference", transaction.getEntryReference())
	),
	CHECK_ID(
			(transaction, json) -> transaction.setCheckId(json.optString("checkId", null)),
			(transaction, json) -> json.put("checkId", transaction.getCheckId())
	),
	BOOKING_DATE(
			(transaction, json) -> transaction.setBookingDate(AonDateUtils.parse(json.optString("bookingDate", null), AonDateUtils.SIMPLE_DATE_FORMAT4)),
			(transaction, json) -> json.put("bookingDate", AonDateUtils.format(transaction.getBookingDate(), AonDateUtils.SIMPLE_DATE_FORMAT4))
	),
	VALUE_DATE(
			(transaction, json) -> transaction.setValueDate(AonDateUtils.parse(json.optString("valueDate", null), AonDateUtils.SIMPLE_DATE_FORMAT4)),
			(transaction, json) -> json.put("valueDate", AonDateUtils.format(transaction.getValueDate(), AonDateUtils.SIMPLE_DATE_FORMAT4))
	),
	TRANSACTION_AMOUNT(
			(transaction, json) -> transaction.setTransactionAmount(NordigenJSONUtils.accountAmountFromJSON(json.optJSONObject("transactionAmount"))),
			(transaction, json) -> json.put("transactionAmount", NordigenJSONUtils.accountAmountToJSON(transaction.getTransactionAmount()))
	),
	REMITTANCE_INFORMATION_UNSTRUCTURED(
			(transaction, json) -> transaction.setRemittanceInformationUnstructured(json.optString("remittanceInformationUnstructured", null)),
			(transaction, json) -> json.put("remittanceInformationUnstructured", transaction.getRemittanceInformationUnstructured())
	),
	PURPOSE_CODE(
			(transaction, json) -> transaction.setPurposeCode(json.optString("purposeCode", null)),
			(transaction, json) -> json.put("purposeCode", transaction.getPurposeCode())
	),
	BANK_TRANSACTION_CODE(
			(transaction, json) -> transaction.setBankTransactionCode(json.optString("bankTransactionCode", null)),
			(transaction, json) -> json.put("bankTransactionCode", transaction.getBankTransactionCode())
	),
	PROPRIETARY_BANK_TRANSACTION_CODE(
			(transaction, json) -> transaction.setProprietaryBankTransactionCode(json.optString("proprietaryBankTransactionCode", null)),
			(transaction, json) -> json.put("proprietaryBankTransactionCode", transaction.getProprietaryBankTransactionCode())
	),
	END_TO_END_ID(
			(transaction, json) -> transaction.setEndToEndId(json.optString("endToEndId", null)),
			(transaction, json) -> json.put("endToEndId", transaction.getEndToEndId())
	)
	;
	private INordigenAccountTransactionFromJSON fromJSON;
	private INordigenAccountTransactionToJSON toJSON;
	
	private NordigenAccountTransactionJSON(INordigenAccountTransactionFromJSON fromJSON, INordigenAccountTransactionToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(NordigenAccountTransaction transaction) {
		if (transaction != null) {
			JSONObject json = new JSONObject();
			for (NordigenAccountTransactionJSON n : NordigenAccountTransactionJSON.values()) {
				n.toJSON.to(transaction, json);
			}
			return json;
		}
		return null;
	}
	
	public static NordigenAccountTransaction fromString(String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}

	public static NordigenAccountTransaction fromJSON(JSONObject json) {
		if (json != null) {
			NordigenAccountTransaction transaction = new NordigenAccountTransaction();
			for (NordigenAccountTransactionJSON n : NordigenAccountTransactionJSON.values()) {
				n.fromJSON.from(transaction, json);
			}
			return transaction;
		}
		return null;
	}
	
	public static List<NordigenAccountTransaction> fromJSONArray(JSONArray jsonArray) {
		List<NordigenAccountTransaction> transactionList = new LinkedList<>();
		if (jsonArray != null) {
			for (int i=0; i<jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				NordigenAccountTransaction transaction = NordigenAccountTransactionJSON.fromJSON(jsonObj);
				transactionList.add(transaction);
			}
		}
		return transactionList;
	}
	
	public static JSONArray toJSONArray(List<NordigenAccountTransaction> list) {
		JSONArray transactionArr = new JSONArray();
		if (list != null) {
			for (NordigenAccountTransaction transaction: list) {
				JSONObject transactionJson = NordigenAccountTransactionJSON.toJSON(transaction);
				transactionArr.put(transactionJson);
			}
		}
		return transactionArr;
	}
}
