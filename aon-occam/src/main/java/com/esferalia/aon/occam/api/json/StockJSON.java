package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.Stock;

public class StockJSON {

	private StockJSON() {
		
	}
	
	public static List<Stock> fromJSON(JSONArray json) {
		LinkedList<Stock> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Stock fromJSON(JSONObject json) {
		if(json == null) return null;
		return new Stock()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setItem(JsonUtils.getInteger(json, IJsonNames.ITEM))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setWarehouse(JsonUtils.getInteger(json, IJsonNames.WAREHOUSE));
	}
	
	public static JSONArray toJSON(List<Stock> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Stock> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(stock -> array.put(toJSON(stock)));
		return array;
	}
	
	
	public static JSONObject toJSON(Stock stock) {
		if(stock == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, stock.getId())
			.put(IJsonNames.DOMAIN, stock.getDomain())
			.put(IJsonNames.ITEM, stock.getItem())
			.put(IJsonNames.QUANTITY, stock.getQuantity())
			.put(IJsonNames.WAREHOUSE, stock.getWarehouse());
	}
}
