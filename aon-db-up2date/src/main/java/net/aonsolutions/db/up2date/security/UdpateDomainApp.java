package net.aonsolutions.db.up2date.security;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;


public class UdpateDomainApp implements Update {


	public static final UdpateDomainApp UPDATE_DOMAIN_APP = new UdpateDomainApp();

	private UdpateDomainApp() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		getDomains(dslContext).stream().forEach(domain -> {
			List<Module> oldModules = getOldModules(dslContext, domain);
			List<AonApp> apps = getApps(dslContext, domain);
			
			checkAccounting(dslContext, apps, oldModules, domain);
			checkFiscal(dslContext, apps, oldModules, domain);
			checkPayroll(dslContext, apps, oldModules, domain);
			checkDocumental(dslContext, apps, oldModules, domain);
			checkCommercial(dslContext, apps, oldModules, domain);
			checkWarehouse(dslContext, apps, oldModules, domain);
			checkComunica(dslContext, apps, oldModules, domain);
			checkMessenger(dslContext, apps, oldModules, domain);
			checkInvoice(dslContext, apps, oldModules, domain);
			checkTreasury(dslContext, apps, oldModules, domain);
			checkMarketing(dslContext, apps, oldModules, domain);
			checkGroupware(dslContext, apps, oldModules, domain);			
		});
		
	}
	
	private List<Integer> getDomains(DSLContext dslContext) {
		return dslContext.select(DOMAIN.ID).from(DOMAIN).fetch().stream().map(r-> r.getValue(DOMAIN.ID)).toList();
	}
	

	private List<AonApp> getApps(DSLContext dslContext, Integer domain) {
		return dslContext.select()
			.from(DOMAIN_APP)
			.where(DOMAIN_APP.DOMAIN.eq(domain))
			.and(DOMAIN_APP.ACTIVE.eq((byte)1))
			.fetch().stream().map(r -> AonApp.safeValueOf(r.getValue(DOMAIN_APP.APP).intValue()))
			.toList();
	}
	
	public boolean hasApp(List<AonApp> apps, AonApp aonApp) {
		return apps.contains(aonApp);
	}
	
	private List<Module> getOldModules(DSLContext dslContext, Integer domain) {
		return dslContext.select()
			.from(DOMAIN_APPLICATION_MODULE)
			.where(DOMAIN_APPLICATION_MODULE.DOMAIN.eq(domain))
			.fetch().stream().map(r -> Module.safeValueOf(r.getValue(DOMAIN_APPLICATION_MODULE.MODULE).intValue()))
			.toList();
	}
	
	private boolean hasOldModule(List<Module> oldModules, Module module) {
		return oldModules.contains(module);
	}
	
	private void checkAccounting(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.ACCOUNTING, AonApp.ACCOUNTING);
	}
	
	private void checkFiscal(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.FISCAL, AonApp.FISCAL);
	}
	
	private void checkPayroll(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.PAYROLL, AonApp.PAYROLL);
		checkApp(dslContext, apps, oldModules, domain, Module.PAYROLL_PORTAL, AonApp.PAYROLL);
	}
	
	private void checkDocumental(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.DOCUMENT, AonApp.DOCUMENTAL);
		checkApp(dslContext, apps, oldModules, domain, Module.DOCUMENT_PORTAL, AonApp.DOCUMENTAL);
	}
	
	private void checkCommercial(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.CRM, AonApp.COMMERCIAL);
	}
	
	private void checkWarehouse(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.WAREHOUSE, AonApp.WAREHOUSE);
	}
	
	private void checkComunica(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.COMUNICA, AonApp.COMUNICA);
	}
	
	private void checkMessenger(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.CALL_CENTER, AonApp.MESSENGER);
	}
	
	private void checkInvoice(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.AON_FINANCE, AonApp.INVOICE);
		checkApp(dslContext, apps, oldModules, domain, Module.FINANCE_PORTAL, AonApp.INVOICE);
		checkApp(dslContext, apps, oldModules, domain, Module.MANAGEMENT, AonApp.INVOICE);
		checkApp(dslContext, apps, oldModules, domain, Module.AON_ONE, AonApp.INVOICE);
	}
	
	private void checkTreasury(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.TREASURY, AonApp.TREASURY);
	}
	
	private void checkMarketing(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.MARKETING, AonApp.MARKETING);
	}
	
	private void checkGroupware(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain) {
		checkApp(dslContext, apps, oldModules, domain, Module.GROUPWARE, AonApp.GROUPWARE);
	}
	
	private void checkApp(DSLContext dslContext, List<AonApp> apps, List<Module> oldModules, Integer domain, Module module, AonApp app) {
		if(hasOldModule(oldModules, module) && !hasApp(apps, app)) {
			save(dslContext, domain, app);
		}
	}
	
	private void save(DSLContext dslContext, Integer domain, AonApp app) {
		Integer id = dslContext.select(DOMAIN_APP.ID)
				.from(DOMAIN_APP)
				.where(DOMAIN_APP.APP.eq(app.value()))
				.and(DOMAIN_APP.DOMAIN.eq(domain))
				.fetch().stream().map(r -> r.getValue(DOMAIN_APP.ID))
				.findFirst().orElse(null);
		
		if(id != null) {
			dslContext.update(DOMAIN_APP)
			.set(DOMAIN_APP.ACTIVE, (byte) 1)
			.where(DOMAIN_APP.ID.eq(id))
			.execute();
		} else {
			dslContext.insertInto(DOMAIN_APP)
			.set(DOMAIN_APP.DOMAIN, domain)
			.set(DOMAIN_APP.APP, app.value())
			.set(DOMAIN_APP.ACTIVE, (byte) 1)
			.execute();
		}
	}
	
	public enum AonApp {
		INVOICE, 
		DOCUMENTAL,
		MESSENGER,
		ACCOUNTING,
		FISCAL,
		PAYROLL,
		OCR,
		AIO,
		ALMA,
		COMUNICA,
		BIDOQ,
		CONVENIOS,
		BANK,
		TIMECONTROL,
		@Deprecated
		MANAGEMENT,
		PACK_SUITE,
		PACK_PORTAL,
		PACK_PAYROLL,
		PACK_FISCAL_ACCOUNTING,
		SELFCONTA,
		CUSTOM_VIEW,
		AULA,
		NOTES,
		SALTRA,
		BASIC_MANAGEMENT,
		STANDAR_MANAGEMENT,
		PROFESSIONAL_MANAGEMENT,
		@Deprecated
		KIT_DIGITAL_FACE,
		@Deprecated
		KIT_DIGITAL_CRM,
		@Deprecated
		KIT_DIGITAL_ERP,
		API_SERVICE,
		WAREHOUSE,
		COMMERCIAL,
		MARKETING,
		TREASURY,
		GROUPWARE,
		INVOFOX,
		SERES;
		
		public byte value() {
			return (byte) this.ordinal();
		}
	   
		public static AonApp safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= AonApp.values().length) return null;
			return AonApp.values()[i];
		}
		
	}
	
	public enum Module {
		MARKETING,
		CRM,
		MANAGEMENT,
		TREASURY,
		WAREHOUSE,
		GROUPWARE,
		ACCOUNTING,
		FISCAL,
		PAYROLL,
		DOCUMENT,
		GARAGE,
		ACADEMY,
		HOTEL,
		INFOWEB,
		PAYROLL_PORTAL,
		DOCUMENT_PORTAL,
		POS,
		COMUNICA,
		CONFIGURATION,
		AON_ONE,
		ECOMMERCE,
		CALL_CENTER,
		FINANCE_PORTAL,
		AON_FINANCE,
		SUITE_PORTAL;
		
		public byte value() {
			return (byte) this.ordinal();
		}
	   
		public static Module safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= Module.values().length) return null;
			return Module.values()[i];
		}
		
	}
}
