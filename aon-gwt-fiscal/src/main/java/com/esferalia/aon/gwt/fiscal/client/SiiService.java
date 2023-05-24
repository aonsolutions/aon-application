package com.esferalia.aon.gwt.fiscal.client;

import java.util.List;

import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.OldInvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/sii")
public interface SiiService extends RemoteService {
	
	SiiConfiguration getSiiConfiguration(String domainName, int domainId, String user);
	List<InvestAsset> getInvestAssets(String domainName, int domainId, String user);
	void assignInvestAsset2Invoice(String domainName, int domainId, String user, String investAsset, Invoice invoice);
	List<Invoice> getInvoices(String domainName, int domainId, String user, InvoiceParams params);
	
	ICResponse altaLroe140(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams);
	String bajaLroe140(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) throws Exception;
	ICResponse altaLroe240(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams);
	String bajaLroe240(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) throws Exception;
	String altaSii(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams);
	String bajaSii(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams);
	Boolean refresh140(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams);
	Boolean refresh240(String domainName, int domainId, String user, OldInvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams);

}
