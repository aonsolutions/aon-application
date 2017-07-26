package com.esferalia.aon.gwt.api.client.warehouse;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Warehouse extends Methods{

	public Warehouse(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getCarrierPacking(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCarrierPacking>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing" + filter, callback);
	}
	
	public void getCarrierPackingSeries(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/series", callback);
	}
	
	public void getCarrierPackingTypes(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/type", callback);
	}
	
	public void getCarrierPackingStatuses(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/status", callback);
	}
	
	public void getCarrierPackingCarriers(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/carrier", callback);
	}
	
	public void insertCarrierPacking(String requestData, AsyncCallback<JsCarrierPacking> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing", requestData, callback);
	}
	
	public void updateCarrierPacking(Integer id, String requestData, AsyncCallback<JsCarrierPacking> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/update/" + id, requestData, callback);
	}
	
	public void deleteCarrierPacking(Integer id) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/delete/" + id, "{}");
	}
	
	public void getOrders(String order, HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsOrder>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/" + order + filter, callback);
	}
	
	public void getPurchases(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsOrder>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/purchase" + filter, callback);
	}
	
	public void getPurchase(Integer id, AsyncCallback<JSON<JsOrder>> callback){
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/purchase/"+id , callback);
	}
	
	public void getPurchaseDetails(Integer id, AsyncCallback<JSON<JsOrderDetail>> callback){
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/purchase/"+id + "/detail" , callback);
	}
	
	public void updatePurchase(Integer id, String requestData, AsyncCallback<JsOrder> callback){
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/purchase/update/" + id, requestData, callback);
	}
	
	public void insertDetail(String orderType, String requestData, AsyncCallback<JsOrderDetail> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/"+ orderType +"/detail", requestData, callback);
	}
	
	public void updateDetail(String orderType, String requestData, AsyncCallback<JsOrder> callback){
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/"+ orderType +"/detail/update", requestData, callback);
	}
	
	public void deleteDetail(String orderType, String requestData, AsyncCallback<JsOrder> callback){
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/"+ orderType +"/detail/delete", requestData, callback);
	}
	
	public void getDeliveries(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsOrder>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/delivery" + filter, callback);
	}
	
	public void getDelivery(Integer id, AsyncCallback<JSON<JsOrder>> callback){
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/delivery/"+id , callback);
	}
	
	public void getDeliveryDetails(Integer id, AsyncCallback<JSON<JsOrderDetail>> callback){
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/delivery/"+id + "/detail" , callback);
	}
	
	public void updateDelivery(Integer id, String requestData, AsyncCallback<JsOrder> callback){
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/delivery/update/" + id, requestData, callback);
	}
	
	public void updateDelivery(Integer id, String requestData){
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/delivery/update/" + id, requestData);
	}
	
	public void getIncomes(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsOrder>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() +"/income" + filter, callback);
	}

	public void getDetails(Integer id, String orderType, AsyncCallback<JSON<JsOrderDetail>> callback){
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() + "/" + orderType + "/" +id + "/detail" , callback);
	}
	
	public void getDetails(String orderType, HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsOrderDetail>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() + "/" + orderType + "/detail" + filter , callback);
	}
	
	public void addCarrierPacking(String orderType, String requestData, AsyncCallback<JSON<JsOrderDetail>> callback){
		post(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() + "/" + orderType + "/carrier_packing" , requestData, callback);
	}
	
	public void addAllCarrierPacking(String orderType, String requestData, AsyncCallback<JSON<JsOrderDetail>> callback){
		post(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() + "/" + orderType + "/all_carrier_packing" , requestData, callback);
	}
	
	public void downloadPackingList(Integer id){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&id="+id;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_packing_list/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void downloadUdapaQuality(Integer id){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&id="+id;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_udapa_quality/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void downloadUdapaQualityList(HashMap<String, LinkedList<String>> filterMap, String type){
		String filter = filterMap.size() > 0 ? getFilter(filterMap) : "";
		String str = filter + "&domain="+ getDomainName() + "&login="+getUserName() + "&option=list&type=" + type;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_udapa_quality_list/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}

	public void sendPackingList(String requestData){
		post(getUrl() + "packing_list_notification/" + getDomainName() + "/" + getUserName()  , requestData);
	}
	
	/*
	 *  ELABORATION
	 */
	public void getElaborationList(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsElaboration>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration" + filter, callback);
	}
	public void getElaboration(Integer id, AsyncCallback<JSON<JsElaboration>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/" + id, callback);
	}
	public void getElaborationSeries(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/series", callback);
	}
	public void getElaborationStatuses(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/status", callback);
	}
	public void getElaborationDetail(Integer id, AsyncCallback<JSON<JsElaborationDetail>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/detail/" + id, callback);
	}
	public void getElaborationDetailComposition(Integer id, AsyncCallback<JSON<JsElaborationDetailComposition>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/detail_composition/" + id, callback);
	}
	public void createElaboration(AsyncCallback<JSON<JsElaboration>> callback) {
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/create", callback);
	}
	
	public void insertElaboration(String requestData, AsyncCallback<JsElaboration> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration", requestData, callback);
	}
	public void insertElaborationDetail(String requestData, AsyncCallback<JsElaborationDetail> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/detail", requestData, callback);
	}
	public void insertElaborationDetailComposition(String requestData, AsyncCallback<JsElaborationDetailComposition> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/detail_composition", requestData, callback);
	}
	
	public void updateElaboration(Integer id, String requestData, AsyncCallback<JsElaboration> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/update/"+id, requestData, callback);
	}
	public void updateElaborationDetail(Integer id, String requestData, AsyncCallback<JsElaborationDetail> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/detail/update/"+id, requestData, callback);
	}
	
	public void deleteElaboration(Integer id, String requestData, AsyncCallback<JsElaboration> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/delete/"+id, requestData, callback);
	}
	public void deleteElaborationDetail(Integer id, String requestData, AsyncCallback<JsElaborationDetail> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/detail/delete/"+id, requestData, callback);
	}
	public void deleteElaborationDetailComposition(Integer id, String requestData, AsyncCallback<JsElaborationDetailComposition> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/detail_composition/delete/"+id, requestData, callback);
	}
	
	public void downloadElaboration(Integer id){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&id="+id;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_elaboration/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	/* WAREHOUSE */
	public void getWarehouseList(AsyncCallback<JSON<JsObject>> callback){ 
		// Se usa en elaboration cambia la clase JavaScript, por lo mas es igual a getWarehouses(callback)!
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/warehouse", callback);
	}
	
	public void getWarehouses(AsyncCallback<JSON<JsWarehouse>> callback) {
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/warehouse", callback);
	}
	
	public void getWarehouse(Integer id, AsyncCallback<JSON<JsWarehouse>> callback) {
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/warehouse/" + id, callback);
	}
	
	/* INCOME */
	public void insertIncome(String requestData, AsyncCallback<JsOrder> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/income", requestData, callback);
	}
	
	public void getIncomeLastLote(String serie, AsyncCallback<JSON<JsObject>> callback) {
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/income/last_lote/"+serie, callback);
	}

	/* SALES */
	public void getSales(Integer id, AsyncCallback<JSON<JsSalesDetail>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/sales/"+id , callback);
	}
	public void getSalesDetail(Integer id, AsyncCallback<JSON<JsSalesDetail>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/sales/detail/"+id , callback);
	}
}
