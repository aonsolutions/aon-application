package net.aonsolutions.aon.verifactu;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
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

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Cnae2009DAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DefaultsDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IAEDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
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
				initializeDomain(context, occam, domain);
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

		Registry r = new Registry() 
			.setDocumentType(DocumentType.CIF)
			.setDocumentCountry(Country.ES)
			.setDocument("B01487271")
			.setName("AON Solutions, S.L.")
			.setAlias("AON")
		;
		
		Company company = new Company()
			.copy(r)
			.setActive( true  )
			.setSurcharge( false )
			.setWithholding( false )
			.setVatAccrualPayment( false )
			.seteInvoice( false )
		;
		CompanyFull companyFull = new CompanyFull();
		companyFull.setRegistry(company);
		companyFull.getRegistry().setDomain(domain);
		
		GeoZone alava = GeoZoneDAO.get(ctx, "01");
		RegistryAddress address = new RegistryAddress()
			.setMain(true)
			.setStreetType(StreetType.CALLE)
			.setAddress("Duque de Wellington")
			.setNumber("52")
			.setAddress2("Bajo")
			.setZip("01010")
			.setCity("VITORIA-GASTEIZ")
			.setGeozone(alava==null?null:alava.getId() )
			.setAlias("CENTRAL")
			.setMunicipalityCode("01059")
		;
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
		.set(WORKPLACE.ECONOMICAGREEMENT, Administration.COMMON_TERRITORY.value())
		.execute();
		ctx.log().info("Workplace insertada correctamente");
		
		return domain;
	}
	
	private static void initializeDomain(AONContext context, Occam occam, Domain domain) {
		
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
			.set(ENTERPRISE_ACTIVITY.START_DATE, AonDateUtils.toSql( AonDateUtils.getYearFirstDay(2010) ) )
			.execute();
		context.log().info("EnterpriseActivity created");

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
		
		ApplicationParameter verifactuActiveParam = AppParamDAO.fetchOne(context, AppParam.VERIFACTU_ACTIVE.toString());
		if (verifactuActiveParam == null || verifactuActiveParam.getId() == null) {
			context.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, context.getDomainId())
			.set(APP_PARAM.NAME, AppParam.VERIFACTU_ACTIVE.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
			context.log().info("App Param VERIFACTU_ACTIVE set to TRUE");
		}
		
		ApplicationParameter verifactuTestParam = AppParamDAO.fetchOne(context, AppParam.VERIFACTU_TEST.toString());
		if (verifactuTestParam == null || verifactuTestParam.getId() == null) {
			context.getDslContext().insertInto(APP_PARAM)
			.set(APP_PARAM.DOMAIN, context.getDomainId())
			.set(APP_PARAM.NAME, AppParam.VERIFACTU_TEST.toString())
			.set(APP_PARAM.VALUE, Boolean.TRUE.toString())
			.execute();
			context.log().info("App Param VERIFACTU_TEST set to TRUE");
		}
		
		Creditor defaultFiscalCreditor = new Creditor();
		defaultFiscalCreditor.setDomain(domain);
		defaultFiscalCreditor.setDocumentCountry(Country.ES);
		defaultFiscalCreditor.setDocumentType(DocumentType.CIF);
		defaultFiscalCreditor.setDocument("Q2826000H");
		defaultFiscalCreditor.setName("Agencia Tributaria");
		defaultFiscalCreditor.setTransaction(InvoiceTransactionType.NATIONAL);
		CreditorDAO.save(context, defaultFiscalCreditor);
		context.log().info("Default Fiscal Creditor inserted!");
		
		AppParamDAO.saveApplicationParameter(context, new ApplicationParameter()
			.setDomain(context.getDomainId())
			.setName(AppParam.FS_ADMON_CREDITOR)
			.setValue(defaultFiscalCreditor.getId().toString()));
		context.log().info("App Param FS_ADMON_CREDITOR set to " + defaultFiscalCreditor.getId());
		
		AppParamDAO.saveApplicationParameter(context, new ApplicationParameter()
			.setDomain(context.getDomainId())
			.setName(AppParam.FS_ADMON_RETENTION_CREDITOR)
			.setValue(defaultFiscalCreditor.getId().toString()));
		context.log().info("App Param FS_ADMON_RETENTION_CREDITOR set to " + defaultFiscalCreditor.getId());
		
		insertCustomers( context, domain );
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
		
		GeoZone france = GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setCode("FR").setName("FRANCIA"));
		GeoZoneDAO.bind(ctx, domain.getId(), null, france.getId());
		GeoZoneDAO.bind(ctx, domain.getId(), france.getId(), GeoZoneDAO.insert(ctx, new GeoZone().setDomain( domain.getId() ).setName("Ile de France").setCode("F1")).getId());
	}
	
	private static Integer getGeozoneId(AONContext ctx,String code) {
		GeoZone g = GeoZoneDAO.get(ctx, code);
		return g == null ? null : g.getId();
	}
	
	private static void insertCustomers(AONContext ctx, Domain domain) {
		CustomerFull C_B98351984 = new CustomerFull();
		C_B98351984
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B98351984").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("PEDROSA MARCO CONSULTORES SL.P")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("Teruel").setNumber("15").setAddress3("Esc. B pta 6").setZip("46008").setCity("Valencia").setGeozone(getGeozoneId(ctx,"46")));
		CustomerDAO.save(ctx, C_B98351984);
		
		CustomerFull C_B95717484 = new CustomerFull();
		C_B95717484
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B95717484").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("IBAIGANE CONSULTING, S.L.")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("LAS MERCEDES").setNumber("38").setAddress2("BAJOS").setAddress3("").setZip("48930").setCity("GETXO").setGeozone(getGeozoneId(ctx,"48")));
		CustomerDAO.save(ctx, C_B95717484);
		
		CustomerFull C_15247056B = new CustomerFull();
		C_15247056B
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("15247056B").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("JAVIER BILBAO LEIZA")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.PLAZA)).setAddress("GUIPUZKOA").setNumber("6").setAddress2("BAJOS").setAddress3("").setZip("20280").setCity("HONDARRIBIA").setGeozone(getGeozoneId(ctx,"20")));
		CustomerDAO.save(ctx, C_15247056B);
		
		CustomerFull C_07485941Q = new CustomerFull();
		C_07485941Q
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("07485941Q").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("JORGE RUIZ ESCAGEDO")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("ARCA DEL AGUA").setNumber("1 2C").setZip("28300").setCity("Aranjuez").setGeozone(getGeozoneId(ctx,"28")));
		CustomerDAO.save(ctx, C_07485941Q);

		CustomerFull C_52717592M = new CustomerFull();
		C_52717592M
		.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("52717592M").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("MIGUEL ANGEL SILVESTRE CALABUIG")))
		.addAddress(new RegistryAddress().setStreetType((StreetType.AV)).setAddress("Francisco Cerdá").setNumber("47").setAddress2("Bajo").setAddress3("").setZip("46870").setCity("Ontinyent").setGeozone(getGeozoneId(ctx,"46")));
		CustomerDAO.save(ctx, C_52717592M);

		CustomerFull C_B66068065 = new CustomerFull();
		C_B66068065
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B66068065").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("RUCS DEL CORREDOR, S.L.")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CSRIO)).setAddress("Rimblas").setNumber("S/N").setZip("08318").setCity("Dosrius").setGeozone(getGeozoneId(ctx,"08")));
		CustomerDAO.save(ctx, C_B66068065);

		CustomerFull C_E07170327 = new CustomerFull();
		C_E07170327
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("E07170327").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("SEBASTIAN MAS C.B.")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("ANSELM CLAVE").setNumber("2").setAddress2("1º").setZip("07002").setCity("PALMA DE MALLORCA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, C_E07170327);

		CustomerFull C_B98465644 = new CustomerFull();
		C_B98465644
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B98465644").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("BUSINESS GROUP CANDEL SL")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.AV)).setAddress("Cortes Valencianas").setNumber("28").setAddress2("13-A").setZip("46015").setCity("VALENCIA").setGeozone(getGeozoneId(ctx,"46")));
		CustomerDAO.save(ctx, C_B98465644);

		CustomerFull C_75407353J = new CustomerFull();
		C_75407353J
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("75407353J").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("JOSE FRANCISCO ROJAS RODRIGUEZ")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("VIRGEN DE CONSOLACION").setNumber("23").setAddress2("1º").setAddress3("CENTRO").setZip("41710").setCity("UTRERA").setGeozone(getGeozoneId(ctx,"41")));
		CustomerDAO.save(ctx, C_75407353J);
		
		CustomerFull C_43162588Y = new CustomerFull();
		C_43162588Y
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("43162588Y").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("MONTIS FORTEZA, FERNANDO")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.PLAZA)).setAddress("SAN JAIME").setNumber("7 BJS").setZip("07012").setCity("PALMA DE MALLORCA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, C_43162588Y);
		
		CustomerFull C_B57551251 = new CustomerFull();
		C_B57551251
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B57551251").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("COBEL CONSULTING, S.L.U.")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("GASPÀR SABATER").setNumber("8").setAddress2("BJS.").setZip("07010").setCity("PALMA MALLORCA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, C_B57551251);
		
		CustomerFull C_43102210A = new CustomerFull();
		C_43102210A
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("43102210A").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("POU VIVES, MIGUEL")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("MAYOR").setNumber("74").setZip("07520").setCity("PETRA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, C_43102210A);
		
		// CLIENTE INTRACOMUNITARIO
		CustomerFull I_393356000000 = new CustomerFull();
		I_393356000000
			.setRegistry(new Customer()
				.copy(new Registry()
					.setDomain(domain)
					.setDocument("12487773327")
					.setDocumentType(DocumentType.OTHER)
					.setDocumentCountry(Country.FR)
					.setName("STE AMAZON EU SARL"))
				.setTransaction(InvoiceTransactionType.INTRACOMMUNITY))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("BOULEVARD DE VAURGIRARD").setNumber("44").setZip("75757").setCity("PARIS").setGeozone(getGeozoneId(ctx,"F1")));
		CustomerDAO.save(ctx, I_393356000000);
		
		// CLIENTE EXTRACOMUNITARIO
		CustomerFull I_999999999A = new CustomerFull();
		I_999999999A
			.setRegistry(new Customer()
				.copy(new Registry()
					.setDomain(domain)
					.setDocument("999999999A")
					.setDocumentType(DocumentType.OTHER)
					.setDocumentCountry(Country.TW)
					.setName("REIFY TECHNOLOGY LTD"))
				.setTransaction(InvoiceTransactionType.INTRACOMMUNITY))
			.addAddress(new RegistryAddress()
				.setStreetType((StreetType.CALLE))
				.setAddress("Bei Xin St")
				.setZip("30044")
				.setCity("Hsinchu City"));
		CustomerDAO.save(ctx, I_999999999A);
		
		// CLIENTE CANARIO
		CustomerFull canarias1 = new CustomerFull();
		canarias1
			.setRegistry(new Customer()
				.copy(new Registry()
					.setDomain(domain)
					.setDocument("A38025938")
					.setDocumentType(DocumentType.CIF)
					.setDocumentCountry(Country.ES)
					.setName("NÁUTICA Y DEPORTES TENERIFE S.A."))
				.setTransaction(InvoiceTransactionType.CAN_CEU_MEL))
			.addAddress(new RegistryAddress()
				.setStreetType((StreetType.CALLE))
				.setAddress("Dársena Pesquera Via SE 18")
				.setZip("38180")
				.setCity("SANTA CRUZ DE TENERIFE")
				.setGeozone(getGeozoneId(ctx,"38")))
			;
		CustomerDAO.save(ctx, canarias1 );
		
		// CLIENTE CONTADO
		CustomerFull C_CONTADO = new CustomerFull();
		C_CONTADO
			.setRegistry(new Customer().copy(new Registry()
				.setDomain(domain)
				.setDocument(null)
				.setDocumentType(DocumentType.NIF)
				.setDocumentCountry(Country.ES)
				.setName("CLIENTE CONTADO")))
			;
		CustomerDAO.save(ctx, C_CONTADO);

		// CARACTER CHUNGO
		CustomerFull C_X3654266A = new CustomerFull();
		C_X3654266A
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("X3654266A").setDocumentType(DocumentType.NIE).setDocumentCountry(Country.ES).setName("JEAN FRANÇOIS VICENT COURTINAT")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("Luis Huici").setNumber("12").setAddress2("1ºB").setZip("15010").setCity("La Coruña").setGeozone(getGeozoneId(ctx,"15")));
		CustomerDAO.save(ctx, C_X3654266A);

		// NO CENSADO EN AEAT
		CustomerFull C_X1485566L = new CustomerFull();
		C_X1485566L
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("X1485566L").setDocumentType(DocumentType.NIE).setDocumentCountry(Country.ES).setName("GOLDEN GATE INSTITUTE")))
			.addAddress(new RegistryAddress().setStreetType((StreetType.CALLE)).setAddress("Errihera Kalea").setNumber("1 Bis").setAddress2("Bajo").setZip("20750").setCity("Zumaia").setGeozone(getGeozoneId(ctx,"20")));
		CustomerDAO.save(ctx, C_X1485566L);
	}
}
