package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Rawdoc")
public interface RawdocService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	LinkedList<Rawdoc> getRawdocs(Occam occam, RawdocParams params, int offset,int limit) throws AonCoreException;
	void delete(Occam occam, Integer rawdocId);
	LinkedList<String> saveToAccounting(Occam occam, LinkedHashSet<Integer> rawdocIds) throws AonCoreException;
	
	Rawdoc toDraft(Occam occam, Integer rawdocId) throws AonCoreException;
	Rawdoc toRejected(Occam occam, Integer rawdocId, String reason, String email) throws AonCoreException;
	Rawdoc toInbox(Occam occam, Integer rawdocId) throws AonCoreException;

	String getS3Url(Rawdoc rawdoc);
	
	TediResult parse(Occam occam, Integer rawdocId) throws AonCoreException;
	
	// --------------------------------------------------------------- USER EMAIL
	String getUserEmail(Occam occam, String userLogin);
	
	// --------------------------------------------------------------- PENDING 
//	AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, String invoice) throws AonCoreException;
//	Boolean processInvoiceFile(String domainName, int domain, String user, String jsonStr, Invoice invoice) throws AonCoreException;
	

}
