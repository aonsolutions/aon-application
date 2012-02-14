package com.esferalia.aon.gwt.employee.client;

import java.util.List;

import com.esferalia.aon.gwt.employee.shared.Document;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DocumentsServiceAsync {

	void getAsHTML(Document doc, AsyncCallback<String> callback);
	void getEnterpriseDocuments(AsyncCallback<List<Document>> callback);

}
