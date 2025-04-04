package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConsoleServiceAsync {

	void getSchemas(Occam occam, AsyncCallback<String[]> callback);
	void getDomains(DomainParams params, AsyncCallback<LinkedList<ConsoleDomain>> asyncCallback);
	void deleteDomain(String schema, Integer domainId, AsyncCallback<Boolean> callback);
	void changeActive(String schema, Integer domainId, boolean active, AsyncCallback<Domain> callback);
	void changeExpirationDate(String schema, Integer domainId, Date expireDate, AsyncCallback<Domain> callback);
	void switchRemoteAccess(String schema, Integer domainId, AsyncCallback<Boolean > callback);
	void availableUsers(Occam occam, Integer domainId, AsyncCallback<LinkedList<User>> callback);
	void getAonTables(AsyncCallback<String[]> asyncCallback);
	void getTableRow(ConsoleTableRow row, AsyncCallback<ConsoleTableRow> callback);
	void getTableRows(ConsoleTableRow row, AsyncCallback<LinkedList<ConsoleTableRow>> callback);
	void getTableRowMetadata(ConsoleTableRow row, AsyncCallback<ConsoleTableRow> callback);
	void update(ConsoleTableRow row, ConsoleTableField field, AsyncCallback<ConsoleTableRow> callback);
	void delete(ConsoleTableRow row, AsyncCallback<Boolean> callback);
	
	void getScopes(String schema, Integer domainId, AsyncCallback<LinkedList<Scope>> asyncCallback);
	void updateScopes(String schema, Integer domainId, Integer wrongScopeId, Integer newScopeId, AsyncCallback<String> asyncCallback);
	
	void testConnections(AsyncCallback<String> callback);
}
