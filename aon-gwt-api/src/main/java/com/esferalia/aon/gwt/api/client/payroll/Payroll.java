package com.esferalia.aon.gwt.api.client.payroll;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

public class Payroll extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Payroll(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
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
	
//	public void printRemunerationRecord(Integer year){
//		String parameters = "domain="+ getDomainName() + "&login="+getUserName() + "&year="+year;
//		Window.open(getUrl() + "remuneration_record/registro_retributivo_" + year + "?" + parameters, "_blank", null);
//	}
	
	public void printRemunerationRecord(Integer year, FlowPanel formContainer){
		String printURL = URL.encode(getUrl() + "remuneration_record/Registro_Retributivo_" + year);
		
		FormPanel formPanel = new FormPanel(/*"_blank"*/);
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(event -> {
			
			JSONObject json = new JSONObject(JsonUtils.safeEval(event.getResults()));
			String base64Excel = json.get("excel").isString().stringValue();
			
//			if (base64Excel.charAt(0) == '\"' && base64Excel.charAt(base64Excel.length() - 1) == '\"') {
//				base64Excel = base64Excel.substring(1, base64Excel.length() - 1);
//			}
//			
			
			String url = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64," + base64Excel;
			
			Window.open(url, "Registro Retributivo", "");
			formContainer.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden("year", String.valueOf(year)));
		flowPanel.add(new Hidden("domain", getDomainName()));
		flowPanel.add(new Hidden("user", getUserName()));
		
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			formContainer.remove(formPanel);
		});
		
		formContainer.add(formPanel);
		
		formPanel.submit();
		formContainer.getElement().getStyle().setDisplay(Display.BLOCK);
	}

}
