package com.code.aon.webservice.warehouse.jooq;

import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.PurchaseProperties;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailProperties;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DBPurchase {
	 
	public static JSONArray getPurchases(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		AON.getPurchaseStream(domain.getName(), domain.getId(), login, f -> purchaseFilter(domain, map, f))
			.forEach(purchase -> array.put(new JSONObject(purchase.toJSON())));
		return array;
	}
	
	@Deprecated
	public static JSONArray getPurchaseList(Domain domain,String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getPurchaseStream(domain.getName(), domain.getId(), login, f -> purchaseFilter(domain, map, f))
    		.forEach(purchase -> array.put(ToJSON.purchaseToJSON(purchase)));
    	return array;
    }
	
	public static JSONObject getPurchase(Domain domain,String login, Integer id){
		return ToJSON.purchaseToJSON(AON.getPurchase(domain.getName(), domain.getId(), login, id));	
	}
	    
	public static JSONArray getPurchaseDetails(Domain domain,String login, Integer id){
	    JSONArray array = new JSONArray();
	    AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()).and(f.getPurchaseProperty().eq(id)))
	    	.forEach(purchaseDetail -> array.put(purchaseDetail.toJSON()));
	    return array;
	}
	
	public static JSONArray getPurchaseDetails(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
/*		AON.getPurchaseStream(domain.getName(), domain.getId(), login, f -> purchaseDetailFilter(domain, map, f))
			.forEach(purchase -> array.put(new JSONObject(purchase.toJSON())));
	*/	return array;
	}
	
	@Deprecated
	public static JSONArray getPurchaseDetailList(Domain domain,String login, Integer id){
	   	JSONArray array = new JSONArray();
	   	AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()).and(f.getPurchaseProperty().eq(id)))
	   		.forEach(purchaseDetail -> array.put(ToJSON.purchaseDetailToJSON(purchaseDetail)));
	  	return array;
	}
	
    public static Filter purchaseFilter(Domain domain, Map<String, String[]> filterMap, PurchaseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.SUPPLIER)){
		}
		
		if(filterMap.containsKey(MSG.CARRIER)){
			Integer carrier = Integer.parseInt(filterMap.get(MSG.CARRIER)[0]);
			filter = filter.and(f.getCarrierProperty().eq(carrier)
					.or(f.getCarrierProperty().isNull()));
		}
		
		if(filterMap.containsKey(MSG.CARRIER_PACKING)){
			Integer carrierPacking = Integer.parseInt(filterMap.get(MSG.CARRIER_PACKING)[0]);
			filter = filter.and(f.getCarrierPackingProperty().eq(carrierPacking));
		} 
		
		if(filterMap.containsKey(MSG.ISSUE_DATE)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.ISSUE_DATE)[0])));
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(date)));
		}
		
		if(filterMap.containsKey(MSG.NOT_CARRIER_PACKING)){
			filter = filter.and(f.getCarrierPackingProperty().isNull());
		}
		
		return filter;
    }
    
    public static Filter purchaseFilter(Domain domain, Map<String, String[]> filterMap, PurchaseDetailProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
			
		if(filterMap.containsKey(MSG.CARRIER)){
		
		}
		
		if(filterMap.containsKey(MSG.CARRIER_PACKING)){
			Integer carrierPacking = Integer.parseInt(filterMap.get(MSG.CARRIER_PACKING)[0]);
			filter = filter.and(f.getCarrierPackingProperty().eq(carrierPacking));
		} 
		
		if(filterMap.containsKey(MSG.ISSUE_DATE)){
	
		}
		
		if(filterMap.containsKey(MSG.NOT_CARRIER_PACKING)){
			filter = filter.and(f.getCarrierPackingProperty().isNull());
		}
		
		return filter;
    }
}
