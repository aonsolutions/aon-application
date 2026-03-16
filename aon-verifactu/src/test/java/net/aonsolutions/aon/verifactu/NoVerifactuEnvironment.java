package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

final class NoVerifactuEnvironment extends VerifactuEnvironmentAbs {
	protected String domainName = "noverifactutest.aonsolutions.test";	
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
		assertTrue(icc.getConfig().isNoVerifactu() ,"Context Configuration WithCertificate NO_VERIFACTU NO ACTIVO");
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

	public void initializeDomain(AONContext ctx) {
		Date today = new Date();
		Date yesterday = AonDateUtils.addDays(today, -1);
		
		Enterprise enterprise = EnterpriseDAO.get( ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		CommunicationData cd = new CommunicationData()
			.setDomain(ctx.getDomainId())
			.setEnterprise(enterprise.getId())
			.setDataName(EnterpriseDataNames.ICC_NO_VERIFACTU)
			.setStartDate(AonDateUtils.yesterday())
			.setTest(true)
			.setAdministration(Administration.COMMON_TERRITORY)
		;
		ICCDAO.enableNoVerifactu(ctx, ctx.getDomainId(), cd);
		ctx.log().info("NO VERIFACTU ENABLED! ( NAVARRA / TEST)");
	}

	@Override
	public CommunicationData getEnablerData( InvoiceCommunicationConfiguration config ) {
		return getEnablerData(config, new Date());
	}
	@Override
	public CommunicationData getEnablerData(InvoiceCommunicationConfiguration config, Date atDate) {
		return config.getNoVerifactuData( atDate ).orElse(null);
	}
}
