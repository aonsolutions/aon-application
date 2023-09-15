package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;

public class CarrierPackingJSON {

	private CarrierPackingJSON() {
	
	}

	public static List<CarrierPacking> fromJSON(JSONArray array) {
		LinkedList<CarrierPacking> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(fromJSON(array.getJSONObject(i)));
		}
 		return list;
	}
	
	public static CarrierPacking fromJSON(JSONObject json) {
		JSONObject carrierJSON = JsonUtils.getJSONObject(json, IJsonNames.CARRIER);
		return new CarrierPacking()
			.setDeliveryDate(JsonUtils.getDate(json, IJsonNames.DATE))
			.setCarrierName(JsonUtils.getString(carrierJSON, IJsonNames.NAME))
			.setCarrierDocument(JsonUtils.getString(carrierJSON, IJsonNames.DOCUMENT))
			.setCarrier(JsonUtils.getInteger(carrierJSON, IJsonNames.ID))
			.setCarrierReference(JsonUtils.getString(json, IJsonNames.CARRIER_REFERENCE))
			.setNumberPlate(JsonUtils.getString(json, IJsonNames.NUMBER_PLATE))
			.setDriverName(JsonUtils.getString(json, IJsonNames.DRIVER_NAME))
			.setDriverDocument(JsonUtils.getString(json, IJsonNames.DRIVER_DOCUMENT))
			;
	}

	public static JSONArray toJSON(List<CarrierPacking> list) {
		JSONArray json = new JSONArray();
		list.forEach(r -> json.put(toJSON(r)));
		return json;
	}
	
	public static JSONObject toJSON(CarrierPacking object) {
		JSONObject json = new JSONObject();
		// TODO
		return json;
	}
}
