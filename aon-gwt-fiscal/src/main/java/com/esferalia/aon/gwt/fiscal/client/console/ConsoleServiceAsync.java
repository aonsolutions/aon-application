package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConsoleServiceAsync {

	void getSchemas(Occam occam, AsyncCallback<String[]> callback);
	void getDomains(DomainParams params, AsyncCallback<LinkedList<ConsoleDomain>> asyncCallback);
	void deleteDomain(DomainParams params, Integer domainId, AsyncCallback<Boolean> callback);
	void changeActive(DomainParams params, Integer domainId, boolean active, AsyncCallback<Domain> callback);
	void changeExpirationDate(DomainParams params, Integer domainId, Date expireDate, AsyncCallback<Domain> callback);
	void remoteAccess(DomainParams params, Integer domainId, AsyncCallback<String> callback);
	void fix(ConsoleDomainMessage consoleMessage, AsyncCallback<Boolean> callback);
	void viewRow(String schema, String tableName, Integer id, AsyncCallback<LinkedHashMap<String, Object>> callback);

}
