package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;

public class DeliveryPackagingJSON {

	private DeliveryPackagingJSON() {
	
	}

	public static List<DeliveryPackaging> fromJSON(JSONArray array) {
		LinkedList<DeliveryPackaging> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(fromJSON(array.getJSONObject(i)));
		}
 		return list;
	}
	
	public static DeliveryPackaging fromJSON(JSONObject json) {
		return new DeliveryPackaging()
			.setProduct(ProductJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PRODUCT)))
			.setContent(fromJSON(JsonUtils.getJSONArray(json, IJsonNames.CONTENT)))
			.setDeliveryLine(JsonUtils.getInteger(json, IJsonNames.DELIVERY_LINE))
			.setSscc(JsonUtils.getString(json, IJsonNames.SSCC))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY));
	}

	public static JSONArray toJSON(List<DeliveryPackaging> list) {
		JSONArray json = new JSONArray();
		list.forEach(r -> json.put(toJSON(r)));
		return json;
	}
	
	public static JSONObject toJSON(DeliveryPackaging object) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.PRODUCT, ProductJSON.toJSON(object.getProduct()));
		json.put(IJsonNames.CONTENT, toJSON(object.getContent()));
		json.put(IJsonNames.SSCC, object.getSscc());
		json.put(IJsonNames.DELIVERY_LINE, object.getDeliveryLine());
		json.put(IJsonNames.QUANTITY, object.getQuantity());
		return json;
	}
}
