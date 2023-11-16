package com.esferalia.aon.occam.api.json.nordigen;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransactions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class NordigenAccountTransactionsJSON {
	
	private NordigenAccountTransactionsJSON() {
	}
	
	public static NordigenAccountTransactions fromTransactions(String text) throws NordigenException {
		try {
			JSONObject json = new JSONObject(text);
			return from( json.optJSONObject("transactions") );
		} catch (JSONException e) {
			throw new NordigenException(e.getMessage()); 
		} 
	}
	
	public static List<NordigenAccountTransactions> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return NordigenJSONUtils.stream(array)
			.map(NordigenAccountTransactionsJSON::from)
			.toList();		
	}
	
	public static NordigenAccountTransactions from(JSONObject json) {
		if (json == null) return null; 
		return new NordigenAccountTransactions()
			.setPending( NordigenAccountTransactionJSON.from(json.optJSONArray("pending")))
			.setBooked( NordigenAccountTransactionJSON.from(json.optJSONArray("booked")));
	}
	
	public static JSONArray to(List<NordigenAccountTransactions> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<NordigenAccountTransactions> stream) {
		return stream
			.map(NordigenAccountTransactionsJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(NordigenAccountTransactions transactions) {
		if (transactions == null) return null;
		return new JSONObject()
			.putOpt("pending", NordigenAccountTransactionJSON.to(transactions.getPending()))
			.putOpt("booked", NordigenAccountTransactionJSON.to(transactions.getBooked()))
			;
	}
	
}
