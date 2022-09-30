package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConsoleServiceAsync {

	void getSchemas(Occam occam, AsyncCallback<String[]> callback);
	void getDomains(DomainParams params, AsyncCallback<LinkedList<ConsoleDomain>> asyncCallback);
	void deleteDomain(DomainParams params, Integer domainId, AsyncCallback<Boolean> callback);
	void changeActive(DomainParams params, Integer domainId, boolean active, AsyncCallback<Domain> callback);
	void changeExpirationDate(DomainParams params, Integer domainId, Date expireDate, AsyncCallback<Domain> callback);

}
