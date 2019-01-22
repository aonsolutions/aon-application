package com.esferalia.aon.gwt.api.client.commercial;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Commission extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Commission(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getOfferCalculatedCommission(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCommission>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "commission/" + getDomainName() + "/" + getUserName() + "/calculated/offer" + filter, callback);
	}
	
	public void getInvoiceCalculatedCommission(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCommission>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "commission/" + getDomainName() + "/" + getUserName() + "/calculated/invoice" + filter, callback);
	}
	
	public void updateOfferCommissionCalculate(String requestData){
		post(getUrl()+ "commission/"+ getDomainName() + "/" + getUserName() + "/calculated/offer", requestData);
	}
	
	public void updateInvoiceCommissionCalculate(String requestData){
		post(getUrl()+ "commission/" + getDomainName() + "/" + getUserName() + "/calculated/invoice", requestData);
	}
	
	// aon-ui-commercial
	public void offerCommissionCalculate(String requestData){
		post(getUrl()+ "commission_calculation/offer", requestData);
	}
	
	public void invoiceCommissionCalculate(String requestData){
		post(getUrl()+ "commission_calculation/invoice", requestData);
	}
	
	public void deleteInvoiceCommission(String requestData, AsyncCallback<JSON<JsCommission>> callback) {
		post(getUrl() + "commission/" + getDomainName() + "/" + getUserName() + "/delete/invoice", requestData, callback);
	}
	
	public void deleteOfferCommission(String requestData, AsyncCallback<JSON<JsCommission>> callback) {
		post(getUrl() + "commission/" + getDomainName() + "/" + getUserName() + "/delete/offer", requestData, callback);
	}
	
}
