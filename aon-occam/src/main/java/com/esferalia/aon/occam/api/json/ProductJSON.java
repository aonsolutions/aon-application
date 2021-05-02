package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.product.Product;

public class ProductJSON {

	public static LinkedList<Product> fromJSON(JSONArray json) {
		LinkedList<Product> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Product fromJSON(JSONObject json) {
		return new Product()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
//				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN));
				.setCode(JsonUtils.getString(json, IJsonNames.CODE))
				.setName(JsonUtils.getString(json, IJsonNames.NAME));
	}
	
	public static JSONArray toJSON(LinkedList<Product> products) {
		return toJSON(products.stream());
	}
	
	public static JSONArray toJSON(Stream<Product> products) {
		JSONArray array = new JSONArray();
		products.forEach(tax -> array.put(toJSON(tax)));
		return array;
	}
	
	
	public static JSONObject toJSON(Product product) {
		return new JSONObject()
				.put(IJsonNames.ID, product.getId())
				.put(IJsonNames.DOMAIN, product.getDomain().getId())
				.put(IJsonNames.CODE, product.getCode())
				.put(IJsonNames.NAME, product.getName());
	}
}
