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
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.PurchaseDetailProperties;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DBPurchase {
	 
	public static JSONArray getPurchases(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		AON.getPurchaseStream(domain.getName(), domain.getId(), login, f -> purchaseFilter(domain, map, f))
			.forEach(purchase -> array.put(ToJSON.purchaseToJSON(purchase)));
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
	    	.forEach(purchaseDetail -> array.put(ToJSON.purchaseDetailToJSON(purchaseDetail)));
	    return array;
	}
	
	public static JSONArray getPurchaseDetails(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, f -> purchaseDetailFilter(domain, map, f))
			.forEach(purchaseDetail -> array.put(ToJSON.purchaseDetailToJSON(purchaseDetail)));
		return array;
	}
	
	@Deprecated
	public static JSONArray getPurchaseDetailList(Domain domain,String login, Integer id){
	   	JSONArray array = new JSONArray();
	   	AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()).and(f.getPurchaseProperty().eq(id)))
	   		.forEach(purchaseDetail -> array.put(ToJSON.purchaseDetailToJSON(purchaseDetail)));
	  	return array;
	}
	
    public static Filter purchaseFilter(Domain domain, Map<String, String[]> filterMap, PurchaseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentTypeProperty().ne(PurchaseType.MANUFACTURE.value()));
		
		if(filterMap.containsKey(MSG.SUPPLIER)){
			
		}
		
		if(filterMap.containsKey(MSG.REGISTRY)){
			Filter fRegistry = f.getRegistryNameProperty().like("%" + filterMap.get(MSG.REGISTRY)[0] + "%")
					.or(f.getRegistryDocumentProperty().like("%" + filterMap.get(MSG.REGISTRY)[0] + "%"));
			filter = filter.and(fRegistry);
		}
		
		if(filterMap.containsKey(MSG.SERIES)){
			filter = filter.and(f.getSeriesProperty().like("%" + filterMap.get(MSG.SERIES)[0] + "%"));
		}
		
		if(filterMap.containsKey(MSG.NUMBER)){
			Integer number = Integer.parseInt(filterMap.get(MSG.NUMBER)[0]);
			filter = filter.and(f.getNumberProperty().eq(number));
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
    
    public static Filter purchaseDetailFilter(Domain domain, Map<String, String[]> filterMap, PurchaseDetailProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey(MSG.PURCHASE)){
			Integer purchase = Integer.parseInt(filterMap.get(MSG.PURCHASE)[0]);
			filter = filter.and(f.getPurchaseProperty().eq(purchase));
		}
		
		if(filterMap.containsKey("parent_id")){
			Integer purchase = Integer.parseInt(filterMap.get("parent_id")[0]);
			filter = filter.and(f.getPurchaseProperty().eq(purchase));
		}
		
		if(filterMap.containsKey(MSG.CARRIER_PACKING)){
			if("null".equalsIgnoreCase(filterMap.get(MSG.CARRIER_PACKING)[0])){
				filter = filter.and(f.getCarrierPackingProperty().isNull());
			} else {
				Integer carrierPacking = Integer.parseInt(filterMap.get(MSG.CARRIER_PACKING)[0]);
				filter = filter.and(f.getCarrierPackingProperty().eq(carrierPacking));
			}
		} 
		
		if(filterMap.containsKey(MSG.SUPPLIER)){
			Integer supplier = Integer.parseInt(filterMap.get(MSG.SUPPLIER)[0]);
			filter = filter.and(f.getSupplierProperty().eq(supplier));
		} 
		
		return filter;
    }

    public static JSONObject insertPurchaseDetail(Domain domain,String login, JSONObject json){
    	// TODO  updatePurchaseDetail(domain, login, map)
    	return new JSONObject();
    }
    
    public static JSONObject updatePurchaseDetail(Domain domain,String login, JSONObject json){
    	// TODO HACER EL MÉTODO PARA TODOS LOS CASOS!!!!! 
    	Integer id = json.getInt("id");
    	Double delivered = json.getDouble("delivered");
    	Boolean saldar = json.getBoolean("saldar");
    	PurchaseDetail purchaseDetail = AON.getPurchaseDetail(domain.getName(), domain.getId(), login, id);
    	purchaseDetail.setDelivered(purchaseDetail.getDelivered() + delivered);
    	if(saldar || (purchaseDetail.getQuantity() - purchaseDetail.getDelivered() < 0)){
    		purchaseDetail.setQuantity(purchaseDetail.getDelivered());
    	}
    	if(purchaseDetail.getDelivered() == 0){
    		purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
    	} else if(purchaseDetail.getDelivered() < purchaseDetail.getQuantity()){
    		purchaseDetail.setStatus(PurchaseDetailStatus.PARTIAL_SETTLED);
    	} else purchaseDetail.setStatus(PurchaseDetailStatus.SETTLED);
    	purchaseDetail = AON.updatePurchaseDetail(domain.getName(), domain.getId(), login, purchaseDetail);
    	return ToJSON.purchaseDetailToJSON(purchaseDetail);
    }
    
    public static JSONObject deletePurchaseDetail(Domain domain,String login, JSONObject json){
    	// TODO  updatePurchaseDetail(domain, login, map)
    	return new JSONObject();
    }
    
}
