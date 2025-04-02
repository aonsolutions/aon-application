package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Rawdoc")
public interface RawdocService extends RemoteService {
	
	// --------------------------------------------------------------- RAWDOC
	LinkedList<Rawdoc> getRawdocs(Occam occam, RawdocParams params, int offset,int limit) throws AonCoreException;
	void delete(Occam occam, Integer rawdocId);
	Rawdoc toDraft(Occam occam, Integer rawdocId) throws AonCoreException;
	Rawdoc toRejected(Occam occam, Integer rawdocId,String reason) throws AonCoreException;
	Rawdoc toInbox(Occam occam, Integer rawdocId) throws AonCoreException;
	
	TediResult parse(Occam occam, Integer rawdocId) throws AonCoreException;
	
	// --------------------------------------------------------------- PENDING 
	AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, String invoice);
	Boolean processInvoiceFile(String domainName, int domain, String user, String jsonStr, Invoice invoice);
	String getS3Url(Rawdoc rawdoc);

}
