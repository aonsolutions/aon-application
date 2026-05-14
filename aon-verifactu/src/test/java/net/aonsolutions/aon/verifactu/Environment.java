package net.aonsolutions.aon.verifactu;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.User;

public interface Environment {
	CloseableAONContext getCtx();
	void setCtx(CloseableAONContext aonContext);
	
	
	Integer getDomainId();
	void setDomainId(Integer id);
	Occam getOccam();
	InvoiceCommunicationConfiguration configuration();
	InvoiceCommunicatorContext getInvoiceCommunicatorContext(List<Invoice> invoices);
	InvoiceCommunicationConfiguration configurationWithCertificate();
	InvoiceCommunicatorContext getInvoiceCommunicatorContextWithCertificate(List<Invoice> invoices);
	CommunicationData getEnablerData(InvoiceCommunicationConfiguration config);
	CommunicationData getEnablerData(InvoiceCommunicationConfiguration config, Date atDate);
	String getDomainName();	
	String getUser();
	
	Domain domain();
	User user();
	Company company();
	
	void initializeDomain(AONContext ctx);
	
	default void close() {
		CloseableAONContext ctx = getCtx();
		if (ctx != null) {
			ctx.close();
		}
	}
}
