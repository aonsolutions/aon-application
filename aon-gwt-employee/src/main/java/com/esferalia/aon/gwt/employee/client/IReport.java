package com.esferalia.aon.gwt.employee.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IReport {
	
	void print();

	void download();
	
	void download(String format);
	
	String [] getSupportedFormats();

	void getAsHTML(int zoom, AsyncCallback<String> callback);
	
}
