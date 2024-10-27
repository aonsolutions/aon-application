package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Optional;

import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.ApplicationParameter;
import net.aonsolutions.occam.api.model.Cnae;
import net.aonsolutions.occam.api.model.Company;
import net.aonsolutions.occam.api.model.CompanyFull;
import net.aonsolutions.occam.api.model.Creditor;
import net.aonsolutions.occam.api.model.Domain;
import net.aonsolutions.occam.api.model.Iae;
import net.aonsolutions.occam.api.model.Occam;
import net.aonsolutions.occam.api.model.RegistryAddress;
import net.aonsolutions.occam.api.model.type.Administration;
import net.aonsolutions.occam.api.model.type.AonApp;
import net.aonsolutions.occam.api.model.type.AppParam;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.IRPFRegime;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.Module;
import net.aonsolutions.occam.api.model.type.RegistryStatus;
import net.aonsolutions.occam.api.model.type.SSRegimeType;
import net.aonsolutions.occam.api.model.type.VATRegime;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.AONContext.CloseableAONContext;

public class TestsDomainProvider {
	
	private TestsDomainProvider() {
	}
	
	public static Domain getOrCreateDomain(AONContext ctx,String domainName, String user) {
		return DomainHandler.get(ctx, domainName )
			.or( () -> {
				Domain domain = ctx.getDslContext().transactionResult(configuration -> createFullDomain(ctx, domainName, user));
				Occam occam = new Occam()
					.setDomainName( domain.getName())
					.setUser(user);
				try ( CloseableAONContext context = AONContext.getAONContext(occam)) {
					context.getDslContext().transaction(config -> initializeDomain(context, domain) );
				}
				return Optional.of(domain);
			})
			.get();
	}
	
	private static Domain createFullDomain(AONContext ctx,String domainName, String user) {
		int newDomainId = ctx
				.getDslContext()
				.insertInto(DOMAIN)
				.set(DOMAIN.CREATION_USER, user)
				.set(DOMAIN.CREATION_DATE, new java.sql.Timestamp(System.currentTimeMillis()))
				.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
				.set(DOMAIN.TYPE, (byte) 0)
				.set(DOMAIN.OWNER, user )
				.set(DOMAIN.NAME, domainName )
				.set(DOMAIN.DESCRIPTION, domainName )
				.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
				.set(DOMAIN.MAXDEFINEDUSERS, 1)
				.set(DOMAIN.MAXDOCUMENTSIZE, 1)
				.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16).returning(DOMAIN.ID)
				.fetchOne().getId();
		Domain domain = DomainHandler.get(ctx, newDomainId)
			.orElseThrow( () -> new AonCoreException("Dominio para tests no creado"));
		ctx.log().info("Dominio " + domain.getName() + " insertado correctamente");
		
		LoadDefaulsForTests loadDefaults = new LoadDefaulsForTests();
		loadDefaults.insertGeozones(ctx, domain);
		
		ctx.getDslContext().insertInto(DOMAIN_APP)
			.set(DOMAIN_APP.DOMAIN, newDomainId)
			.set(DOMAIN_APP.APP, AonApp.OCR.value())
			.set(DOMAIN_APP.ACTIVE, (byte) 1)
			.execute();
		ctx.getDslContext().insertInto(DOMAIN_APP)
			.set(DOMAIN_APP.DOMAIN, newDomainId)
			.set(DOMAIN_APP.APP, AonApp.TIMECONTROL.value())
			.set(DOMAIN_APP.ACTIVE, (byte) 1)
			.execute();
		
		int newDomainApplicationId = ctx.getDslContext().insertInto(DOMAIN_APPLICATION)
				.set(DOMAIN_APPLICATION.DOMAIN, newDomainId)
				.set(DOMAIN_APPLICATION.APPLICATION, 28)
				.set(DOMAIN_APPLICATION.ACTIVE, (byte) 1)
				.set(DOMAIN_APPLICATION.AUDIT_LEVEL, (byte) 0)
				.returning(DOMAIN_APPLICATION.ID)
				.fetchOne()
				.getId();
		ctx.log().info("Aplicacion de dominio insertada correctamente");

		
		Module[] modules = new Module[] {
			 Module.CRM			,Module.MANAGEMENT	,Module.WAREHOUSE	,Module.GROUPWARE
			,Module.ACCOUNTING	,Module.FISCAL		,Module.PAYROLL		,Module.DOCUMENT
			,Module.POS			,Module.CALL_CENTER	,Module.SUITE_PORTAL};
		for (Module module : modules) {
			ctx.getDslContext().insertInto(DOMAIN_APPLICATION_MODULE)
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN, newDomainId)
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION, newDomainApplicationId)
				.set(DOMAIN_APPLICATION_MODULE.MODULE, module.value())
				.execute();
		}

		Company r = new Company()
			.setDomain(newDomainId)
			.setDocumentType(DocumentType.CIF)
			.setDocumentCountry(Country.ES)
			.setDocument("B01487271")
			.setName("AON Solutions, S.L.")
			.setAlias("AON")
			.setNationality(Country.ES)
			.setConfidential( !AonRandom.gt(3) )
			.setActive( AonRandom.gt(2) )
			.setSurcharge( AonRandom.gt(95) )
			.setWithholding( AonRandom.gt(85) )
			.setWithholding( AonRandom.gt(85) )
			.setVatAccrualPayment( AonRandom.gt(99) )
			.seteInvoice( AonRandom.gt(50) )
		;

		CompanyFull companyFull = new CompanyFull();
		companyFull.setRegistry(r);
		
		
		RegistryAddress address = AonFaker.getRegistryAddress(ctx, companyFull.getRegistry())
				.setMain(true); 
		companyFull.addAddress( address );
		
		CompanyHandler.save(ctx, companyFull);
		
		int newScopeId = ctx.getDslContext().insertInto(SCOPE)
				.set(SCOPE.DESCRIPTION, "DEFAULT")
				.set(SCOPE.DOMAIN , newDomainId)
				.returning(SCOPE.ID).fetchOne()
				.getId();
		ctx.log().info("Scope insertado correctamente");
		
		int newUserId = ctx.getDslContext().insertInto(USER)
				.set(USER.DOMAIN , newDomainId)
				.set(USER.NAME, "DEFAULT USER")
				.set(USER.LOGIN, user)
				.set(USER.PASSWORD, "0jtZh1BMGz3khL8uR8dvdau3lNM=") // org
				.returning(USER.ID)
				.fetchOne()
				.getId();
		ctx.log().info("User insertado correctamente");
		
		ctx.getDslContext().insertInto(USER_SCOPE)
			.set(USER_SCOPE.USER_ID, newUserId)
			.set(USER_SCOPE.DOMAIN , newDomainId)
			.set(USER_SCOPE.SCOPE, newScopeId)
			.execute();
		ctx.log().info("User Scope insertado correctamente");

		ctx.getDslContext().insertInto(ENTERPRISE)
			.set(ENTERPRISE.REGISTRY, companyFull.getRegistry().getId())
			.set(ENTERPRISE.DOMAIN, newDomainId)
			.set(ENTERPRISE.SCOPE, newScopeId)
			.execute();
		ctx.log().info("Enterprise insertada correctamente");

		int applicationUserId = ctx.getDslContext().insertInto(APPLICATION_USER)
				.set(APPLICATION_USER.DOMAIN, newDomainId)
				.set(APPLICATION_USER.USER_ID, newUserId)
				.set(APPLICATION_USER.DOMAIN_APPLICATION, newDomainApplicationId)
				.set(APPLICATION_USER.ACTIVE, (byte) 1)
				.returning(APPLICATION_USER.ID)
				.fetchOne()
				.getId();
		ctx.log().info("Aplicacion de usuario insertada correctamente");

		ctx.getDslContext().insertInto(APPLICATION_USER_PROFILE)
			.set(APPLICATION_USER_PROFILE.DOMAIN, newDomainId)
			.set(APPLICATION_USER_PROFILE.APPLICATION_USER, applicationUserId)
			.set(APPLICATION_USER_PROFILE.PROFILE, 71)
			.returning(DOMAIN_APPLICATION.ID)
			.fetchOne()
			.getId();
		ctx.log().info("Perfil de usuario en la aplicación insertada correctamente");

		ctx.getDslContext().insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, newDomainId)
			.set(WORKPLACE.DESCRIPTION, "DEFAULT")
			.set(WORKPLACE.ADDRESS, address.getId())
			.set(WORKPLACE.ENTERPRISE, companyFull.getRegistry().getId())
			.set(WORKPLACE.SCOPE, newScopeId)
			.set(WORKPLACE.ECONOMICAGREEMENT, AonEnumUtils.getByte( AonRandom.getEnum(Administration.class,true) ))
			.execute();
		ctx.log().info("Workplace insertada correctamente");
		return domain;
	}
	
	private static void initializeDomain(AONContext ctx, Domain domain) {
		
		int newDomainId = domain.getId();
		
		LoadDefaulsForTests loadDefaults = new LoadDefaulsForTests();
		loadDefaults.loadIAE(ctx);
		loadDefaults.loadCnae(ctx);
		
		Iae iae = IaeHandler.getRandom(ctx, null);
		Cnae cnae = CnaeHandler.getRandom(ctx, null);
		Company company = CompanyHandler.getByDomain(ctx, newDomainId)
			.orElseThrow(() -> new AonCoreException("Company no encontrada"));
		
		ctx.getDslContext().insertInto(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DOMAIN, newDomainId )
			.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte) 1)
			.set(ENTERPRISE_ACTIVITY.ENTERPRISE, company.getId())
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, AonStringUtils.abbreviate(iae.getTitle(), 64))
			.set(ENTERPRISE_ACTIVITY.IAE, iae.getId())
			.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae.getId())
			.set(ENTERPRISE_ACTIVITY.TYPE, SSRegimeType.GENERAL.value())
			.set(ENTERPRISE_ACTIVITY.VAT_REGIME, VATRegime.GENERAL.value())
			.set(ENTERPRISE_ACTIVITY.RETENTION_REGIME, IRPFRegime.NORMAL.value())
			.set(ENTERPRISE_ACTIVITY.START_DATE, AonDateUtils.toSql( AonRandom.getPastDate(0)) )
			.execute();
		ctx.log().info("EnterpriseActivity created");

		if (AppParamHandler.get(ctx, newDomainId, AppParam.AON_BETA_ENABLED).isEmpty()) {
			AppParamHandler.save(ctx, 
				new ApplicationParameter()
				.setDomain(newDomainId )
				.setParam(AppParam.AON_BETA_ENABLED)
				.setValue( true )
			);
			ctx.log().info("App Param AON_BETA_ENABLED set to TRUE");
		}
	
		if (AppParamHandler.get(ctx, newDomainId, AppParam.AON_ALPHA_ENABLED).isEmpty()) {
			AppParamHandler.save(ctx, 
				new ApplicationParameter()
				.setDomain(newDomainId )
				.setParam(AppParam.AON_ALPHA_ENABLED)
				.setValue( true )
			);
			ctx.log().info("App Param AON_ALPHA_ENABLED set to TRUE");
		}

		Creditor dfc = new Creditor ()
			.setDomain(newDomainId)
			.setDocumentType(DocumentType.CIF)
			.setDocumentCountry(Country.ES)
			.setDocument("Q2826000H")
			.setName("Agencia Tributaria")
			.setAlias("AON")
			.setNationality(Country.ES)
			.setConfidential( !AonRandom.gt(3) )
			.setWithholding( AonRandom.gt(85) )
			.setVatAccrualPayment( AonRandom.gt(99) )
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setStatus(RegistryStatus.ACTIVE)
			.setScope(null)
			.setAccount(null)
		;
		CreditorHandler.save(ctx, dfc);
		ctx.log().info("Default Fiscal Creditor inserted!");
		
		AppParamHandler.save(ctx, 
				new ApplicationParameter()
				.setDomain(newDomainId )
				.setParam(AppParam.FS_ADMON_CREDITOR)
				.setValue( dfc.getId())
			);
		ctx.log().info("App Param FS_ADMON_CREDITOR set to " + dfc.getId());
		
		AppParamHandler.save(ctx, 
				new ApplicationParameter()
				.setDomain(newDomainId )
				.setParam(AppParam.FS_ADMON_RETENTION_CREDITOR)
				.setValue( dfc.getId())
			);
		ctx.log().info("App Param FS_ADMON_RETENTION_CREDITOR set to " + dfc.getId());

		AppParamHandler.save(ctx, 
				new ApplicationParameter()
				.setDomain(newDomainId )
				.setParam(AppParam.FS_ADMON_VAT_CREDITOR)
				.setValue( dfc.getId())
			);
		ctx.log().info("App Param FS_ADMON_VAT_CREDITOR set to " + dfc.getId());
		
	}
	

}
