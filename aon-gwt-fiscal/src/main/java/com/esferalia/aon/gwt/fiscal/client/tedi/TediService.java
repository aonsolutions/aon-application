package com.esferalia.aon.gwt.fiscal.client.tedi;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import es.translogia.tedi.ewok.TediInvoice;

@RemoteServiceRelativePath("roms/Tedi")
public interface TediService extends RemoteService {

	AonConfiguration getAonConfiguration(String domainName, String user, int domain) throws AonCoreException;
	
	LinkedList<TediResult> getVerifiedInvoices(String domainName, String user, int domain, boolean snapshot) throws AonCoreException;
	TediResult getInvoice(String domainName, String user, int domain, boolean snapshot, String uuid, String tediStatus) throws AonCoreException;
	TediResult putInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice) throws AonCoreException;
	LinkedList<TediResult> putInvoices(String domainName, String user, int domain, boolean snapshot, LinkedList<TediResult> results) throws AonCoreException;
	TediResult validateInvoice(String domainName, String user, int domain, boolean snapshot, TediResult result) throws AonCoreException;
	String getInvoiceAttachURL(String domainName, String user, int domain, boolean snapshot, String uuid) throws AonCoreException;
}
