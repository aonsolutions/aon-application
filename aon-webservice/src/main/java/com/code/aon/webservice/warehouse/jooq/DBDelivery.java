package com.code.aon.webservice.warehouse.jooq;

import java.util.Map;

import org.json.JSONArray;

import com.code.aon.webservice.common.MSG;
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
		
		if(filterMap.containsKey(MSG.CARRIER_PACKING)){
			Integer[] array = AON.getDeliveryStream(domain.getName(), domain.getId(), "", h -> 
				h.getCarrierPackingProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER_PACKING)[0])))
			.map(i -> i.getId()).toArray(Integer[]::new);
			Filter fcarrierPacking = f.getDelivery().in(array); 
			filter = filter.and(fcarrierPacking);
		}
		
		return filter;
    }
    
}
