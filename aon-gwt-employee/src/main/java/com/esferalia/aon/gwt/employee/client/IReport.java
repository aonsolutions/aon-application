package com.esferalia.aon.gwt.employee.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IReport {
	
	void print();

	void getAsHTML(float zoomRatio, AsyncCallback<String> callback);
	
}
