package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DomainApp;

import net.aonsolutions.db.up2date.Update;


public class UdpatePacksDomainApp implements Update {


	public static final UdpatePacksDomainApp UPDATE_PACKS_DOMAIN_APP = new UdpatePacksDomainApp();

	public static final byte PACK_SUITE = (byte) 15;
	public static final byte PACK_PORTAL = (byte) 16;
	public static final byte PACK_PAYROLL = (byte) 17;
	public static final byte PACK_FISCONTA = (byte) 18;
	
	public static final byte INVOICE = (byte) 0;
	public static final byte FISCAL = (byte) 4;
	public static final byte DOCUMENTAL = (byte) 1;
	public static final byte MESSENGER = (byte) 2;
	public static final byte ACCOUNTING = (byte) 3;
	public static final byte PAYROLL = (byte) 5;
	public static final byte COMUNICA = (byte) 9;
	public static final byte TIMECONTROL = (byte) 13;
	
	private UdpatePacksDomainApp() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		List<DomainApps> packSuiteList = getPackList(dslContext, PACK_SUITE);
		
		for (DomainApps  packSuite : packSuiteList) {
			if(packSuite.isActive()) {
				updateDomainApp(dslContext, packSuite.getDomain(), INVOICE);
				updateDomainApp(dslContext, packSuite.getDomain(), FISCAL);
				updateDomainApp(dslContext, packSuite.getDomain(), DOCUMENTAL);
				updateDomainApp(dslContext, packSuite.getDomain(), MESSENGER);
				updateDomainApp(dslContext, packSuite.getDomain(), ACCOUNTING);
				updateDomainApp(dslContext, packSuite.getDomain(), PAYROLL);
				updateDomainApp(dslContext, packSuite.getDomain(), COMUNICA);
				updateDomainApp(dslContext, packSuite.getDomain(), TIMECONTROL);
			}
		}
			
		List<DomainApps> packPortalList = getPackList(dslContext, PACK_PORTAL);
		
		for (DomainApps  packPortal : packPortalList) {
			if(packPortal.isActive()) {
				updateDomainApp(dslContext, packPortal.getDomain(), INVOICE);
				updateDomainApp(dslContext, packPortal.getDomain(), DOCUMENTAL);
				updateDomainApp(dslContext, packPortal.getDomain(), MESSENGER);
				updateDomainApp(dslContext, packPortal.getDomain(), TIMECONTROL);
			}
		}

		List<DomainApps> packPayrollList = getPackList(dslContext, PACK_PAYROLL);
		
		for (DomainApps  packPayroll : packPayrollList) {
			if(packPayroll.isActive()) {
				updateDomainApp(dslContext, packPayroll.getDomain(), PAYROLL);
				updateDomainApp(dslContext, packPayroll.getDomain(), COMUNICA);
			}
		}
		
		List<DomainApps> packFiscontaList = getPackList(dslContext, PACK_FISCONTA);
		
		for (DomainApps  packFisconta : packFiscontaList) {
			if(packFisconta.isActive()) {
				updateDomainApp(dslContext, packFisconta.getDomain(), FISCAL);
				updateDomainApp(dslContext, packFisconta.getDomain(), ACCOUNTING);
			}
		}
		
	}
	
	private List<DomainApps> getPackList(DSLContext dslContext, byte pack) {
		return dslContext.select().from(DomainApp.DOMAIN_APP).where(DomainApp.DOMAIN_APP.APP.eq(pack))
			.fetch().stream().map(r -> new DomainApps()
				.setId(r.getValue(DomainApp.DOMAIN_APP.ID))
				.setDomain(r.getValue(DomainApp.DOMAIN_APP.DOMAIN))
				.setApp(r.getValue(DomainApp.DOMAIN_APP.APP))
				.setActive(r.getValue(DomainApp.DOMAIN_APP.ACTIVE)))
			.toList();
	}
	
	private void updateDomainApp(DSLContext dslContext, Integer domain, byte app) {
		if(!hasDomainApp(dslContext, domain, app)) {
			createDomainApp(dslContext, domain, app);
		}
	}
	
	private boolean hasDomainApp(DSLContext dslContext, Integer domain, byte app) {
		long a = dslContext.select(DomainApp.DOMAIN_APP.ID)
		.from(DomainApp.DOMAIN_APP)
		.where(DomainApp.DOMAIN_APP.DOMAIN.eq(domain)
			.and(DomainApp.DOMAIN_APP.APP.eq(app)))
		.fetch().stream().count();
		return a > 0;
	}
	
	private void createDomainApp(DSLContext dslContext, Integer domain,  byte app) {
		dslContext.insertInto(DomainApp.DOMAIN_APP)
		.set(DomainApp.DOMAIN_APP.DOMAIN, domain)
		.set(DomainApp.DOMAIN_APP.APP, app)
		.set(DomainApp.DOMAIN_APP.ACTIVE, (byte) 1)
		.execute();	
	}
	
	public static class DomainApps {
		
		Integer id;
		Integer domain;
		byte app;
		boolean active;
	
		public Integer getId() {
			return id;
		}
		
		public DomainApps setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getDomain() {
			return domain;
		}

		public DomainApps setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
		
		public byte getApp() {
			return app;
		}
		
		public DomainApps setApp(byte app) {
			this.app = app;
			return this;
		}
						
		public boolean isActive() {
			return active;
		}
		
		public DomainApps setActive(boolean active) {
			this.active = active;
			return this;
		}
		
		public DomainApps setActive(byte active) {
			this.active = active == 1;
			return this;
		}
		
	}
}
