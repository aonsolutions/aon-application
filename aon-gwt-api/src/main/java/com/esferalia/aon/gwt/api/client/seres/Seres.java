package com.esferalia.aon.gwt.api.client.seres;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.warehouse.JsDelivery;
import com.esferalia.aon.gwt.api.client.warehouse.JsSales;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Seres extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Seres(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	
	public void getSummary(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsSummary>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/summary"+filter, callback);
	}

	public void getOutcomeDelivery(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDelivery>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/outcome_delivery"+filter, callback);
	}

	public void getOutcomeInvoice(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsInvoice>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/outcome_invoice"+filter, callback);
	}

	public void getIncomeSalesDelivery(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsSales>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/income_sales"+filter, callback);
	}

	public void getIncomeInvoice(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsInvoice>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/income_invoice"+filter, callback);
	}
	
	public void getIngenetDelivery(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDelivery>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/ingenet_delivery"+filter, callback);
	}
	
	public void getHistory(AsyncCallback<JSON<JsDataResponse>> callback){
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/history", callback);
	}
	
	public void getHistoryDetail(Integer id, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/history_detail?id="+id, callback);
	}
	
	public void sendSeres(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = getFilter(filterMap) + "&domain="+ getDomainName() + "&login="+getUserName();
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				get(getUrl() + "seres/" + result, callback);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
}
