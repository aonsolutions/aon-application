package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConsoleServiceAsync {

	void getSchemas(Occam occam, AsyncCallback<String[]> callback);
	void getDomains(DomainParams params, AsyncCallback<LinkedList<Domain>> asyncCallback);
	void deleteDomain(DomainParams params, Integer domainId, AsyncCallback<Boolean> callback);
	void changeActive(DomainParams params, Domain domain, AsyncCallback<Domain> callback);
	void changeExpirationDate(DomainParams params, Domain domain, AsyncCallback<Domain> callback);

}
