package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Warehouse extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Warehouse(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
	}
	
	public void getCarrierPacking(AsyncCallback<JSON<JsCarrierPacking>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing", callback);
	}
	
	public void getCarrierPackingSeries(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/series", callback);
	}
	
	public void getCarrierPackingTypes(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/type/", callback);
	}
	
	public void getCarrierPackingStatuses(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/status", callback);
	}
	
	public void getCarrierPackingCarriers(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/carrier", callback);
	}
	
	public void insertCarrierPacking(String requestData, AsyncCallback<JSON<JsCarrierPacking>> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing", requestData, callback);
	}
	
	public void updateCarrierPacking(Integer id, String requestData, AsyncCallback<JSON<JsCarrierPacking>> callback) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/update/" + id, requestData, callback);
	}
	
	public void deleteCarrierPacking(Integer id) {
		post(getUrl() + "warehouse/"+getDomainName()+"/"+getUserName()+"/carrier_packing/delete/" + id, "{}");
	}
}
