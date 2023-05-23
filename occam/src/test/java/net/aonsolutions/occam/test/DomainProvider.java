package net.aonsolutions.occam.test;

import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.config.RegistryAddress;
import net.aonsolutions.occam.api.constants.AonApp;
import net.aonsolutions.occam.api.constants.AonModule;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.SecurityLevel;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.dao.GeozoneDAO;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.server.AonEnumUtils;

class DomainProvider {
	
	private DomainProvider() {
	}
	
	static Domain getOrCreateDomain(AONContext ctx,String domainName, String user) {
		return DomainDAO
			.get(ctx, p -> p.withName().eq(domainName), b -> b)
			.orElseGet(() -> createFullDomain(ctx, domainName, user)); 
	}

	private static Domain createFullDomain(AONContext ctx, String domainName, String user) {
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
		Domain domain = DomainDAO.get(ctx, f -> f.withId().eq(newDomainId), b -> b).get();
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

		
		AonModule[] modules = new AonModule[] {
			 AonModule.CRM			,AonModule.MANAGEMENT	,AonModule.WAREHOUSE	,AonModule.GROUPWARE
			,AonModule.ACCOUNTING	,AonModule.FISCAL		,AonModule.PAYROLL		,AonModule.DOCUMENT
			,AonModule.POS			,AonModule.CALL_CENTER	,AonModule.SUITE_PORTAL};
		for (AonModule module : modules) {
			ctx.getDslContext().insertInto(DOMAIN_APPLICATION_MODULE)
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN, newDomainId)
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION, newDomainApplicationId)
				.set(DOMAIN_APPLICATION_MODULE.MODULE, module.value())
				.execute();
		}

		Registry r = AonFaker.getRegistry() 
				.setDocumentType(DocumentType.CIF)
				.setDocumentCountry(Country.ES)
				.setDocument("B01487271")
				.setName("AON Solutions, S.L.")
				.setAlias("AON")
				.setNationality(Country.ES);
		int newCompanyId = ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN, newDomainId)
			.set(REGISTRY.DOCUMENT, r.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, r.getDocumentType().value())
			.set(REGISTRY.DOCUMENT_COUNTRY, r.getDocumentCountry().value())
			.set(REGISTRY.NAME, r.getDocument())
			.set(REGISTRY.ALIAS, r.getDocument())
			.set(REGISTRY.NATIONALITY, r.getNationality().value())
			.set(REGISTRY.SECURITY_LEVEL, SecurityLevel.OFFICIAL.value() )
			.returning(REGISTRY.ID)
			.fetchOne()
			.getId();
		ctx.log().info("Aplicacion de dominio insertada correctamente");
		
		ctx.getDslContext().insertInto(COMPANY)
			.set(COMPANY.REGISTRY, newCompanyId)
			.set(COMPANY.DOMAIN, newDomainId)
			.set(COMPANY.ACTIVE, (byte) 1)
			.set(COMPANY.SURCHARGE, (byte) 0)
			.set(COMPANY.VAT_ACCRUAL_PAYMENT, (byte) 0)
			.set(COMPANY.E_INVOICE, (byte) 0)
			.execute();

		RegistryAddress address = AonFaker.getRegistryAddress()
				.setRegistry(newCompanyId)
				.setDomain(newDomainId)
				.setGeozone(GeozoneDAO.get(ctx, f -> f.withCode().eq("01"), b -> b).orElse(null));
		ctx.getDslContext().insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN,address.getDomain())
			.set(RADDRESS.REGISTRY,address.getRegistry())
			.set(RADDRESS.TYPE, (byte) (address.isMain()? 1:0))
			.set(RADDRESS.RECIPIENT,address.getRecipient())
			.set(RADDRESS.STREET_TYPE,address.getStreetType()==null?null:address.getStreetType().getAeatCode())
			.set(RADDRESS.ADDRESS,address.getAddress())	
			.set(RADDRESS.NUMBER,address.getNumber())	
			.set(RADDRESS.ADDRESS2,address.getAddress2())
			.set(RADDRESS.ADDRESS3,address.getAddress3())
			.set(RADDRESS.ZIP,address.getZip())
			.set(RADDRESS.CITY,address.getCity())
			.set(RADDRESS.GEOZONE,address.getGeozone() == null ? null : address.getGeozone().getId())	
			.set(RADDRESS.ALIAS,address.getAlias())
			.set(RADDRESS.MUNICIPALITY_CODE,address.getMunicipalityCode())
			.execute();

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
			.set(ENTERPRISE.REGISTRY, newCompanyId)
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
		.set(WORKPLACE.ENTERPRISE, newCompanyId )
		.set(WORKPLACE.SCOPE, newScopeId)
		.set(WORKPLACE.ECONOMICAGREEMENT, AonEnumUtils.getByte( AonRandom.getAdministration(10).orElse(null) ))
		.execute();
		ctx.log().info("Workplace insertada correctamente");
		
		return domain;
	}
	
	private static void insertGeozones(AONContext ctx, Domain domain) {
		Geozone spain = insert(ctx, new Geozone().setDomain( domain.getId() ).setCode("ES").setName("ESPAÑA"));
		bind(ctx, domain.getId(), null, spain.getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Álava").setCode("01")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Albacete").setCode("02")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Alicante").setCode("03")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Almería").setCode("04")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Ávila").setCode("05")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Badajoz").setCode("06")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Islas Baleares").setCode("07")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Barcelona").setCode("08")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Burgos").setCode("09")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Cáceres").setCode("10")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Cádiz").setCode("11")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Castellón").setCode("12")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Ciudad Real").setCode("13")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Córdoba").setCode("14")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("La Coruña").setCode("15")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Cuenca").setCode("16")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Gerona").setCode("17")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Granada").setCode("18")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Guadalajara").setCode("19")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Guipúzcoa").setCode("20")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Huelva").setCode("21")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Huesca").setCode("22")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Jaén").setCode("23")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("León").setCode("24")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Lleida").setCode("25")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("La Rioja").setCode("26")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Lugo").setCode("27")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Madrid").setCode("28")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Málaga").setCode("29")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Murcia").setCode("30")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Navarra").setCode("31")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Orense").setCode("32")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Asturias").setCode("33")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Palencia").setCode("34")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Las Palmas").setCode("35")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Pontevedra").setCode("36")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Salamanca").setCode("37")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Santa Cruz de Tenerife").setCode("38")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Cantabria").setCode("39")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Segovia").setCode("40")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Sevilla").setCode("41")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Soria").setCode("42")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Tarragona").setCode("43")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Teruel").setCode("44")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Toledo").setCode("45")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Valencia").setCode("46")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Valladolid").setCode("47")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Vizcaya").setCode("48")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Zamora").setCode("49")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Zaragoza").setCode("50")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Ceuta").setCode("51")).getId());
		bind(ctx, domain.getId(), spain.getId(), insert(ctx, new Geozone().setDomain( domain.getId() ).setName("Melilla").setCode("52")).getId());
	}
	public static void bind(AONContext ctx, Integer domain, Integer parentId, Integer childId) {
		ctx.checkWrite();
		ctx.getDslContext()
			.insertInto(GEOTREE)
			.set(GEOTREE.DOMAIN, domain)
			.set(GEOTREE.PARENT,parentId)
			.set(GEOTREE.CHILD,childId)
			.execute();
	}
	public static Geozone insert(AONContext ctx, Geozone geozone) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
			.insertInto(GEOZONE)
			.set(GEOZONE.DOMAIN,geozone.getDomain())
			.set(GEOZONE.CODE,geozone.getCode())
			.set(GEOZONE.NAME,geozone.getName())
			.set(GEOZONE.SYSTEM, AonEnumUtils.getByte(geozone.isSystem()))
			.returning(GEOZONE.ID)
			.fetchOne()
			.getValue(GEOZONE.ID);
		return geozone.setId(id);
	}
	
}
