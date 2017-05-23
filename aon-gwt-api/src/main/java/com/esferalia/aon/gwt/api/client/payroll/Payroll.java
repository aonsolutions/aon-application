package com.esferalia.aon.gwt.api.client.payroll;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Payroll extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Payroll(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	// ------------------- CONTRACT
	
	public void getContractMediaList(Integer year, AsyncCallback<JSON<JsObject>> callback){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&year="+year;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				get(getUrl() + "contract/media_list/" + result, callback);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});	
	}
	
	public void printContractMediaList(Integer year){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&year="+year;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "print_contract_media_list/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});	
	}

	public void getContractMediaResume(Integer year, AsyncCallback<JSON<JsObject>> callback){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&year="+year;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				get(getUrl() + "contract/media_resume/" + result, callback);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void printContractMediaResume(Integer year){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&year="+year;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "print_contract_media_resume/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void printContractMedia(Integer year, Boolean resume, Boolean detail){
		String str = "domain="+ getDomainName() + "&login="+getUserName() + "&year="+year + "&resume=" + resume + "&detail=" + detail;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "print_contract_media/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}

}
