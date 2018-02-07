package com.esferalia.aon.gwt.api.client.sii;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Sii extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Sii(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getSiiHistory(AsyncCallback<JSON<JsDataResponse>> callback){
		get(getUrl() + "s11/"+getDomainName()+"/"+getUserName()+"/history", callback);
	}
	
	public void getSiiHistoryDetail(Integer id, AsyncCallback<JSON<JsInvoice>> callback){
		get(getUrl() + "s11/"+getDomainName()+"/"+getUserName()+"/historyDetail?id="+id, callback);
	}
	
	public void downloadSiiXml(Integer id, String option){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&id="+id + "&option=" + option;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_sii_xml/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
}
