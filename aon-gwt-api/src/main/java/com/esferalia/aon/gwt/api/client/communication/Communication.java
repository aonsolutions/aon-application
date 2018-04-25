package com.esferalia.aon.gwt.api.client.communication;

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

public class Communication extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Communication(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	
	public void getSummary(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsSummary>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/summary"+filter, callback);
	}
	
	public void getHistory(AsyncCallback<JSON<JsDataResponse>> callback){
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/history", callback);
	}
	
	public void getHistoryDetail(Integer id, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/history_detail?id="+id, callback);
	}
	
	
	/*
	 * SERES
	 * 
	 */

	public void getOutcomeDelivery(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCommunicationInfo>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/outcome_delivery"+filter, callback);
	}

	public void getOutcomeInvoice(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCommunicationInfo>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/outcome_invoice"+filter, callback);
	}

	public void getIncomeSales(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCommunicationInfo>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/income_sales"+filter, callback);
	}

	public void getIncomeInvoice(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCommunicationInfo>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/income_invoice"+filter, callback);
	}
	
	public void sendInvoices(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/ftp/outcome_invoice" + str, callback);
	}
	
	public void sendDeliveries(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/ftp/outcome_delivery" + str, callback);
	}
	
	public void retrieveInvoices(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/ftp/income_invoice" + str, callback);
	}
	
	public void retrieveSales(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/seres/ftp/income_sales" + str, callback);
	}
	
	/*
	 * INGENET
	 * 
	 */
	public void getIngenetDelivery(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsAttachFile>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/ingenet/delivery"+filter, callback);
	}
	
	public void getIngenetSales(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCommunicationInfo>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/ingenet/sales"+filter, callback);
	}
	
	public void reopenIngenetSales(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		post(getUrl() + "communication/"+getDomainName()+"/"+getUserName()+"/ingenet/sales/reopen"+str, str, callback);
	}
	
	public void processIngenetAttach(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "ingenet_attach/"+getDomainName()+"/"+getUserName()+"/delivery" + str, callback);
	}
	
	
	
}
