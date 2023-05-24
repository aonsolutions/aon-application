package net.aonsolutions.occam.test;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
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

import java.util.LinkedList;
import java.util.Optional;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.config.ApplicationParameter;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.Geozone;
import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.config.RegistryAddress;
import net.aonsolutions.occam.api.constants.AonApp;
import net.aonsolutions.occam.api.constants.AonModule;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.SecurityLevel;
import net.aonsolutions.occam.dao.AccountDAO;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.dao.GeozoneDAO;
import net.aonsolutions.occam.test.faker.AonFaker;
import net.aonsolutions.occam.test.faker.AonRandom;
import net.aonsolutions.watson.client.Pair;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonEnumUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

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
				.fetchOne()
				.getId();
		
		insertGeozones(ctx, newDomainId);
		
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
		int addressId = ctx.getDslContext().insertInto(RADDRESS)
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
			.set(RADDRESS.GEOZONE, AonObjectUtils.<Geozone,Integer>ifOptionalPresent(address.getGeozone(), g -> g.getId()) )	
			.set(RADDRESS.ALIAS,address.getAlias())
			.set(RADDRESS.MUNICIPALITY_CODE,address.getMunicipalityCode())
			.returning(RADDRESS.ID).fetchOne()
			.getId();
		
		

		address.setId(addressId);

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
		
		insertAccounts(ctx, newDomainId);
		insertAppParams(ctx, newDomainId);
		
		Domain domain = DomainDAO.get(ctx, f -> f.withId().eq(newDomainId), b -> b).get();
		ctx.log().info("Dominio " + domain.getName() + " insertado correctamente");
		return domain;
	}
	
	private static void insertGeozones(AONContext ctx, int domain) {
		Geozone spain = insert(ctx, new Geozone().setDomain( domain).setCode("ES").setName("ESPAÑA"));
		bind(ctx, domain, null, spain.getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Álava").setCode("01")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Albacete").setCode("02")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Alicante").setCode("03")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Almería").setCode("04")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Ávila").setCode("05")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Badajoz").setCode("06")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Islas Baleares").setCode("07")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Barcelona").setCode("08")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Burgos").setCode("09")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Cáceres").setCode("10")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Cádiz").setCode("11")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Castellón").setCode("12")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Ciudad Real").setCode("13")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Córdoba").setCode("14")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("La Coruña").setCode("15")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Cuenca").setCode("16")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Gerona").setCode("17")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Granada").setCode("18")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Guadalajara").setCode("19")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Guipúzcoa").setCode("20")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Huelva").setCode("21")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Huesca").setCode("22")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Jaén").setCode("23")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("León").setCode("24")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Lleida").setCode("25")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("La Rioja").setCode("26")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Lugo").setCode("27")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Madrid").setCode("28")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Málaga").setCode("29")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Murcia").setCode("30")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Navarra").setCode("31")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Orense").setCode("32")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Asturias").setCode("33")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Palencia").setCode("34")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Las Palmas").setCode("35")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain).setName("Pontevedra").setCode("36")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Salamanca").setCode("37")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Santa Cruz de Tenerife").setCode("38")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Cantabria").setCode("39")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Segovia").setCode("40")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Sevilla").setCode("41")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Soria").setCode("42")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Tarragona").setCode("43")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Teruel").setCode("44")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Toledo").setCode("45")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Valencia").setCode("46")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Valladolid").setCode("47")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Vizcaya").setCode("48")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Zamora").setCode("49")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Zaragoza").setCode("50")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Ceuta").setCode("51")).getId());
		bind(ctx, domain, spain.getId(), insert(ctx, new Geozone().setDomain( domain ).setName("Melilla").setCode("52")).getId());
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
	
	private static final Account[] ACCOUNTS = new Account[] { 
			new Account().setCode("1").setDescription("FINANCIACION BASICA"),
			new Account().setCode("10").setDescription("CAPITAL."),
			new Account().setCode("100").setDescription("Capital social."),
			new Account().setCode("1000").setDescription("Capital social."),
			new Account().setCode("100000000").setDescription("CAPITAL SOCIAL"),
			new Account().setCode("101").setDescription("Fondo social."),
			new Account().setCode("1010").setDescription("Fondo social."),
			new Account().setCode("102").setDescription("Capital."),
			new Account().setCode("1020").setDescription("Capital."),
			new Account().setCode("103").setDescription("Socios por desembolsos no exigidos.."),
			new Account().setCode("1030").setDescription("Socios por desembolsos no exigidos, capital social."),
			new Account().setCode("1034").setDescription("Socios por desembolsos no exigidos, capital pendiente de inscripcion."),
			new Account().setCode("104").setDescription("Socios por aportaciones no dinerarias pendientes.."),
			new Account().setCode("1040").setDescription("Socios por aportaciones no dinerarias pendientes, capital social."),
			new Account().setCode("1044").setDescription("Socios por aportaciones no dinerarias pendientes, capital pendiente de inscripcion."),
			new Account().setCode("108").setDescription("Acciones o participaciones propias en situaciones especiales."),
			new Account().setCode("1080").setDescription("Acciones o participaciones propias en situaciones especiales."),
			new Account().setCode("109").setDescription("Acciones o participaciones propias para reduccion de capital."),
			new Account().setCode("1090").setDescription("Acciones o participaciones propias para reduccion de capital."),
			new Account().setCode("11").setDescription("RESERVAS."),
			new Account().setCode("110").setDescription("Prima de emision o asuncion."),
			new Account().setCode("1100").setDescription("Prima de emision o asuncion."),
			new Account().setCode("110000001").setDescription("PRIMA DE EMISION"),
			new Account().setCode("111").setDescription("Patrimonio neto por emision de instrumentos financieros compuestos."),
			new Account().setCode("1110").setDescription("Patrimonio neto por emision de instrumentos financieros compuestos."),
			new Account().setCode("1111").setDescription("Resto de instrumentos de patrimonio neto."),
			new Account().setCode("112").setDescription("Reserva legal."),
			new Account().setCode("1120").setDescription("Reserva legal."),
			new Account().setCode("112000000").setDescription("RESERVA LEGAL"),
			new Account().setCode("113").setDescription("Reservas voluntarias."),
			new Account().setCode("1130").setDescription("Reservas voluntarias."),
			new Account().setCode("113000000").setDescription("RESERVAS VOLUNTARIAS"),
			new Account().setCode("114").setDescription("Reservas especiales."),
			new Account().setCode("1140").setDescription("Reservas para acciones o participaciones de la sociedad dominante."),
			new Account().setCode("1141").setDescription("Reservas estatutarias."),
			new Account().setCode("1142").setDescription("Reserva por capital amortizado."),
			new Account().setCode("1143").setDescription("Reserva por fondo de comercio."),
			new Account().setCode("1144").setDescription("Reservas por acciones propias aceptadas en garantia."),
			new Account().setCode("115").setDescription("Reservas por perdidas y ganancias actuariales y otros ajustes."),
			new Account().setCode("1150").setDescription("Reservas por perdidas y ganancias actuariales y otros ajustes."),
			new Account().setCode("118").setDescription("Aportaciones de socios o propietarios."),
			new Account().setCode("1180").setDescription("Aportaciones de socios o propietarios."),
			new Account().setCode("119").setDescription("Diferencias por ajuste del capital a euros."),
			new Account().setCode("1190").setDescription("Diferencias por ajuste del capital a euros."),
			new Account().setCode("12").setDescription("RESULTADOS PENDIENTES DE APLICACION"),
			new Account().setCode("120").setDescription("Remanente."),
			new Account().setCode("1200").setDescription("Remanente."),
			new Account().setCode("120000000").setDescription("RESULTADO EJERCICIO Pendiente de Aplicacion"),
			new Account().setCode("121").setDescription("Resultados negativos de ejercicios anteriores."),
			new Account().setCode("1210").setDescription("Resultados negativos de ejercicios anteriores."),
			new Account().setCode("121000000").setDescription("RESULTADOS NEGATIVOS DE EJERCICIOS ANTERIORES"),
			new Account().setCode("129").setDescription("Resultados del ejercicio."),
			new Account().setCode("1290").setDescription("Resultados del ejercicio."),
			new Account().setCode("129000000").setDescription("RESULTADO DEL EJERCICIO"),
			new Account().setCode("13").setDescription("SUBVENCIONES, DONACIONES Y AJUSTES POR CAMBIOS DE VALOR."),
			new Account().setCode("130").setDescription("Subvenciones oficiales de capital."),
			new Account().setCode("1300").setDescription("Subvenciones oficiales de capital."),
			new Account().setCode("130000001").setDescription("AUSARTU-11"),
			new Account().setCode("131").setDescription("Donaciones y legados de capital."),
			new Account().setCode("1310").setDescription("Donaciones y legados de capital."),
			new Account().setCode("132").setDescription("Otras subvenciones, donaciones y legados."),
			new Account().setCode("1320").setDescription("Otras subvenciones, donaciones y legados."),
			new Account().setCode("133").setDescription("Ajustes por valoracion en instrumentos financieros."),
			new Account().setCode("1330").setDescription("Ajustes por valoracion en instrumentos financieros."),
			new Account().setCode("134").setDescription("Operaciones de cobertura."),
			new Account().setCode("1340").setDescription("Cobertura de flujos de efectivo."),
			new Account().setCode("1341").setDescription("Cobertura de una inversion neta en un negocio en el extranjero."),
			new Account().setCode("135").setDescription("Diferencias de conversion."),
			new Account().setCode("1350").setDescription("Diferencias de conversion."),
			new Account().setCode("136").setDescription("Ajustes por valoracion en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("1360").setDescription("Ajustes por valoracion en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("137").setDescription("Ingresos fiscales a distribuir en varios ejercicios."),
			new Account().setCode("1370").setDescription("Ingresos fiscales por diferencias permanentes a distribuir en varios ejercicios."),
			new Account().setCode("1371").setDescription("Ingresos fiscales por deducciones y bonificaciones a distribuir en varios ejercicios."),
			new Account().setCode("14").setDescription("PROVISIONES."),
			new Account().setCode("140").setDescription("Provision para retribuciones y otras prestaciones al personal."),
			new Account().setCode("1400").setDescription("Provision para retribuciones y otras prestaciones al personal."),
			new Account().setCode("141").setDescription("Provision para impuestos."),
			new Account().setCode("1410").setDescription("Provision para impuestos."),
			new Account().setCode("142").setDescription("Provision para otras responsabilidades."),
			new Account().setCode("1420").setDescription("Provision para otras responsabilidades."),
			new Account().setCode("143").setDescription("Provision por desmantelamiento, retiro o rehabilitacion del inmovilizado."),
			new Account().setCode("1430").setDescription("Provision por desmantelamiento, retiro o rehabilitacion del inmovilizado."),
			new Account().setCode("145").setDescription("Provision para actuaciones medioambientales."),
			new Account().setCode("1450").setDescription("Provision para actuaciones medioambientales."),
			new Account().setCode("146").setDescription("Provision para reestructuraciones."),
			new Account().setCode("1460").setDescription("Provision para reestructuraciones."),
			new Account().setCode("147").setDescription("Provisiones por transacciones con pagos basados en instrumentos de patrimonio."),
			new Account().setCode("1470").setDescription("Provisiones por transacciones con pagos basados en instrumentos de patrimonio."),
			new Account().setCode("15").setDescription("DEUDAS A LARGO PLAZO CON CARACTERISTICAS ESPECIALES."),
			new Account().setCode("150").setDescription("Acciones o participaciones a largo plazo contabilizadas como pasivo."),
			new Account().setCode("1500").setDescription("Acciones o participaciones a largo plazo contabilizadas como pasivo."),
			new Account().setCode("153").setDescription("Desembolsos no exigidos por acciones o participaciones contabilizadas como pasivo."),
			new Account().setCode("1530").setDescription("Desembolsos no exigidos por acciones o participaciones contabilizadas como pasivo."),
			new Account().setCode("154").setDescription("Aportaciones no dinerarias pendientes por acciones o participaciones contabilizadas como pasivo."),
			new Account().setCode("1540").setDescription("Aportaciones no dinerarias pendientes por acciones o participaciones contabilizadas como pasivo."),
			new Account().setCode("16").setDescription("DEUDAS A LARGO PLAZO CON PARTES VINCULADAS."),
			new Account().setCode("160").setDescription("Deudas a largo plazo con entidades de credito vinculadas."),
			new Account().setCode("1600").setDescription("Deudas a largo plazo con entidades de credito vinculadas."),
			new Account().setCode("161").setDescription("Proveedores de inmovilizado a largo plazo, partes vinculadas."),
			new Account().setCode("1610").setDescription("Proveedores de inmovilizado a largo plazo, partes vinculadas."),
			new Account().setCode("162").setDescription("Otras deudas a largo plazo con partes vinculadas."),
			new Account().setCode("1620").setDescription("Otras deudas a largo plazo con partes vinculadas."),
			new Account().setCode("163").setDescription("Otras deudas a largo plazo con partes vinculadas."),
			new Account().setCode("1633").setDescription("Otras deudas a largo plazo empresas del grupo."),
			new Account().setCode("1634").setDescription("Otras deudas a largo plazo empresas asociadas."),
			new Account().setCode("1635").setDescription("Otras deudas a largo plazo con otras partes vinculadas."),
			new Account().setCode("17").setDescription("DEUDAS A LARGO PLAZO POR PRESTAMOS RECIBIDOS Y OTROS CONCEPTOS."),
			new Account().setCode("170").setDescription("Deudas a largo plazo con entidades de credito."),
			new Account().setCode("1700").setDescription("Deudas a largo plazo con entidades de credito."),
			new Account().setCode("170000001").setDescription("PRESTAMO ICO - B.Sabadell"),
			new Account().setCode("170000002").setDescription("PRESTAMO ELKARGI SGR - Bankoa"),
			new Account().setCode("170000003").setDescription("PRESTAMO INVERSION - CaixaBank"),
			new Account().setCode("170000004").setDescription("PRESTAMO INVERSION BEI - Bankia "),
			new Account().setCode("170000005").setDescription("PRESTAMO PSA Finnace (DS5 2443KBS)"),
			new Account().setCode("170000007").setDescription("PRESTAMO BEI - B.Popular"),
			new Account().setCode("170000008").setDescription("PRESTAMO FINANCIACION - BBVA"),
			new Account().setCode("170000011").setDescription("Prestamo B.Santander - ICO (COVID-19)"),
			new Account().setCode("170000012").setDescription("Prestamo BBVA - ICO (COVID 19)"),
			new Account().setCode("170000013").setDescription("Prestamo BANKOA - ELKARGI/ICO"),
			new Account().setCode("171").setDescription("Deudas a largo plazo."),
			new Account().setCode("1710").setDescription("Deudas a largo plazo."),
			new Account().setCode("171000001").setDescription("Deuda CDTI (MAC Asesores)"),
			new Account().setCode("172").setDescription("Deudas a largo plazo transformables en subvenciones, donaciones y legados."),
			new Account().setCode("1720").setDescription("Deudas a largo plazo transformables en subvenciones, donaciones y legados."),
			new Account().setCode("173").setDescription("Proveedores de inmovilizado a largo plazo."),
			new Account().setCode("1730").setDescription("Proveedores de inmovilizado a largo plazo."),
			new Account().setCode("174").setDescription("Acreedores de arrendamiento financiero a largo plazo."),
			new Account().setCode("1740").setDescription("Acreedores de arrendamiento financiero a largo plazo."),
			new Account().setCode("175").setDescription("Efectos a pagar a largo plazo."),
			new Account().setCode("1750").setDescription("Efectos a pagar a largo plazo."),
			new Account().setCode("176").setDescription("Pasivos por derivados financieros a largo plazo."),
			new Account().setCode("1765").setDescription("Pasivos por derivados financieros a largo plazo, cartera de negociacion."),
			new Account().setCode("1768").setDescription("Pasivos por derivados financieros a largo plazo, instrumentos de cobertura."),
			new Account().setCode("177").setDescription("Obligaciones y bonos."),
			new Account().setCode("1770").setDescription("Obligaciones y bonos."),
			new Account().setCode("178").setDescription("Obligaciones y bonos convertibles."),
			new Account().setCode("1780").setDescription("Obligaciones y bonos convertibles."),
			new Account().setCode("179").setDescription("Deudas representadas en otros valores negociables."),
			new Account().setCode("1790").setDescription("Deudas representadas en otros valores negociables."),
			new Account().setCode("18").setDescription("PASIVOS POR FIANZAS Y GARANTIAS Y OTROS CONCEPTOS A LARGO PLAZO."),
			new Account().setCode("180").setDescription("Fianzas recibidas a largo plazo."),
			new Account().setCode("1800").setDescription("Fianzas recibidas a largo plazo."),
			new Account().setCode("180000000").setDescription("FIANZAS FRANQUICIADOS"),
			new Account().setCode("181").setDescription("Anticipos recibidos por ventas o prestaciones de servicios a largo plazo."),
			new Account().setCode("1810").setDescription("Anticipos recibidos por ventas o prestaciones de servicios a largo plazo."),
			new Account().setCode("185").setDescription("Depositos recibidos a largo plazo."),
			new Account().setCode("1850").setDescription("Depositos recibidos a largo plazo."),
			new Account().setCode("189").setDescription("Garantias financieras a largo plazo."),
			new Account().setCode("1890").setDescription("Garantias financieras a largo plazo."),
			new Account().setCode("19").setDescription("SITUACIONES TRANSITORIAS DE FINANCIACION."),
			new Account().setCode("190").setDescription("Acciones o participaciones emitidas."),
			new Account().setCode("1900").setDescription("Acciones o participaciones emitidas."),
			new Account().setCode("192").setDescription("Suscriptores de acciones."),
			new Account().setCode("1920").setDescription("Suscriptores de acciones."),
			new Account().setCode("194").setDescription("Capital emitido pendiente de inscripcion."),
			new Account().setCode("1940").setDescription("Capital emitido pendiente de inscripcion."),
			new Account().setCode("195").setDescription("Acciones o participaciones emitidas consideradas como pasivos financieros."),
			new Account().setCode("1950").setDescription("Acciones o participaciones emitidas consideradas como pasivos financieros."),
			new Account().setCode("197").setDescription("Suscriptores de acciones consideradas como pasivos financieros."),
			new Account().setCode("1970").setDescription("Suscriptores de acciones consideradas como pasivos financieros."),
			new Account().setCode("199").setDescription("Acciones o participaciones emitidas consideradas como pasivos financieros pendientes de inscripcion."),
			new Account().setCode("1990").setDescription("Acciones o participaciones emitidas consideradas como pasivos financieros pendientes de inscripcion."),
			new Account().setCode("2").setDescription("ACTIVO NO CORRIENTE"),
			new Account().setCode("20").setDescription("INMOVILIZACIONES INTANGIBLES."),
			new Account().setCode("200").setDescription("Gastos de investigacion."),
			new Account().setCode("2000").setDescription("Gastos de investigacion."),
			new Account().setCode("201").setDescription("Desarrollo."),
			new Account().setCode("2010").setDescription("Desarrollo."),
			new Account().setCode("201000001").setDescription("Desarrollo plataforma aonSolutions"),
			new Account().setCode("202").setDescription("Concesiones administrativas."),
			new Account().setCode("2020").setDescription("Concesiones administrativas."),
			new Account().setCode("203").setDescription("Propiedad industrial."),
			new Account().setCode("2030").setDescription("Propiedad industrial."),
			new Account().setCode("204").setDescription("Fondo de comercio."),
			new Account().setCode("2040").setDescription("Fondo de comercio."),
			new Account().setCode("205").setDescription("Derechos de traspaso."),
			new Account().setCode("2050").setDescription("Derechos de traspaso."),
			new Account().setCode("206").setDescription("Aplicaciones informáticas."),
			new Account().setCode("2060").setDescription("Aplicaciones informáticas."),
			new Account().setCode("209").setDescription("Anticipos para inmovilizaciones intangibles."),
			new Account().setCode("2090").setDescription("Anticipos para inmovilizaciones intangibles."),
			new Account().setCode("21").setDescription("INMOVILIZACIONES MATERIALES."),
			new Account().setCode("210").setDescription("Terrenos y bienes naturales."),
			new Account().setCode("2100").setDescription("Terrenos y bienes naturales."),
			new Account().setCode("211").setDescription("Construcciones ."),
			new Account().setCode("2110").setDescription("Construcciones ."),
			new Account().setCode("212").setDescription("Instalaciones tecnicas."),
			new Account().setCode("2120").setDescription("Instalaciones tecnicas."),
			new Account().setCode("213").setDescription("Maquinaria."),
			new Account().setCode("2130").setDescription("Maquinaria."),
			new Account().setCode("214").setDescription("Utillaje."),
			new Account().setCode("2140").setDescription("Utillaje."),
			new Account().setCode("215").setDescription("Otras instalaciones."),
			new Account().setCode("2150").setDescription("Otras instalaciones."),
			new Account().setCode("216").setDescription("Mobiliario."),
			new Account().setCode("2160").setDescription("Mobiliario."),
			new Account().setCode("217").setDescription("Equipos para procesos de informacion."),
			new Account().setCode("2170").setDescription("Equipos para procesos de informacion."),
			new Account().setCode("218").setDescription("Elementos de transporte."),
			new Account().setCode("2180").setDescription("Elementos de transporte."),
			new Account().setCode("219").setDescription("Otro inmovilizado material."),
			new Account().setCode("2190").setDescription("Otro inmovilizado material."),
			new Account().setCode("22").setDescription("INVERSIONES INMOBILIARIAS."),
			new Account().setCode("220").setDescription("Inversiones en terrenos y bienes naturales."),
			new Account().setCode("2200").setDescription("Inversiones en terrenos y bienes naturales."),
			new Account().setCode("221").setDescription("Inversiones en construcciones."),
			new Account().setCode("2210").setDescription("Inversiones en construcciones."),
			new Account().setCode("23").setDescription("INMOVILIZACIONES MATERIALES EN CURSO."),
			new Account().setCode("230").setDescription("Adaptacion de terrenos y bienes naturales."),
			new Account().setCode("2300").setDescription("Adaptacion de terrenos y bienes naturales."),
			new Account().setCode("231").setDescription("Construcciones en curso."),
			new Account().setCode("2310").setDescription("Construcciones en curso."),
			new Account().setCode("232").setDescription("Instalaciones tecnicas en montaje."),
			new Account().setCode("2320").setDescription("Instalaciones tecnicas en montaje."),
			new Account().setCode("233").setDescription("Maquinaria en montaje."),
			new Account().setCode("2330").setDescription("Maquinaria en montaje."),
			new Account().setCode("237").setDescription("Equipos para procesos de informacion en montaje."),
			new Account().setCode("2370").setDescription("Equipos para procesos de informacion en montaje."),
			new Account().setCode("239").setDescription("Anticipos para inmovilizaciones materiales."),
			new Account().setCode("2390").setDescription("Anticipos para inmovilizaciones materiales."),
			new Account().setCode("24").setDescription("INVERSIONES FINANCIERAS A LARGO PLAZO EN PARTES VINCULADAS."),
			new Account().setCode("240").setDescription("Participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("2400").setDescription("Participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("241").setDescription("Valores representativos de deuda a largo plazo de partes vinculadas."),
			new Account().setCode("2410").setDescription("Valores representativos de deuda a largo plazo de partes vinculadas."),
			new Account().setCode("242").setDescription("Creditos a largo plazo a partes vinculadas."),
			new Account().setCode("2420").setDescription("Creditos a largo plazo a partes vinculadas."),
			new Account().setCode("249").setDescription("Desembolsos pendientes sobre participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("2490").setDescription("Desembolsos pendientes sobre participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("25").setDescription("OTRAS INVERSIONES FINANCIERAS A LARGO PLAZO."),
			new Account().setCode("250").setDescription("Inversiones financieras a largo plazo en instrumentos de patrimonio."),
			new Account().setCode("2500").setDescription("Inversiones financieras a largo plazo en instrumentos de patrimonio."),
			new Account().setCode("251").setDescription("Valores representativos de deuda a largo plazo."),
			new Account().setCode("2510").setDescription("Valores representativos de deuda a largo plazo."),
			new Account().setCode("252").setDescription("Creditos a largo plazo."),
			new Account().setCode("2520").setDescription("Creditos a largo plazo."),
			new Account().setCode("253").setDescription("Creditos a largo plazo por enajenacion de inmovilizado."),
			new Account().setCode("2530").setDescription("Creditos a largo plazo por enajenacion de inmovilizado."),
			new Account().setCode("254").setDescription("Creditos a largo plazo al personal."),
			new Account().setCode("2540").setDescription("Creditos a largo plazo al personal."),
			new Account().setCode("255").setDescription("Activos por derivados financieros a largo plazo."),
			new Account().setCode("2550").setDescription("Activos por derivados financieros a largo plazo."),
			new Account().setCode("257").setDescription("Activos por retribuciones a largo plazo de prestacion definida."),
			new Account().setCode("2570").setDescription("Activos por retribuciones a largo plazo de prestacion definida."),
			new Account().setCode("258").setDescription("Imposiciones a largo plazo."),
			new Account().setCode("2580").setDescription("Imposiciones a largo plazo."),
			new Account().setCode("259").setDescription("Desembolsos pendientes sobre participaciones en el patrimonio neto a largo plazo."),
			new Account().setCode("2590").setDescription("Desembolsos pendientes sobre participaciones en el patrimonio neto a largo plazo."),
			new Account().setCode("26").setDescription("FIANZAS Y DEPOSITOS CONSTITUIDOS A LARGO PLAZO."),
			new Account().setCode("260").setDescription("Fianzas constituidas a largo plazo."),
			new Account().setCode("2600").setDescription("Fianzas recibidas por Franquiciados a largo plazo."),
			new Account().setCode("265").setDescription("Depositos constituidos a largo plazo."),
			new Account().setCode("2650").setDescription("Depositos constituidos a largo plazo."),
			new Account().setCode("28").setDescription("AMORTIZACION ACUMULADA DEL INMOVILIZADO."),
			new Account().setCode("280").setDescription("Amortizacion acumulada del inmovilizado intangible."),
			new Account().setCode("2800").setDescription("Amortizacion acumulada del inmovilizado intangible."),
			new Account().setCode("2801").setDescription("Amortizacion acumulada de investigacion"),
			new Account().setCode("2802").setDescription("Amortizacion acumulada de desarrollo"),
			new Account().setCode("2803").setDescription("Amortizacion acumulada de concesiones administrativas"),
			new Account().setCode("2804").setDescription("Amortizacion acumulada de propiedad industrial"),
			new Account().setCode("2805").setDescription("Amortizacion acumulada de derechos de traspaso"),
			new Account().setCode("2806").setDescription("Amortizacion acumulada de aplicaciones informáticas"),
			new Account().setCode("281").setDescription("Amortizacion acumulada del inmovilizado material."),
			new Account().setCode("2810").setDescription("Amortizacion acumulada del inmovilizado material."),
			new Account().setCode("2811").setDescription("Amortizacion acumulada de construcciones"),
			new Account().setCode("2812").setDescription("Amortizacion acumulada de instalaciones tecnicas"),
			new Account().setCode("2813").setDescription("Amortizacion acumulada de maquinaria"),
			new Account().setCode("2814").setDescription("Amortizacion acumulada de utillaje"),
			new Account().setCode("2815").setDescription("Amortizacion acumulada de otras instalaciones"),
			new Account().setCode("2816").setDescription("Amortizacion acumulada de mobiliario"),
			new Account().setCode("2817").setDescription("Amortizacion acumulada de equipos para el proceso de informacion"),
			new Account().setCode("2818").setDescription("Amortizacion acumulada de elementos de transporte"),
			new Account().setCode("2819").setDescription("Amortizacion acumulada de otro inmovilizado material"),
			new Account().setCode("282").setDescription("Amortizacion acumulada de las inversiones inmobiliarias."),
			new Account().setCode("2820").setDescription("Amortizacion acumulada de las inversiones inmobiliarias."),
			new Account().setCode("29").setDescription("DETERIORO DE VALOR DE ACTIVOS NO CORRIENTES."),
			new Account().setCode("290").setDescription("Deterioro de valor del inmovilizado intangible."),
			new Account().setCode("2900").setDescription("Deterioro de valor del inmovilizado intangible."),
			new Account().setCode("291").setDescription("Deterioro de valor del inmovilizado material."),
			new Account().setCode("2910").setDescription("Deterioro de valor del inmovilizado material."),
			new Account().setCode("292").setDescription("Deterioro de valor de las inversiones inmobiliarias."),
			new Account().setCode("2920").setDescription("Deterioro de valor de las inversiones inmobiliarias."),
			new Account().setCode("293").setDescription("Deterioro de valor de participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("2930").setDescription("Deterioro de valor de participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("294").setDescription("Deterioro de valor de valores representativos de deuda a largo plazo de partes vinculadas."),
			new Account().setCode("2940").setDescription("Deterioro de valor de valores representativos de deuda a largo plazo de partes vinculadas."),
			new Account().setCode("295").setDescription("Deterioro de valor de creditos a largo plazo a partes vinculadas."),
			new Account().setCode("2950").setDescription("Deterioro de valor de creditos a largo plazo a partes vinculadas."),
			new Account().setCode("297").setDescription("Deterioro de valor de valores representativos de deuda a largo plazo."),
			new Account().setCode("2970").setDescription("Deterioro de valor de valores representativos de deuda a largo plazo."),
			new Account().setCode("298").setDescription("Deterioro de valor de creditos a largo plazo."),
			new Account().setCode("2980").setDescription("Deterioro de valor de creditos a largo plazo."),
			new Account().setCode("3").setDescription("EXISTENCIAS"),
			new Account().setCode("30").setDescription("COMERCIALES."),
			new Account().setCode("300").setDescription("Mercaderias A."),
			new Account().setCode("3000").setDescription("Mercaderias A."),
			new Account().setCode("301").setDescription("Mercaderias B."),
			new Account().setCode("3010").setDescription("Mercaderias B."),
			new Account().setCode("31").setDescription("MATERIAS PRIMAS."),
			new Account().setCode("310").setDescription("Materias primas A."),
			new Account().setCode("3100").setDescription("Materias primas A."),
			new Account().setCode("311").setDescription("Materias primas B."),
			new Account().setCode("3110").setDescription("Materias primas B."),
			new Account().setCode("32").setDescription("OTROS APROVISIONAMIENTOS."),
			new Account().setCode("320").setDescription("Elementos conjuntos incorporables."),
			new Account().setCode("3200").setDescription("Elementos conjuntos incorporables."),
			new Account().setCode("321").setDescription("Combustibles."),
			new Account().setCode("3210").setDescription("Combustibles."),
			new Account().setCode("322").setDescription("Repuestos."),
			new Account().setCode("3220").setDescription("Repuestos."),
			new Account().setCode("325").setDescription("Materiales diversos."),
			new Account().setCode("3250").setDescription("Materiales diversos."),
			new Account().setCode("326").setDescription("Embalajes."),
			new Account().setCode("3260").setDescription("Embalajes."),
			new Account().setCode("327").setDescription("Envases."),
			new Account().setCode("3270").setDescription("Envases."),
			new Account().setCode("328").setDescription("Material de oficina."),
			new Account().setCode("3280").setDescription("Material de oficina."),
			new Account().setCode("33").setDescription("PRODUCTOS EN CURSO."),
			new Account().setCode("330").setDescription("Productos en curso A."),
			new Account().setCode("3300").setDescription("Productos en curso A."),
			new Account().setCode("331").setDescription("Productos en curso B."),
			new Account().setCode("3310").setDescription("Productos en curso B."),
			new Account().setCode("34").setDescription("PRODUCTOS SEMITERMINADOS."),
			new Account().setCode("340").setDescription("Productos semiterminados A."),
			new Account().setCode("3400").setDescription("Productos semiterminados A."),
			new Account().setCode("341").setDescription("Productos semiterminados B."),
			new Account().setCode("3410").setDescription("Productos semiterminados B."),
			new Account().setCode("35").setDescription("PRODUCTOS TERMINADOS."),
			new Account().setCode("350").setDescription("Productos terminados A."),
			new Account().setCode("3500").setDescription("Productos terminados A."),
			new Account().setCode("351").setDescription("Productos terminados B."),
			new Account().setCode("3510").setDescription("Productos terminados B."),
			new Account().setCode("36").setDescription("SUBPRODUCTOS, RESIDUOS Y MATERIALES RECUPERADOS."),
			new Account().setCode("360").setDescription("Subproductos A."),
			new Account().setCode("3600").setDescription("Subproductos A."),
			new Account().setCode("361").setDescription("Subproductos B."),
			new Account().setCode("3610").setDescription("Subproductos B."),
			new Account().setCode("365").setDescription("Residuos A."),
			new Account().setCode("3650").setDescription("Residuos A."),
			new Account().setCode("366").setDescription("Residuos B."),
			new Account().setCode("3660").setDescription("Residuos B."),
			new Account().setCode("368").setDescription("Materiales recuperados A."),
			new Account().setCode("3680").setDescription("Materiales recuperados A."),
			new Account().setCode("369").setDescription("Materiales recuperados B."),
			new Account().setCode("3690").setDescription("Materiales recuperados B."),
			new Account().setCode("39").setDescription("DETERIORO DE VALOR DE LAS EXISTENCIAS."),
			new Account().setCode("390").setDescription("Deterioro de valor de las mercaderias."),
			new Account().setCode("3900").setDescription("Deterioro de valor de las mercaderias."),
			new Account().setCode("391").setDescription("Deterioro de valor de las materias primas."),
			new Account().setCode("3910").setDescription("Deterioro de valor de las materias primas."),
			new Account().setCode("392").setDescription("Deterioro de valor de otros aprovisionamientos."),
			new Account().setCode("3920").setDescription("Deterioro de valor de otros aprovisionamientos."),
			new Account().setCode("393").setDescription("Deterioro de valor de los productos en curso."),
			new Account().setCode("3930").setDescription("Deterioro de valor de los productos en curso."),
			new Account().setCode("394").setDescription("Deterioro de valor de los productos semiterminados."),
			new Account().setCode("3940").setDescription("Deterioro de valor de los productos semiterminados."),
			new Account().setCode("395").setDescription("Deterioro de valor de los productos terminados."),
			new Account().setCode("3950").setDescription("Deterioro de valor de los productos terminados."),
			new Account().setCode("396").setDescription("Deterioro de valor de los subproductos, residuos y materiales recuperados."),
			new Account().setCode("3960").setDescription("Deterioro de valor de los subproductos, residuos y materiales recuperados."),
			new Account().setCode("4").setDescription("ACREEDORES Y DEUDORES POR OPERACIONES COMERCIALES"),
			new Account().setCode("40").setDescription("PROVEEDORES."),
			new Account().setCode("400").setDescription("Proveedores."),
			new Account().setCode("4000").setDescription("Proveedores."),
			new Account().setCode("401").setDescription("Proveedores, efectos comerciales a pagar."),
			new Account().setCode("4010").setDescription("Proveedores, efectos comerciales a pagar."),
			new Account().setCode("403").setDescription("Proveedores, empresas del grupo."),
			new Account().setCode("4030").setDescription("Proveedores, empresas del grupo."),
			new Account().setCode("404").setDescription("Proveedores, empresas asociadas."),
			new Account().setCode("4040").setDescription("Proveedores, empresas asociadas."),
			new Account().setCode("405").setDescription("Proveedores, otras partes vinculadas."),
			new Account().setCode("4050").setDescription("Proveedores, otras partes vinculadas."),
			new Account().setCode("406").setDescription("Envases y embalajes a devolver a proveedores."),
			new Account().setCode("4060").setDescription("Envases y embalajes a devolver a proveedores."),
			new Account().setCode("407").setDescription("Anticipos a proveedores."),
			new Account().setCode("4070").setDescription("Anticipos a proveedores."),
			new Account().setCode("4071").setDescription("Anticipos a acreedores."),
			new Account().setCode("41").setDescription("ACREEDORES VARIOS."),
			new Account().setCode("410").setDescription("Acreedores por prestaciones de servicios."),
			new Account().setCode("4100").setDescription("Acreedores por prestaciones de servicios."),
			new Account().setCode("411").setDescription("Acreedores, efectos comerciales a pagar."),
			new Account().setCode("4110").setDescription("Acreedores, efectos comerciales a pagar."),
			new Account().setCode("419").setDescription("Acreedores por operaciones en comun."),
			new Account().setCode("4190").setDescription("Acreedores por operaciones en comun."),
			new Account().setCode("43").setDescription("CLIENTES."),
			new Account().setCode("430").setDescription("Clientes."),
			new Account().setCode("4300").setDescription("Clientes."),
			new Account().setCode("431").setDescription("Clientes, efectos comerciales a cobrar."),
			new Account().setCode("4310").setDescription("Clientes, efectos comerciales a cobrar."),
			new Account().setCode("432").setDescription("Clientes operaciones de factoring."),
			new Account().setCode("4320").setDescription("Clientes operaciones de factoring."),
			new Account().setCode("433").setDescription("Clientes, empresas del grupo."),
			new Account().setCode("4330").setDescription("Clientes, empresas del grupo."),
			new Account().setCode("434").setDescription("Clientes, empresas asociadas."),
			new Account().setCode("4340").setDescription("Clientes, empresas asociadas."),
			new Account().setCode("435").setDescription("Clientes, otras partes vinculadas."),
			new Account().setCode("4350").setDescription("Clientes, otras partes vinculadas."),
			new Account().setCode("436").setDescription("Clientes de dudoso cobro."),
			new Account().setCode("4360").setDescription("Clientes de dudoso cobro."),
			new Account().setCode("437").setDescription("Envases y embalajes a devolver por clientes."),
			new Account().setCode("4370").setDescription("Envases y embalajes a devolver por clientes."),
			new Account().setCode("438").setDescription("Anticipos de clientes."),
			new Account().setCode("4380").setDescription("Anticipos de clientes."),
			new Account().setCode("44").setDescription("DEUDORES VARIOS."),
			new Account().setCode("440").setDescription("Deudores."),
			new Account().setCode("4400").setDescription("Deudores."),
			new Account().setCode("440000000").setDescription("Deudores"),
			new Account().setCode("441").setDescription("Deudores, efectos comerciales a cobrar."),
			new Account().setCode("4410").setDescription("Deudores, efectos comerciales a cobrar."),
			new Account().setCode("446").setDescription("Deudores de dudoso cobro."),
			new Account().setCode("4460").setDescription("Deudores de dudoso cobro."),
			new Account().setCode("449").setDescription("Deudores por operaciones en comun."),
			new Account().setCode("4490").setDescription("Deudores por operaciones en comun."),
			new Account().setCode("46").setDescription("PERSONAL."),
			new Account().setCode("460").setDescription("Anticipos de remuneraciones."),
			new Account().setCode("4600").setDescription("Anticipos de remuneraciones."),
			new Account().setCode("460000000").setDescription("ANTICIPO DE REMUNERACIONES"),
			new Account().setCode("465").setDescription("Remuneraciones pendientes de pago."),
			new Account().setCode("4650").setDescription("Remuneraciones pendientes de pago."),
			new Account().setCode("465000000").setDescription("REMUNERACIONES PENDIENTES DE PAGO"),
			new Account().setCode("466").setDescription("Remuneraciones mediante sistemas de aportacion definida pendientes de pago."),
			new Account().setCode("4660").setDescription("Remuneraciones mediante sistemas de aportacion definida pendientes de pago."),
			new Account().setCode("47").setDescription("ADMINISTRACIONES PUBLICAS."),
			new Account().setCode("470").setDescription("Hacienda Publica deudora por diversos conceptos."),
			new Account().setCode("4700").setDescription("Hacienda Publica, deudora por IVA."),
			new Account().setCode("470000000").setDescription("H.P. DEUDORA POR IVA"),
			new Account().setCode("4708").setDescription("Hacienda Publica, deudora por subvenciones concedidas."),
			new Account().setCode("4709").setDescription("Hacienda Publica, deudora por devolucion de impuestos."),
			new Account().setCode("471").setDescription("Organismos de la Seguridad Social, deudores."),
			new Account().setCode("4710").setDescription("Organismos de la Seguridad Social, deudores."),
			new Account().setCode("472").setDescription("Hacienda Publica, IVA soportado."),
			new Account().setCode("4720").setDescription("Hacienda Publica, IVA soportado."),
			new Account().setCode("472000000").setDescription("H.P. IVA SOPORTADO"),
			new Account().setCode("473").setDescription("Hacienda Publica, retenciones y pagos a cuenta."),
			new Account().setCode("4730").setDescription("Hacienda Publica, retenciones y pagos a cuenta."),
			new Account().setCode("473000000").setDescription("H.P. RETENCIONES Y PAGOS A CUENTA"),
			new Account().setCode("474").setDescription("Activos por impuesto diferido."),
			new Account().setCode("4740").setDescription("Activos por diferencias temporarias deducibles."),
			new Account().setCode("4742").setDescription("Derechos por deducciones y bonificaciones pendientes de aplicar."),
			new Account().setCode("474200003").setDescription("Ded con lim 2017 ANCN"),
			new Account().setCode("4745").setDescription("Credito por perdidas a compensar del ejercicio."),
			new Account().setCode("475").setDescription("Hacienda Publica acreedora por conceptos fiscales."),
			new Account().setCode("4750").setDescription("Hacienda Publica, acreedora por IVA."),
			new Account().setCode("475000000").setDescription("Hª Pª Acreedora por IMPUESTOS (IVA e IRPF)"),
			new Account().setCode("475000001").setDescription("HP Acreedora por derivacion de deuda"),
			new Account().setCode("4751").setDescription("Hacienda Publica, acreedora por retenciones practicadas."),
			new Account().setCode("475100000").setDescription("H.P. ACREEDORA POR RETENCIONES PRACTICADAS"),
			new Account().setCode("475100001").setDescription("H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Nominas)"),
			new Account().setCode("475100002").setDescription("H.P. ACREEDORA POR RETENCIONES EN ESPECIE (Nominas)"),
			new Account().setCode("475100003").setDescription("H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Profesionales)"),
			new Account().setCode("475100004").setDescription("H.P. ACREEDORA POR RETENCIONES PRACTICADAS (Arrendamiento)"),
			new Account().setCode("4752").setDescription("Hacienda Publica, acreedora por impuesto sobre sociedades."),
			new Account().setCode("475200000").setDescription("HP ACREEDORA POR Impuesto Sociedades"),
			new Account().setCode("4758").setDescription("Hacienda Publica, acreedora por subvenciones a reintegrar."),
			new Account().setCode("476").setDescription("Organismos de la Seguridad Social, acreedores."),
			new Account().setCode("4760").setDescription("Organismos de la Seguridad Social, acreedores."),
			new Account().setCode("476000000").setDescription("ORGANISMOS DE LA S.S. ACREEDORES"),
			new Account().setCode("476000002").setDescription("ORGANISMOS DE LA S.S. ACREEDORES POR AUTONOMOS"),
			new Account().setCode("476000003").setDescription("S.S. Acreedora por Actas Inspeccion"),
			new Account().setCode("477").setDescription("Hacienda Publica, IVA repercutido."),
			new Account().setCode("4770").setDescription("Hacienda Publica, IVA repercutido."),
			new Account().setCode("477000000").setDescription("H.P. IVA REPERCUTIDO"),
			new Account().setCode("479").setDescription("Pasivos por diferencias temporarias imponibles."),
			new Account().setCode("4790").setDescription("Pasivos por diferencias temporarias imponibles."),
			new Account().setCode("479000000").setDescription("PASIVOS POR DIFERENCIAS TEMPORARIAS IMPONIBLES"),
			new Account().setCode("48").setDescription("AJUSTES POR PERIODIFICACION."),
			new Account().setCode("480").setDescription("Gastos anticipados."),
			new Account().setCode("4800").setDescription("Gastos anticipados."),
			new Account().setCode("480000000").setDescription("GASTOS ANTICIPADOS"),
			new Account().setCode("485").setDescription("Ingresos anticipados."),
			new Account().setCode("4850").setDescription("Ingresos anticipados."),
			new Account().setCode("49").setDescription("DETERIORO DE VALOR DE CREDITOS COMERCIALES Y PROVISIONES A CORTO PLAZO."),
			new Account().setCode("490").setDescription("Deterioro de valor de creditos por operaciones comerciales."),
			new Account().setCode("4900").setDescription("Deterioro de valor de creditos por operaciones comerciales."),
			new Account().setCode("493").setDescription("Deterioro de valor de creditos por operaciones comerciales con partes vinculadas."),
			new Account().setCode("4930").setDescription("Deterioro de valor de creditos por operaciones comerciales con partes vinculadas."),
			new Account().setCode("499").setDescription("Provisiones por operaciones comerciales."),
			new Account().setCode("4994").setDescription("Provision para contratos onerosos."),
			new Account().setCode("4999").setDescription("Provision para otras operaciones comerciales."),
			new Account().setCode("5").setDescription("CUENTAS FINANCIERAS"),
			new Account().setCode("50").setDescription("EMPRESTITOS, DEUDAS CON CARACTERISTICAS ESPECIALES Y OTRAS EMISIONES ANALOGAS A CORTO PLAZO."),
			new Account().setCode("500").setDescription("Obligaciones y bonos a corto plazo."),
			new Account().setCode("5000").setDescription("Obligaciones y bonos a corto plazo."),
			new Account().setCode("501").setDescription("Obligaciones y bonos convertibles a corto plazo."),
			new Account().setCode("5010").setDescription("Obligaciones y bonos convertibles a corto plazo."),
			new Account().setCode("502").setDescription("Acciones o participaciones a corto plazo contabilizadas como pasivo."),
			new Account().setCode("5020").setDescription("Acciones o participaciones a corto plazo contabilizadas como pasivo."),
			new Account().setCode("505").setDescription("Deudas representadas en otros valores negociables a corto plazo."),
			new Account().setCode("5050").setDescription("Deudas representadas en otros valores negociables a corto plazo."),
			new Account().setCode("506").setDescription("Intereses de emprestitos y otras emisiones análogas."),
			new Account().setCode("5060").setDescription("Intereses de emprestitos y otras emisiones análogas."),
			new Account().setCode("507").setDescription("Dividendos de emisiones contabilizadas como pasivo."),
			new Account().setCode("5070").setDescription("Dividendos de emisiones contabilizadas como pasivo."),
			new Account().setCode("509").setDescription("Valores negociables amortizados."),
			new Account().setCode("5090").setDescription("Valores negociables amortizados."),
			new Account().setCode("51").setDescription("DEUDAS A CORTO PLAZO CON PARTES VINCULADAS."),
			new Account().setCode("510").setDescription("Deudas a corto plazo con entidades de credito vinculadas."),
			new Account().setCode("5100").setDescription("Deudas a corto plazo con entidades de credito vinculadas."),
			new Account().setCode("511").setDescription("Proveedores de inmovilizado a corto plazo, partes vinculadas."),
			new Account().setCode("5110").setDescription("Proveedores de inmovilizado a corto plazo, partes vinculadas."),
			new Account().setCode("512").setDescription("Acreedores por arrendamiento financiero a corto plazo, partes vinculadas."),
			new Account().setCode("5120").setDescription("Acreedores por arrendamiento financiero a corto plazo, partes vinculadas."),
			new Account().setCode("513").setDescription("Otras deudas a corto plazo con partes vinculadas."),
			new Account().setCode("5130").setDescription("Otras deudas a corto plazo con partes vinculadas."),
			new Account().setCode("514").setDescription("Intereses a corto plazo de deudas con partes vinculadas."),
			new Account().setCode("5140").setDescription("Intereses a corto plazo de deudas con partes vinculadas."),
			new Account().setCode("52").setDescription("DEUDAS A CORTO PLAZO POR PRESTAMOS RECIBIDOS Y OTROS CONCEPTOS."),
			new Account().setCode("520").setDescription("Deudas a corto plazo con entidades de credito."),
			new Account().setCode("5200").setDescription("Prestamos a corto plazo de entidades de credito."),
			new Account().setCode("5201").setDescription("Deudas a corto plazo por credito dispuesto."),
			new Account().setCode("5208").setDescription("Deudas por efectos descontados."),
			new Account().setCode("5209").setDescription("Deudas por operaciones de factoring."),
			new Account().setCode("521").setDescription("Deudas a corto plazo."),
			new Account().setCode("5210").setDescription("Deudas a corto plazo."),
			new Account().setCode("522").setDescription("Deudas a corto plazo transformables en subvenciones, donaciones y legados."),
			new Account().setCode("5220").setDescription("Deudas a corto plazo transformables en subvenciones, donaciones y legados."),
			new Account().setCode("523").setDescription("Proveedores de inmovilizado a corto plazo."),
			new Account().setCode("5230").setDescription("Proveedores de inmovilizado a corto plazo."),
			new Account().setCode("525").setDescription("Efectos a pagar a corto plazo."),
			new Account().setCode("5250").setDescription("Efectos a pagar a corto plazo."),
			new Account().setCode("526").setDescription("Dividendo activo a pagar."),
			new Account().setCode("5260").setDescription("Dividendo activo a pagar."),
			new Account().setCode("527").setDescription("Intereses a corto plazo de deudas con entidades de credito."),
			new Account().setCode("5270").setDescription("Intereses a corto plazo de deudas con entidades de credito."),
			new Account().setCode("528").setDescription("Intereses a corto plazo de deudas."),
			new Account().setCode("5280").setDescription("Intereses a corto plazo de deudas."),
			new Account().setCode("529").setDescription("Provisiones a corto plazo."),
			new Account().setCode("5290").setDescription("Provisiones a corto plazo."),
			new Account().setCode("5291").setDescription("Provision para impuestos"),
			new Account().setCode("53").setDescription("INVERSIONES FINANCIERAS A CORTO PLAZO EN PARTES VINCULADAS."),
			new Account().setCode("530").setDescription("Participaciones a corto plazo en partes vinculadas."),
			new Account().setCode("5303").setDescription("Participaciones a corto plazo en empresas del grupo."),
			new Account().setCode("5304").setDescription("Participaciones a corto plazo en empresas del asociadas."),
			new Account().setCode("5305").setDescription("Participaciones a corto plazo, en otras partes vinculadas."),
			new Account().setCode("531").setDescription("Valores representativos de deuda a corto plazo de partes vinculadas."),
			new Account().setCode("5310").setDescription("Valores representativos de deuda a corto plazo de partes vinculadas."),
			new Account().setCode("532").setDescription("Creditos a corto plazo a partes vinculadas."),
			new Account().setCode("5320").setDescription("Creditos a corto plazo a partes vinculadas."),
			new Account().setCode("533").setDescription("Intereses a corto plazo de inversiones financieras en partes vinculadas."),
			new Account().setCode("5330").setDescription("Intereses a corto plazo de inversiones financieras en partes vinculadas."),
			new Account().setCode("534").setDescription("Intereses a corto plazo de creditos a partes vinculadas."),
			new Account().setCode("5340").setDescription("Intereses a corto plazo de creditos a partes vinculadas."),
			new Account().setCode("535").setDescription("Dividendo a cobrar de inversiones financieras en partes vinculadas."),
			new Account().setCode("5350").setDescription("Dividendo a cobrar de inversiones financieras en partes vinculadas."),
			new Account().setCode("539").setDescription("Desembolsos pendientes sobre participaciones a corto plazo de partes vinculadas."),
			new Account().setCode("5390").setDescription("Desembolsos pendientes sobre participaciones a corto plazo de partes vinculadas."),
			new Account().setCode("54").setDescription("OTRAS INVERSIONES FINANCIERAS A CORTO PLAZO."),
			new Account().setCode("540").setDescription("Inversiones financieras temporales en instrumentos de patrimonio."),
			new Account().setCode("5400").setDescription("Inversiones financieras temporales en instrumentos de patrimonio."),
			new Account().setCode("541").setDescription("Valores representativos de deuda a corto plazo."),
			new Account().setCode("5410").setDescription("Valores representativos de deuda a corto plazo."),
			new Account().setCode("542").setDescription("Creditos a corto plazo."),
			new Account().setCode("5420").setDescription("Creditos a corto plazo."),
			new Account().setCode("543").setDescription("Creditos a corto plazo por enajenacion de inmovilizado."),
			new Account().setCode("5430").setDescription("Creditos a corto plazo por enajenacion de inmovilizado."),
			new Account().setCode("544").setDescription("Creditos a corto plazo al personal."),
			new Account().setCode("5440").setDescription("Creditos a corto plazo al personal."),
			new Account().setCode("545").setDescription("Dividendo a cobrar."),
			new Account().setCode("5450").setDescription("Dividendo a cobrar."),
			new Account().setCode("546").setDescription("Intereses a corto plazo de valores representativos de deuda."),
			new Account().setCode("5460").setDescription("Intereses a corto plazo de valores representativos de deuda."),
			new Account().setCode("547").setDescription("Intereses a corto plazo de creditos."),
			new Account().setCode("5470").setDescription("Intereses a corto plazo de creditos."),
			new Account().setCode("548").setDescription("Imposiciones a corto plazo."),
			new Account().setCode("5480").setDescription("Imposiciones a corto plazo."),
			new Account().setCode("549").setDescription("Desembolsos pendientes sobre instrumentos de patrimonio a corto plazo."),
			new Account().setCode("5490").setDescription("Desembolsos pendientes sobre instrumentos de patrimonio a corto plazo."),
			new Account().setCode("55").setDescription("OTRAS CUENTAS NO BANCARIAS."),
			new Account().setCode("550").setDescription("Titular de la explotacion."),
			new Account().setCode("5500").setDescription("Titular de la explotacion."),
			new Account().setCode("551").setDescription("Cuenta corriente con socios administradores."),
			new Account().setCode("5510").setDescription("Cuenta corriente con socios administradores."),
			new Account().setCode("552").setDescription("Cuenta corriente con otras personas y entidades vinculadas."),
			new Account().setCode("5520").setDescription("Cuenta corriente con otras personas y entidades vinculadas."),
			new Account().setCode("553").setDescription("Cuentas corrientes en fusiones y escisiones."),
			new Account().setCode("5530").setDescription("Socios de sociedad disuelta."),
			new Account().setCode("5531").setDescription("Socios, cuenta de fusion."),
			new Account().setCode("5532").setDescription("Socios de sociedad escindida."),
			new Account().setCode("5533").setDescription("Socios, cuenta de escision."),
			new Account().setCode("554").setDescription("Cuenta corriente con uniones temporales de empresas y comunidades de bienes."),
			new Account().setCode("5540").setDescription("Cuenta corriente con uniones temporales de empresas y comunidades de bienes."),
			new Account().setCode("555").setDescription("Partidas pendientes de aplicacion."),
			new Account().setCode("5550").setDescription("Partidas pendientes de aplicacion."),
			new Account().setCode("555000000").setDescription("PARTIDAS PENDIENTES DE APLICACION"),
			new Account().setCode("5559").setDescription("Suplidos y pagos a cuenta."),
			new Account().setCode("555900000").setDescription("SUPLIDOS Y PAGOS A CUENTA"),
			new Account().setCode("555900001").setDescription("SUPLIDOS POR GASTOS PERSONALES"),
			new Account().setCode("556").setDescription("Desembolsos exigidos sobre participaciones en el patrimonio neto."),
			new Account().setCode("5560").setDescription("Desembolsos exigidos sobre participaciones en el patrimonio neto."),
			new Account().setCode("557").setDescription("Dividendo activo a cuenta."),
			new Account().setCode("5570").setDescription("Dividendo activo a cuenta."),
			new Account().setCode("558").setDescription("Socios por desembolsos exigidos."),
			new Account().setCode("5580").setDescription("Socios por desembolsos exigidos sobre acciones o participaciones ordinarias."),
			new Account().setCode("5585").setDescription("Socios por desembolsos exigidos sobre acciones o participaciones consideradas como pasivos financieros."),
			new Account().setCode("559").setDescription("Derivados financieros a corto plazo."),
			new Account().setCode("5590").setDescription("Activos por derivados financieros a corto plazo, cartera de negociacion."),
			new Account().setCode("5593").setDescription("Activos de derivados financieros a corto plazo, instrumentos de cobertura."),
			new Account().setCode("5595").setDescription("Pasivos por derivados financieros a corto plazo, cartera de negociacion."),
			new Account().setCode("5598").setDescription("Pasivos por derivados financieros a corto plazo, instrumentos de cobertura."),
			new Account().setCode("56").setDescription("FIANZAS Y DEPOSITOS RECIBIDOS Y CONSTITUIDOS A CORTO PLAZO Y AJUSTES POR PERIODIFICACION."),
			new Account().setCode("560").setDescription("Fianzas recibidas a corto plazo."),
			new Account().setCode("5600").setDescription("Fianzas recibidas a corto plazo."),
			new Account().setCode("561").setDescription("Depositos recibidos a corto plazo."),
			new Account().setCode("5610").setDescription("Depositos recibidos a corto plazo."),
			new Account().setCode("565").setDescription("Fianzas constituidas a corto plazo."),
			new Account().setCode("5650").setDescription("Fianzas constituidas a corto plazo."),
			new Account().setCode("566").setDescription("Depositos constituidos a corto plazo."),
			new Account().setCode("5660").setDescription("Depositos constituidos a corto plazo."),
			new Account().setCode("567").setDescription("Intereses pagados por anticipado."),
			new Account().setCode("5670").setDescription("Intereses pagados por anticipado."),
			new Account().setCode("568").setDescription("Intereses cobrados por anticipado."),
			new Account().setCode("5680").setDescription("Intereses cobrados por anticipado."),
			new Account().setCode("569").setDescription("Garantias financieras a corto plazo."),
			new Account().setCode("5690").setDescription("Garantias financieras a corto plazo."),
			new Account().setCode("57").setDescription("TESORERIA."),
			new Account().setCode("570").setDescription("Caja, euros."),
			new Account().setCode("5700").setDescription("Caja, euros."),
			new Account().setCode("570000000").setDescription("CAJA, EUROS."),
			new Account().setCode("571").setDescription("Caja, moneda extranjera."),
			new Account().setCode("5710").setDescription("Caja, moneda extranjera."),
			new Account().setCode("572").setDescription("Bancos e instituciones de credito c/c vista, euros."),
			new Account().setCode("5720").setDescription("Bancos e instituciones de credito c/c vista, euros."),
			new Account().setCode("573").setDescription("Bancos e instituciones de credito c/c vista, moneda extranjera."),
			new Account().setCode("5730").setDescription("Bancos e instituciones de credito c/c vista, moneda extranjera."),
			new Account().setCode("574").setDescription("Bancos e instituciones de credito, cuentas de ahorro, euros."),
			new Account().setCode("5740").setDescription("Bancos e instituciones de credito, cuentas de ahorro, euros."),
			new Account().setCode("575").setDescription("Bancos e instituciones de credito, cuentas de ahorro, moneda extranjera."),
			new Account().setCode("5750").setDescription("Bancos e instituciones de credito, cuentas de ahorro, moneda extranjera."),
			new Account().setCode("576").setDescription("Inversiones a corto plazo de gran liquidez."),
			new Account().setCode("5760").setDescription("Inversiones a corto plazo de gran liquidez."),
			new Account().setCode("58").setDescription("ACTIVOS NO CORRIENTES MANTENIDOS PARA LA VENTA Y ACTIVOS Y PASIVOS ASOCIADOS."),
			new Account().setCode("580").setDescription("Inmovilizado."),
			new Account().setCode("5800").setDescription("Inmovilizado."),
			new Account().setCode("581").setDescription("Inversiones con personas y entidades vinculadas."),
			new Account().setCode("5810").setDescription("Inversiones con personas y entidades vinculadas."),
			new Account().setCode("582").setDescription("Inversiones financieras."),
			new Account().setCode("5820").setDescription("Inversiones financieras."),
			new Account().setCode("583").setDescription("Existencias, deudores comerciales y otras cuentas a cobrar."),
			new Account().setCode("5830").setDescription("Existencias, deudores comerciales y otras cuentas a cobrar."),
			new Account().setCode("584").setDescription("Otros activos."),
			new Account().setCode("5840").setDescription("Otros activos."),
			new Account().setCode("585").setDescription("Provisiones."),
			new Account().setCode("5850").setDescription("Provisiones."),
			new Account().setCode("586").setDescription("Deudas con caracteristicas especiales."),
			new Account().setCode("5860").setDescription("Deudas con caracteristicas especiales."),
			new Account().setCode("587").setDescription("Deudas con personas y entidades vinculadas."),
			new Account().setCode("5870").setDescription("Deudas con personas y entidades vinculadas."),
			new Account().setCode("588").setDescription("Acreedores comerciales y otras cuentas a pagar."),
			new Account().setCode("5880").setDescription("Acreedores comerciales y otras cuentas a pagar."),
			new Account().setCode("589").setDescription("Otros pasivos."),
			new Account().setCode("5890").setDescription("Otros pasivos."),
			new Account().setCode("59").setDescription("DETERIORO DEL VALOR DE INVERSIONES FINANCIERAS A CORTO PLAZO Y DE ACTIVOS NO CORRIENTES MANTENIDOS PARA LA VENTA."),
			new Account().setCode("593").setDescription("Deterioro de valor de participaciones a corto plazo en partes vinculadas."),
			new Account().setCode("5930").setDescription("Deterioro de valor de participaciones a corto plazo en partes vinculadas."),
			new Account().setCode("594").setDescription("Deterioro del valor de valores representativos de deuda a corto plazo de partes vinculadas."),
			new Account().setCode("5940").setDescription("Deterioro del valor de valores representativos de deuda a corto plazo de partes vinculadas."),
			new Account().setCode("595").setDescription("Deterioro del valor de creditos a corto plazo a partes vinculadas."),
			new Account().setCode("5950").setDescription("Deterioro del valor de creditos a corto plazo a partes vinculadas."),
			new Account().setCode("597").setDescription("Deterioro de valor de valores representativos de deuda a corto plazo."),
			new Account().setCode("5970").setDescription("Deterioro de valor de valores representativos de deuda a corto plazo."),
			new Account().setCode("598").setDescription("Deterioro de valor de creditos a corto plazo."),
			new Account().setCode("5980").setDescription("Deterioro de valor de creditos a corto plazo."),
			new Account().setCode("599").setDescription("Deterioro de valor de activos no corrientes mantenidos para la venta."),
			new Account().setCode("5990").setDescription("Deterioro de valor de activos no corrientes mantenidos para la venta."),
			new Account().setCode("6").setDescription("COMPRAS Y GASTOS"),
			new Account().setCode("60").setDescription("COMPRAS."),
			new Account().setCode("600").setDescription("Compras de mercaderias."),
			new Account().setCode("6000").setDescription("Compras de mercaderias."),
			new Account().setCode("600000000").setDescription("COMPRAS DE MERCADERIAS"),
			new Account().setCode("601").setDescription("Compras de materias primas."),
			new Account().setCode("6010").setDescription("Compras de materias primas."),
			new Account().setCode("602").setDescription("Compras de otros aprovisionamientos."),
			new Account().setCode("6020").setDescription("Compras de otros aprovisionamientos."),
			new Account().setCode("606").setDescription("Descuentos sobre compras por pronto pago."),
			new Account().setCode("6060").setDescription("Descuentos sobre compras por pronto pago."),
			new Account().setCode("607").setDescription("Trabajos realizados por otras empresas."),
			new Account().setCode("6070").setDescription("Trabajos realizados por otras empresas."),
			new Account().setCode("608").setDescription("Devoluciones de compras y operaciones similares."),
			new Account().setCode("6080").setDescription("Devoluciones de compras y operaciones similares."),
			new Account().setCode("609").setDescription("Rappels por compras."),
			new Account().setCode("6090").setDescription("Rappels por compras."),
			new Account().setCode("61").setDescription("VARIACION DE EXISTENCIAS."),
			new Account().setCode("610").setDescription("Variacion de existencias de mercaderias."),
			new Account().setCode("6100").setDescription("Variacion de existencias de mercaderias."),
			new Account().setCode("611").setDescription("Variacion de existencias de materias primas."),
			new Account().setCode("6110").setDescription("Variacion de existencias de materias primas."),
			new Account().setCode("612").setDescription("Variacion de existencias de otros aprovisionamientos."),
			new Account().setCode("6120").setDescription("Variacion de existencias de otros aprovisionamientos."),
			new Account().setCode("62").setDescription("SERVICIOS EXTERIORES."),
			new Account().setCode("620").setDescription("Gastos en investigacion y desarrollo del ejercicio."),
			new Account().setCode("6200").setDescription("Gastos en investigacion y desarrollo del ejercicio."),
			new Account().setCode("620000000").setDescription("Gastos de I+D"),
			new Account().setCode("621").setDescription("Arrendamientos y cánones."),
			new Account().setCode("6210").setDescription("Arrendamientos y cánones."),
			new Account().setCode("622").setDescription("Reparaciones y conservacion."),
			new Account().setCode("6220").setDescription("Reparaciones y conservacion."),
			new Account().setCode("622000000").setDescription("REPARACION Y CONSERVACION OFICINA"),
			new Account().setCode("623").setDescription("Servicios de profesionales independientes."),
			new Account().setCode("6230").setDescription("Servicios de profesionales independientes."),
			new Account().setCode("623000000").setDescription("Servicios de profesionales independientes"),
			new Account().setCode("624").setDescription("Transportes."),
			new Account().setCode("6240").setDescription("Transportes."),
			new Account().setCode("624000001").setDescription("TRANSPORTES Y MENSAJERIA"),
			new Account().setCode("625").setDescription("Primas de seguros."),
			new Account().setCode("6250").setDescription("Primas de seguros."),
			new Account().setCode("625000000").setDescription("SEGUROS VEHICULOS"),
			new Account().setCode("625000001").setDescription("SEGUROS OFICINA"),
			new Account().setCode("625000002").setDescription("SEGUROS PRESTAMOS (Garantias)"),
			new Account().setCode("626").setDescription("Servicios bancarios y similares."),
			new Account().setCode("6260").setDescription("Servicios bancarios y similares."),
			new Account().setCode("626000000").setDescription("SERVICIOS BANCARIOS Y SIMILARES"),
			new Account().setCode("626000001").setDescription("Comisiones avales"),
			new Account().setCode("627").setDescription("Publicidad, propaganda y relaciones publicas."),
			new Account().setCode("6270").setDescription("Publicidad, propaganda y relaciones publicas."),
			new Account().setCode("627000001").setDescription("PUBLICIDAD Y PROPAGANDA"),
			new Account().setCode("627000002").setDescription("CUOTAS DE ASOCIACIONES"),
			new Account().setCode("628").setDescription("Suministros."),
			new Account().setCode("6280").setDescription("Suministros."),
			new Account().setCode("628000000").setDescription("SUMINISTROS"),
			new Account().setCode("6281").setDescription("Telefonia"),
			new Account().setCode("628100001").setDescription("TELEFONIA MOVIL"),
			new Account().setCode("628100002").setDescription("TELEFONIA FIJA"),
			new Account().setCode("6282").setDescription("Suministros de Internet"),
			new Account().setCode("628200001").setDescription("INTERNET 3G"),
			new Account().setCode("628200002").setDescription("ACCESO ADSL"),
			new Account().setCode("629").setDescription("Otros servicios."),
			new Account().setCode("6290").setDescription("Otros servicios."),
			new Account().setCode("629000000").setDescription("GASTOS VARIOS"),
			new Account().setCode("629000002").setDescription("MATERIAL DE OFICINA"),
			new Account().setCode("629000003").setDescription("BECARIOS"),
			new Account().setCode("629000004").setDescription("LIMPIEZA"),
			new Account().setCode("629000099").setDescription("Gastos no deducibles"),
			new Account().setCode("629000100").setDescription("SUPLIDOS Y GASTOS SIN IVA"),
			new Account().setCode("6292").setDescription("Gastos por viajes y desplazamientos"),
			new Account().setCode("629200000").setDescription("DESPLAZAMIENTOS (Peaje, Parking, etc.)"),
			new Account().setCode("629200001").setDescription("ALOJAMIENTO (hoteles, hostales...)"),
			new Account().setCode("629200002").setDescription("DIETAS (desayunos, comidas, cenas)"),
			new Account().setCode("629200003").setDescription("TRANSPORTES (autobus, avion, taxi etc...)"),
			new Account().setCode("629200004").setDescription("GASOLINA"),
			new Account().setCode("6295").setDescription("Gastos de Formacion"),
			new Account().setCode("629500000").setDescription("GASTOS DE FORMACION"),
			new Account().setCode("6296").setDescription("Gastos de aplicaciones informáticas"),
			new Account().setCode("629600000").setDescription("MAIL CORPORATIVO"),
			new Account().setCode("629600001").setDescription("CUOTA ACCESO A SALA VIDEOCONFERENCIA"),
			new Account().setCode("629600002").setDescription("INTRANET CORPORATIVA"),
			new Account().setCode("629600003").setDescription("GASTOS DE SOFTWARE"),
			new Account().setCode("63").setDescription("TRIBUTOS."),
			new Account().setCode("630").setDescription("Impuesto sobre beneficios."),
			new Account().setCode("6300").setDescription("Impuesto corriente."),
			new Account().setCode("630000000").setDescription("IMPUESTO DE SOCIEDADES"),
			new Account().setCode("6301").setDescription("Impuesto diferido."),
			new Account().setCode("630100000").setDescription("IMPUESTO SOBRE SOCIEDADES DIFERIDO"),
			new Account().setCode("631").setDescription("Otros tributos."),
			new Account().setCode("6310").setDescription("Otros tributos."),
			new Account().setCode("631000000").setDescription("TRIBUTOS"),
			new Account().setCode("633").setDescription("Ajustes negativos en la imposicion sobre beneficios."),
			new Account().setCode("6330").setDescription("Ajustes negativos en la imposicion sobre beneficios."),
			new Account().setCode("633000000").setDescription("Ajustes negativos en IVA"),
			new Account().setCode("634").setDescription("Ajustes negativos en la imposicion indirecta."),
			new Account().setCode("6340").setDescription("Ajustes negativos en la imposicion indirecta."),
			new Account().setCode("6341").setDescription("Ajustes negativos en IVA"),
			new Account().setCode("634100000").setDescription("Ajustes negativos en IVA"),
			new Account().setCode("636").setDescription("Devolucion de impuestos."),
			new Account().setCode("6360").setDescription("Devolucion de impuestos."),
			new Account().setCode("638").setDescription("Ajustes positivos en la imposicion sobre beneficios."),
			new Account().setCode("6380").setDescription("Ajustes positivos en la imposicion sobre beneficios."),
			new Account().setCode("638000000").setDescription("AJUSTES POSITIVOS EN LA IMPOSICION S/B"),
			new Account().setCode("639").setDescription("Ajustes positivos en la imposicion indirecta."),
			new Account().setCode("6390").setDescription("Ajustes positivos en la imposicion indirecta."),
			new Account().setCode("639000000").setDescription("Ajustes positivos en IVA"),
			new Account().setCode("64").setDescription("GASTOS DE PERSONAL."),
			new Account().setCode("640").setDescription("Sueldos y salarios."),
			new Account().setCode("6400").setDescription("Sueldos y salarios."),
			new Account().setCode("640000000").setDescription("SUELDOS Y SALARIOS"),
			new Account().setCode("640000001").setDescription("SUELDOS Y SALARIOS Dpto. I+D+i"),
			new Account().setCode("640000009").setDescription("SUELDOS Y SALARIOS EN ESPECIE"),
			new Account().setCode("641").setDescription("Indemnizaciones."),
			new Account().setCode("6410").setDescription("Indemnizaciones."),
			new Account().setCode("641000000").setDescription("INDEMNIZACIONES"),
			new Account().setCode("642").setDescription("Seguridad Social a cargo de la empresa."),
			new Account().setCode("6420").setDescription("Seguridad Social a cargo de la empresa."),
			new Account().setCode("642000000").setDescription("S.S. A CARGO DE LA EMPRESA"),
			new Account().setCode("642000001").setDescription("S.S. I+D+i A CARGO DE LA EMPRESA"),
			new Account().setCode("642000002").setDescription("S.S. Inspeccion Ekain"),
			new Account().setCode("642000003").setDescription("S.S.Tarifa Plana"),
			new Account().setCode("643").setDescription("Retribuciones a largo plazo mediante sistemas de aportacion definida."),
			new Account().setCode("6430").setDescription("Retribuciones a largo plazo mediante sistemas de aportacion definida."),
			new Account().setCode("644").setDescription("Retribuciones a largo plazo mediante sistemas de prestacion definida."),
			new Account().setCode("6440").setDescription("Contribuciones anuales."),
			new Account().setCode("6442").setDescription("Otros costes."),
			new Account().setCode("645").setDescription("Retribuciones al personal mediante instrumentos de patrimonio."),
			new Account().setCode("6450").setDescription("Retribuciones al personal mediante instrumentos de patrimonio."),
			new Account().setCode("649").setDescription("Otros gastos sociales."),
			new Account().setCode("6490").setDescription("Otros gastos sociales."),
			new Account().setCode("65").setDescription("OTROS GASTOS DE GESTION."),
			new Account().setCode("650").setDescription("Perdidas de creditos comerciales incobrables."),
			new Account().setCode("6500").setDescription("Perdidas de creditos comerciales incobrables."),
			new Account().setCode("650000000").setDescription("PDAS. POR CREDITOS INCOBRABLES"),
			new Account().setCode("651").setDescription("Resultados de operaciones en comun."),
			new Account().setCode("6510").setDescription("Beneficio transferido (gestor)."),
			new Account().setCode("6511").setDescription("Perdida soportada (participe o asociado no gestor)."),
			new Account().setCode("659").setDescription("Otras perdidas en gestion corriente."),
			new Account().setCode("6590").setDescription("Otras perdidas en gestion corriente."),
			new Account().setCode("659000001").setDescription("DIFERENCIAS ENTRE COBROS Y PAGOS"),
			new Account().setCode("66").setDescription("GASTOS FINANCIEROS."),
			new Account().setCode("660").setDescription("Gastos financieros por actualizacion de provisiones."),
			new Account().setCode("6600").setDescription("Gastos financieros por actualizacion de provisiones."),
			new Account().setCode("661").setDescription("Intereses de obligaciones y bonos."),
			new Account().setCode("6610").setDescription("Intereses de obligaciones y bonos."),
			new Account().setCode("662").setDescription("Intereses de deudas."),
			new Account().setCode("6620").setDescription("Intereses de deudas de empresas del grupo"),
			new Account().setCode("6623").setDescription("Intereses de deudas con Entidades de Credito"),
			new Account().setCode("662300000").setDescription("INTERESES DE DEUDAS CON ENTIDADES DE CREDITO"),
			new Account().setCode("662300001").setDescription("INTERESES POR DESCUENTO DE EFECTOS"),
			new Account().setCode("6625").setDescription("Intereses de deudas con Entidades Publicas"),
			new Account().setCode("662500000").setDescription("INTERESES DE DEUDAS CON LA HACIENDA PUBLICA"),
			new Account().setCode("662500001").setDescription("INTERESES DE DEUDAS CON LA SEGURIDAD SOCIAL"),
			new Account().setCode("663").setDescription("Perdidas por valoracion de instrumentos financieros por su valor razonable."),
			new Account().setCode("6630").setDescription("Perdidas de cartera de negociacion."),
			new Account().setCode("6631").setDescription("Perdidas de designados por la empresa."),
			new Account().setCode("6632").setDescription("Perdidas de disponibles para la venta."),
			new Account().setCode("6633").setDescription("Perdidas de instrumentos de cobertura."),
			new Account().setCode("664").setDescription("Dividendos de acciones o participaciones contabilizadas como pasivo."),
			new Account().setCode("6640").setDescription("Dividendos de acciones o participaciones contabilizadas como pasivo."),
			new Account().setCode("665").setDescription("Intereses por descuento de efectos y operaciones de factoring."),
			new Account().setCode("6650").setDescription("Intereses por descuento de efectos y operaciones de factoring."),
			new Account().setCode("666").setDescription("Perdidas en participaciones y valores representativos de deuda."),
			new Account().setCode("6660").setDescription("Perdidas en participaciones y valores representativos de deuda."),
			new Account().setCode("667").setDescription("Perdidas de creditos no comerciales."),
			new Account().setCode("6670").setDescription("Perdidas de creditos no comerciales."),
			new Account().setCode("668").setDescription("Diferencias negativas de cambio."),
			new Account().setCode("6680").setDescription("Diferencias negativas de cambio."),
			new Account().setCode("668000000").setDescription("Diferencias negativas de cambio"),
			new Account().setCode("669").setDescription("Otros gastos financieros."),
			new Account().setCode("6690").setDescription("Otros gastos financieros."),
			new Account().setCode("669000000").setDescription("OTROS GASTOS FINANCIEROS"),
			new Account().setCode("67").setDescription("PERDIDAS PROCEDENTES DE ACTIVOS NO CORRIENTES Y GASTOS EXCEPCIONALES."),
			new Account().setCode("670").setDescription("Perdidas procedentes del inmovilizado intangible."),
			new Account().setCode("6700").setDescription("Perdidas procedentes del inmovilizado intangible."),
			new Account().setCode("671").setDescription("Perdidas procedentes del inmovilizado material."),
			new Account().setCode("6710").setDescription("Perdidas procedentes del inmovilizado material."),
			new Account().setCode("672").setDescription("Perdidas procedentes de las inversiones inmobiliarias."),
			new Account().setCode("6720").setDescription("Perdidas procedentes de las inversiones inmobiliarias."),
			new Account().setCode("673").setDescription("Perdidas procedentes de participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("6730").setDescription("Perdidas procedentes de participaciones a largo plazo en partes vinculadas."),
			new Account().setCode("675").setDescription("Perdidas por operaciones con obligaciones propias."),
			new Account().setCode("6750").setDescription("Perdidas por operaciones con obligaciones propias."),
			new Account().setCode("678").setDescription("Gastos excepcionales."),
			new Account().setCode("6780").setDescription("Gastos excepcionales."),
			new Account().setCode("678000000").setDescription("GASTOS EXCEPCIONALES"),
			new Account().setCode("68").setDescription("DOTACIONES PARA AMORTIZACIONES."),
			new Account().setCode("680").setDescription("Amortizacion del inmovilizado intangible."),
			new Account().setCode("6800").setDescription("Amortizacion del inmovilizado intangible."),
			new Account().setCode("6801").setDescription("Amortizacion de investigacion"),
			new Account().setCode("6802").setDescription("Amortizacion de desarrollo"),
			new Account().setCode("6803").setDescription("Amortizacion de concesiones administrativas"),
			new Account().setCode("6804").setDescription("Amortizacion de propiedad industrial"),
			new Account().setCode("6805").setDescription("Amortizacion de derechos de traspaso"),
			new Account().setCode("6806").setDescription("Amortizacion de aplicaciones informáticas"),
			new Account().setCode("680600001").setDescription("AMORTIZACION Sistema Informático de Gestion (SIG)"),
			new Account().setCode("680600002").setDescription("Amortizacion Plataforma aonSolutions ejercicio 2014"),
			new Account().setCode("680600003").setDescription("Amortizacion Plataforma aonSolutions ejercicio 2015"),
			new Account().setCode("680600004").setDescription("Amortizacion Plataforma aonSolutions ejercicio 2016"),
			new Account().setCode("680600005").setDescription("Amortizacion Plataforma aonSolutions ejercicio 2017"),
			new Account().setCode("680600006").setDescription("Amortizacion Plataforma aonSolutions ejercicio 2018"),
			new Account().setCode("680600007").setDescription("Amortizacion Plataforma aonSolutions ejercicio 2019"),
			new Account().setCode("680600008").setDescription("Amortizacion Plataforma aonSolutions ejercicio 2019"),
			new Account().setCode("681").setDescription("Amortizacion del inmovilizado material."),
			new Account().setCode("6810").setDescription("Amortizacion del inmovilizado material."),
			new Account().setCode("6811").setDescription("Amortizacion de construcciones"),
			new Account().setCode("6812").setDescription("Amortizacion de instalaciones tecnicas"),
			new Account().setCode("6813").setDescription("Amortizacion de maquinaria"),
			new Account().setCode("6814").setDescription("Amortizacion de utillaje"),
			new Account().setCode("6815").setDescription("Amortizacion de otras instalaciones"),
			new Account().setCode("6816").setDescription("Amortizacion de mobiliario"),
			new Account().setCode("6817").setDescription("Amortizacion de equipos para el proceso de informacion"),
			new Account().setCode("681700005").setDescription("Amortizacion Ordenadores + monitores"),
			new Account().setCode("6818").setDescription("Amortizacion de elementos de transporte"),
			new Account().setCode("681800001").setDescription("Amortizacion Citroen DS5 (2443KBS) "),
			new Account().setCode("6819").setDescription("Amortizacion de otro inmovilizado material"),
			new Account().setCode("682").setDescription("Amortizacion de las inversiones inmobiliarias."),
			new Account().setCode("6820").setDescription("Amortizacion de las inversiones inmobiliarias."),
			new Account().setCode("69").setDescription("PERDIDAS POR DETERIORO Y OTRAS DOTACIONES."),
			new Account().setCode("690").setDescription("Perdidas por deterioro del inmovilizado intangible."),
			new Account().setCode("6900").setDescription("Perdidas por deterioro del inmovilizado intangible."),
			new Account().setCode("691").setDescription("Perdidas por deterioro del inmovilizado material."),
			new Account().setCode("6910").setDescription("Perdidas por deterioro del inmovilizado material."),
			new Account().setCode("692").setDescription("Perdidas por deterioro de las inversiones inmobiliarias."),
			new Account().setCode("6920").setDescription("Perdidas por deterioro de las inversiones inmobiliarias."),
			new Account().setCode("693").setDescription("Perdidas por deterioro de existencias."),
			new Account().setCode("6930").setDescription("Perdidas por deterioro de existencias."),
			new Account().setCode("694").setDescription("Perdidas por deterioro de creditos comerciales."),
			new Account().setCode("6940").setDescription("Perdidas por deterioro de creditos comerciales."),
			new Account().setCode("695").setDescription("Dotacion a la provision por operaciones comerciales."),
			new Account().setCode("6954").setDescription("Dotacion a la provision por contratos onerosos."),
			new Account().setCode("6959").setDescription("Dotacion a la provision para otras operaciones comerciales."),
			new Account().setCode("696").setDescription("Perdidas por deterioro de participaciones y valores representativos de deuda a largo plazo."),
			new Account().setCode("6960").setDescription("Perdidas por deterioro de participaciones y valores representativos de deuda a largo plazo."),
			new Account().setCode("697").setDescription("Perdidas por deterioro de creditos a largo plazo."),
			new Account().setCode("6970").setDescription("Perdidas por deterioro de creditos a largo plazo."),
			new Account().setCode("698").setDescription("Perdidas por deterioro de participaciones y valores representativos de deuda a corto plazo."),
			new Account().setCode("6980").setDescription("Perdidas por deterioro de participaciones y valores representativos de deuda a corto plazo."),
			new Account().setCode("699").setDescription("Perdidas por deterioro de creditos a corto plazo."),
			new Account().setCode("6990").setDescription("Perdidas por deterioro de creditos a corto plazo."),
			new Account().setCode("7").setDescription("VENTAS E INGRESOS"),
			new Account().setCode("70").setDescription("VENTAS DE MERCADERIAS, DE PRODUCCION PROPIA, DE SERVICIOS, ETC."),
			new Account().setCode("700").setDescription("Ventas de mercaderias."),
			new Account().setCode("7000").setDescription("Ventas de mercaderias."),
			new Account().setCode("700000000").setDescription("VENTAS DE MERCADERIAS"),
			new Account().setCode("701").setDescription("Ventas de productos terminados."),
			new Account().setCode("7010").setDescription("Ventas de productos terminados."),
			new Account().setCode("702").setDescription("Ventas de productos semiterminados."),
			new Account().setCode("7020").setDescription("Ventas de productos semiterminados."),
			new Account().setCode("703").setDescription("Ventas de subproductos y residuos."),
			new Account().setCode("7030").setDescription("Ventas de subproductos y residuos."),
			new Account().setCode("704").setDescription("Ventas de envases y embalajes."),
			new Account().setCode("7040").setDescription("Ventas de envases y embalajes."),
			new Account().setCode("705").setDescription("Prestacion de servicios."),
			new Account().setCode("7050").setDescription("Prestacion de servicios CLOUD"),
			new Account().setCode("705000000").setDescription("PLATAFORMA AON SOLUTIONS"),
			new Account().setCode("7051").setDescription("Canon Franquiciados/Distribuidores"),
			new Account().setCode("7059").setDescription("Otros Servicios Prestados"),
			new Account().setCode("706").setDescription("Descuentos sobre ventas por pronto pago."),
			new Account().setCode("7060").setDescription("Descuentos sobre ventas por pronto pago."),
			new Account().setCode("708").setDescription("Devoluciones de ventas y operaciones similares."),
			new Account().setCode("7080").setDescription("Devoluciones de ventas y operaciones similares."),
			new Account().setCode("7081").setDescription("Promociones temporales (Asesorias)"),
			new Account().setCode("7082").setDescription("Promociones temporales (Empresas)"),
			new Account().setCode("709").setDescription("Rappels sobre ventas."),
			new Account().setCode("7090").setDescription("Rappels sobre ventas."),
			new Account().setCode("71").setDescription("VARIACION DE EXISTENCIAS."),
			new Account().setCode("710").setDescription("Variacion de existencias de productos en curso."),
			new Account().setCode("7100").setDescription("Variacion de existencias de productos en curso."),
			new Account().setCode("711").setDescription("Variacion de existencias de productos semiterminados."),
			new Account().setCode("7110").setDescription("Variacion de existencias de productos semiterminados."),
			new Account().setCode("712").setDescription("Variacion de existencias de productos terminados."),
			new Account().setCode("7120").setDescription("Variacion de existencias de productos terminados."),
			new Account().setCode("713").setDescription("Variacion de existencias de subproductos, residuos y materiales recuperados."),
			new Account().setCode("7130").setDescription("Variacion de existencias de subproductos, residuos y materiales recuperados."),
			new Account().setCode("73").setDescription("TRABAJOS REALIZADOS PARA LA EMPRESA."),
			new Account().setCode("730").setDescription("Trabajos realizados para el inmovilizado intangible."),
			new Account().setCode("7300").setDescription("Trabajos realizados para el inmovilizado intangible."),
			new Account().setCode("730000000").setDescription("DESARROLLOS REALIZADOS EN LA PLATAFORMA aonSolutions"),
			new Account().setCode("731").setDescription("Trabajos realizados para el inmovilizado material."),
			new Account().setCode("7310").setDescription("Trabajos realizados para el inmovilizado material."),
			new Account().setCode("732").setDescription("Trabajos realizados en inversiones inmobiliarias."),
			new Account().setCode("7320").setDescription("Trabajos realizados en inversiones inmobiliarias."),
			new Account().setCode("733").setDescription("Trabajos realizados para el inmovilizado en curso."),
			new Account().setCode("7330").setDescription("Trabajos realizados para el inmovilizado en curso."),
			new Account().setCode("74").setDescription("SUBVENCIONES, DONACIONES Y LEGADOS."),
			new Account().setCode("740").setDescription("Subvenciones, donaciones y legados a la explotacion."),
			new Account().setCode("7400").setDescription("Subvenciones, donaciones y legados a la explotacion."),
			new Account().setCode("746").setDescription("Subvenciones, donaciones y legados de capital transferidos al resultado del ejercicio."),
			new Account().setCode("7460").setDescription("Subvenciones, donaciones y legados de capital transferidos al resultado del ejercicio."),
			new Account().setCode("747").setDescription("Otras subvenciones, donaciones y legados transferidos al resultado del ejercicio."),
			new Account().setCode("7470").setDescription("Otras subvenciones, donaciones y legados transferidos al resultado del ejercicio."),
			new Account().setCode("75").setDescription("OTROS INGRESOS DE GESTION."),
			new Account().setCode("751").setDescription("Resultados de operaciones en comun."),
			new Account().setCode("7510").setDescription("Perdida transferida (gestor)."),
			new Account().setCode("7511").setDescription("Beneficio atribuido (participe o asociado no gestor)."),
			new Account().setCode("752").setDescription("Ingresos por arrendamientos."),
			new Account().setCode("7520").setDescription("Ingresos por arrendamientos."),
			new Account().setCode("753").setDescription("Ingresos de propiedad industrial cedida en explotacion."),
			new Account().setCode("7530").setDescription("Ingresos de propiedad industrial cedida en explotacion."),
			new Account().setCode("754").setDescription("Ingresos por comisiones."),
			new Account().setCode("7540").setDescription("Ingresos por comisiones."),
			new Account().setCode("755").setDescription("Ingresos por servicios al personal."),
			new Account().setCode("7550").setDescription("Ingresos por servicios al personal."),
			new Account().setCode("759").setDescription("Ingresos por servicios diversos."),
			new Account().setCode("7590").setDescription("Ingresos por servicios diversos."),
			new Account().setCode("759000000").setDescription("INGRESOS POR SERVICIOS DIVERSOS"),
			new Account().setCode("759000001").setDescription("INGRESOS POR PUBLICIDAD COMPARTIDA"),
			new Account().setCode("76").setDescription("INGRESOS FINANCIEROS."),
			new Account().setCode("760").setDescription("Ingresos de participaciones en instrumentos de patrimonio."),
			new Account().setCode("7600").setDescription("Ingresos de participaciones en instrumentos de patrimonio."),
			new Account().setCode("761").setDescription("Ingresos de valores representativos de deuda."),
			new Account().setCode("7610").setDescription("Ingresos de valores representativos de deuda."),
			new Account().setCode("762").setDescription("Ingresos de creditos."),
			new Account().setCode("7620").setDescription("Ingresos de creditos."),
			new Account().setCode("763").setDescription("Beneficios por valoracion de instrumentos financieros por su valor razonable."),
			new Account().setCode("7630").setDescription("Beneficios de cartera de negociacion."),
			new Account().setCode("7631").setDescription("Beneficios de designados por la empresa."),
			new Account().setCode("7632").setDescription("Beneficios de disponibles para la venta."),
			new Account().setCode("7633").setDescription("Beneficios de instrumentos de cobertura."),
			new Account().setCode("766").setDescription("Beneficios en participaciones y valores representativos de deuda."),
			new Account().setCode("7660").setDescription("Beneficios en participaciones y valores representativos de deuda."),
			new Account().setCode("767").setDescription("Ingresos de activos afectos y de derechos de reembolso relativos a retribuciones a largo plazo."),
			new Account().setCode("7670").setDescription("Ingresos de activos afectos y de derechos de reembolso relativos a retribuciones a largo plazo."),
			new Account().setCode("768").setDescription("Diferencias positivas de cambio."),
			new Account().setCode("7680").setDescription("Diferencias positivas de cambio."),
			new Account().setCode("769").setDescription("Otros ingresos financieros."),
			new Account().setCode("7690").setDescription("Otros ingresos financieros."),
			new Account().setCode("769000000").setDescription("OTROS INGRESOS FINANCIEROS"),
			new Account().setCode("77").setDescription("BENEFICIOS PROCEDENTES DE ACTIVOS NO CORRIENTES E INGRESOS EXCEPCIONALES."),
			new Account().setCode("770").setDescription("Beneficios procedentes del inmovilizado intangible."),
			new Account().setCode("7700").setDescription("Beneficios procedentes del inmovilizado intangible."),
			new Account().setCode("771").setDescription("Beneficios procedentes del inmovilizado material."),
			new Account().setCode("7710").setDescription("Beneficios procedentes del inmovilizado material."),
			new Account().setCode("771000000").setDescription("Beneficio Procedente del Inmovilizado Material"),
			new Account().setCode("772").setDescription("Beneficios procedentes de las inversiones inmobiliarias."),
			new Account().setCode("7720").setDescription("Beneficios procedentes de las inversiones inmobiliarias."),
			new Account().setCode("773").setDescription("Beneficios procedentes de participaciones.."),
			new Account().setCode("7730").setDescription("Beneficios procedentes de participaciones.."),
			new Account().setCode("774").setDescription("Diferencia negativa en combinaciones de negocios."),
			new Account().setCode("7740").setDescription("Diferencia negativa en combinaciones de negocios."),
			new Account().setCode("775").setDescription("Beneficios por operaciones con obligaciones propias."),
			new Account().setCode("7750").setDescription("Beneficios por operaciones con obligaciones propias."),
			new Account().setCode("778").setDescription("Ingresos excepcionales."),
			new Account().setCode("7780").setDescription("Ingresos excepcionales."),
			new Account().setCode("778000000").setDescription("INGRESOS EXCEPCIONALES"),
			new Account().setCode("79").setDescription("EXCESOS Y APLICACIONES DE PROVISIONES Y DE PERDIDAS POR DETERIORO."),
			new Account().setCode("790").setDescription("Reversion del deterioro del inmovilizado intangible."),
			new Account().setCode("7900").setDescription("Reversion del deterioro del inmovilizado intangible."),
			new Account().setCode("791").setDescription("Reversion del deterioro del inmovilizado material."),
			new Account().setCode("7910").setDescription("Reversion del deterioro del inmovilizado material."),
			new Account().setCode("792").setDescription("Reversion del deterioro de las inversiones inmobiliarias."),
			new Account().setCode("7920").setDescription("Reversion del deterioro de las inversiones inmobiliarias."),
			new Account().setCode("793").setDescription("Reversion del deterioro de existencias."),
			new Account().setCode("7930").setDescription("Reversion del deterioro de existencias."),
			new Account().setCode("794").setDescription("Reversion del deterioro de creditos por operaciones comerciales."),
			new Account().setCode("7940").setDescription("Reversion del deterioro de creditos por operaciones comerciales."),
			new Account().setCode("795").setDescription("Exceso de provisiones."),
			new Account().setCode("7950").setDescription("Exceso de provisiones."),
			new Account().setCode("796").setDescription("Reversion del deterioro de participaciones y valores representativos de deuda a largo plazo."),
			new Account().setCode("7960").setDescription("Reversion del deterioro de participaciones y valores representativos de deuda a largo plazo."),
			new Account().setCode("797").setDescription("Reversion del deterioro de creditos a largo plazo."),
			new Account().setCode("7970").setDescription("Reversion del deterioro de creditos a largo plazo."),
			new Account().setCode("798").setDescription("Reversion del deterioro de participaciones y valores representativos de deuda a corto plazo."),
			new Account().setCode("7980").setDescription("Reversion del deterioro de participaciones y valores representativos de deuda a corto plazo."),
			new Account().setCode("799").setDescription("Reversion del deterioro de creditos a corto plazo."),
			new Account().setCode("7990").setDescription("Reversion del deterioro de creditos a corto plazo."),
			new Account().setCode("8").setDescription("GASTOS IMPUTADOS AL PATRIMONIO NETO"),
			new Account().setCode("80").setDescription("GASTOS FINANCIEROS POR VALORACION DE ACTIVOS FINANCIEROS."),
			new Account().setCode("800").setDescription("Perdidas en activos financieros disponibles para la venta."),
			new Account().setCode("8000").setDescription("Perdidas en activos financieros disponibles para la venta."),
			new Account().setCode("802").setDescription("Transferencia de beneficios en activos financieros disponibles para la venta."),
			new Account().setCode("8020").setDescription("Transferencia de beneficios en activos financieros disponibles para la venta."),
			new Account().setCode("81").setDescription("GASTOS EN OPERACIONES DE COBERTURA."),
			new Account().setCode("810").setDescription("Perdidas por coberturas de flujos de efectivo."),
			new Account().setCode("8100").setDescription("Perdidas por coberturas de flujos de efectivo."),
			new Account().setCode("811").setDescription("Perdidas por coberturas de inversiones netas en un negocio en el extranjero."),
			new Account().setCode("8110").setDescription("Perdidas por coberturas de inversiones netas en un negocio en el extranjero."),
			new Account().setCode("812").setDescription("Transferencia de beneficios por coberturas de flujos de efectivo."),
			new Account().setCode("8120").setDescription("Transferencia de beneficios por coberturas de flujos de efectivo."),
			new Account().setCode("813").setDescription("Transferencia de beneficios por coberturas de inversiones netas en un negocio en el extranjero."),
			new Account().setCode("8130").setDescription("Transferencia de beneficios por coberturas de inversiones netas en un negocio en el extranjero."),
			new Account().setCode("82").setDescription("GASTOS POR DIFERENCIAS EN CONVERSION."),
			new Account().setCode("820").setDescription("Diferencias de conversion negativas."),
			new Account().setCode("8200").setDescription("Diferencias de conversion negativas."),
			new Account().setCode("821").setDescription("Transferencia de diferencias de conversion positivas."),
			new Account().setCode("8210").setDescription("Transferencia de diferencias de conversion positivas."),
			new Account().setCode("83").setDescription("IMPUESTOS SOBRE BENEFICIOS."),
			new Account().setCode("830").setDescription("Impuestos sobre beneficios."),
			new Account().setCode("8300").setDescription("Impuesto corriente."),
			new Account().setCode("8301").setDescription("Impuesto diferido."),
			new Account().setCode("833").setDescription("Ajustes negativos en la imposicion sobre beneficios."),
			new Account().setCode("8330").setDescription("Ajustes negativos en la imposicion sobre beneficios."),
			new Account().setCode("834").setDescription("Ingresos fiscales por diferencias permanentes."),
			new Account().setCode("8340").setDescription("Ingresos fiscales por diferencias permanentes."),
			new Account().setCode("835").setDescription("Ingresos fiscales por deducciones y bonificaciones."),
			new Account().setCode("8350").setDescription("Ingresos fiscales por deducciones y bonificaciones."),
			new Account().setCode("836").setDescription("Transferencia de diferencias permanentes."),
			new Account().setCode("8360").setDescription("Transferencia de diferencias permanentes."),
			new Account().setCode("837").setDescription("Transferencia de deducciones y bonificaciones."),
			new Account().setCode("8370").setDescription("Transferencia de deducciones y bonificaciones."),
			new Account().setCode("838").setDescription("Ajustes positivos en la imposicion sobre beneficios."),
			new Account().setCode("8380").setDescription("Ajustes positivos en la imposicion sobre beneficios."),
			new Account().setCode("84").setDescription("TRANSFERENCIAS DE SUBVENCIONES, DONACIONES Y LEGADOS."),
			new Account().setCode("840").setDescription("Transferencia de subvenciones oficiales de capital."),
			new Account().setCode("8400").setDescription("Transferencia de subvenciones oficiales de capital."),
			new Account().setCode("841").setDescription("Transferencia de donaciones y legados de capital."),
			new Account().setCode("8410").setDescription("Transferencia de donaciones y legados de capital."),
			new Account().setCode("842").setDescription("Transferencia de otras subvenciones, donaciones y legados."),
			new Account().setCode("8420").setDescription("Transferencia de otras subvenciones, donaciones y legados."),
			new Account().setCode("85").setDescription("GASTOS POR PERDIDAS ACTUARIALES Y AJUSTES EN LOS ACTIVOS POR RETRIBUCIONES A LARGO PLAZO DE PRESTACION DEFINIDA."),
			new Account().setCode("850").setDescription("Perdidas actuariales."),
			new Account().setCode("8500").setDescription("Perdidas actuariales."),
			new Account().setCode("851").setDescription("Ajustes negativos en activos por retribuciones a largo plazo de prestacion definida."),
			new Account().setCode("8510").setDescription("Ajustes negativos en activos por retribuciones a largo plazo de prestacion definida."),
			new Account().setCode("86").setDescription("GASTOS POR ACTIVOS NO CORRIENTES EN VENTA."),
			new Account().setCode("860").setDescription("Perdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("8600").setDescription("Perdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("862").setDescription("Transferencia de beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("8620").setDescription("Transferencia de beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("89").setDescription("GASTOS DE PARTICIPACIONES EN EMPRESAS DEL GRUPO O ASOCIADAS CON AJUSTES VALORATIVOS POSITIVOS PREVIOS."),
			new Account().setCode("891").setDescription("Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas del grupo."),
			new Account().setCode("8910").setDescription("Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas del grupo."),
			new Account().setCode("892").setDescription("Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas asociadas."),
			new Account().setCode("8920").setDescription("Deterioro de participaciones en el patrimonio y valores representativos de deuda de empresas asociadas."),
			new Account().setCode("9").setDescription("INGRESOS IMPUTADOS AL PATRIMONIO NETO"),
			new Account().setCode("90").setDescription("INGRESOS FINANCIEROS POR VALORACION DE ACTIVOS FINANCIEROS."),
			new Account().setCode("900").setDescription("Beneficios en activos financieros disponibles para la venta."),
			new Account().setCode("9000").setDescription("Beneficios en activos financieros disponibles para la venta."),
			new Account().setCode("902").setDescription("Transferencia de perdidas de activos financieros disponibles para la venta."),
			new Account().setCode("9020").setDescription("Transferencia de perdidas de activos financieros disponibles para la venta."),
			new Account().setCode("91").setDescription("INGRESOS EN OPERACIONES DE COBERTURA."),
			new Account().setCode("910").setDescription("Beneficios por coberturas de flujos de efectivo."),
			new Account().setCode("9100").setDescription("Beneficios por coberturas de flujos de efectivo."),
			new Account().setCode("911").setDescription("Beneficios por coberturas de una inversion neta en un negocio en el extranjero."),
			new Account().setCode("9110").setDescription("Beneficios por coberturas de una inversion neta en un negocio en el extranjero."),
			new Account().setCode("912").setDescription("Transferencia de perdidas por coberturas de flujos de efectivo."),
			new Account().setCode("9120").setDescription("Transferencia de perdidas por coberturas de flujos de efectivo."),
			new Account().setCode("913").setDescription("Transferencia de perdidas por coberturas de una inversion neta en un negocio en el extranjero."),
			new Account().setCode("9130").setDescription("Transferencia de perdidas por coberturas de una inversion neta en un negocio en el extranjero."),
			new Account().setCode("92").setDescription("INGRESOS POR DIFERENCIAS DE CONVERSION."),
			new Account().setCode("920").setDescription("Diferencias de conversion positivas."),
			new Account().setCode("9200").setDescription("Diferencias de conversion positivas."),
			new Account().setCode("921").setDescription("Transferencia de diferencias de conversion negativas."),
			new Account().setCode("9210").setDescription("Transferencia de diferencias de conversion negativas."),
			new Account().setCode("94").setDescription("INGRESOS POR SUBVENCIONES, DONACIONES Y LEGADOS."),
			new Account().setCode("940").setDescription("Ingresos de subvenciones oficiales de capital."),
			new Account().setCode("9400").setDescription("Ingresos de subvenciones oficiales de capital."),
			new Account().setCode("941").setDescription("Ingresos de donaciones y legados de capital."),
			new Account().setCode("9410").setDescription("Ingresos de donaciones y legados de capital."),
			new Account().setCode("942").setDescription("Ingresos de otras subvenciones, donaciones y legados."),
			new Account().setCode("9420").setDescription("Ingresos de otras subvenciones, donaciones y legados."),
			new Account().setCode("95").setDescription("INGRESOS POR GANANCIAS ACTUARIALES Y AJUSTES EN LOS ACTIVOS POR RETRIBUCIONES A LARGO PLAZO DE PRESTACION DEFINIDA."),
			new Account().setCode("950").setDescription("Ganancias actuariales."),
			new Account().setCode("9500").setDescription("Ganancias actuariales."),
			new Account().setCode("951").setDescription("Ajustes positivos en activos por retribuciones a largo plazo de prestacion definida."),
			new Account().setCode("9510").setDescription("Ajustes positivos en activos por retribuciones a largo plazo de prestacion definida."),
			new Account().setCode("96").setDescription("INGRESOS POR ACTIVOS NO CORRIENTES EN VENTA."),
			new Account().setCode("960").setDescription("Beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("9600").setDescription("Beneficios en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("962").setDescription("Transferencia de perdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("9620").setDescription("Transferencia de perdidas en activos no corrientes y grupos enajenables de elementos mantenidos para la venta."),
			new Account().setCode("99").setDescription("INGRESOS DE PARTICIPACIONES EN EL PATRIMONIO DE EMPRESAS DEL GRUPO O ASOCIADAS CON AJUSTES VALORATIVOS NEGATIVOS PREVIOS."),
			new Account().setCode("991").setDescription("Recuperacion de ajustes valorativos negativos previos, empresas del grupo."),
			new Account().setCode("9910").setDescription("Recuperacion de ajustes valorativos negativos previos, empresas del grupo."),
			new Account().setCode("992").setDescription("Recuperacion de ajustes valorativos negativos previos, empresas asociadas."),
			new Account().setCode("9920").setDescription("Recuperacion de ajustes valorativos negativos previos, empresas asociadas."),
			new Account().setCode("993").setDescription("Transferencia por deterioro de ajustes valorativos negativos previos, empresas del grupo."),
			new Account().setCode("9930").setDescription("Transferencia por deterioro de ajustes valorativos negativos previos, empresas del grupo."),
			new Account().setCode("994").setDescription("Transferencia por deterioro de ajustes valorativos negativos previos, empresas asociadas."),
			new Account().setCode("9940").setDescription("Transferencia por deterioro de ajustes valorativos negativos previos, empresas asociadas."),
	};
	
	private static void insertAccounts(AONContext ctx, int domain) {
		for (Account acc : ACCOUNTS) {
			if (!AccountDAO.get(ctx, acc.getCode()).isPresent() ) {
				ctx.getDslContext()
					.insertInto(ACCOUNT)
					.set(ACCOUNT.DOMAIN,domain )
					.set(ACCOUNT.CODE,acc.getCode())
					.set(ACCOUNT.DESCRIPTION,acc.getDescription())
					.set(ACCOUNT.ACTIVE, AonEnumUtils.getByte( true ))
					.execute();
				
			}
		}
	}
	
	private static void insertAppParams(AONContext ctx, int domain) {
		LinkedList<Pair<AppParam,String>> pairs = new LinkedList<>();
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_CASH_ACC,"570000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_CHARGED_RET_ACC,"475100000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC,"477000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC,"642000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_FINAN_EXPENSES_ACC,"669000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_PAID_RET_ACC,"473000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_PAID_VAT_ACC,"472000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC,"465000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_PURCHASE_ACC,"600000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_SALARY_ACC,"640000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_SALES_ACC,"700000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC,"476000000"));
		pairs.add(Pair.of(AppParam.ACC_DEFAULT_PREPAYMENT_ACC,"555900000"));
		pairs.add(Pair.of(AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC,"634100000"));
		pairs.stream()
			.map(p -> new ApplicationParameter().setDomain(domain).setName(p.getLeft()).setValue(p.getRight()))
			.map(p -> {
				Optional<Account> account = AccountDAO.get(ctx, p.getValue());
				if (account.isPresent()) {
					p.setValue(AonNumberUtils.toString( account.get().getId())); 	
					return p;
				}
				return null;
			})
			.filter(p -> p != null)
			.forEach( app -> ctx.getDslContext()
					.insertInto(APP_PARAM)
						.set(APP_PARAM.DOMAIN,domain)
						.set(APP_PARAM.NAME,app.getName().toString())
						.set(APP_PARAM.VALUE,app.getValue())
					.execute()
		);
	};
}
