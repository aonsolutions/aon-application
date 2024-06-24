package com.esferalia.aon.occam.api.json.invoice;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.InvoiceSeries;

public class InvoiceSeriesJSON {
	
	private InvoiceSeriesJSON() {
	
	}
	
	public static List<InvoiceSeries> fromJSON(JSONArray json) {
		LinkedList<InvoiceSeries> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static InvoiceSeries fromJSON(JSONObject json) {
		return new InvoiceSeries()
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setCount(JsonUtils.getInt(json, IJsonNames.COUNT))
			.setFromNumber(JsonUtils.getInt(json, IJsonNames.FROM_NUMBER))
			.setToNumber(JsonUtils.getInt(json, IJsonNames.TO_NUMBER))
			.setSales(JsonUtils.getboolean(json, IJsonNames.SALES))
		;
	}
	
	public static JSONArray toJSON(List<InvoiceSeries> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<InvoiceSeries> invoiceSeries) {
		JSONArray array = new JSONArray();
		invoiceSeries.forEach(series -> array.put(toJSON(series)));
		return array;
	}
	
	public static JSONObject toJSON(InvoiceSeries series) {
		return new JSONObject()
				.put(IJsonNames.DESCRIPTION, series.getDescription() != null ? series.getDescription() : "")
				.put(IJsonNames.COUNT, series.getCount())
				.put(IJsonNames.FROM_NUMBER, series.getFromNumber())
				.put(IJsonNames.TO_NUMBER, series.getToNumber())
				.put(IJsonNames.SALES, series.isSales())
				;
	}

}
