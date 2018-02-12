package com.esferalia.aon.gwt.fiscal.client.invoice;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("roms/VAT")
public interface VATService extends RemoteService {

	// --------------------------------------------------------- CONFIGURATION
	AonConfiguration getAonConfiguration(String domainName, String user, int domain) throws AonCoreException;

	// --------------------------------------------------------------- VAT
	LinkedList<VatSummaryContext> getVatSummaryContext(String domainName, String user, int domain,VatParams params) throws AonCoreException;
	LinkedList<VatContext> getVatContext(String domainName, String user, int domain,VatParams params) throws AonCoreException;
	String getVatContextReport(String domainName, String user, int domain,VatParams params) throws AonCoreException;
	
}
