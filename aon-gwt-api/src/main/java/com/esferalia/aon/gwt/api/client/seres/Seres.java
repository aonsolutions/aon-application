package com.esferalia.aon.gwt.api.client.seres;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
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

	public void getOutcomeDelivery(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsSeresFile>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/outcome_delivery"+filter, callback);
	}

	public void getOutcomeInvoice(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsSeresFile>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/outcome_invoice"+filter, callback);
	}

	public void getIncomeSales(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsSeresFile>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/income_sales"+filter, callback);
	}

	public void getIncomeInvoice(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsSeresFile>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/income_invoice"+filter, callback);
	}
	
	public void getIngenetDeliveryAttach(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsAttachFile>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/ingenet_delivery"+filter, callback);
	}
	
	public void getHistory(AsyncCallback<JSON<JsDataResponse>> callback){
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/history", callback);
	}
	
	public void getHistoryDetail(Integer id, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "seres/"+getDomainName()+"/"+getUserName()+"/history_detail?id="+id, callback);
	}
	
	public void processIngenetAttach(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "ingenet_attach/"+getDomainName()+"/"+getUserName()+"/delivery" + str, callback);
	}
	
	public void sendInvoices(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "seres_ftp/"+getDomainName()+"/"+getUserName()+"/outcome_invoice" + str, callback);
	}
	
	public void sendDeliveries(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "seres_ftp/"+getDomainName()+"/"+getUserName()+"/outcome_delivery", callback);
	}
	
	public void retrieveInvoices(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "seres_ftp/"+getDomainName()+"/"+getUserName()+"/income_invoice", callback);
	}
	
	public void retrieveSales(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "seres_ftp/"+getDomainName()+"/"+getUserName()+"/income_sales", callback);
	}
	
}
