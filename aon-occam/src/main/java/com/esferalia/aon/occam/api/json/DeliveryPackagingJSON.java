package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.api.model.warehouse.SerfruitDeliveryPackaging;

public class DeliveryPackagingJSON {

	public DeliveryPackagingJSON() {
	
	}
	
	public static List<DeliveryPackaging> fromJSON(JSONArray array) {
		if(array == null) return null;
		LinkedList<DeliveryPackaging> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(fromJSON(array.getJSONObject(i)));
		}
 		return list;
	}
	
	public static DeliveryPackaging fromJSON(JSONObject json) {
		return new DeliveryPackaging()
			;
	}

	public static JSONArray toJSON(List<DeliveryPackaging> list) {
		JSONArray json = new JSONArray();
		list.forEach(r -> json.put(toJSON(r)));
		return json;
	}
	
	public static JSONObject toJSON(DeliveryPackaging object) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, object.getId());
		json.put(IJsonNames.DOMAIN, object.getDomain());
		json.put(IJsonNames.ITEM, ItemJSON.toJSON(object.getItem()));
		json.put(IJsonNames.DELIVERY, DeliveryJSON.toJSON(object.getDelivery()));
		return json;
	}
}
