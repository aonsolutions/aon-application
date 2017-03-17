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

	public void sendPackingList(String requestData){
		post(getUrl() + "packing_list_notification/" + getDomainName() + "/" + getUserName()  , requestData);
	}
	
	/* ELABORATION */
	public void getElaborationList(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsElaboration>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration" + filter, callback);
	}
	public void getElaboration(Integer id, AsyncCallback<JSON<JsElaboration>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration/" + id, callback);
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
	public void insertElaboration(String requestData, AsyncCallback<JsCarrierPacking> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/elaboration", requestData, callback);
	}
}
