package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransaction;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class NordigenAccountTransactionJSON {
	
	private NordigenAccountTransactionJSON() {
	}
	
	public static NordigenAccountTransaction from(String text) throws NordigenException {
		try {
			return from(new JSONObject(text));
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static List<NordigenAccountTransaction> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAccountTransactionJSON::from)
			.toList();		
	}
	
	public static NordigenAccountTransaction from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAccountTransaction()
			.setTransactionId(json.optString("transactionId", null))
			.setInternalTransactionId(json.optString("internalTransactionId", null))
			.setEntryReference(json.optString("entryReference", null))
			.setCheckId(json.optString("checkId", null))
			.setBookingDate(AonDateUtils.parse(json.optString("bookingDate", null), AonDateUtils.SIMPLE_DATE_FORMAT4))
			.setValueDate(AonDateUtils.parse(json.optString("valueDate", null), AonDateUtils.SIMPLE_DATE_FORMAT4))
			.setTransactionAmount(NordigenJSONUtils.accountAmountFromJSON(json.optJSONObject("transactionAmount")))
			.setRemittanceInformationUnstructured(json.optString("remittanceInformationUnstructured", null))
			.setRemittanceInformationStructured(json.optString("remittanceInformationStructured", null))
			.setRemittanceInformationUnstructuredArray( getRemittanceInformationUnstructuredArray( json )  )
			.setPurposeCode(json.optString("purposeCode", null))
			.setBankTransactionCode(json.optString("bankTransactionCode", null))
			.setProprietaryBankTransactionCode(json.optString("proprietaryBankTransactionCode", null))
			.setEndToEndId(json.optString("endToEndId", null))
			;
	}
	
	private static String[] getRemittanceInformationUnstructuredArray(JSONObject json) {
		JSONArray array = json.optJSONArray( "remittanceInformationUnstructuredArray" );
		if (array != null) {
			String[] arr = new String[array.length()];
			for (int i = 0; i < array.length(); i++ ) {
				arr[i] = array.getString(i);
			}
			return arr;
		}
		return null;
	}

	public static JSONArray to(List<NordigenAccountTransaction> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAccountTransaction> stream) {
		return stream
			.map(NordigenAccountTransactionJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAccountTransaction transaction) {
		if (transaction == null) return null;
		return new JSONObject()
			.put("transactionId", transaction.getTransactionId())
			.put("internalTransactionId", transaction.getInternalTransactionId())
			.put("entryReference", transaction.getEntryReference())
			.put("checkId", transaction.getCheckId())
			.put("bookingDate", AonDateUtils.format(transaction.getBookingDate(), AonDateUtils.SIMPLE_DATE_FORMAT4))
			.put("valueDate", AonDateUtils.format(transaction.getValueDate(), AonDateUtils.SIMPLE_DATE_FORMAT4))
			.put("transactionAmount", NordigenJSONUtils.accountAmountToJSON(transaction.getTransactionAmount()))
			.put("remittanceInformationUnstructured", transaction.getRemittanceInformationUnstructured())
			.put("remittanceInformationStructured", transaction.getRemittanceInformationStructured())
			.put("purposeCode", transaction.getPurposeCode())
			.put("bankTransactionCode", transaction.getBankTransactionCode())
			.put("proprietaryBankTransactionCode", transaction.getProprietaryBankTransactionCode())
			.put("endToEndId", transaction.getEndToEndId())
			;
	}
	
}
