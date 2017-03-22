package com.code.aon.webservice.warehouse.jooq;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;
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
}
