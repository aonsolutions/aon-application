package com.esferalia.aon.gwt.fiscal.client.mod190;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("Mod190")
public interface Model190Service extends RemoteService {
	
	LinkedList<Mod190> getMod190s(String domainName, int domain) throws AonCoreException;
	Mod190 getMod190(String domainName, int domain,Integer id) throws AonCoreException;
	Mod190Detail getDetail(String domainName, int domain,Integer id) throws AonCoreException;
	void delete(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 save(String domainName, int domain,Mod190 mod190) throws AonCoreException;
	Mod190 initialize(String domainName, Integer domain, Integer year);
	Mod190 saveComments(String domainName, Mod190 mod190) throws AonCoreException;
	Mod190 changeStatus(String domainName, Mod190 mod190, FiscalStatus newStatus) throws AonCoreException;

}
