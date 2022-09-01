package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.Domain;
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
	public void getDomains(Occam occam, String schema, String query, AsyncCallback<LinkedList<Domain>> callback) {
		AON.start();
		fsa.getDomains(occam, schema, query, new AsyncCallbackWrapper<>(callback));
	}
}
