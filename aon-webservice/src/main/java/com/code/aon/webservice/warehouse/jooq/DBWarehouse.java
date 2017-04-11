package com.code.aon.webservice.warehouse.jooq;

import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
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
	
	public static JSONObject createElaboration(Domain domain, String login) {
		Elaboration elaboration = new Elaboration();
		elaboration.setStatus(ElaborationStatus.PENDING.value());
		elaboration.setDate(new Date());
		elaboration.setItem(new Item());
		return ToJSON.elaborationToJSON(elaboration);
	}
	
	public static JSONObject insertElaboration(Domain domain, String login, JSONObject json) {
		Elaboration elaboration = getElaboration(domain, login, json, new Elaboration());
		elaboration.setStatus(ElaborationStatus.PENDING.value());
		Integer id = AON.insertElaboration(domain.getName(), domain.getId(), login, elaboration);
		elaboration.setId(id);
		return ToJSON.elaborationToJSON(elaboration);
	}
	    
	public static JSONObject updateElaboration(Domain domain,String login, int id, JSONObject json){
		Elaboration elaboration = AON.getFullElaboration(domain.getName(), domain.getId(), login, id);
		elaboration = getElaboration(domain, login, json, elaboration);
		AON.updateElaboration(domain.getName(), domain.getId(), login, elaboration);
		return ToJSON.elaborationToJSON(elaboration);
	}
	    
	public static JSONObject deleteElaboration(Domain domain,String login, int elaborationId){
		// delete all compositions 
		Integer[] detailIds = AON.getElaborationDetailList(domain.getName(), domain.getId(), login, elaborationId).stream()
				.mapToInt(ElaborationDetail::getId).boxed().toArray(Integer[]::new);
		AON.deleteElaborationDetailComposition(domain.getName(), domain.getId(), login,
				f -> f.getElaborationDetailProperty().in(detailIds));
		// delete all details
		AON.deleteElaborationDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().in(detailIds));
		
		// delete elaboration
		AON.deleteElaboration(domain.getName(), domain.getId(), login, elaborationId);
		return new JSONObject();
	}

	public static JSONObject insertElaborationDetail(Domain domain, String login, JSONObject json) {
		ElaborationDetail detail = getElaborationDetail(domain, login, json, new ElaborationDetail());
		Elaboration elaboration = AON.getFullElaboration(domain.getName(), domain.getId(), login, detail.getElaboration().getId());
		
		Integer baseItemId = elaboration.getItem().getId();
		Item item = elaboration.getItem();
		if (json.opt(MSG.NUMBER) != null
				&& !MSG.EMPTY.equals(json.opt(MSG.NUMBER))) {
			// create serialized item
			item.setSerialNumber(json.getString(MSG.NUMBER));
			item.setSerialDate(new java.sql.Date(detail.getDate().getTime()));
			item.setBarcode(null);
			int itemId = AON.insertItem(domain.getName(), domain.getId(), login, item).getId();
			item.setId(itemId);
		}
		
		detail.setItem(item);
		Integer detailId = AON.insertElaborationDetail(domain.getName(), domain.getId(), login, detail);
		detail.setId(detailId);
		
		// create composition
		AON.getItemCompositionList(domain.getName(), domain.getId(), login, baseItemId).forEach(ic -> {
			ElaborationDetailComposition composition = new ElaborationDetailComposition();
			composition.setElaborationDetail(detail);
			composition.setItem(new Item().setId(ic.getCompositionItemId()));
			composition.setQuantity(detail.getQuantity()*ic.getQuantity());
			composition.setWarehouse(detail.getWarehouse());
			AON.insertElaborationDetailComposition(domain.getName(), domain.getId(), login, composition);
		});
		
		return ToJSON.elaborationDetailToJSON(detail);
	}
	
	public static JSONObject updateElaborationDetail(Domain domain,String login, int id, JSONObject json){
		ElaborationDetail detail = AON.getFullElaborationDetail(domain.getName(), domain.getId(), login, id);
		detail = getElaborationDetail(domain, login, json, detail);
		AON.updateElaborationDetail(domain.getName(), domain.getId(), login, detail);
		return ToJSON.elaborationDetailToJSON(detail);
	}
	
	public static JSONObject deleteElaborationDetail(Domain domain, String login, int detailId) {
		AON.deleteElaborationDetailComposition(domain.getName(), domain.getId(), login,
				f -> f.getElaborationDetailProperty().eq(detailId));
		AON.deleteElaborationDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(detailId));
		return new JSONObject();
	}
	
	private static Elaboration getElaboration(Domain domain, String login,
			JSONObject json, Elaboration elaboration) {
		if (json.opt(MSG.SERIES) != null) {
			elaboration.setSeries(json.getString(MSG.SERIES));
		}
		if (json.opt(MSG.NUMBER) != null
				&& !MSG.EMPTY.equals(json.opt(MSG.NUMBER))) {
			elaboration.setNumber(json.optInt(MSG.NUMBER, 0));
		}
		if (json.opt(MSG.DATE) != null && !MSG.EMPTY.equals(json.opt(MSG.DATE))) {
			elaboration.setDate(new Date(json.getLong(MSG.DATE)));
		}
		if (json.opt(MSG.ITEM) != null && !MSG.EMPTY.equals(json.opt(MSG.ITEM))) {
			elaboration.setItem(new Item().setId(json.getInt(MSG.ITEM)));
		}
		if (json.opt(MSG.DESCRIPTION) != null && !MSG.EMPTY.equals(json.opt(MSG.DESCRIPTION))) {
			elaboration.setDescription(json.getString(MSG.DESCRIPTION));
		}
		if (json.opt(MSG.QUANTITY) != null
				&& !MSG.EMPTY.equals(json.opt(MSG.QUANTITY))) {
			elaboration.setQuantity(json.getDouble(MSG.QUANTITY));
		}
		if (json.opt(MSG.WAREHOUSE) != null
				&& !MSG.EMPTY.equals(json.opt(MSG.WAREHOUSE))) {
			elaboration.setWarehouse(new Warehouse().setId(json
					.getInt(MSG.WAREHOUSE)));
		}
		if (json.opt(MSG.STATUS) != null
				&& !MSG.EMPTY.equals(json.opt(MSG.STATUS))) {
			elaboration.setStatus(ElaborationStatus.values()[json
					.getInt(MSG.STATUS)].value());
		}
		if (json.opt(MSG.COMMENTS) != null) {
			elaboration.setComments(json.getString(MSG.COMMENTS));
		}
		if (json.opt(MSG.REMARKS) != null) {
			elaboration.setRemarks(json.getString(MSG.REMARKS));
		}

		return elaboration;
	}

	private static ElaborationDetail getElaborationDetail(Domain domain,
			String login, JSONObject json, ElaborationDetail detail) {
		if (json.opt(MSG.ELABORATION) != null && !MSG.EMPTY.equals(json.opt(MSG.ELABORATION))) {
			detail.setElaboration(new Elaboration().setId(json.getInt(MSG.ELABORATION)));
		}
		if (json.opt(MSG.DATE) != null && !MSG.EMPTY.equals(json.opt(MSG.DATE))) {
			detail.setDate(new Date(json.getLong(MSG.DATE)));
		}
		if (json.opt(MSG.ITEM) != null && !MSG.EMPTY.equals(json.opt(MSG.ITEM))) {
			detail.setItem(new Item().setId(json.getInt(MSG.ITEM)));
		}
		if (json.opt(MSG.QUANTITY) != null
				&& !MSG.EMPTY.equals(json.opt(MSG.QUANTITY))) {
			detail.setQuantity(json.getDouble(MSG.QUANTITY));
		}
		if (json.opt(MSG.WAREHOUSE) != null
				&& !MSG.EMPTY.equals(json.opt(MSG.WAREHOUSE))) {
			detail.setWarehouse(new Warehouse().setId(json
					.getInt(MSG.WAREHOUSE)));
		}
		if (json.opt(MSG.COMMENTS) != null) {
			detail.setAddInfo(json.getString(MSG.COMMENTS));
		}

		return detail;
	}
}
