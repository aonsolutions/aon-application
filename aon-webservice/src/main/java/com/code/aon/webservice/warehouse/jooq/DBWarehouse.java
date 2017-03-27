package com.code.aon.webservice.warehouse.jooq;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class DBWarehouse {
	
	public static JSONArray getWarehouses(Domain domain,String login, Map<String, String[]> map){
	    JSONArray array = new JSONArray();
	    AON.getWarehouseStream(domain.getName(), domain.getId(), login, f ->  warehouseFilter(domain, map, f))
	    	.forEach(warehouse -> array.put(warehouseToJSON(warehouse)));
	    return array;
	}
	
    public static JSONObject getWarehouse(Domain domain,String login, Integer id){
    	Warehouse warehouse = AON.getWarehouse(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id));
    	return warehouseToJSON(warehouse);
    }

	public static Filter warehouseFilter(Domain domain, Map<String, String[]> filterMap, WarehouseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		return filter;
	}
	
	public static JSONObject warehouseToJSON(Warehouse warehouse){
		JSONObject json = new JSONObject();
		if(warehouse != null){
			json.put(MSG.ID, warehouse.getId());
			json.put(MSG.DOMAIN, warehouse.getDomain());
			json.put(MSG.NAME, warehouse.getName());
			json.put(MSG.WORKPLACE, warehouse.getWorkplace());
			json.put(MSG.DEPARTMENT, warehouse.getDepartment());
			json.put(MSG.ACTIVE, warehouse.isActive());
		}
		return json;
	}
	
	// FIXME insertElaboration
	public static JSONObject insertElaboration(Domain domain,String login, JSONObject json){
//		Integer elaborationId = json.getInt("elaboration");
//		Integer workplaceId = json.getInt("workplace");
//		Warehouse warehouse = AON.getWarehouse(domain.getName(), domain.getId(), login, f -> f.getWorkplaceProperty().eq(workplaceId));
		Elaboration elaboration = new Elaboration()
				.setSeries(json.getString("series"))
				.setNumber(0)
				.setDate(null)
				.setItem(new Item().setId(json.getInt("item")))
//				.setWarehouse(warehouse.getId())
				.setWarehouse(json.getInt("warehouse"))
				.setQuantity(json.getDouble("quantity"))
				.setStatus(ElaborationStatus.PENDING.value())
				.setComments(json.getString("comments"))
				;
		AON.insertElaboration(domain.getName(), domain.getId(), login, elaboration);
		return new JSONObject();	
	}
	    
	// FIXME updateElaboration
	public static JSONObject updateElaboration(Domain domain,String login, JSONObject json){
		Integer id = json.getInt("id");
		Integer workplaceId = json.getInt("workplace");
		Warehouse warehouse = AON.getWarehouse(domain.getName(), domain.getId(), login, f -> f.getWorkplaceProperty().eq(workplaceId));
		Elaboration elaboration = AON.getFullElaboration(domain.getName(), domain.getId(), login, id);
		if(elaboration!=null && elaboration.getId()!=null){
			elaboration.setDate(null)
			.setItem(new Item().setId(json.getInt("item")))
			.setWarehouse(warehouse.getId())
			.setQuantity(json.getDouble("quantity"))
			.setStatus(ElaborationStatus.values()[json.getInt("status")].value())
			.setComments(json.getString("comments"));
			elaboration = AON.updateElaboration(domain.getName(), domain.getId(), login, elaboration);
			return ToJSON.elaborationToJSON(elaboration);
		}
		return new JSONObject();
	}
	    
	// FIXME deleteElaboration
	public static JSONObject deleteElaboration(Domain domain,String login, JSONObject json){
		Integer id = json.getInt("id");
		Elaboration elaboration = AON.deleteElaboration(domain.getName(), domain.getId(), login, id);
		return ToJSON.elaborationToJSON(elaboration);
	}
}
