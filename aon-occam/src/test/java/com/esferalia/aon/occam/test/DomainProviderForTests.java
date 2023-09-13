package com.esferalia.aon.occam.test;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Iae;
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
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Cnae2009DAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DefaultsDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO.GeoZoneFiller;
import com.esferalia.aon.occam.impl.jooq.dao.IAEDAO;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
			try ( CloseableAONContext context = AONContext.getAONContext(occam)) {
				initializeDomain(context, occam);
			}
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
		
		insertGeozones(ctx, domain);
		
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
		
		GeoZone geozone = ctx.getDslContext()
			.selectFrom(GEOZONE)
			.where(GEOZONE.DOMAIN.eq(newDomainId))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new GeoZoneFiller())
			.findFirst()
			.orElse(null);
			;
		RegistryAddress address = AonFaker.getRegistryAddress(ctx,companyFull.getRegistry(), geozone).setDomain(newDomainId);
		address.setMain(true);
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
		
		DefaultsDAO.loadDefaults(context);
		Iae iae = IAEDAO.getRandom(context, null);
		Cnae2009 cnae2009 = Cnae2009DAO.getRandom(context, null);
		Company company = CompanyDAO.getCompany(context, context.getDomainId());
		context.getDslContext().insertInto(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DOMAIN, context.getDomainId())
			.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte) 1)
			.set(ENTERPRISE_ACTIVITY.ENTERPRISE, company.getId())
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, AonStringUtils.abbreviate(iae.getTitle(), 64))
			.set(ENTERPRISE_ACTIVITY.IAE, iae.getId())
			.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009.getId())
			.set(ENTERPRISE_ACTIVITY.TYPE, SSRegimeType.GENERAL.getValue())
			.set(ENTERPRISE_ACTIVITY.VAT_REGIME, VATRegime.GENERAL.value())
			.set(ENTERPRISE_ACTIVITY.RETENTION_REGIME, IRPFRegime.NORMAL.value())
			.set(ENTERPRISE_ACTIVITY.START_DATE, AonDateUtils.toSql( AonRandom.getPastDate(0)) )
			.execute();
		context.log().info("EnterpriseActivity created");

	/*
  `cnae` int(4) DEFAULT NULL COMMENT 'Identificador del CNAE',
  `cnae2009` int(4) DEFAULT NULL COMMENT 'Identificador del CNAE 2009',
  `surcharge` tinyint(1) DEFAULT 0 COMMENT 'Indica si la Actividad tiene de recargo de equivalencia',
  `retention_tax` int(4) DEFAULT NULL COMMENT 'Identificador del IRPF por defecto',
  `prorata` double(5,2) DEFAULT 100.00 COMMENT 'Porcentaje de prorrata',
  `prorata_type` tinyint(1) DEFAULT 0 COMMENT 'Indica el tipo de prorrata',
  */
		
		ApplicationParameter betaParam = AppParamDAO.fetchOne(context, AppParam.AON_BETA_ENABLED.toString());
		if (betaParam == null || betaParam.getId() == null) {
			context.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, context.getDomainId())
			.set(APP_PARAM.NAME, AppParam.AON_BETA_ENABLED.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
			context.log().info("App Param AON_BETA_ENABLED set to TRUE");
		}
	
		ApplicationParameter alphaParam = AppParamDAO.fetchOne(context, AppParam.AON_ALPHA_ENABLED.toString());
		if (alphaParam == null || alphaParam.getId() == null) {
			context.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, context.getDomainId())
			.set(APP_PARAM.NAME, AppParam.AON_ALPHA_ENABLED.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
			context.log().info("App Param AON_ALPHA_ENABLED set to TRUE");
		}
		
		Creditor defaultFiscalCreditor = AonFaker.getCreditor(context);
		defaultFiscalCreditor.setDocumentCountry(Country.ES);
		defaultFiscalCreditor.setDocumentType(DocumentType.CIF);
		defaultFiscalCreditor.setDocument("Q2826000H");
		defaultFiscalCreditor.setTransaction(InvoiceTransactionType.NATIONAL);
		defaultFiscalCreditor.setName("Agencia Tributaria");
		CreditorDAO.save(context, defaultFiscalCreditor);
		context.log().info("Default Fiscal Creditor inserted!");
		
		AppParamDAO.saveApplicationParameter(context, new ApplicationParameter()
				.setDomain(context.getDomainId())
				.setName(AppParam.FS_ADMON_CREDITOR)
				.setValue(defaultFiscalCreditor.getId().toString()));
		context.log().info("App Param FS_ADMON_CREDITOR set to " + defaultFiscalCreditor.getId());
	}
	
	private static void insertGeozones(AONContext ctx, Domain domain) {
		GeoZone spain = GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setCode("ES").setName("ESPAÑA"));
		GeoZoneDAO.bind(ctx, domain.getId(), null, spain.getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Álava").setCode("01")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Albacete").setCode("02")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Alicante").setCode("03")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Almería").setCode("04")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Ávila").setCode("05")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Badajoz").setCode("06")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Islas Baleares").setCode("07")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Barcelona").setCode("08")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Burgos").setCode("09")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Cáceres").setCode("10")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Cádiz").setCode("11")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Castellón").setCode("12")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Ciudad Real").setCode("13")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Córdoba").setCode("14")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("La Coruña").setCode("15")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Cuenca").setCode("16")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Gerona").setCode("17")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Granada").setCode("18")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Guadalajara").setCode("19")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Guipúzcoa").setCode("20")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Huelva").setCode("21")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Huesca").setCode("22")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Jaén").setCode("23")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("León").setCode("24")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Lleida").setCode("25")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("La Rioja").setCode("26")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Lugo").setCode("27")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Madrid").setCode("28")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Málaga").setCode("29")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Murcia").setCode("30")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Navarra").setCode("31")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Orense").setCode("32")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Asturias").setCode("33")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Palencia").setCode("34")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Las Palmas").setCode("35")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Pontevedra").setCode("36")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Salamanca").setCode("37")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Santa Cruz de Tenerife").setCode("38")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Cantabria").setCode("39")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Segovia").setCode("40")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Sevilla").setCode("41")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Soria").setCode("42")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Tarragona").setCode("43")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Teruel").setCode("44")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Toledo").setCode("45")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Valencia").setCode("46")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Valladolid").setCode("47")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Vizcaya").setCode("48")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Zamora").setCode("49")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Zaragoza").setCode("50")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Ceuta").setCode("51")).getId());
		GeoZoneDAO.bind(ctx, domain.getId(), spain.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Melilla").setCode("52")).getId());
	}
	
}
