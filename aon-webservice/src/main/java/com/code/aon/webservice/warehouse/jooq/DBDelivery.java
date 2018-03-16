package com.code.aon.webservice.warehouse.jooq;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.DeliveryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DBDelivery {
	
	public static JSONArray getDeliveryDetails(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> deliveryDetailFilter(domain, map, f))
			.forEach(purchaseDetail -> array.put(ToJSON.deliveryDetailToJSON(purchaseDetail)));
		return array;
	}
	
	public static JSONArray getDeliveryMovements(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login,
				f -> deliveryFilter(domain, map, f),
				f -> deliveryDetailFilter(domain, map, f),
				f -> productFilter(domain, map, f))
			.forEach(detail -> array.put(deliveryDetailFullToJSON(detail)));
		return array;
	}
    
	public static Filter deliveryFilter(Domain domain, Map<String, String[]> filterMap, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.FROM)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(date)));
		}

		if(filterMap.containsKey(MSG.TO)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.TO)[0])));
			filter = filter.and(f.getIssueTimeProperty().le(AonDateUtils.toTimestamp(date)));
		}
		
		return filter;
	}
	
    public static Filter deliveryDetailFilter(Domain domain, Map<String, String[]> filterMap, DeliveryDetailProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey("parent_id")){
			Integer delivery = Integer.parseInt(filterMap.get("parent_id")[0]);
			filter = filter.and(f.getDelivery().eq(delivery));
		}
		
		if(filterMap.containsKey(MSG.ITEM)){
			Integer id = Integer.parseInt(filterMap.get(MSG.ITEM)[0]);
			filter = filter.and(f.getItem().eq(id));
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
    
    public static Filter productFilter(Domain domain, Map<String, String[]> filterMap, ProductProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.PRODUCT)){
			Integer[] ids = Arrays.stream(filterMap.get(MSG.PRODUCT)).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
			filter = filter.and(f.getIdProperty().in(ids));
		}
		
		if(filterMap.containsKey(MSG.CATEGORY)){
			Integer[] ids = Arrays.stream(filterMap.get(MSG.CATEGORY)).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
			filter = filter.and(f.getCategoryProperty().in(ids));
		}
		
		return filter;
	}
    
    public static JSONObject deliveryDetailFullToJSON(DeliveryDetail detail){
		JSONObject json = new JSONObject();
		if(detail != null){
			json.put(MSG.ID, detail.getId());
			json.put(MSG.DOMAIN, detail.getDomain());
			json.put(MSG.DELIVERY,
					new JSONObject()
					.put(MSG.ID, detail.getDelivery().getId())
					.put(MSG.REGISTRY, new JSONObject()
										.put(MSG.ID, detail.getDelivery().getCustomer2().getId())
										.put(MSG.NAME, detail.getDelivery().getCustomer2().getName()))
					.put(MSG.SERIES, detail.getDelivery().getSeries())
					.put(MSG.NUMBER, detail.getDelivery().getNumber())
					.put(MSG.ISSUE_DATE, AonDateUtils.dateTimeFormat(detail.getDelivery().getIssueTime())));
			json.put(MSG.LINE, detail.getLine());
			json.put(MSG.ITEM, detail.getItem().getId());
			json.put(MSG.DESCRIPTION, detail.getDescription());
			json.put(MSG.QUANTITY, detail.getQuantity());
			json.put(MSG.PRICE, detail.getPrice());
			json.put(MSG.DISCOUNT_EXPR, detail.getDiscountExpression());
			
			json.put(MSG.CREATION_DATE, detail.getCreationDate());
			json.put(MSG.CREATION_USER, detail.getCreationUser());
			json.put(MSG.MODIFICATION_DATE, detail.getModificationDate());
			json.put(MSG.MODIFICATION_USER, detail.getModificationUser());
		}
		return json;
	}
    
}
