package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Document;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DocumentsServiceAsync {

	void getAsHTML(Document doc, int zoom, AsyncCallback<String> callback);
	void getEnterpriseDocuments(AsyncCallback<List<Document>> callback);

}
