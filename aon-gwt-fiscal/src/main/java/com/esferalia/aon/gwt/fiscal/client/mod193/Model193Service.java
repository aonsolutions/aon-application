package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod193")
public interface Model193Service extends RemoteService {

	LinkedList<Mod193> getMod193s(String domainName, String user, int domain) throws AonCoreException;
	Mod193 getMod193(String domainName, String user, int domain,Integer id) throws AonCoreException;
	void delete(String domainName, String user, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 save(String domainName, String user, int domain,Mod193 mod193) throws AonCoreException;
	Mod193 initialize(String domainName, String user, Integer domain, Integer year);
	Mod193 saveComments(String domainName, String user, Mod193 mod193) throws AonCoreException;
	Mod193 changeStatus(String domainName, String user, Mod193 mod193, FiscalStatus newStatus) throws AonCoreException;
	Mod193 duplicateNextYear(String domainName, String user, Integer domain,Integer id) throws AonCoreException;

}
