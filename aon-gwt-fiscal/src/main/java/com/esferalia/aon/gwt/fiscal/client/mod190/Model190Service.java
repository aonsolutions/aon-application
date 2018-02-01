package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Mod190")
public interface Model190Service extends RemoteService {
	
	LinkedList<Mod190> getMod190s(String domainName, String user, int domain) throws AonCoreException;
	Mod190 getMod190(String domainName, String user, int domain,Integer id) throws AonCoreException;
	Mod190Detail getDetail(String domainName, String user, int domain,Integer id) throws AonCoreException;
	void delete(String domainName, String user, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 save(String domainName, String user, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 initialize(String domainName, String user, Integer domain, Integer year);
	Mod190 saveComments(String domainName, String user, Mod190 mod190) throws AonCoreException;
	Mod190 changeStatus(String domainName, String user, Mod190 mod190, FiscalStatus newStatus) throws AonCoreException;
	Mod190 duplicateNextYear(String domainName, String user, Integer domain,Integer id) throws AonCoreException;

}
