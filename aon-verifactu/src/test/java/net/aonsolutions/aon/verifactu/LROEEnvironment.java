package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ICCDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

final class LROEEnvironment extends VerifactuEnvironmentAbs {
	protected String domainName = "lroetest.aonsolutions.test";	
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

	private Certificate insertBizkaiaCertificate(AONContext ctx) throws IOException {
		InputStream input = this.getClass().getResourceAsStream("bizkaia.p12");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		AonIOUtils.copy(input, baos);
		Integer userId = null;
		Certificate certificate = new Certificate();
		certificate.setTags(Arrays.asList(CertificateType.values()));
		certificate.setDomain(ctx.getDomainId());
		certificate.setDescription("CERT. BIZKAIA TEST");
		certificate.setOwner(CertificateOwner.ENTERPRISE);
		certificate.setConfidential(false);
		String CERT_PASSWORD = "IZDesa2021";
		certificate.setPassword(CERT_PASSWORD);
		certificate.setData( baos.toByteArray() );
		CertificateDAO.save(ctx, ctx.getDomainId(), userId, certificate);
		return certificate;
	}

	public void initializeDomain(AONContext ctx) {
		try {
			insertBizkaiaCertificate(ctx);
		} catch (IOException e) {
			throw new AonCoreException("Error inserting Bizkaia certificate", e);
		}
		Enterprise enterprise = EnterpriseDAO.get( ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		CommunicationData cd = new CommunicationData()
			.setDomain(ctx.getDomainId())
			.setEnterprise(enterprise.getId())
			.setStartDate(AonDateUtils.yesterday())
			.setTest(true)
			.setAdministration(Administration.BIZKAIA)
		;
		ICCDAO.enableLroe(ctx, ctx.getDomainId(), cd);
		ctx.log().info("LROE ENABLED! ( BIZKAIA / TEST)");
	}

	@Override
	public CommunicationData getEnablerData( InvoiceCommunicationConfiguration config ) {
		return getEnablerData(config, new Date());
	}
	@Override
	public CommunicationData getEnablerData(InvoiceCommunicationConfiguration config, Date atDate) {
		return config.getLroeData( atDate ).orElse(null);
	}
}
