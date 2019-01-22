package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AgrarianAFIServiceAsync {
	
	void getParentDomainId(AsyncCallback<Integer> callback);
	
	void getDomainId(AsyncCallback<Integer> callback);
	
	void getDomainName(AsyncCallback<String> callback);

}
