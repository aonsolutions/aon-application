package com.code.aon.webservice.warehouse.jooq;

import java.util.Map;

import org.json.JSONArray;

import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.DeliveryDetailProperties;

public class DBDelivery {
	
	public static JSONArray getDeliveryDetails(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> deliveryDetailFilter(domain, map, f))
			.forEach(purchaseDetail -> array.put(ToJSON.deliveryDetailToJSON(purchaseDetail)));
		return array;
	}
    
    public static Filter deliveryDetailFilter(Domain domain, Map<String, String[]> filterMap, DeliveryDetailProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey("parent_id")){
			Integer delivery = Integer.parseInt(filterMap.get("parent_id")[0]);
			filter = filter.and(f.getDelivery().eq(delivery));
		}
		
		return filter;
    }
    
}
