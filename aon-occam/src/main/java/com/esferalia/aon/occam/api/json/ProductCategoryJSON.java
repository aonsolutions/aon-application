package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.product.ProductCategory;

public class ProductCategoryJSON {
	
	private ProductCategoryJSON() {
	
	}
	
	public static List<ProductCategory> fromJSON(JSONArray json) {
		LinkedList<ProductCategory> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ProductCategory fromJSON(JSONObject json) {
		return new ProductCategory()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setName(JsonUtils.getString(json, IJsonNames.NAME))
				.setDetail(JsonUtils.getString(json, IJsonNames.DETAIL))
				.setDetail2(JsonUtils.getString(json, IJsonNames.DETAIL2))
				.setDetail3(JsonUtils.getString(json, IJsonNames.DETAIL3));
	}
	
	public static JSONArray toJSON(List<ProductCategory> categories) {
		return toJSON(categories.stream());
	}
	
	public static JSONArray toJSON(Stream<ProductCategory> categories) {
		JSONArray array = new JSONArray();
		categories.forEach(category -> array.put(toJSON(category)));
		return array;
	}
	
	
	public static JSONObject toJSON(ProductCategory category) {
		return new JSONObject()
				.put(IJsonNames.ID, category.getId())
				.put(IJsonNames.DOMAIN, category.getDomain())
				.put(IJsonNames.NAME, category.getName())
				.put(IJsonNames.DETAIL, category.getDetail())
				.put(IJsonNames.DETAIL2, category.getDetail2())
				.put(IJsonNames.DETAIL3, category.getDetail3());
	}
}
