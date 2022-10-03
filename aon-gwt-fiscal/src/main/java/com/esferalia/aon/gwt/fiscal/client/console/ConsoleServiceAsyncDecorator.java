package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
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
	public void deleteDomain(DomainParams params, Integer domainId, AsyncCallback<Boolean> callback) {
		AON.start();
		fsa.deleteDomain(params, domainId, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void changeActive(DomainParams params, Integer domainId, boolean active, AsyncCallback<Domain> callback) {
		AON.start();
		fsa.changeActive(params, domainId, active, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void changeExpirationDate(DomainParams params, Integer domainId, Date expireDate, AsyncCallback<Domain> callback) {
		AON.start();
		fsa.changeExpirationDate(params, domainId, expireDate, new AsyncCallbackWrapper<>(callback));
	}
	
	@Override
	public void remoteAccess(DomainParams params, Integer domainId, AsyncCallback<String> callback) {
		AON.start();
		fsa.remoteAccess(params, domainId, new AsyncCallbackWrapper<>(callback));
	}
}
