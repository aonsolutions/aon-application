package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;

public class InventoryDetailJSON {
	
	private InventoryDetailJSON() {
		
	}

	public static List<InventoryDetail> from(JSONArray json) {
		LinkedList<InventoryDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(from(json.getJSONObject(i)).get());
		}
 		return list;
	}
		
	public static Optional<InventoryDetail> from(JSONObject json) {
		return from(json, InventoryDetail::new );
	}
	
	public static Optional<InventoryDetail> from(JSONObject json, Supplier<InventoryDetail> inventory) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( 
			inventory.get()
				.setId(JsonUtils.getInteger( json, IJsonNames.ID ))
				.setDomain(JsonUtils.getInteger( json, IJsonNames.DOMAIN ))
				.setInventory(new Inventory().setId(JsonUtils.getInteger(json, IJsonNames.INVENTORY)))
				.setItem(new OldItem().setId(JsonUtils.getInteger(json, IJsonNames.ITEM)))
				.setRealQuantity(JsonUtils.getdouble(json, IJsonNames.REAL_QUANTITY))
				.setActualQuantity(JsonUtils.getdouble(json, IJsonNames.ACTUAL_QUANTITY))
				.setCost(JsonUtils.getdouble(json, IJsonNames.COST))
				.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
				.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
				.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
				.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
		);
	}
	
	public static JSONArray to(List<InventoryDetail> list) {
		return to(list.stream());
	}
		
	public static JSONArray to(Stream<InventoryDetail> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(i -> array.put(to(i).get()));
		return array;
	}
	
	public static Optional<JSONObject> to(Optional<InventoryDetail> detail) {
		return detail.flatMap( a -> to( a) );
	}
	
	public static Optional<JSONObject> to(InventoryDetail detail) {
		if (detail == null) return Optional.empty();

		return Optional.of(
			new JSONObject()
				.put(IJsonNames.ID, detail.getId())
				.put(IJsonNames.DOMAIN, detail.getDomain())
				.put(IJsonNames.INVENTORY, detail.getInventory().getId())
				.put(IJsonNames.ITEM, detail.getItem().getId())
				.put(IJsonNames.REAL_QUANTITY, detail.getRealQuantity())
				.put(IJsonNames.ACTUAL_QUANTITY, detail.getActualQuantity())
				.put(IJsonNames.COST, detail.getCost())
				.put(IJsonNames.CREATION_DATE, detail.getCreationDate())
				.put(IJsonNames.CREATION_USER, detail.getCreationUser())
				.put(IJsonNames.MODIFICATION_DATE, detail.getModificationDate())
				.put(IJsonNames.MODIFICATION_USER, detail.getModificationUser())
			);
	}

}


	
	
	
