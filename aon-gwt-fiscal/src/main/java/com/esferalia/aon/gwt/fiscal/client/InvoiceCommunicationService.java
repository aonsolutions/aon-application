package com.esferalia.aon.gwt.fiscal.client;

import java.util.List;

import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/invoiceCommunication")
public interface InvoiceCommunicationService extends RemoteService {
	
	SiiConfiguration getSiiConfiguration(String domainName, int domainId, String user);
	TbaiConfiguration getTbaiConfiguration(String domainName, int domainId, String user);
	
	List<InvestAsset> getInvestAssets(String domainName, int domainId, String user);
	void assignInvestAsset2Invoice(String domainName, int domainId, String user, String investAsset, Invoice invoice);
	void addDocumentInvoice(String domainName, int domainId, String user, Invoice invoice);
	
	List<Invoice> getInvoices(String domainName, int domainId, String user, InvoiceParams params);
	
	ICResponse altaLroe(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	String bajaLroe140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams) throws Exception;
	String bajaLroe240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams) throws Exception;
	String altaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	String bajaSii(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	Boolean refresh140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);
	Boolean refresh240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams);

	String cancel(String domainName, int domainId, String user, InvoiceCommunicationType type, Invoice invoice, AEATParams aeatParams);
	
	List<InvoiceCommunicationTracking> getInvoiceCommunicationTrackingList(String domainName, int domainId, String login, Integer invoice);

	String getRequestUrl(String domainName, int domainId, String login, Integer dataResponse);
	String getResponseUrl(String domainName, int domainId, String login, Integer dataResponse);
}
