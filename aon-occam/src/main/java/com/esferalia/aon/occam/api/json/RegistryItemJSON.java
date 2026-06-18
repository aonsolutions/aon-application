package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryItemJSON {

	private RegistryItemJSON() {
		
	}
	
	public static List<RegistryItem> fromJSON(JSONArray json) {
		LinkedList<RegistryItem> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistryItem fromJSON(JSONObject json) {
		if(json == null) return new RegistryItem();
		RegistryItem ritem = new RegistryItem()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
				.setCode(JsonUtils.getString(json, IJsonNames.CODE))
				.setPrice(JsonUtils.getdouble(json, IJsonNames.PRICE))
				.setDiscountExpression(JsonUtils.getString(json, "discount_expr"))
				.setWorkplace(JsonUtils.getInteger(json, IJsonNames.WORKPLACE))
				.setQuantity(JsonUtils.getString(json, IJsonNames.QUANTITY))
				.setStartDate(JsonUtils.getDate(json, IJsonNames.START_DATE2))
				.setEndDate(JsonUtils.getDate(json, IJsonNames.END_DATE2))
				.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
				.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
				.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
				.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
				.setPriority(Priority.safeValueOf(JsonUtils.getString(json, IJsonNames.PRIORITY)));

				String type = JsonUtils.getString(json, IJsonNames.TYPE);
				if (AonStringUtils.isNotBlank(type)) {
					ritem.setType(RegistryMode.valueOf(type));
				}
				String ritemStatus = JsonUtils.getString(json, IJsonNames.STATUS);
				if (AonStringUtils.isNotBlank(ritemStatus)) {
					ritem.setStatus(RegistryItemStatus.valueOf(ritemStatus));
				}
				return ritem;
	}
	
	public static JSONArray toJSON(List<RegistryItem> items) {
		return toJSON(items.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryItem> items) {
		JSONArray array = new JSONArray();
		items.forEach(item -> array.put(toJSON(item)));
		return array;
	}
	
	
	public static JSONObject toJSON(RegistryItem item) {
		if(item == null) return new JSONObject();
		return new JSONObject()
				.put(IJsonNames.ID, item.getId())
				.put(IJsonNames.DOMAIN, item.getDomain())
				.put(IJsonNames.REGISTRY, item.getRegistry())
				.put(IJsonNames.ITEM, ItemJSON.toJSON(item.getItem()))
				.put(IJsonNames.CODE, item.getCode())
				.put(IJsonNames.PRICE, item.getPrice())
				.put("discount_expr", item.getDiscountExpression().getDiscountExpr())
				.put(IJsonNames.PRIORITY, item.getPriority() != null ? item.getPriority().name() : "")
				.put(IJsonNames.WORKPLACE, item.getWorkplace())
				.put(IJsonNames.STATUS, item.getStatus() != null ? item.getStatus().name() : "")
				.put(IJsonNames.TYPE, item.getType() != null ? item.getType().name() : "")
				.put(IJsonNames.QUANTITY, item.getQuantity())
				.put(IJsonNames.START_DATE2, item.getStartDate())
				.put(IJsonNames.END_DATE2, item.getEndDate())
				.put(IJsonNames.CREATION_DATE, item.getCreationDate())
				.put(IJsonNames.CREATION_USER, item.getCreationUser())
				.put(IJsonNames.MODIFICATION_DATE, item.getModificationDate())
				.put(IJsonNames.MODIFICATION_USER, item.getModificationUser())
				.put(IJsonNames.REMOVED, item.isRemoved())
				.putOpt(item.getBookingStatus() != null ? "bookingStatus" : null, item.getBookingStatus())
				;
	}
}
