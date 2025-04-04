package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleTableField;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ConsoleServiceAsyncDecorator implements ConsoleServiceAsync {

	private ConsoleServiceAsync fsa;

	public ConsoleServiceAsyncDecorator(ConsoleServiceAsync rawdocServiceAsync) {
		this.fsa = rawdocServiceAsync;
	}
	
	@Override
	public void getSchemas(Occam occam, AsyncCallback<String[]> callback) {
		AON.start();
		fsa.getSchemas(occam, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getDomains(DomainParams params, AsyncCallback<LinkedList<ConsoleDomain>> callback) {
		AON.start();
		fsa.getDomains(params, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void deleteDomain(String schema, Integer domainId, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.deleteDomain(schema, domainId, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void changeActive(String schema, Integer domainId, boolean active, AsyncCallback<Domain> callback) {
		AON.start();
		fsa.changeActive(schema, domainId, active, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void changeExpirationDate(String schema, Integer domainId, Date expireDate, AsyncCallback<Domain> callback) {
		AON.start();
		fsa.changeExpirationDate(schema, domainId, expireDate, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void switchRemoteAccess(String schema, Integer domainId, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.switchRemoteAccess(schema, domainId, new AsyncCallbackWrapper<>(callback));
	}
	@Override
	public void availableUsers(Occam occam, Integer domainId, AsyncCallback<LinkedList<User>> callback) {
		AON.start();
		fsa.availableUsers(occam, domainId, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getTableRow(ConsoleTableRow row, AsyncCallback<ConsoleTableRow> callback) {
		AON.start();
		fsa.getTableRow(row, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void getTableRows(ConsoleTableRow row, AsyncCallback<LinkedList<ConsoleTableRow>> callback) {
		AON.start();
		fsa.getTableRows(row, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getTableRowMetadata(ConsoleTableRow row, AsyncCallback<ConsoleTableRow> callback) {
		AON.start();
		fsa.getTableRowMetadata(row, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getAonTables(AsyncCallback<String[]> callback) {
		AON.start();
		fsa.getAonTables(new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void update(ConsoleTableRow row, ConsoleTableField field, AsyncCallback<ConsoleTableRow> callback) {
		AON.start();
		fsa.update(row, field, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void delete(ConsoleTableRow row, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.delete(row, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void testConnections(AsyncCallback<String> callback) {
		AON.start();
		fsa.testConnections(new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getScopes(String schema, Integer domainId, AsyncCallback<LinkedList<Scope>> callback) {
		AON.start();
		fsa.getScopes(schema, domainId, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void updateScopes(String schema, Integer domainId, Integer wrongScopeId, Integer newScopeId, AsyncCallback<String> callback) {
		AON.start();
		fsa.updateScopes(schema, domainId, wrongScopeId, newScopeId, new AsyncCallbackWrapper<>(callback));
	}
}
