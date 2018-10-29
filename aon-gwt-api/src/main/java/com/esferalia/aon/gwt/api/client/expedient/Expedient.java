package com.esferalia.aon.gwt.api.client.expedient;

import com.esferalia.aon.gwt.api.client.Methods;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Expedient extends Methods{
	
	String url;
	String userName;
	String domainName;
	
	
	public Expedient(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	// -------------------- DOWNLOAD
	
	public void downloadResumeExpedient(String filter){	
		String str ="domainId="+ getDomainId() + "&domainName=" + getDomainName() + "&filter=" + filter;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "print_resume_expedient/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void downloadFullExpedient(String filter){	
		String str ="domainId="+ getDomainId() + "&domainName=" + getDomainName() + "&filter=" + filter;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "print_full_expedient/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	//---------------------- Métodos Get & Set
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
