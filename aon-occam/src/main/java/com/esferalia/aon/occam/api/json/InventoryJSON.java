package com.esferalia.aon.occam.api.json;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryStatus;

public class InventoryJSON {
	
	private InventoryJSON() {
		
	}

	public static Optional<Inventory> from(JSONObject json) {
		return from(json, Inventory::new );
	}
	
	public static Optional<Inventory> from(JSONObject json, Supplier<Inventory> inventory) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of( 
			inventory.get()
				.setId(JsonUtils.getInteger( json, IJsonNames.ID ))
				.setDomain(JsonUtils.getInteger( json, IJsonNames.DOMAIN ))
				.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
				.setInventoryDate(JsonUtils.getDate(json, IJsonNames.DATE))
				.setStatus(InventoryStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)).ordinal())
				.setWarehouse(JsonUtils.getInteger(json, IJsonNames.WAREHOUSE))
				.setDetails(InventoryDetailJSON.from(JsonUtils.getJSONArray(json, IJsonNames.DETAILS)))
				.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
				.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER))
				.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE))
				.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER))
		);
	}
	
	public static JSONArray to(List<Inventory> list) {
		return to(list.stream());
	}
		
	public static JSONArray to(Stream<Inventory> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(i -> array.put(to(i).get()));
		return array;
	}
	
	public static Optional<JSONObject> to(Optional<Inventory> inventory) {
		return inventory.flatMap( a -> to( a) );
	}
	
	public static Optional<JSONObject> to(Inventory inventory) {
		if (inventory == null) return Optional.empty();
		return Optional.of(
			new JSONObject()
				.put(IJsonNames.ID, inventory.getId())
				.put(IJsonNames.DOMAIN, inventory.getDomain())
				.put(IJsonNames.DESCRIPTION, inventory.getDescription())
				.put(IJsonNames.DATE, inventory.getInventoryDate())
				.put(IJsonNames.STATUS, InventoryStatus.safeValueOf(inventory.getStatus()).name())
				.put(IJsonNames.WAREHOUSE, inventory.getWarehouse())
				.put(IJsonNames.DETAILS, InventoryDetailJSON.to(inventory.getDetails()))
				.put(IJsonNames.CREATION_DATE, inventory.getCreationDate())
				.put(IJsonNames.CREATION_USER, inventory.getCreationUser())
				.put(IJsonNames.MODIFICATION_DATE, inventory.getModificationDate())
				.put(IJsonNames.MODIFICATION_USER, inventory.getModificationUser())
			);
	}
}
	
	
