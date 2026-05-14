package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;

final class ICCConfigurationEnvironment extends VerifactuEnvironmentAbs {
	protected String domainName = "icctest.aonsolutions.test";	
	protected String user 		= "admin";
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	
	@Override
	public String getUser() {
		return user;
	}
	
	@Override
	public InvoiceCommunicatorContext getInvoiceCommunicatorContextWithCertificate(List<Invoice> invoices) {
		InvoiceCommunicatorContext icc = super.getInvoiceCommunicatorContextWithCertificate(invoices);
		assertNotNull(icc,"Context WithCertificate NULL" );
		assertNotNull(icc.getConfig(),"Context Configuration WithCertificate NULL" );
		assertTrue(icc.getConfig().isSif() ,"Context Configuration WithCertificate SIF NO ACTIVO");
		assertNotNull(icc.getConfig().getCertificate(), "Context Configuration WithCertificate Certificate NULL");
		return icc;
	}

	@Override
	public InvoiceCommunicationConfiguration configurationWithCertificate() {
		synchronized (this) {
			if (getCommunicationConfigurationWithCertificate() == null) {
				setCommunicationConfigurationWithCertificate( InvoiceCommunicationDAO.get(getCtx(),getDomainId())); 
			}
			getCommunicationConfigurationWithCertificate().setCertificate(AonSecret.getSigCert()); 
			return getCommunicationConfigurationWithCertificate();
		}
	}

	@Override
	public void initializeDomain(AONContext ctx) {
		// NOTHING TO DO
	}

	@Override
	public CommunicationData getEnablerData( InvoiceCommunicationConfiguration config ) {
		return getEnablerData(config, new Date());
	}
	@Override
	public CommunicationData getEnablerData(InvoiceCommunicationConfiguration config, Date atDate) {
		// NOTHING TO RETURN
		return null;
	}

}
