package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Model193ServiceAsync {

	void getMod193s(String domainName, String user, int domain, AsyncCallback<LinkedList<Mod193>> callback);
	void getMod193(String domainName, String user, int domain,Integer id, AsyncCallback<Mod193> callback);
	void delete(String domainName, String user, int domain,Mod193 mod193, AsyncCallback<Void> callback);
	void save(String domainName, String user, int domain,Mod193 mod193, AsyncCallback<Mod193> callback);
	void initialize(String domainName, String user, Integer domain, Integer year,AsyncCallback<Mod193> callback);
	void changeStatus(String domainName, String user, Mod193 mod193, FiscalStatus newStatus, AsyncCallback<Mod193> callback);
	void saveComments(String domainName, String user, Mod193 mod193, AsyncCallback<Mod193> callback);
	void duplicateNextYear(String domainName, String user, Integer domain, Integer id, AsyncCallback<Mod193> callback);

}
