package com.esferalia.aon.gwt.api.client.finance;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.stat.JsStatData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Finance extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Finance(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
	}
	
	public void getBillingPeriods( AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "finance/"+getDomainName()+"/"+getUserName()+"/billing_period/", callback);
	}
	
	public void getStatDataFeeProjection(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsStatData>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "stat/"+getDomainName()+"/"+getUserName()+"/fee" + str, callback);
	}
	
	public void downloadExcelFeeProjection(HashMap<String, LinkedList<String>> filterMap){
		String str = getFilter(filterMap) + "&domain="+ getDomainName() + "&login="+getUserName() + "&type=excel";
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_fee_projection/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void downloadPdfFeeProjection(HashMap<String, LinkedList<String>> filterMap){
		String str = getFilter(filterMap) + "&domain="+ getDomainName() + "&login="+getUserName() + "&type=pdf";
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_fee_projection/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	

}
