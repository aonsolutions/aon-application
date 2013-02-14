package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Document;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("documents")
public interface DocumentsService extends RemoteService {
	
	String getAsHTML(Document doc, int zoom);
	List<Document> getEnterpriseDocuments();
}
