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
	
	public static InvoiceSeries from(String json) {
		return from(new JSONObject(json));
	}

	public static List<InvoiceSeries> from(JSONArray json) {
		LinkedList<InvoiceSeries> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(from(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static InvoiceSeries from(JSONObject json) {
		return new InvoiceSeries()
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setCount(JsonUtils.getInt(json, IJsonNames.COUNT))
			.setFromNumber(JsonUtils.getInt(json, IJsonNames.FROM_NUMBER))
			.setToNumber(JsonUtils.getInt(json, IJsonNames.TO_NUMBER))
			.setSales(JsonUtils.getboolean(json, IJsonNames.SALES))
		;
	}
	
	public static JSONArray to(List<InvoiceSeries> list) {
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<InvoiceSeries> invoiceSeries) {
		JSONArray array = new JSONArray();
		invoiceSeries.forEach(series -> array.put(to(series)));
		return array;
	}
	
	public static JSONObject to(InvoiceSeries series) {
		return new JSONObject()
			.putOpt(IJsonNames.DESCRIPTION, series.getDescription() )
			.put(IJsonNames.COUNT, series.getCount())
			.put(IJsonNames.FROM_NUMBER, series.getFromNumber())
			.put(IJsonNames.TO_NUMBER, series.getToNumber())
			.put(IJsonNames.SALES, series.isSales())
			;
	}

}
