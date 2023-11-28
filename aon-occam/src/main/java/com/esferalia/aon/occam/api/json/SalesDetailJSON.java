package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;

public class SalesDetailJSON {
	
	private SalesDetailJSON() {
	
	}
	
	public static List<SalesDetail> fromJSON(JSONArray json) {
		LinkedList<SalesDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static SalesDetail fromJSON(JSONObject json) {
		return new SalesDetail()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setSales(new Sales().setId(JsonUtils.getInteger(json, IJsonNames.SALES)))
			.setLine(JsonUtils.getShort(json, IJsonNames.LINE))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setPrice(JsonUtils.getdouble(json, IJsonNames.PRICE))
			.setDiscountExpression(JsonUtils.getString(json, IJsonNames.DISCOUNT))
			.setTaxes(JsonUtils.getdouble(json, IJsonNames.TAXES))
			.setStatus(SalesDetailStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setOfferDetail(JsonUtils.getInteger(json, IJsonNames.OFFER_DETAIL))
			.setDelivered(JsonUtils.getdouble(json, IJsonNames.DELIVERED))
			.setDeliveryDate(JsonUtils.getDate(json, IJsonNames.DELIVERY_DATE))
			.setCarrier(CarrierJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CARRIER)))
			.setCarrierPacking(JsonUtils.getInteger(json, IJsonNames.CARRIER_PACKING))
			;
	}
	
	public static JSONArray toJSON(List<SalesDetail> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<SalesDetail> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(SalesDetail object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.SALES, object.getSales().getId())
			.put(IJsonNames.LINE, object.getLine())
			.put(IJsonNames.ITEM, ItemJSON.toJSON(object.getItem()))
			.put(IJsonNames.DESCRIPTION, object.getDescription())
			.put(IJsonNames.QUANTITY, object.getQuantity())
			.put(IJsonNames.PRICE, object.getPrice())
			.put(IJsonNames.DISCOUNT, object.getDiscount())
			.put(IJsonNames.TAXES, object.getTaxes())
			.put(IJsonNames.STATUS, object.getStatus().getName())
			.put(IJsonNames.OFFER_DETAIL, object.getOfferDetail())
			.put(IJsonNames.DELIVERED, object.getDelivered())
			.put(IJsonNames.CARRIER, CarrierJSON.toJSON(object.getCarrier()))
			.put(IJsonNames.CARRIER_PACKING, object.getCarrierPacking())
			;
	}
}
