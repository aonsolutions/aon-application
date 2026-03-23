package com.esferalia.aon.gwt.fiscal.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/invoiceCommunication")
public interface InvoiceCommunicationService extends RemoteService {
	
	InvoiceCommunicationConfiguration getConfiguration(String domainName, int domainId, String user);
	
	LinkedList<InvestAsset> getInvestAssets(String domainName, int domainId, String user);
	void assignInvestAsset2Invoice(String domainName, int domainId, String user, String investAsset, Invoice invoice);
	void addDocumentInvoice(String domainName, int domainId, String user, Invoice invoice);
	
	LinkedList<Invoice> getInvoices(Occam occam, InvoiceCommunicationParams params);
	
	ICResponse altaLroe(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	String bajaLroe140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams) throws Exception;
	String bajaLroe240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams) throws Exception;
	ICResponse altaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	String bajaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	Boolean refresh140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	Boolean refresh240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);

	String cancel(String domainName, int domainId, String user, InvoiceCommunicationType type, Invoice invoice, AEATParams aeatParams);
	
	LinkedList<InvoiceCommunicationTracking> getInvoiceCommunicationTrackings(String domainName, int domainId, String login, Integer invoice);

	String getRequestUrl(String domainName, int domainId, String login, Integer dataResponse);
	String getResponseUrl(String domainName, int domainId, String login, Integer dataResponse);
	void prepareNewSii(String domainName, int domainId, String user, Integer year);
}
