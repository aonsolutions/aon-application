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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

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
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Cnae2009DAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DefaultsDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IAEDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class TestDomainProvider {
	
	private TestDomainProvider() {
	}
	
	static Domain getOrCreateDomain(AONContext ctx, Environment env) {
		Domain domain = DomainDAO.getDomain(ctx, p -> p.getNameProperty().eq(env.getDomainName()));
		if (domain == null || domain.getId() == null) {
			domain = createFullDomain(ctx, env.getDomainName(), env.getUser());
			Occam occam = new Occam()
				.setDomainName( domain.getName())
				.setDomain(domain.getId())
				.setUser(env.getUser());
			try ( CloseableAONContext context = AONContext.getAONContext(occam)) {
				initializeDomain(env, context , domain);
			}
		}
		return domain;
	}
	
	private static Domain createFullDomain(AONContext ctx,String domainName, String user) {
		
		Domain consoleDomain = DomainDAO.getDomain(ctx, 0);
		if (consoleDomain == null ) {
			int newConsoleId = ctx
				.getDslContext()
				.insertInto(DOMAIN)
				.set(DOMAIN.CREATION_USER, user)
				.set(DOMAIN.CREATION_DATE, new java.sql.Timestamp(System.currentTimeMillis()))
				.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
				.set(DOMAIN.TYPE, DomainType.ADMIN.value())
				.set(DOMAIN.OWNER, user )
				.set(DOMAIN.NAME, "console-aonsolutions.test" )
				.set(DOMAIN.DESCRIPTION, "console-aonsolutions.test" )
				.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
				.set(DOMAIN.MAXDEFINEDUSERS, 1)
				.set(DOMAIN.MAXDOCUMENTSIZE, 1)
				.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16).returning(DOMAIN.ID)
				.fetchOne().getId();
			ctx.getDslContext().execute("UPDATE domain SET id = 0 WHERE id = " + newConsoleId);
			ctx.log().info("Dominio CONSOLE insertado correctamente");
		}
		
		int newDomainId = ctx
			.getDslContext()
			.insertInto(DOMAIN)
			.set(DOMAIN.CREATION_USER, user)
			.set(DOMAIN.CREATION_DATE, new java.sql.Timestamp(System.currentTimeMillis()))
			.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
			.set(DOMAIN.TYPE, DomainType.ENTERPRISE.value())
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
		
		TestDomainDefaults.insertGeozones(ctx, domain);
		
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
		
		Auth auth = new Auth()
			.setEmail( domainName + "@aonsolutions.net" )
			.setPassword("0DPiKuNIrrVmD8IUCuw1hQxNqZc=")
			.setName("Default")
			.setSurname("User")
			.setDocument("11111111H")
			.setPhone("666666666")			
		;
		auth = AuthDAO.saveAuth(ctx, auth);
		
		int newUserId = ctx.getDslContext().insertInto(USER)
			.set(USER.DOMAIN , newDomainId)
			.set(USER.NAME, "DEFAULT USER")
			.set(USER.LOGIN, user)
			.set(USER.AUTH, auth.getAuth() )
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
	
    public static String hex(byte[] bytes) {
    	if ( bytes == null )
    		return null;
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
        }
        return result.toString();
    }

	private static void initializeDomain(Environment env, AONContext context, Domain domain) {
		
		DefaultsDAO.loadDefaults(context);
		Iae iae = IAEDAO.getRandom(context, null);
		Cnae2009 cnae2009 = Cnae2009DAO.getRandom(context, null);
		Company company = CompanyDAO.getCompany(context, context.getDomainId());
		AonCollectionUtils.stream(VATRegime.values())
			.forEach( v -> {
				context.getDslContext().insertInto(ENTERPRISE_ACTIVITY)
					.set(ENTERPRISE_ACTIVITY.DOMAIN, context.getDomainId())
					.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte) 1)
					.set(ENTERPRISE_ACTIVITY.ENTERPRISE, company.getId())
					.set(ENTERPRISE_ACTIVITY.DESCRIPTION, AonStringUtils.abbreviate(iae.getTitle(), 64))
					.set(ENTERPRISE_ACTIVITY.IAE, iae.getId())
					.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009.getId())
					.set(ENTERPRISE_ACTIVITY.TYPE, SSRegimeType.GENERAL.getValue())
					.set(ENTERPRISE_ACTIVITY.VAT_REGIME, v.value())
					.set(ENTERPRISE_ACTIVITY.VAT_EXEMPTION_CAUSE, VATExemptionCause.E1.value())
					.set(ENTERPRISE_ACTIVITY.RETENTION_REGIME, IRPFRegime.NORMAL.value())
					.set(ENTERPRISE_ACTIVITY.START_DATE, AonDateUtils.toSql( AonDateUtils.getYearFirstDay(2010) ) )
				.execute();
				context.log().info("EnterpriseActivity created. Regime:  " + v.name());
		});
		

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
		
		TestDomainDefaults.insertAccounts(context, domain);
		TestDomainDefaults.insertTaxes( context, domain );		
		insertProducts( context, domain );
		insertCustomers( context, domain );
		loadCustomers( context, domain );
		insertCustomerFees( context, domain );
		env.initializeDomain(context);
	}

	private static Integer getGeozoneId(AONContext ctx,String code) {
		GeoZone g = GeoZoneDAO.get(ctx, code);
		return g == null ? null : g.getId();
	}
	
	private static void insertProducts(AONContext ctx, Domain domain) {
		Tax tax = TaxDAO.stream(ctx, domain.getId())
			.filter( t -> AonNumberUtils.equals(21, t.getPercentage()))
			.findFirst()
			.orElse(null);
		AonCollectionUtils.stream( 20).forEach( i -> insertProduct(ctx, domain, tax, i + 1));
		AonCollectionUtils.stream( 20).forEach( i -> insertPrepayment(ctx, domain, tax, i + 1));
	}
	
	private static void insertProduct(AONContext ctx, Domain domain, Tax tax, int i) {
		insertProduct(ctx, domain, tax, i, String.format("PRO_%03d", i), ProductType.COMMERCIAL_PRODUCT);
	}
	private static void insertPrepayment(AONContext ctx, Domain domain, Tax tax, int i) {
		insertProduct(ctx, domain, tax, i, String.format("SUP_%03d", i), ProductType.PREPAYMENT);
	}
	
	private static void insertProduct(AONContext ctx, Domain domain, Tax tax, int i, String code, ProductType type) {
		Product product = new Product()
			.setDomain(domain)
			.setName("PRODUCTO DE PRUEBAS " + code)
			.setCode(code)
			.setStatus(ProductStatus.ACTIVE)
			.setType(type)
			.setKind(ProductKind.SALE_PURCHASE)
			.setVat(tax)
		;
		Item item = new Item();
		item
			.setDomain(domain)
			.setProduct(product)
			.setDescription("PRODUCTO DE PRUEBAS " + code)
			.setStatus(ProductStatus.ACTIVE)
			.setPrice(100.0 + i)
		;
		ItemDAO.save(ctx, item);
	}

	private static void insertCustomers(AONContext ctx, Domain domain) {
		CustomerFull cB98351984 = new CustomerFull();
		cB98351984
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B98351984").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("PEDROSA MARCO CONSULTORES SL.P")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("Teruel").setNumber("15").setAddress3("Esc. B pta 6").setZip("46008").setCity("Valencia").setGeozone(getGeozoneId(ctx,"46")));
		CustomerDAO.save(ctx, cB98351984);
		
		CustomerFull cB95717484 = new CustomerFull();
		cB95717484
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B95717484").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("IBAIGANE CONSULTING, S.L.")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("LAS MERCEDES").setNumber("38").setAddress2("BAJOS").setAddress3("").setZip("48930").setCity("GETXO").setGeozone(getGeozoneId(ctx,"48")));
		CustomerDAO.save(ctx, cB95717484);
		
		CustomerFull c15247056B = new CustomerFull();
		c15247056B
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("15247056B").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("JAVIER BILBAO LEIZA")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.PLAZA)).setAddress("GUIPUZKOA").setNumber("6").setAddress2("BAJOS").setAddress3("").setZip("20280").setCity("HONDARRIBIA").setGeozone(getGeozoneId(ctx,"20")));
		CustomerDAO.save(ctx, c15247056B);
		
		CustomerFull c07485941Q = new CustomerFull();
		c07485941Q
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("07485941Q").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("JORGE RUIZ ESCAGEDO")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("ARCA DEL AGUA").setNumber("1 2C").setZip("28300").setCity("Aranjuez").setGeozone(getGeozoneId(ctx,"28")));
		CustomerDAO.save(ctx, c07485941Q);

		CustomerFull c52717592M = new CustomerFull();
		c52717592M
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("52717592M").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("MIGUEL ANGEL SILVESTRE CALABUIG")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.AV)).setAddress("Francisco Cerdá").setNumber("47").setAddress2("Bajo").setAddress3("").setZip("46870").setCity("Ontinyent").setGeozone(getGeozoneId(ctx,"46")));
		CustomerDAO.save(ctx, c52717592M);

		CustomerFull cB66068065 = new CustomerFull();
		cB66068065
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B66068065").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("RUCS DEL CORREDOR, S.L.")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CSRIO)).setAddress("Rimblas").setNumber("S/N").setZip("08318").setCity("Dosrius").setGeozone(getGeozoneId(ctx,"08")));
		CustomerDAO.save(ctx, cB66068065);

		CustomerFull cE07170327 = new CustomerFull();
		cE07170327
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("E07170327").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("SEBASTIAN MAS C.B.")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("ANSELM CLAVE").setNumber("2").setAddress2("1º").setZip("07002").setCity("PALMA DE MALLORCA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, cE07170327);

		CustomerFull cB98465644 = new CustomerFull();
		cB98465644
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B98465644").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("BUSINESS GROUP CANDEL SL")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.AV)).setAddress("Cortes Valencianas").setNumber("28").setAddress2("13-A").setZip("46015").setCity("VALENCIA").setGeozone(getGeozoneId(ctx,"46")));
		CustomerDAO.save(ctx, cB98465644);

		CustomerFull c75407353J = new CustomerFull();
		c75407353J
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("75407353J").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("JOSE FRANCISCO ROJAS RODRIGUEZ")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("VIRGEN DE CONSOLACION").setNumber("23").setAddress2("1º").setAddress3("CENTRO").setZip("41710").setCity("UTRERA").setGeozone(getGeozoneId(ctx,"41")));
		CustomerDAO.save(ctx, c75407353J);
		
		CustomerFull c43162588Y = new CustomerFull();
		c43162588Y
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("43162588Y").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("MONTIS FORTEZA, FERNANDO")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.PLAZA)).setAddress("SAN JAIME").setNumber("7 BJS").setZip("07012").setCity("PALMA DE MALLORCA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, c43162588Y);
		
		CustomerFull cB57551251 = new CustomerFull();
		cB57551251
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("B57551251").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("COBEL CONSULTING, S.L.U.")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("GASPÀR SABATER").setNumber("8").setAddress2("BJS.").setZip("07010").setCity("PALMA MALLORCA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, cB57551251);
		
		CustomerFull c43102210A = new CustomerFull();
		c43102210A
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("43102210A").setDocumentType(DocumentType.NIF).setDocumentCountry(Country.ES).setName("POU VIVES, MIGUEL")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("MAYOR").setNumber("74").setZip("07520").setCity("PETRA").setGeozone(getGeozoneId(ctx,"07")));
		CustomerDAO.save(ctx, c43102210A);
		
		// CLIENTE INTRACOMUNITARIO
		CustomerFull i393356000000 = new CustomerFull();
		i393356000000
			.setRegistry(new Customer()
				.copy(new Registry()
					.setDomain(domain)
					.setDocument("12487773327")
					.setDocumentType(DocumentType.OTHER)
					.setDocumentCountry(Country.FR)
					.setName("STE AMAZON EU SARL"))
				.setTransaction(InvoiceTransactionType.INTRACOMMUNITY))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("BOULEVARD DE VAURGIRARD").setNumber("44").setZip("75757").setCity("PARIS").setGeozone(getGeozoneId(ctx,"F1")));
		CustomerDAO.save(ctx, i393356000000);
		
		// CLIENTE EXTRACOMUNITARIO
		CustomerFull i999999999A = new CustomerFull();
		i999999999A
			.setRegistry(new Customer()
				.copy(new Registry()
					.setDomain(domain)
					.setDocument("999999999A")
					.setDocumentType(DocumentType.OTHER)
					.setDocumentCountry(Country.TW)
					.setName("REIFY TECHNOLOGY LTD"))
				.setTransaction(InvoiceTransactionType.INTRACOMMUNITY))
			.addAddress(new RegistryAddress().setMain(true)
				.setStreetType((StreetType.CALLE))
				.setAddress("Bei Xin St")
				.setZip("30044")
				.setCity("Hsinchu City"));
		CustomerDAO.save(ctx, i999999999A);
		
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
			.addAddress(new RegistryAddress().setMain(true)
				.setStreetType((StreetType.CALLE))
				.setAddress("Dársena Pesquera Via SE 18")
				.setZip("38180")
				.setCity("SANTA CRUZ DE TENERIFE")
				.setGeozone(getGeozoneId(ctx,"38")))
			;
		CustomerDAO.save(ctx, canarias1 );
		
		// CLIENTE CONTADO
		CustomerFull cContado = new CustomerFull();
		cContado
			.setRegistry(new Customer().copy(new Registry()
				.setDomain(domain)
				.setDocument(null)
				.setDocumentType(DocumentType.NIF)
				.setDocumentCountry(Country.ES)
				.setName("CLIENTE CONTADO")))
			;
		CustomerDAO.save(ctx, cContado);

		// CARACTER CHUNGO
		CustomerFull cX3654266A = new CustomerFull();
		cX3654266A
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("X3654266A").setDocumentType(DocumentType.NIE).setDocumentCountry(Country.ES).setName("JEAN FRANÇOIS VICENT COURTINAT")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("Luis Huici").setNumber("12").setAddress2("1ºB").setZip("15010").setCity("La Coruña").setGeozone(getGeozoneId(ctx,"15")));
		CustomerDAO.save(ctx, cX3654266A);
		
		CustomerFull cF61024808 = new CustomerFull();
		cF61024808
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("F61024808").setDocumentType(DocumentType.CIF).setDocumentCountry(Country.ES).setName("L´OBRADOR, S.C.C.L.")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("LOURDES").setNumber("7").setZip("08358").setCity("ARENYS DE MUNT").setGeozone(getGeozoneId(ctx,"08")));
		CustomerDAO.save(ctx, cF61024808);
		

		// NO CENSADO EN AEAT
		CustomerFull cX1485566L = new CustomerFull();
		cX1485566L
			.setRegistry(new Customer().copy(new Registry().setDomain(domain).setDocument("X1485566L").setDocumentType(DocumentType.NIE).setDocumentCountry(Country.ES).setName("GOLDEN GATE INSTITUTE")))
			.addAddress(new RegistryAddress().setMain(true).setStreetType((StreetType.CALLE)).setAddress("Errihera Kalea").setNumber("1 Bis").setAddress2("Bajo").setZip("20750").setCity("Zumaia").setGeozone(getGeozoneId(ctx,"20")));
		CustomerDAO.save(ctx, cX1485566L);
	}

	private static void insertCustomerFees(AONContext context, Domain domain) {
		Integer workplaceId = context.getDslContext()
			.select(WORKPLACE.ID)
			.from(WORKPLACE)
			.where(WORKPLACE.DOMAIN.eq(domain.getId()))
			.limit(1)
			.fetchOneInto(Integer.class);
		HashSet<String> nifs = new HashSet<>();
		AonCollectionUtils.stream(ThreadLocalRandom.current().nextInt(50, 250))
			.mapToObj( i -> CustomerDAO.getRandom(context, f -> f.getDomainProperty().eq(domain.getId())) )
			.filter( customer -> {
				String doc = customer.getDocument();
				if ( nifs.contains( doc ) ) {
					return false;
				} else {
					nifs.add( doc );
					return true;
				}
			})
			.forEach( customer -> {
				AonCollectionUtils.stream(ThreadLocalRandom.current().nextInt(1, 6))
					.forEach( i -> {
						Fee fee = new Fee().setCustomer( customer ).setDomain( domain );
						Date start = AonDateUtils.getMonthFirstDay(AonDateUtils.today());
						Item item = ItemDAO.getRandom( context, f -> 
							f.getDomainProperty().eq(domain.getId())
								.and( f.getProductTypeProperty().eq( ProductType.COMMERCIAL_PRODUCT.value()) )
						);
						if (item != null) {
							fee.setItem( new OldItem().setId( item.getId() ) )
								.setDescription( "CUOTA " + item.getDescription() )
								.setQuantity( 1.0 )
								.setPrice( item.getPrice() )
								.setStartDate( start )
								.setBillingDate( start )
								.setPeriod( BillingPeriod.MONTHLY )
								.setSecurityLevel( SecurityLevel.OFFICIAL )
								.setWorkplace( new Workplace().setId( workplaceId ) );
							FeeDAO.save(context, fee);
						}
					});
			});
	}
	
	private static void loadCustomers(AONContext ctx, Domain domain) {
		try {
			ctx.log().info("Cargando resto de clientes");
			InputStream input = TestDomainProvider.class.getResourceAsStream("customers.csv");
			InputStreamReader isr = new InputStreamReader(input, StandardCharsets.ISO_8859_1);
			LineNumberReader reader = new LineNumberReader(isr);
			while (reader.ready()) {
				String line = reader.readLine();
				String[] parts = AonStringUtils.splitPreserveAllTokens(line,'|');
				String doc = parts[0];
				String[] docParts = AonStringUtils.splitPreserveAllTokens(doc,'/');
				String documentType = docParts[0];
				DocumentType dt = DocumentType.safeValueOf(documentType);
				String documentCountry = docParts[1];
				Country dc = Country.safeValueOf(documentCountry);
				String document = docParts[2];
				if ("B95767604".equals(document)) {
					System.out.println( document );
				}
				String name = parts[1];
				String nationality = parts[2];
				Country nat = Country.safeValueOf(nationality);
				
				
				
				String address = parts[3];
				String streetType = AonStringUtils.substring(address, 0,2);
				String adr = AonStringUtils.substring(address, 2);
				StreetType st = StreetType.safeValueOf(streetType);
				String city = parts[4];
				String zip = parts[5];
				String geozone = parts[6];
				Integer geozoneId = getGeozoneId(ctx,geozone);
				
				CustomerFull cf = new CustomerFull();
				cf.setRegistry(
					new Customer().copy(
						new Registry()
							.setDomain(domain)
							.setDocument(document)
							.setDocumentType(dt)
							.setDocumentCountry(dc)
							.setNationality(nat)
							.setName(name)))
					.addAddress(
						new RegistryAddress()
							.setMain(true)
							.setStreetType(st)
							.setAddress(adr)
							.setZip(zip)
							.setCity(city)
							.setGeozone(getGeozoneId(ctx, geozone)));
				CustomerDAO.save(ctx, cf);
			}
			ctx.log().info("Fin carga de clientes");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
}





