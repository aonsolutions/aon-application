package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Rawdoc")
public interface RawdocService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	LinkedList<Rawdoc> getRawdocs(String domainName, int domain, String  user, RawdocParams params, int offset,int limit) throws AonCoreException;
	LinkedList<RawdocDomainData> getDomainData(String domainName, int domain, String  user, int searchDomain) throws AonCoreException;
	TediResult parse(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException;
	void delete(String domainName, int domain, String user, Integer rawdocId);
	void toDraft(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException;
	void toRejected(String domainName, int domain, String user, Integer rawdocId,String reason) throws AonCoreException;
	void toInbox(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException;

	AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, String invoice);
}
