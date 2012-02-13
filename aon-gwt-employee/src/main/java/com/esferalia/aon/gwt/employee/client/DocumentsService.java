package com.esferalia.aon.gwt.employee.client;

import java.util.List;

import com.esferalia.aon.gwt.employee.shared.Document;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("documents")
public interface DocumentsService extends RemoteService {
	
	List<Document> getEnterpriseDocuments();
}
