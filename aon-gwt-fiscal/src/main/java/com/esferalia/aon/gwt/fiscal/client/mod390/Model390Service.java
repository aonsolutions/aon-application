package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod390")
public interface Model390Service extends RemoteService {

	LinkedList<Mod390> getMod390s(String domainName, Integer domain, String user) throws AonCoreException;
	Mod390 create(String domainName, int domain, String user, Mod390 mod390) throws AonCoreException;
	Mod390 initialize(String domainName, int domain, String user, int year);
	Mod390 saveComments(String currentDomainName, String user, Mod390 mod390);
	
}
