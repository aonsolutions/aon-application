package com.code.aon.webservice.warehouse;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
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
import com.code.aon.webservice.warehouse.jooq.DBDelivery;
import com.code.aon.webservice.warehouse.jooq.DBIncome;
import com.code.aon.webservice.warehouse.jooq.DBPurchase;
import com.code.aon.webservice.warehouse.jooq.DBSales;
import com.code.aon.webservice.warehouse.jooq.DBWarehouse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.ElaborationProperties;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseProperties;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.impl.jooq.dao.StatDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

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
							object = getSeriesList(domain, userName);
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
						if(MSG.DETAIL.equalsIgnoreCase(pathInfo[4])){
							object = DBPurchase.getPurchaseDetails(domain, userName, req.getParameterMap());
						}else if(pathInfo.length > 5){
							object = DBPurchase.getPurchaseDetailList(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = DBPurchase.getPurchase(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = DBPurchase.getPurchases(domain, userName, req.getParameterMap());
				} else if(MSG.DELIVERY.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(MSG.DETAIL.equalsIgnoreCase(pathInfo[4])){
							object = DBDelivery.getDeliveryDetails(domain, userName, req.getParameterMap());
						} else if(pathInfo.length > 5){
							object = getDeliveryDetailList(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = getDelivery(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = getDeliveryList(domain, userName, req.getParameterMap());
				} else if(MSG.INCOME.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(MSG.DETAIL.equalsIgnoreCase(pathInfo[4])){
							object = DBIncome.getIncomeDetails(domain, userName, req.getParameterMap());
						} else if("last_lote".equalsIgnoreCase(pathInfo[4])){
							object = DBIncome.getIncomeLastLote(domain, userName, pathInfo[5]);
						} else if(pathInfo.length > 5){
							object = DBIncome.getIncomeDetails(domain, userName, Integer.parseInt(pathInfo[4]));
						} else object = DBIncome.getIncome(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = DBIncome.getIncomes(domain, userName, req.getParameterMap());
				} else if("incomeQ".equals(pathInfo[3])){
					object = DBIncome.getIncomesQ(domain, userName, req.getParameterMap());
				} else if(MSG.ELABORATION.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(MSG.SERIES.equals(pathInfo[4])){
							object = getElaborationSeriesList(domain, userName);
						} else if(MSG.STATUS.equals(pathInfo[4])){
							object = getElaborationStatusList();
						} else if(MSG.DETAIL.equals(pathInfo[4])){
							object = getElaborationDetailList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(MSG.DETAIL_COMPOSITION.equals(pathInfo[4])){
							object = getElaborationDetailCompositionList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else {
							if("create".equals(pathInfo[4])){
								object = DBWarehouse.createElaboration(domain, userName);
							} else {
								object = getElaboration(domain, userName, Integer.parseInt(pathInfo[4]));
							}
						}
					} else object = getElaborationList(domain, userName, req.getParameterMap());
				} else if("stock_forecast".equals(pathInfo[3])){
					object = getStockForecast(domain, userName, req.getParameterMap());
				} else if(MSG.WAREHOUSE.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						object = DBWarehouse.getWarehouse(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = DBWarehouse.getWarehouses(domain, userName, req.getParameterMap());
				} else if(MSG.SALES.equals(pathInfo[3])){
					if(pathInfo.length > 4){
						if(MSG.DETAIL.equals(pathInfo[4])){
							if(pathInfo.length > 5){
								object = DBSales.getSalesDetail(domain, userName, Integer.parseInt(pathInfo[5]));
							} else object = DBSales.getSalesDetails(domain, userName, req.getParameterMap());
						} else object = DBSales.getSales(domain, userName, Integer.parseInt(pathInfo[4]));
					} else object = DBSales.getSales(domain, userName, Integer.parseInt(pathInfo[4]));
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
						object = ToJSON.objectToJSON(Integer.parseInt(pathInfo[5]), "delete");
					} 
				} else {
					object = insertCarrierPacking(domain, userName, json);
				}	
			}
			else if(MSG.PURCHASE.equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					if(MSG.DETAIL.equalsIgnoreCase(pathInfo[4])){
						if(pathInfo.length > 5){
							if(MSG.UPDATE.equalsIgnoreCase(pathInfo[5])){
								object = DBPurchase.updatePurchaseDetail(domain, userName, json);
							} else if(MSG.DELETE.equalsIgnoreCase(pathInfo[5])){
								object = DBPurchase.deletePurchaseDetail(domain, userName, json);
							}
						} else DBPurchase.insertPurchaseDetail(domain, userName, json);
					}else if(MSG.UPDATE.equals(pathInfo[4])){
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
			} else if(MSG.ELABORATION.equals(pathInfo[3])) {
				if(pathInfo.length > 4){ 
					if(MSG.UPDATE.equals(pathInfo[4])){
						object = DBWarehouse.updateElaboration(domain, userName, Integer.parseInt(pathInfo[5]), json);
					} else if(MSG.DELETE.equalsIgnoreCase(pathInfo[4])){
						object = DBWarehouse.deleteElaboration(domain, userName, Integer.parseInt(pathInfo[5]));
					} else if(MSG.DETAIL.equalsIgnoreCase(pathInfo[4])){
						if(pathInfo.length > 5){
							if(MSG.UPDATE.equals(pathInfo[5])){
								object = DBWarehouse.updateElaborationDetail(domain, userName, Integer.parseInt(pathInfo[6]), json);
							} else if(MSG.DELETE.equalsIgnoreCase(pathInfo[5])){
								object = DBWarehouse.deleteElaborationDetail(domain, userName, Integer.parseInt(pathInfo[6]));
							}
						} else {
							object = DBWarehouse.insertElaborationDetail(domain, userName, json);
						}
					} else if(MSG.DETAIL_COMPOSITION.equalsIgnoreCase(pathInfo[4])){
						if(pathInfo.length > 5){
							if(MSG.UPDATE.equals(pathInfo[5])){
								
							} else if(MSG.DELETE.equalsIgnoreCase(pathInfo[5])){
								object = DBWarehouse.deleteElaborationDetailComposition(domain, userName, Integer.parseInt(pathInfo[6]));
							}
						} else {
							object = DBWarehouse.insertElaborationDetailComposition(domain, userName, json);
						}
					}
				} else {
					object = DBWarehouse.insertElaboration(domain, userName, json);
				}	
			} else if(MSG.INCOME.equals(pathInfo[3])){
				if(pathInfo.length > 4){  // TODO 
					if(MSG.DETAIL.equalsIgnoreCase(pathInfo[4])){
						if(pathInfo.length > 5){
							if(MSG.UPDATE.equalsIgnoreCase(pathInfo[5])){
								object = DBIncome.updateIncomeDetail(domain, userName, json);
							} else if(MSG.DELETE.equalsIgnoreCase(pathInfo[5])){
								object = DBIncome.deleteIncomeDetail(domain, userName, json);
							}
						} else object = DBIncome.insertIncomeDetail(domain, userName, json);
					} else if(MSG.DELETE.equalsIgnoreCase(pathInfo[4])) {
						object = DBIncome.deleteIncome(domain, userName, json);
					}
				} else object = DBIncome.insertIncome(domain, userName, json);
			} else if("update_reception_quantity".equals(pathInfo[3])) {
				object = updateReceptionQuantity(domain, userName, json);
			} else if("update_reception_detail_quantity".equals(pathInfo[3])) {
				object = updateReceptionDetailQuantity(domain, userName, json);
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
			carrierPacking.setIssueDate(AonDateUtils.addHours(new Date(json.getLong(MSG.ISSUE_DATE)),2));
		}
		if(json.opt(MSG.CARRIER) != null && !MSG.EMPTY.equals(json.opt(MSG.CARRIER))){
			carrierPacking.setCarrier(json.getInt(MSG.CARRIER));
		}
		if(json.opt(MSG.DELIVERY_DATE) != null && !MSG.EMPTY.equals(json.opt(MSG.DELIVERY_DATE))){
			carrierPacking.setDeliveryDate(AonDateUtils.addHours(new Date(json.getLong(MSG.DELIVERY_DATE)), 2));
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
		if(json.opt(MSG.PARAMS) != null){
			carrierPacking.setComments(carrierPacking.getObservation() + json.getString(MSG.PARAMS));
		}
		if(json.opt(MSG.OBSERVATION) != null){
			carrierPacking.setComments(json.getString(MSG.OBSERVATION) + carrierPacking.getParams());
		}
		if(json.opt(MSG.GROSS) != null){
			carrierPacking.setGross(json.getDouble(MSG.GROSS));
		}
		if(json.opt(MSG.TARE) != null){
			carrierPacking.setTare(json.getDouble(MSG.TARE));
		}
		if(json.opt(MSG.ADDITIONAL_TARE) != null){
			carrierPacking.setAdditionalTare(json.getDouble(MSG.ADDITIONAL_TARE));
		}
		if(json.opt(MSG.NET) != null){
			carrierPacking.setNet(json.getDouble(MSG.NET));
		}
		if(json.opt(MSG.RECEPTION_START_DATE) != null){
			carrierPacking.setReceptionStartDate(AonDateUtils.dateTimeParse(json.getString(MSG.RECEPTION_START_DATE)));
		}
		if(json.opt(MSG.RECEPTION_END_DATE) != null){
			carrierPacking.setReceptionEndDate(AonDateUtils.dateTimeParse(json.getString(MSG.RECEPTION_END_DATE)));
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
    	HashMap<Integer, JSONObject> map = new HashMap<>();
    	AON.getCarrierPackingStream(domain.getName(), domain.getId(), login,
    		f -> carrierPackingFilter(domain, filterMap, f))
    	.forEach(cp -> map.put(cp.getId(), ToJSON.carrierPackingToJSON(cp)));
    	
    	Set<Integer> keys = map.keySet();
    	HashMap<Integer, LinkedList<Integer>> map2 = new HashMap<>();
    	AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login, 
    		f -> f.getCarrierPackingProperty().in(keys.toArray(new Integer[keys.size()])))
    	.forEach(pd -> {
    		if(!map2.containsKey(pd.getCarrierPacking()))
    			map2.put(pd.getCarrierPacking(), new LinkedList<>());
    		if(!map2.get(pd.getCarrierPacking()).contains(pd.getPurchase().getSupplier())) {
    			map2.get(pd.getCarrierPacking()).add(pd.getPurchase().getSupplier());
    			map.get(pd.getCarrierPacking()).put("supplier", map.get(pd.getCarrierPacking()).get("supplier").equals("-") ?
    				pd.getPurchase().getSupplierName() : map.get(pd.getCarrierPacking()).get("supplier") + "; " + pd.getPurchase().getSupplierName());	
    			map.get(pd.getCarrierPacking()).put("supplier_array", map.get(pd.getCarrierPacking()).getJSONArray("supplier_array").put(ToJSON.objectToJSON(pd.getPurchase().getSupplier(), pd.getPurchase().getSupplierName())));

    		}
   		});  
    	
    	HashMap<Integer, LinkedList<Integer>> map3 = new HashMap<>();
    	AON.getDeliveryStream(domain.getName(), domain.getId(), login, 
        	f -> f.getCarrierPackingProperty().in(keys.toArray(new Integer[keys.size()])))
        .forEach(d -> {
    		if(!map3.containsKey(d.getCarrierPacking()))
    			map3.put(d.getCarrierPacking(), new LinkedList<>());
        	if(!map3.get(d.getCarrierPacking()).contains(d.getCustomer())) {
        		map3.get(d.getCarrierPacking()).add(d.getCustomer());
    			map.get(d.getCarrierPacking()).put("customer", map.get(d.getCarrierPacking()).get("customer").equals("-") ? 
    					d.getCustomerName() : map.get(d.getCarrierPacking()).get("customer") + "; " + d.getCustomerName());
    			map.get(d.getCarrierPacking()).put("customer_array", map.get(d.getCarrierPacking()).getJSONArray("customer_array").put(ToJSON.objectToJSON(d.getCustomer(), d.getCustomerName())));
    			
        	}
       	}); 
 
    	map.keySet().stream().forEach(cp -> array.put(map.get(cp)));
    	
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
		if(filterMap.containsKey("per_page")){
			String per_page = filterMap.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			filter.perPage(perPage);
		}
		if(filterMap.containsKey("page")){
			String page_str = filterMap.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			filter.page(page);
		}
		return filter;
    }
    
    private Filter deliveryFilter(Domain domain, Map<String, String[]> filterMap, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
			
		if(filterMap.containsKey(MSG.REGISTRY)){
			Filter fRegistry = f.getRegistryNameProperty().like("%" + filterMap.get(MSG.REGISTRY)[0] + "%")
					.or(f.getRegistryDocumentProperty().like("%" + filterMap.get(MSG.REGISTRY)[0] + "%"));
			filter = filter.and(fRegistry);
		}
		
		if(filterMap.containsKey(MSG.CUSTOMER)){
			filter = filter.and(f.getCustomerProperty().eq(Integer.parseInt(filterMap.get(MSG.CUSTOMER)[0])));
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
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(date)));
		}
		if(filterMap.containsKey(MSG.FROM)){
			filter = filter.and(f.getIssueTimeProperty().ge(new java.sql.Timestamp(Long.parseLong(filterMap.get(MSG.FROM)[0]))));
		}
		if(filterMap.containsKey(MSG.TO)){
			filter = filter.and(f.getIssueTimeProperty().le(new java.sql.Timestamp(Long.parseLong(filterMap.get(MSG.TO)[0]))));
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
    
    private JSONArray getSeriesList(Domain domain,String login) {
    	Calendar c = Calendar.getInstance();
    	Integer year = c.getWeekYear();
		JSONArray array = new JSONArray();
    	array.put(ToJSON.objectToJSON(year, year.toString()));
    	AON.getCarrierPackingSeries(domain.getName(), domain.getId(), login)
    	.forEach(series -> {
    		if(!series.equals(year.toString()))
    			array.put(ToJSON.objectToJSON(Integer.parseInt(series), series));
    	});
    	return array;
    }
    
    private JSONArray getElaborationSeriesList(Domain domain,String login) {
    	JSONArray array = new JSONArray();
    	array.put(ToJSON.objectToJSON(2017, "PV18"));
    	array.put(ToJSON.objectToJSON(2016, "S17"));
    	return array;
    }
    
    private JSONArray getElaborationList(Domain domain,String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getElaborationStream(domain.getName(), domain.getId(), login, f -> elaborationFilter(domain, map, f))
    			.forEach(elaboration -> {
    				array.put(ToJSON.elaborationToJSON(elaboration));
				});
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
    
    private Filter elaborationFilter(Domain domain, Map<String, String[]> filterMap, ElaborationProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey(MSG.DATE)){
			filter = filter.and(f.getDateProperty().ge(new Timestamp(Long.parseLong(filterMap.get(MSG.DATE)[0])))
					.or(f.getDateProperty().isNull()));
		}
		
		if(filterMap.containsKey(MSG.FROM)){
			filter = filter.and(f.getDateProperty().ge(new Timestamp(Long.parseLong(filterMap.get(MSG.FROM)[0])))
					.or(f.getDateProperty().isNull()));
		}

		if(filterMap.containsKey(MSG.TO)){
			filter = filter.and(f.getDateProperty().le(new Timestamp(Long.parseLong(filterMap.get(MSG.TO)[0])))
					.or(f.getDateProperty().isNull()));
		}
		
		if(filterMap.containsKey(MSG.SERIES)){
			Filter fseries = f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[0]); 
			for(Integer i = 1; i < filterMap.get(MSG.SERIES).length ; i++){
				fseries = fseries.or(f.getSeriesProperty().eq(filterMap.get(MSG.SERIES)[i]));
			}
			filter = filter.and(fseries);
		}
		
		if(filterMap.containsKey(MSG.NUMBER)){
			Filter fnumber = f.getNumberProperty().eq(Integer.parseInt(filterMap.get(MSG.NUMBER)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.NUMBER).length ; i++){
				fnumber = fnumber.or(f.getSeriesProperty().eq(filterMap.get(MSG.NUMBER)[i]));
			}
			filter = filter.and(fnumber);
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
		if(filterMap.containsKey("per_page")){
			String per_page = filterMap.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			filter.perPage(perPage);
		}
		if(filterMap.containsKey("page")){
			String page_str = filterMap.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			filter.page(page);
		}

		return filter;
    }
    
    /*
     * StockForecast
     */
    private JSONArray getStockForecast(Domain domain,String login, Map<String, String[]> filterMap){
    	JSONArray array = new JSONArray();
    	if(filterMap.containsKey(MSG.FROM) && filterMap.containsKey(MSG.TO)) {
    		Date from = new Date(Long.parseLong(filterMap.get(MSG.FROM)[0]));
    		Date to = new Date(Long.parseLong(filterMap.get(MSG.TO)[0]));
    		long daysCount = AonDateUtils.getDaysBetweenDates(from, to);
    		Integer accumulationDays = 1;
    		if(filterMap.containsKey(MSG.FROM) && filterMap.containsKey("accumulation_days")) {
    			accumulationDays = Integer.parseInt(filterMap.get("accumulation_days")[0]);	
    		}
    		
    		StatData<Integer, String, Double> stat = AON.getProductStat(domain.getName(), domain.getId(), login,
    				f -> productFilter(domain, filterMap, f),
					f -> invoiceFilter(domain, filterMap, f),
					f -> deliveryFilter(domain, filterMap, f),
					f -> salesFilter(domain, filterMap, f),
					f -> purchaseFilter(domain, filterMap, f));
        	
    		Integer[] productIds = stat.getMap().keySet().toArray(new Integer[stat.getMap().keySet().size()]);
        	Map<Integer, Product> productMap = new HashMap<>();
        	AON.getProductStream(domain.getName(), domain.getId(), login, f->f.getIdProperty().in(productIds)).forEach(product -> {
        		productMap.put(product.getId(), product);
        	});
    		
        	for(Integer productId: productIds) {
    			if(stat.getMap().containsKey(productId)){
    				Product product = productMap.get(productId);
    				Double quantity = new Double(stat.get(productId, StatDAO.PRODUCT_CONSUMED));
    				Double dailyQuantity = quantity / daysCount;
    				Double accumulation = dailyQuantity * accumulationDays;
    				Double stock = stat.get(productId, StatDAO.PRODUCT_STOCK)!=null?stat.get(productId, StatDAO.PRODUCT_STOCK):0.0;
    				Double pendingPurchases = stat.get(productId, StatDAO.PRODUCT_PENDING_PURCHASES)!=null?stat.get(productId, StatDAO.PRODUCT_PENDING_PURCHASES):0.0;
    				Double pendingSales = stat.get(productId, StatDAO.PRODUCT_PENDING_SALES)!=null?stat.get(productId, StatDAO.PRODUCT_PENDING_SALES):0.0;
    				Double proposal = accumulation - stock - pendingPurchases + pendingSales;
    				array.put(
    						new JSONObject()
    						.put(MSG.ID, productId)
    						.put(MSG.DOMAIN, product.getDomain())
    						.put("product_name", product.getCode()+" / "+product.getName())
    						.put(MSG.QUANTITY, quantity)
    						.put("daily_quantity", dailyQuantity)
    						.put("accumulation", accumulation)
    						.put("stock", stock)
    						.put("pending_purchases", pendingPurchases)
    						.put("pending_sales", pendingSales)
    						.put("proposal", proposal)
    						);    			
    			}
    		}
    	}
    	return array;
    }
    
	private Filter productFilter(Domain domain, Map<String, String[]> filterMap, ProductProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		filter.page(1).perPage(1000);
		if(filterMap.containsKey("category")){
			filter = filter.and(f.getCategoryProperty().eq(Integer.parseInt(filterMap.get("category")[0])));
		}
		if(filterMap.containsKey("product")){
			filter = filter.and(f.getIdProperty().eq(Integer.parseInt(filterMap.get("product")[0])));
		}
		
		return filter;
    }
	private Filter invoiceFilter(Domain domain, Map<String, String[]> filterMap, InvoiceProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		filter.page(1).perPage(1000);
		if(filterMap.containsKey(MSG.FROM)){
			filter = filter.and(f.getStartIssueDateProperty().ge(new java.sql.Date(Long.parseLong(filterMap.get(MSG.FROM)[0]))));
		}
		if(filterMap.containsKey(MSG.TO)){
			filter = filter.and(f.getEndIssueDateProperty().le(new java.sql.Date(Long.parseLong(filterMap.get(MSG.TO)[0]))));
		}
		if(filterMap.containsKey(MSG.CUSTOMER)){
			filter = filter.and(f.getRegistryProperty().eq(Integer.parseInt(filterMap.get(MSG.CUSTOMER)[0])));
		}
		return filter;
	}
	private Filter salesFilter(Domain domain, Map<String, String[]> filterMap, SalesProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		filter.page(1).perPage(1000);		
		if(filterMap.containsKey(MSG.FROM)){
			filter = filter.and(f.getIssueDateProperty().ge(new java.sql.Date(Long.parseLong(filterMap.get(MSG.FROM)[0]))));
		}
		if(filterMap.containsKey(MSG.TO)){
			filter = filter.and(f.getIssueDateProperty().le(new java.sql.Date(Long.parseLong(filterMap.get(MSG.TO)[0]))));
		}
		if(filterMap.containsKey(MSG.CUSTOMER)){
			filter = filter.and(f.getCustomerProperty().eq(Integer.parseInt(filterMap.get(MSG.CUSTOMER)[0])));
		}
		return filter;
    }
	private Filter purchaseFilter(Domain domain, Map<String, String[]> filterMap, PurchaseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		filter.page(1).perPage(1000);
		if(filterMap.containsKey(MSG.FROM)){
			filter = filter.and(f.getIssueDateProperty().ge(new java.sql.Date(Long.parseLong(filterMap.get(MSG.FROM)[0]))));
		}
		if(filterMap.containsKey(MSG.TO)){
			filter = filter.and(f.getIssueDateProperty().le(new java.sql.Date(Long.parseLong(filterMap.get(MSG.TO)[0]))));
		}
		return filter;
    }

	
    private Double total = 0.0;

    private JSONObject updateReceptionQuantity(Domain domain, String login, JSONObject json) {
    	Integer neto = json.getInt("net");
    	Integer cpId = Integer.parseInt(json.getString("carrier_packing"));
		
    	total = 0.0;
    	HashMap<Integer, IncomeDetail> map = new HashMap<>();
    	AON.getIncomeStream(domain.getName(), domain.getId(), login, f-> f.getCarrierPackingProperty().eq(cpId))
    	.forEach(income -> {
    		AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> f.getIncomeProperty().eq(income.getId()))
    		.forEach(detail -> {
    			total = total + detail.getQuantity();
    			map.put(detail.getId(), detail);
    		});
    	});
    	for (Integer key : map.keySet()) {
    		IncomeDetail id = map.get(key);
    		Double q = AonMathUtils.round((id.getQuantity()/total) * neto);
    		id.setQuantity(q);
    		AON.updateIncomeDetail(domain.getName(), domain.getId(), login, id);
    		PurchaseDetail pd = AON.getPurchaseDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id.getPurchaseDetail()));
    		pd.setQuantity(q);
    		pd.setDelivered(q);
    		AON.updatePurchaseDetail(domain.getName(), domain.getId(), login, pd);
    	}
    	return new JSONObject();
    }
    
    private JSONObject updateReceptionDetailQuantity(Domain domain, String login, JSONObject json) {
    	Double neto = Double.parseDouble(json.getString("net"));
    	Integer cpId = Integer.parseInt(json.getString("carrier_packing"));
    	Integer incomeId =  Integer.parseInt(json.getString("income"));
    	Double quantity = json.getDouble("quantity");
    
    	HashMap<Integer, IncomeDetail> map = new HashMap<>();
    	AON.getIncomeStream(domain.getName(), domain.getId(), login, f-> f.getCarrierPackingProperty().eq(cpId))
    	.forEach(income -> {
    		AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> f.getIncomeProperty().eq(income.getId()))
    		.forEach(detail -> {
    			map.put(detail.getId(), detail);
    		});
    	});
    		
    	Double oldPer = map.get(incomeId).getQuantity() / neto;
    	Double newPer = quantity / neto;
    		
    	for (Integer key : map.keySet()) {
    		if(!key.equals(incomeId)) {
    			Double op = map.get(key).getQuantity() / neto;
    			Double np = (op * (1-newPer)) / (1-oldPer); 
    	    	IncomeDetail id = map.get(key);
    	    	Double q = AonMathUtils.round(np * neto);
        		id.setQuantity(q);
        		AON.updateIncomeDetail(domain.getName(), domain.getId(), login, id);
        		PurchaseDetail pd = AON.getPurchaseDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id.getPurchaseDetail()));
        		pd.setQuantity(q);
        		pd.setDelivered(q);
        		AON.updatePurchaseDetail(domain.getName(), domain.getId(), login, pd);
    		} else {
    			AonMathUtils.round(quantity);
    			IncomeDetail id = map.get(key);
    			id.setQuantity(quantity);
        		AON.updateIncomeDetail(domain.getName(), domain.getId(), login, id);
        		PurchaseDetail pd = AON.getPurchaseDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id.getPurchaseDetail()));
        		pd.setQuantity(quantity);
        		pd.setDelivered(quantity);
        		AON.updatePurchaseDetail(domain.getName(), domain.getId(), login, pd);
    		}
    	}
		return new JSONObject();
	}
}

