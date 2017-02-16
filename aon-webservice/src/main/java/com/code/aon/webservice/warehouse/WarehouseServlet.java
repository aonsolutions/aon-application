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
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseProperties;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
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
							object = getPurchaseDetailList(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = getPurchase(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = getPurchaseList(domain, userName, req.getParameterMap());
				} else if(MSG.DELIVERY.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(pathInfo.length > 5){
							object = getDeliveryDetailList(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = getDelivery(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = getDeliveryList(domain, userName, req.getParameterMap());
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
				
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}

	private JSONObject insertCarrierPacking(Domain domain, String login, JSONObject json) {
		CarrierPacking carrierPacking = getCarrierPacking(domain, login, json, new CarrierPacking());
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
		delivery.setCarrierPacking(getOrderCarrierPacking(json));
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
    	.forEach(cp -> array.put(ToJSON.carrierPackingToJSON(cp)));
    	return array;
    }
    
    private JSONArray getCarrierPackingTypeList() {
    	JSONArray array = new JSONArray();
    	for(CarrierPackingType cpt :CarrierPackingType.values()){
    		array.put(ToJSON.objectToJSON(cpt.ordinal(), cpt.getName()));
    	}
    	return array;
    }
    
    private JSONArray getPurchaseList(Domain domain,String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getPurchaseStream(domain.getName(), domain.getId(), login, f -> purchaseFilter(domain, map, f))
    		.forEach(purchase -> array.put(ToJSON.purchaseToJSON(purchase)));
    	return array;
    }
    
    private JSONObject getPurchase(Domain domain,String login, Integer id){
    	return ToJSON.purchaseToJSON(AON.getPurchase(domain.getName(), domain.getId(), login, id));
    }
    
    private JSONArray getPurchaseDetailList(Domain domain,String login, Integer id){
    	JSONArray array = new JSONArray();
    	AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()).and(f.getPurchaseProperty().eq(id)))
    		.forEach(purchaseDetail -> array.put(ToJSON.purchaseDetailToJSON(purchaseDetail)));
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

		return filter;
    }
    
    private Filter purchaseFilter(Domain domain, Map<String, String[]> filterMap, PurchaseProperties f) {
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
    			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(date)));
    		}
    		
    		if(filterMap.containsKey(MSG.NOT_CARRIER_PACKING)){
    			filter = filter.and(f.getCarrierPackingProperty().isNull());
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
  
}
