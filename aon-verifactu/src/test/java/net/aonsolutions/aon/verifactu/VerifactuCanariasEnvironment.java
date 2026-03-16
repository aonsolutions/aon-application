package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

final class VerifactuCanariasEnvironment extends VerifactuEnvironmentAbs {
	protected String domainName = "verifactucanariastest.aonsolutions.test";	
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
	public InvoiceCommunicationConfiguration configurationWithCertificate() {
		synchronized (this) {
			if (getCommunicationConfigurationWithCertificate() == null) {
				setCommunicationConfigurationWithCertificate( InvoiceCommunicationDAO.get(getCtx(),getDomainId())); 
			}
			assertNotNull(getCommunicationConfigurationWithCertificate(),"communicationConfigurationWithCertificate NULL" );
			InvoiceCommunicationConfiguration icc = getCommunicationConfigurationWithCertificate();
			assertTrue(icc.isVerifactu() ,"communicationConfigurationWithCertificate VERIFACTU NO ACTIVO");
			assertTrue(icc.getVerifactuData().isPresent());
			CommunicationData vd = icc.getVerifactuData().get();
			assertTrue(vd.isTest(),"communicationConfigurationWithCertificate NO ENTORNO TEST" );
			Certificate c = AonSecret.getSigCert();
			assertNotNull(c, "Verifactu Certificate NULL");
			getCommunicationConfigurationWithCertificate().setCertificate(AonSecret.getSigCert()); 
			return getCommunicationConfigurationWithCertificate();
		}

	}

	public void initializeDomain(AONContext ctx) {
		insertAONCertificate(ctx);
		Enterprise enterprise = EnterpriseDAO.get( ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		CommunicationData cd = new CommunicationData()
			.setDomain(ctx.getDomainId())
			.setEnterprise(enterprise.getId())
			.setStartDate(AonDateUtils.yesterday())
			.setTest(true)
			.setAdministration(Administration.CANARIAS)
		;
		ICCDAO.enableVerifactu(ctx, ctx.getDomainId(), cd);
		ctx.log().info("Verifactu ENABLED! ( CANARIAS / TEST)");
	}

	@Override
	public CommunicationData getEnablerData( InvoiceCommunicationConfiguration config ) {
		return getEnablerData(config, new Date());
	}
	@Override
	public CommunicationData getEnablerData(InvoiceCommunicationConfiguration config, Date atDate) {
		return config.getVerifactuData( atDate ).orElse(null);
	}
}
