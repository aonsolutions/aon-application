package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Console")
public interface ConsoleService extends RemoteService {

	String[] getSchemas(Occam occam) throws AonCoreException;
	LinkedList<Domain> getDomains(Occam occam, DomainParams params) throws AonCoreException;
	Boolean deleteDomain(Occam occam, DomainParams params) throws AonCoreException;
}
