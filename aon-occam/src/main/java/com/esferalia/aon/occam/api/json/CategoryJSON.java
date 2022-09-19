package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.type.CategoryType;

public class CategoryJSON {
	
	public static LinkedList<Category> fromJSON(JSONArray json) {
		LinkedList<Category> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Category fromJSON(JSONObject json) {
		return new Category()
			.setId(JsonUtils.optInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.optInteger(json, IJsonNames.DOMAIN))
			.setName(JsonUtils.optString(json, IJsonNames.NAME))
			.setCategoryType( CategoryType.safeValueOf(JsonUtils.optString(json, IJsonNames.TYPE))) 
			.setScope(JsonUtils.optInteger(json, IJsonNames.SCOPE))
			.setDescription(JsonUtils.optString(json, IJsonNames.DESCRIPTION))
			.setUrl(JsonUtils.optString(json, IJsonNames.URL))
			.setRattach(JsonUtils.optInteger(json, IJsonNames.RATTACH))
			;
	}
	
	public static JSONArray toJSON(LinkedList<Category> categorys) {
		return toJSON(categorys.stream());
	}
	
	public static JSONArray toJSON(List<Category> categorys) {
		JSONArray array = new JSONArray();
		categorys.forEach(t -> array.put(toJSON(t)));
		return array;
	}
	
	public static JSONArray toJSON(Stream<Category> categorys) {
		JSONArray array = new JSONArray();
		categorys.forEach(t -> array.put(toJSON(t)));
		return array;
	}
	
	public static JSONObject toJSON(Category category) {
		if(category==null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, category.getId())
			.put(IJsonNames.DOMAIN, category.getDomain())
			.put(IJsonNames.NAME, category.getName())
			.put(IJsonNames.TYPE,  category.getCategoryType()!=null ? category.getCategoryType().getName() : null)
			.put(IJsonNames.SCOPE, category.getScope())
			.put(IJsonNames.DESCRIPTION, category.getDescription())
			.put(IJsonNames.URL, category.getUrl())
			.put(IJsonNames.RATTACH, category.getRattach())
			;
	}

}
