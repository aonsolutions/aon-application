package com.esferalia.aon.gwt.api.client.warehouse;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Warehouse extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Warehouse(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
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

	public void getDetails(Integer id, String orderType, AsyncCallback<JSON<JsOrderDetail>> callback){
		get(getUrl() + "warehouse/" + getDomainName() + "/" + getUserName() + "/" + orderType + "/" +id + "/detail" , callback);
	}
	
}
