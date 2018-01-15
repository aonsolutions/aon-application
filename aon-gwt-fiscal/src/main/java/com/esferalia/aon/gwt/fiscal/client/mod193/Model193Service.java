package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod193")
public interface Model193Service extends RemoteService {

	LinkedList<Mod193> getMod193s(String domainName, int domain) throws AonCoreException;
	Mod193 getMod193(String domainName, int domain,Integer id) throws AonCoreException;
	void delete(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 save(String domainName, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 initialize(String domainName, Integer domain, Integer year);
	Mod193 saveComments(String domainName, Mod193 mod193) throws AonCoreException;
	Mod193 changeStatus(String domainName, Mod193 mod193, FiscalStatus newStatus) throws AonCoreException;
	Mod193 duplicateNextYear(String domainName, Integer domain,Integer id) throws AonCoreException;

}
