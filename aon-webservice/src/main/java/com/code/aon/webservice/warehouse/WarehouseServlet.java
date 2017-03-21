package com.code.aon.webservice.warehouse;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.code.aon.webservice.warehouse.jooq.DBIncome;
import com.code.aon.webservice.warehouse.jooq.DBPurchase;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationProperties;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "WarehouseServlet", urlPatterns = { "/warehouse/*",
													 "/aon_gwt_aio/warehouse/*"})
public class WarehouseServlet extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(WarehouseServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("Warehouse Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				if(MSG.CARRIER_PACKING.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(MSG.SERIES.equals(pathInfo[4])){
							object = getSeriesList();
						} else if(MSG.TYPE.equals(pathInfo[4])){
							object = getCarrierPackingTypeList();
						} else if(MSG.STATUS.equals(pathInfo[4])){
							object = getCarrierPackingStatusList();
						} else if(MSG.CARRIER.equals(pathInfo[4])){
							object = getCarrierList(domain, userName);
						} 
					} else {
						object = getCarrierPackingList(domain, userName, req.getParameterMap());
					}
				} else if(MSG.PURCHASE.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							//DBPurchase.getPurchaseDetails(domain, login, req.getParameterMap())
							object = DBPurchase.getPurchaseDetailList(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = DBPurchase.getPurchase(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = DBPurchase.getPurchaseList(domain, userName, req.getParameterMap());
				} else if(MSG.DELIVERY.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							object = getDeliveryDetailList(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = getDelivery(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = getDeliveryList(domain, userName, req.getParameterMap());
				} else if(MSG.INCOME.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							object = DBIncome.getIncomeDetails(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = DBIncome.getIncome(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = DBIncome.getIncomes(domain, userName, req.getParameterMap());
				} else if(MSG.ELABORATION.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(MSG.STATUS.equals(pathInfo[4])){
							object = getElaborationStatusList();
						} else if(MSG.DETAIL.equals(pathInfo[4])){
							object = getElaborationDetailList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(MSG.DETAIL_COMPOSITION.equals(pathInfo[4])){
							object = getElaborationDetailCompositionList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else {
							object = getElaboration(domain, userName, Integer.parseInt(pathInfo[4]));
						}
					} else object = getElaborationList(domain, userName, req.getParameterMap());
				}
				
				Utils.giveBack(req, resp, object, new JSONObject());
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Warehouse Servlet - POST METHOD");
		
		JSONObject json = Utils.getRequestJSON(req);

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			if(MSG.CARRIER_PACKING.equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					if(MSG.UPDATE.equals(pathInfo[4])){
						object = updateCarrierPacking(domain, userName, Integer.parseInt(pathInfo[5]), json);
					} else if(MSG.DELETE.equals(pathInfo[4])){
						AON.deleteCarrierPacking(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[5]));
					} 
				} else {
					object = insertCarrierPacking(domain, userName, json);
				}	
			}
			else if(MSG.PURCHASE.equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					if(MSG.UPDATE.equals(pathInfo[4])){
						object = updatePurchase(domain, userName, Integer.parseInt(pathInfo[5]), json);
					} else if(MSG.CARRIER_PACKING.equals(pathInfo[4])){
						object = updatePurhcaseCarrierPacking(domain, userName, json);
					} else if("all_carrier_packing".equals(pathInfo[4])){
						object = updateAllPurhcaseCarrierPacking(domain, userName, json);
					} 
				} 
			}
			else if(MSG.DELIVERY.equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					if(MSG.UPDATE.equals(pathInfo[4])){
						object = updateDelivery(domain, userName, Integer.parseInt(pathInfo[5]), json);
					} 
				} 
			}
			else if(MSG.ELABORATION.equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					 
				} else {
					object = insertElaboration(domain, userName, json);
				}	
			}
				
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}

	private JSONObject insertCarrierPacking(Domain domain, String login, JSONObject json) {
		CarrierPacking carrierPacking = new CarrierPacking();
		if(json.opt("action")!= null){
			Carrier carrier = AON.getCarrierStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())).findFirst().orElse(new Carrier());
			carrierPacking = new CarrierPacking()
					.setDomain(domain.getId())
					.setSeries(Integer.toString(AonDateUtils.getYear(new Date())))
					.setType(CarrierPackingType.SHIPMENT_REQUEST)
					.setStatus(CarrierPackingStatus.PENDING)
					.setIssueDate(new Date())
					.setCarrier(carrier.getId());
		}else {
			carrierPacking = getCarrierPacking(domain, login, json, new CarrierPacking());
		}
		String observation = carrierPacking.getObservation() != null ? carrierPacking.getObservation() : "";
		if(carrierPacking.getType().equals(CarrierPackingType.SHIPMENT_REQUEST)){
			carrierPacking.setComments(observation + params(domain, login, "AON_PL_SC%"));
		} else {
			carrierPacking.setComments(params(domain, login, "AON_PL_HR%"));	
		}
		Integer id = AON.insertCarrierPacking(domain.getName(), domain.getId(), login, carrierPacking);
		carrierPacking.setId(id);
		return ToJSON.carrierPackingToJSON(carrierPacking);
	}
	
	private JSONObject updateCarrierPacking(Domain domain, String login, Integer id, JSONObject json) {
		CarrierPacking carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, id);
		carrierPacking = getCarrierPacking(domain, login, json, carrierPacking);
		AON.updateCarrierPacking(domain.getName(), domain.getId(), login, carrierPacking);
		return ToJSON.carrierPackingToJSON(carrierPacking);
	}
	
	private String params(Domain domain, String login, String name) {
		StringBuilder params = new StringBuilder("<params>");
		AON.getApplicationParameterStream(domain.getName(), domain.getId(), login, f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getNameProperty().like(name)))
		.forEach(app -> {
			String[] arr = app.getName().split("_");
			String name2 = arr[arr.length -1]; 
			params.append("<param><name>" + name2 + "</name>"
					+ "<value>"+ app.getValue() + "</value></param>");
		});
		params.append("</params>");
		return params.toString();
	}
	
	private JSONObject updatePurchase(Domain domain, String login, Integer id, JSONObject json) {
		Purchase purchase = AON.getPurchase(domain.getName(), domain.getId(), login, id);
		purchase.setCarrierPacking(getOrderCarrierPacking(json));
		AON.updatePurchase(domain.getName(), domain.getId(), login, purchase);
		return ToJSON.purchaseToJSON(purchase);
	}
	
	private JSONObject updateAllPurhcaseCarrierPacking(Domain domain, String login, JSONObject json) {
		String action =json.getString(MSG.ACTION);
		if(MSG.ADD.equals(action)){
			AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, 
				f -> f.getCarrierPackingProperty().isNull()
				.and(f.getPurchaseProperty().eq(json.optInt(MSG.PURCHASE)))).forEach(
					purchaseDetail ->{
						json.put(MSG.QUANTITY, purchaseDetail.getQuantity());
						updatePurchaseCarrierPacking(domain, login, json, purchaseDetail);	
					});
		} else if(MSG.DELETE.equals(action)) {
			AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, 
				f -> f.getCarrierPackingProperty().eq(json.optInt(MSG.CARRIER_PACKING))
				.and(f.getPurchaseProperty().eq(json.optInt(MSG.PURCHASE)))).forEach(
					purchaseDetail -> updatePurchaseCarrierPacking(domain, login, json, purchaseDetail));
		}
		return new JSONObject();
	}
	
	private JSONObject updatePurhcaseCarrierPacking(Domain domain, String login, JSONObject json) {
		Integer detail = json.getInt(MSG.ID);
		PurchaseDetail purchaseDetail = AON.getPurchaseDetail(domain.getName(), domain.getId(), login, detail);
		return updatePurchaseCarrierPacking(domain, login, json, purchaseDetail);
	}
	
	private JSONObject updatePurchaseCarrierPacking(Domain domain, String login, JSONObject json, PurchaseDetail purchaseDetail){
		String action =json.getString(MSG.ACTION);
		if (MSG.ADD.equals(action)) {
			Double quantity =json.getDouble(MSG.QUANTITY);
			Integer carrierPacking = json.getInt(MSG.CARRIER_PACKING);
			if(purchaseDetail.getQuantity() > quantity){
				Double q = purchaseDetail.getQuantity() - quantity;
				AON.insertPurchaseDetail(domain.getName(), domain.getId(), login, purchaseDetail.setQuantity(q));
			} 
			AON.updatePurchaseDetail(domain.getName(), domain.getId(), login, purchaseDetail.setQuantity(quantity)
					.setCarrierPacking(carrierPacking));
		} else if(MSG.DELETE.equals(action)) {
			AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, 
					f -> f.getDomainProperty().eq(domain.getId())
					.and(f.getPurchaseProperty().eq(purchaseDetail.getPurchaseId()))
					.and(f.getItemProperty().eq(purchaseDetail.getItem()))
					.and(f.getCarrierPackingProperty().isNull())
					.and(f.getPriceProperty().eq(purchaseDetail.getPrice()))
					.and(f.getDiscountExpressionProperty().eq(purchaseDetail.getDiscountExpression())))
			.forEach(pd -> {
				purchaseDetail.setQuantity(purchaseDetail.getQuantity() + pd.getQuantity());
				AON.deletePurchaseDetail(domain.getName(), domain.getId(), login, pd.getId());
			});
			AON.updatePurchaseDetail(domain.getName(), domain.getId(), login, purchaseDetail.setCarrierPacking(null));
		}
		return ToJSON.purchaseDetailToJSON(purchaseDetail);
	}
	
	
	private JSONObject updateDelivery(Domain domain, String login, Integer id, JSONObject json) {
		Delivery delivery = AON.getDelivery(domain.getName(), domain.getId(), login, id);
		if(json.opt(MSG.CARRIER_PACKING) != null){
			delivery.setCarrierPacking(getOrderCarrierPacking(json));
		}
		if(json.opt("total_packages") != null){
			delivery.setTotalPackages(json.getDouble("total_packages"));
		}
		if(json.opt("total_weight") != null){
			delivery.setTotalWeight(json.getDouble("total_weight"));
		}
		AON.updateDelivery(domain.getName(), domain.getId(), login, delivery);
		return ToJSON.deliveryToJSON(delivery);
	}
	
	private JSONArray getCarrierList(Domain domain, String login) {
    	JSONArray array = new JSONArray();
		AON.getCarrierStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()))
		.forEach(c -> 
			array.put(ToJSON.objectToJSON(c.getId(), c.getName()))
		);
		return array;
	}
	
	private CarrierPacking getCarrierPacking(Domain domain, String login, JSONObject json, CarrierPacking carrierPacking) {
		if(json.opt(MSG.SERIES) != null){
			carrierPacking.setSeries(json.getString(MSG.SERIES));
		}
		if(json.opt(MSG.NUMBER) != null && !MSG.EMPTY.equals(json.opt(MSG.NUMBER))){
			carrierPacking.setNumber(json.getInt(MSG.NUMBER));
		}
		if(json.opt(MSG.TYPE) != null && !MSG.EMPTY.equals(json.opt(MSG.TYPE))){
			carrierPacking.setType(CarrierPackingType.values()[json.getInt(MSG.TYPE)]);
		}
		if(json.opt(MSG.STATUS) != null && !MSG.EMPTY.equals(json.opt(MSG.STATUS))){
			carrierPacking.setStatus(CarrierPackingStatus.values()[json.getInt(MSG.STATUS)]);
		}
		if(json.opt(MSG.ISSUE_DATE) != null && !MSG.EMPTY.equals(json.opt(MSG.ISSUE_DATE))){
			carrierPacking.setIssueDate(new Date(json.getLong(MSG.ISSUE_DATE)));
		}
		if(json.opt(MSG.CARRIER) != null && !MSG.EMPTY.equals(json.opt(MSG.CARRIER))){
			carrierPacking.setCarrier(json.getInt(MSG.CARRIER));
		}
		if(json.opt(MSG.DELIVERY_DATE) != null && !MSG.EMPTY.equals(json.opt(MSG.DELIVERY_DATE))){
			carrierPacking.setDeliveryDate(new Date(json.getLong(MSG.DELIVERY_DATE)));
		}
		if(json.opt(MSG.CARRIER_REFERENCE) != null){
			carrierPacking.setCarrierReference(json.getString(MSG.CARRIER_REFERENCE));
		}
		if(json.opt(MSG.NUMBER_PLATE) != null){
			carrierPacking.setNumberPlate(json.getString(MSG.NUMBER_PLATE));
		}
		if(json.opt(MSG.DRIVER_DOCUMENT) != null){
			carrierPacking.setDriverDocument(json.getString(MSG.DRIVER_DOCUMENT));
		}
		if(json.opt(MSG.DRIVER_NAME) != null){
			carrierPacking.setDriverName(json.getString(MSG.DRIVER_NAME));
		}
		if(json.opt(MSG.COMMENTS) != null){
			carrierPacking.setComments(json.getString(MSG.COMMENTS));
		}
		
		if(json.opt("params") != null){
			carrierPacking.setComments(carrierPacking.getObservation() + json.getString("params"));
		}
		if(json.opt("observation") != null){
			carrierPacking.setComments(json.getString("observation") + carrierPacking.getParams());
		}
	
		return carrierPacking;
	}
	
	private Integer getOrderCarrierPacking(JSONObject json){
		if(json.opt(MSG.CARRIER_PACKING) != null && 
			!MSG.EMPTY.equals(json.opt(MSG.CARRIER_PACKING))){
			return json.getInt(MSG.CARRIER_PACKING);
		}
		return null;
	}
	
    private JSONArray getCarrierPackingList(Domain domain, String login, Map<String, String[]> filterMap){
    	JSONArray array = new JSONArray();
    	AON.getCarrierPackingStream(domain.getName(), domain.getId(), login,
    		f -> carrierPackingFilter(domain, filterMap, f))
    	.forEach(cp -> {	
    		JSONObject json = ToJSON.carrierPackingToJSON(cp);
    		Long lines = (long) 0;
    		if(cp.getType().equals(CarrierPackingType.SHIPMENT_REQUEST)){
    			lines = AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, f -> 
    					f.getCarrierPackingProperty().eq(cp.getId())).count();
    		}else {
    			Integer[] ids  = AON.getDeliveryStream(domain.getName(), domain.getId(), login, f -> 
    					f.getCarrierPackingProperty().eq(cp.getId())).map(f -> f.getId()).toArray(Integer[]::new);
    			lines = AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> f.getDelivery().in(ids)).count();
    		}
    		json.put("lines", lines.intValue());
    		array.put(json);	
    	});
    	return array;
    }
    
    private JSONArray getCarrierPackingTypeList() {
    	JSONArray array = new JSONArray();
    	for(CarrierPackingType cpt :CarrierPackingType.values()){
    		array.put(ToJSON.objectToJSON(cpt.ordinal(), cpt.getName()));
    	}
    	return array;
    }
    
    private JSONArray getDeliveryList(Domain domain,String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getDeliveryStream(domain.getName(), domain.getId(), login, f -> deliveryFilter(domain, map, f))
    		.forEach(delivery -> array.put(ToJSON.deliveryToJSON(delivery)));
    	return array;
    }
    
    private JSONObject getDelivery(Domain domain,String login, Integer id){
    	return ToJSON.deliveryToJSON(AON.getDelivery(domain.getName(), domain.getId(), login, id));
    }
    
    private JSONArray getDeliveryDetailList(Domain domain,String login, Integer id){
    	JSONArray array = new JSONArray();
    	AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()).and(f.getDelivery().eq(id)))
    		.forEach(deliveryDetail -> array.put(ToJSON.deliveryDetailToJSON(deliveryDetail)));
    	return array;
    }
    
    private Filter carrierPackingFilter(Domain domain, Map<String, String[]> filterMap, CarrierPackingProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey(MSG.ISSUE_DATE)){
			filter = filter.and(f.getIssueDateProperty().ge(new Timestamp(Long.parseLong(filterMap.get(MSG.ISSUE_DATE)[0])))
					.or(f.getIssueDateProperty().isNull()));
		}
		
		if(filterMap.containsKey(MSG.DELIVERY_DATE)){
			filter = filter.and(f.getDeliveryDateProperty().ge(new Timestamp(Long.parseLong(filterMap.get(MSG.DELIVERY_DATE)[0])))
					.or(f.getDeliveryDateProperty().isNull()));	
		}
		
		if(filterMap.containsKey(MSG.SERIES)){
			Filter fseries = f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[0]); 
			for(Integer i = 1; i < filterMap.get(MSG.SERIES).length ; i++){
				fseries = fseries.or(f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[i]));
			}
			filter = filter.and(fseries);
		}
		
		if(filterMap.containsKey(MSG.CARRIER)){
			Filter fcarrier = f.getCarrierProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.CARRIER).length ; i++){
				fcarrier = fcarrier.or(f.getCarrierProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER)[i])));
			}
			filter = filter.and(fcarrier);
		}
		
		if(filterMap.containsKey(MSG.TYPE)){
			Filter ftype = f.getTypeProperty().eq((byte) Integer.parseInt(filterMap.get(MSG.TYPE)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.TYPE).length ; i++){
				ftype = ftype.or(f.getTypeProperty().eq((byte) Integer.parseInt(filterMap.get(MSG.TYPE)[i])));
			}
			filter = filter.and(ftype);
		}
		
		if(filterMap.containsKey(MSG.STATUS)){
			Filter fstatus = f.getStatusProperty().eq((byte) Integer.parseInt(filterMap.get(MSG.STATUS)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.STATUS).length ; i++){
				fstatus = fstatus.or(f.getStatusProperty().eq((byte) Integer.parseInt(filterMap.get(MSG.STATUS)[i])));
			}
			filter = filter.and(fstatus);
		}
		
		if(filterMap.containsKey("text") && !"".equals(filterMap.get("text")[0])){
			Filter ftext = f.getNumberPlateProperty().like("%" + filterMap.get("text")[0] + "%");
			ftext = ftext.or(f.getDriverNameProperty().like("%" + filterMap.get("text")[0] + "%"));
			ftext = ftext.or(f.getDriverDocumentProperty().like("%" + filterMap.get("text")[0] + "%"));
			ftext = ftext.or(f.getCarrierReferenceProperty().like("%" + filterMap.get("text")[0] + "%"));
			ftext = filter = filter.and(ftext);
		}

		return filter;
    }
    
    private Filter deliveryFilter(Domain domain, Map<String, String[]> filterMap, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
			
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
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		
		if(filterMap.containsKey(MSG.NOT_CARRIER_PACKING)){
			filter = filter.and(f.getCarrierPackingProperty().isNull());
		}
		return filter;
    }
    
    private JSONArray getCarrierPackingStatusList() {
    	JSONArray array = new JSONArray();
    	for(CarrierPackingStatus cps :CarrierPackingStatus.values()){
    		array.put(ToJSON.objectToJSON(cps.ordinal(), cps.getName()));
    	}
    	return array;
    }
    
    private JSONArray getSeriesList() {
    	JSONArray array = new JSONArray();
    	array.put(ToJSON.objectToJSON(2017, "2017"));
    	array.put(ToJSON.objectToJSON(2016, "2016"));
    	array.put(ToJSON.objectToJSON(2015, "2015"));
    	return array;
    }
    
    private JSONArray getElaborationList(Domain domain,String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getFullElaborationList(domain.getName(), domain.getId(), login, f -> elaborationFilter(domain, map, f))
    		.forEach(elaboration -> array.put(ToJSON.elaborationToJSON(elaboration)));
    	return array;
    }
    
    private JSONArray getElaborationStatusList() {
    	JSONArray array = new JSONArray();
    	for(ElaborationStatus status: ElaborationStatus.values()){
    		array.put(ToJSON.objectToJSON(status.ordinal(), status.getName()));
    	}
    	return array;
    }
    
    private JSONObject getElaboration(Domain domain,String login, Integer id){
    	return ToJSON.elaborationToJSON(AON.getFullElaboration(domain.getName(), domain.getId(), login, id));
    }
    
    private JSONObject insertElaboration(Domain domain, String login, JSONObject json) {
    	Elaboration elaboration = getElaboration(domain, login, json, new Elaboration());
		Integer id = AON.insertElaboration(domain.getName(), domain.getId(), login, elaboration);
		elaboration.setId(id);
		return ToJSON.elaborationToJSON(elaboration);
	}
    
    private JSONArray getElaborationDetailList(Domain domain,String login, Integer elaboration){
    	JSONArray array = new JSONArray();
    	AON.getElaborationDetailList(domain.getName(), domain.getId(), login, elaboration)
    		.forEach(detail -> array.put(ToJSON.elaborationDetailToJSON(detail)));
    	return array;
    }
    
    private JSONArray getElaborationDetailCompositionList(Domain domain,String login, Integer elaborationDetail){
    	JSONArray array = new JSONArray();
    	AON.getElaborationDetailCompositionList(domain.getName(), domain.getId(), login, elaborationDetail)
    		.forEach(composition -> array.put(ToJSON.elaborationDetailCompositionToJSON(composition)));
    	return array;
    }
    
    private Elaboration getElaboration(Domain domain, String login, JSONObject json, Elaboration elaboration) {
		if(json.opt(MSG.SERIES) != null){
			elaboration.setSeries(json.getString(MSG.SERIES));
		}
		if(json.opt(MSG.NUMBER) != null && !MSG.EMPTY.equals(json.opt(MSG.NUMBER))){
			elaboration.setNumber(json.getInt(MSG.NUMBER));
		}
		if(json.opt(MSG.DATE) != null && !MSG.EMPTY.equals(json.opt(MSG.DATE))){
			elaboration.setDate(new Date(json.getLong(MSG.DATE)));
		}
		if(json.opt(MSG.ITEM) != null && !MSG.EMPTY.equals(json.opt(MSG.ITEM))){
			elaboration.setItem(new Item().setId(json.getInt(MSG.ITEM)));
		}
		if(json.opt(MSG.QUANTITY) != null && !MSG.EMPTY.equals(json.opt(MSG.QUANTITY))){
			elaboration.setQuantity(json.getDouble(MSG.QUANTITY));
		}
		if(json.opt(MSG.WAREHOUSE) != null && !MSG.EMPTY.equals(json.opt(MSG.WAREHOUSE))){
			elaboration.setWarehouse(json.getInt(MSG.WAREHOUSE));
		}
		if(json.opt(MSG.STATUS) != null && !MSG.EMPTY.equals(json.opt(MSG.STATUS))){
			elaboration.setStatus(ElaborationStatus.values()[json.getInt(MSG.STATUS)].value());
		}
		if(json.opt(MSG.COMMENTS) != null){
			elaboration.setComments(json.getString(MSG.COMMENTS));
		}
		
		return elaboration;
	}
    
    private Filter elaborationFilter(Domain domain, Map<String, String[]> filterMap, ElaborationProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey(MSG.DATE)){
			filter = filter.and(f.getDateProperty().ge(new Timestamp(Long.parseLong(filterMap.get(MSG.DATE)[0])))
					.or(f.getDateProperty().isNull()));
		}
		
		if(filterMap.containsKey(MSG.SERIES)){
			Filter fseries = f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[0]); 
			for(Integer i = 1; i < filterMap.get(MSG.SERIES).length ; i++){
				fseries = fseries.or(f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[i]));
			}
			filter = filter.and(fseries);
		}
		
		if(filterMap.containsKey(MSG.ITEM)){
			Filter fitem = f.getItemProperty().eq(Integer.parseInt(filterMap.get(MSG.ITEM)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.ITEM).length ; i++){
				fitem = fitem.or(f.getItemProperty().eq(Integer.parseInt(filterMap.get(MSG.ITEM)[i])));
			}
			filter = filter.and(fitem);
		}
		
		if(filterMap.containsKey(MSG.QUANTITY)){
			Filter ftype = f.getQuantityProperty().eq(Double.parseDouble(filterMap.get(MSG.QUANTITY)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.QUANTITY).length ; i++){
				ftype = ftype.or(f.getQuantityProperty().eq(Double.parseDouble(filterMap.get(MSG.QUANTITY)[i])));
			}
			filter = filter.and(ftype);
		}
		
		if(filterMap.containsKey(MSG.STATUS)){
			Filter fstatus = f.getStatusProperty().eq((byte) Integer.parseInt(filterMap.get(MSG.STATUS)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.STATUS).length ; i++){
				fstatus = fstatus.or(f.getStatusProperty().eq((byte) Integer.parseInt(filterMap.get(MSG.STATUS)[i])));
			}
			filter = filter.and(fstatus);
		}
		
		if(filterMap.containsKey(MSG.WAREHOUSE)){
			Filter fwh = f.getWarehouseProperty().eq(Integer.parseInt(filterMap.get(MSG.WAREHOUSE)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.WAREHOUSE).length ; i++){
				fwh = fwh.or(f.getWarehouseProperty().eq(Integer.parseInt(filterMap.get(MSG.WAREHOUSE)[i])));
			}
			filter = filter.and(fwh);
		}

		return filter;
    }
  
}