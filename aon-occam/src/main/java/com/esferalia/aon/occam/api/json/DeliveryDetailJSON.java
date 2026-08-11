package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;

public class DeliveryDetailJSON {
	
	private DeliveryDetailJSON() {
	
	}
	
	public static List<DeliveryDetail> fromJSON(JSONArray json) {
		LinkedList<DeliveryDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static DeliveryDetail fromJSON(JSONObject json) {
		return new DeliveryDetail()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setDelivery(new Delivery().setId(JsonUtils.getInteger(json, IJsonNames.DELIVERY)))
			.setLine(JsonUtils.getShort(json, IJsonNames.LINE))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setWarehouse(JsonUtils.getInteger(json, IJsonNames.WAREHOUSE))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setPrice(JsonUtils.getdouble(json, IJsonNames.PRICE))
			.setDiscount(JsonUtils.getdouble(json, IJsonNames.DISCOUNT))
			.setSalesDetail(JsonUtils.getInteger(json, IJsonNames.SALES_DETAIL))
			.setPurchaseReference(JsonUtils.getString(json, IJsonNames.PURCHASE_REFERENCE));
	}
	
	public static JSONArray toJSON(List<DeliveryDetail> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<DeliveryDetail> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(DeliveryDetail object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.DELIVERY, object.getDelivery().getId())
			.put(IJsonNames.LINE, object.getLine())
			.put(IJsonNames.ITEM, ItemJSON.toJSON(object.getItem()))
			.put(IJsonNames.DESCRIPTION, object.getDescription())
			.put(IJsonNames.WAREHOUSE, object.getWarehouse())
			.put(IJsonNames.QUANTITY, object.getQuantity())
			.put(IJsonNames.PRICE, object.getPrice())
			.put(IJsonNames.DISCOUNT, object.getDiscount())
			.put(IJsonNames.SALES_DETAIL, object.getSalesDetail())
			.put(IJsonNames.PURCHASE_REFERENCE, object.getPurchaseReference())
			.put(IJsonNames.CREATION_DATE2, object.getCreationDate())
			.put(IJsonNames.CREATION_USER2, object.getCreationUser())
			.put(IJsonNames.MODIFICATION_DATE2, object.getModificationDate())
			.put(IJsonNames.MODIFICATION_USER2, object.getModificationUser())
			;
	}
}
