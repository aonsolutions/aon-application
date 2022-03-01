package com.esferalia.aon.occam.test;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.server.AonEnumUtils;

class DomainProviderForTests {
	
	private DomainProviderForTests() {
	}
	
	static Domain getOrCreateDomain(AONContext ctx,String domainName, String user) {
		Domain domain = DomainDAO.getDomain(ctx, p -> p.getNameProperty().eq(domainName));
		if (domain == null || domain.getId() == null) {
			domain = createFullDomain(ctx, domainName, user);
			Occam occam = new Occam()
					.setDomainName( domain.getName())
					.setDomain(domain.getId())
					.setUser(user);
			AONContext context = AONContext.getAONContext(occam);
			initializeDomain(context, occam);
		}
		return domain;
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
		Domain domain = DomainDAO.getDomain(ctx, newDomainId);
		ctx.log().info("Dominio " + domain.getName() + " insertado correctamente");
		
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

		Registry r = AonFaker.getRegistry(ctx) 
				.setDocumentType(DocumentType.CIF)
				.setDocumentCountry(Country.ES)
				.setDocument("B01487271")
				.setName("AON Solutions, S.L.")
				.setAlias("AON");
		CompanyFull companyFull = new CompanyFull();
		companyFull.setRegistry(AonFaker.getCompany(ctx, r));
		companyFull.getRegistry().setDomain(domain);
		RegistryAddress address = AonFaker.getRegistryAddress(ctx,companyFull.getRegistry()).setDomain(newDomainId); 
		companyFull.addAddress( address );
		CompanyDAO.save(ctx, companyFull);
		
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
		.set(WORKPLACE.ECONOMICAGREEMENT, AonEnumUtils.getByte( AonRandom.getRandomAdministration(10) ))
		.execute();
		ctx.log().info("Workplace insertada correctamente");
		
		return domain;
	}
	
	private static void initializeDomain(AONContext context, Occam occam) {
		context.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, context.getDomainId())
			.set(APP_PARAM.NAME, AppParam.AON_BETA_ENABLED.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
		context.log().info("App Param AON_BETA_ENABLED set to TRUE");
	
		context.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, context.getDomainId())
			.set(APP_PARAM.NAME, AppParam.AON_ALPHA_ENABLED.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
		context.log().info("App Param AON_ALPHA_ENABLED set to TRUE");
		
		Creditor defaultFiscalCreditor = AonFaker.getCreditor(context);
		defaultFiscalCreditor.setDocumentCountry(Country.ES);
		defaultFiscalCreditor.setDocumentType(DocumentType.CIF);
		defaultFiscalCreditor.setDocument("Q2826000H");
		defaultFiscalCreditor.setTransaction(InvoiceTransactionType.NATIONAL);
		defaultFiscalCreditor.setName("Agencia Tributaria");
		CreditorDAO.save(context, defaultFiscalCreditor);
		context.log().info("Default Fiscal Creditor inserted!");
		
		context.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, context.getDomainId())
			.set(APP_PARAM.NAME, AppParam.FS_ADMON_CREDITOR.toString())
			.set(APP_PARAM.VALUE, defaultFiscalCreditor.getId().toString() )
			.execute();
		context.log().info("App Param FS_ADMON_CREDITOR set to " + defaultFiscalCreditor.getId());
	}
	
	
	
}
