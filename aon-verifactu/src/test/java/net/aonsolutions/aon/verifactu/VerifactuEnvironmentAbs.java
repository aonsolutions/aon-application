package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;

abstract class VerifactuEnvironmentAbs implements Environment {
	protected CloseableAONContext ctx;
	protected Integer domainId;
	protected InvoiceCommunicationConfiguration communicationConfiguration;
	protected InvoiceCommunicationConfiguration communicationConfigurationWithCertificate;
	protected Domain domain;
	protected User user;
	protected Company company;
	
	@Override
	public CloseableAONContext getCtx() {
		return ctx;
	}
	@Override
	public void setCtx(CloseableAONContext aonContext) {
		this.ctx = aonContext;
	}
	
	@Override
	public Integer getDomainId() {
		return domainId;
	}
	@Override
	public void setDomainId(Integer id) {
		this.domainId = id;
	}

	@Override
	public InvoiceCommunicatorContext getInvoiceCommunicatorContext(List<Invoice> invoices) {
		return new InvoiceCommunicatorContext(domain(),user(), null, invoices)
			.setCompany( company() )
			.setConfig( configuration() )
		;
	}
	private InvoiceCommunicationConfiguration getCommunicationConfiguration() {
		return communicationConfiguration; 
	}
	private void setCommunicationConfiguration(InvoiceCommunicationConfiguration config) {
		this.communicationConfiguration = config;
	}
	@Override
	public InvoiceCommunicationConfiguration configuration() {
		synchronized (this) {
			if (getCommunicationConfiguration() == null) {
				setCommunicationConfiguration( InvoiceCommunicationDAO.get(getCtx(),getDomainId())); 
			}
			assertNotNull(getCommunicationConfiguration(),"communicationConfiguration NULL" );
			assertTrue(getCommunicationConfiguration().isVerifactu() ,"communicationConfiguration VERIFACTU NO ACTIVO");
			assertTrue(getCommunicationConfiguration().getVerifactuData().isPresent());
			CommunicationData vd = getCommunicationConfiguration().getVerifactuData().get();
			assertTrue(vd.isTest(),"communicationConfiguration NO ENTORNO TEST" );
			return getCommunicationConfiguration();
		}
	}

	
	@Override
	public InvoiceCommunicatorContext getInvoiceCommunicatorContextWithCertificate(List<Invoice> invoices) {
		return new InvoiceCommunicatorContext(domain(),user(), null, invoices)
			.setCompany( company() )
			.setConfig( configurationWithCertificate() )
		;
	}
	protected InvoiceCommunicationConfiguration getCommunicationConfigurationWithCertificate() {
		return communicationConfigurationWithCertificate; 
	}
	protected void setCommunicationConfigurationWithCertificate(InvoiceCommunicationConfiguration config) {
		this.communicationConfigurationWithCertificate = config;
	}
	
	@Override
	public Company company() {
		return CompanyDAO.getCompany(getCtx(), getDomainId());
	}
	
	@Override
	public User user() {
		synchronized (this) {
			if (user == null) {
				user = UserDAO.get(getCtx(), getDomainId(), getUser())
					.orElseThrow( () -> new IllegalStateException("User " + getUser() + " not found in domain " + getDomainId()) );
			}
			return user;
		}
	}

	@Override
	public Domain domain() {
		synchronized (this) {
			if (domain == null) {
				domain = DomainDAO.getDomain(getCtx(), getDomainId());
			}
			return domain;
		}
	}
	
	@Override
	public Occam getOccam() {
		return new Occam()
			.setDomainName(getDomainName())
			.setDomain(domainId)
			.setUser(getUser());
	}
	
	protected Certificate insertAONCertificate(AONContext ctx) {
		Integer userId = null;
		Certificate certificate = AonSecret.getSigCert();
		String pass = certificate.getPassword();
		certificate.setTags(Arrays.asList(CertificateType.values()));
		certificate.setDomain(ctx.getDomainId());
		certificate.setDescription("certificado");
		certificate.setOwner(CertificateOwner.ENTERPRISE);
		certificate.setConfidential(false);
		certificate.setPassword(pass);
		CertificateDAO.save(ctx, ctx.getDomainId(), userId, certificate);
		return certificate;
	}
	
}
