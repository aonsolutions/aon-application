package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ItemJSON {

	private ItemJSON() {
		
	}
	
	public static List<Item> fromJSON(JSONArray json) {
		LinkedList<Item> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Item fromJSON(JSONObject json) {
		if(json == null) return new Item();
		return new Item()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
				.setDetail(JsonUtils.getString(json, IJsonNames.DETAIL))
				.setDetail2(JsonUtils.getString(json, IJsonNames.DETAIL2))
				.setDetail3(JsonUtils.getString(json, IJsonNames.DETAIL3))
				.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
				.setSerialNumber(JsonUtils.getString(json, IJsonNames.SERIAL_NUMBER))
				.setSerialDate(JsonUtils.getDate(json, IJsonNames.SERIAL_DATE))
				.setBarcode(AonStringUtils.isBlank(JsonUtils.getString(json, IJsonNames.BARCODE))
						? null : JsonUtils.getString(json, IJsonNames.BARCODE))
	
				.setStatus(ProductStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
				.setProduct(ProductJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PRODUCT)))
				.setPrice(JsonUtils.getdouble(json, IJsonNames.PRICE))
				.setExpensesPercent(JsonUtils.getdouble(json, IJsonNames.EXPENSES_PERCENT))
				.setProfitPercent(JsonUtils.getdouble(json, IJsonNames.PROFIT_PERCENT))
				.setPurchasePrice(JsonUtils.getdouble(json, IJsonNames.PURCHASE_PRICE))
				.setExpensesFixed(JsonUtils.getdouble(json, IJsonNames.EXPENSES_FIXED))

				.setInternet(JsonUtils.getboolean(json, IJsonNames.INTERNET))

				.setPackFormatTag(new Tag().setId(JsonUtils.getInteger(json, IJsonNames.PACK_FORMAT_TAG)))
				.setPackUnits(JsonUtils.getInteger(json, IJsonNames.PACK_UNITS))
				.setPackUnitsTag(new Tag().setId(JsonUtils.getInteger(json, IJsonNames.PACK_UNITS_TAG)))
				.setPackMeasurement(JsonUtils.getdouble(json, IJsonNames.PACK_MEASUREMENT))
				.setPackMeasurementTag(new Tag().setId(JsonUtils.getInteger(json, IJsonNames.PACK_MEASUREMENT_TAG)))
				.setStockUnitTag(new Tag().setId(JsonUtils.getInteger(json, IJsonNames.STOCK_UNIT_TAG)))				
				.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
				.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
				.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
				.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
				
				.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED))
				
				;
	}
	
	public static JSONArray toJSON(List<Item> items) {
		return toJSON(items.stream());
	}
	
	public static JSONArray toJSON(Stream<Item> items) {
		JSONArray array = new JSONArray();
		items.forEach(item -> array.put(toJSON(item)));
		return array;
	}
	
	
	public static JSONObject toJSON(Item item) {
		if(item == null) return new JSONObject();
		return new JSONObject()
				.put(IJsonNames.ID, item.getId())
				.put(IJsonNames.DOMAIN, DomainJSON.toJSON(item.getDomain()))
				.put(IJsonNames.DETAIL, item.getDetail())
				.put(IJsonNames.DETAIL2, item.getDetail2())
				.put(IJsonNames.DETAIL3, item.getDetail3())
				.put(IJsonNames.CODE, item.getProduct().getCode())
				.put(IJsonNames.NAME, item.getProduct().getName())
				.put(IJsonNames.PRICE, item.getPrice())
				.put(IJsonNames.DESCRIPTION, item.getDescription())
				.put(IJsonNames.SERIAL_NUMBER, item.getSerialNumber())
				.put(IJsonNames.SERIAL_DATE, AonDateUtils.format(item.getSerialDate(), AonDateUtils.DATE_TIME_FORMAT_AUX))
				.put(IJsonNames.BARCODE, item.getBarcode())
				.put(IJsonNames.STATUS, item.getStatus() != null ? item.getStatus().name() : "")
				.put(IJsonNames.PRODUCT, ProductJSON.toJSON(item.getProduct()))
				.put(IJsonNames.EXPENSES_PERCENT, item.getExpensesPercent())
				.put(IJsonNames.PROFIT_PERCENT, item.getProfitPercent())
				.put(IJsonNames.PURCHASE_PRICE, item.getPurchasePrice())
				.put(IJsonNames.EXPENSES_FIXED, item.getExpensesFixed())
				.put(IJsonNames.INTERNET, item.isInternet())
				.put(IJsonNames.PACK_FORMAT_TAG, item.getPackFormatTag().getId())
				.put(IJsonNames.PACK_UNITS, item.getPackUnits())
				.put(IJsonNames.PACK_UNITS_TAG, item.getPackUnitsTag().getId())
				.put(IJsonNames.PACK_MEASUREMENT, item.getPackMeasurement())
				.put(IJsonNames.PACK_MEASUREMENT_TAG, item.getPackMeasurementTag().getId())
				.put(IJsonNames.STOCK_UNIT_TAG, item.getStockUnitTag().getId())
				.put(IJsonNames.CREATION_USER, item.getCreationUser())
				.put(IJsonNames.CREATION_DATE, item.getCreationDate())
				.put(IJsonNames.MODIFICATION_DATE, item.getModificationDate())
				.put(IJsonNames.MODIFICATION_USER, item.getModificationUser())
				.put(IJsonNames.ITEM_COMPOSITION, ItemCompositionJSON.toJSON(item.getItemComposition()))
				.put(IJsonNames.REMOVED, item.isRemoved())
				;
	}
}
