package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDocument {
	
	void print();

	void download();
	
	void download(String format);
	
	String [] getSupportedFormats();

	void getAsHTML(int zoom, AsyncCallback<String> callback);
	
}
