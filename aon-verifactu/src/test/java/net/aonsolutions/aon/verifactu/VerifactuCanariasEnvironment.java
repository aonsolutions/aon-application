package net.aonsolutions.aon.verifactu;

import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;
import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
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
		
		Date today = new Date();
		Date yesterday = AonDateUtils.addDays(today, -1);
		
		Enterprise enterprise = EnterpriseDAO.get( ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()));
		ctx.getDslContext().insertInto(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.DOMAIN, ctx.getDomainId())
			.set(ENTERPRISE_DATA.ENTERPRISE, enterprise.getId() )
			.set(ENTERPRISE_DATA.NAME, EnterpriseDataNames.ICC_VERIFACTU.name() )
			.set(ENTERPRISE_DATA.EXPRESSION, "test" )
			.set(ENTERPRISE_DATA.START_DATE,  AonDateUtils.toSql(yesterday))
			.execute();
		ctx.log().info("Enterprise Data: ICC_VERIFACTU set to TRUE / TEST");
		
		ctx.getDslContext().insertInto(ENTERPRISE_DATA)
			.set(ENTERPRISE_DATA.DOMAIN, ctx.getDomainId())
			.set(ENTERPRISE_DATA.ENTERPRISE, enterprise.getId() )
			.set(ENTERPRISE_DATA.NAME, EnterpriseDataNames.ICC_ADMINISTRATION.name() )
			.set(ENTERPRISE_DATA.EXPRESSION, Administration.CANARIAS.name() )
			.set(ENTERPRISE_DATA.START_DATE,  AonDateUtils.toSql(yesterday))
			.execute();
		ctx.log().info("Enterprise Data: ICC_ADMINISTRATION set to CANARIAS");
	}

}
