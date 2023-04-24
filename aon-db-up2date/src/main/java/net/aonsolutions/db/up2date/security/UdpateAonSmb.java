package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DomainApp;
import com.esferalia.aon.jooq.tables.DomainApplicationModule;

import net.aonsolutions.db.up2date.Update;


public class UdpateAonSmb implements Update {


	public static final UdpateAonSmb UPDATE_AON_SMB= new UdpateAonSmb();

	public static final byte AON_ONE = (byte) 19;
	
	public static final byte STANDAR = (byte) 25;
	public static final byte INVOICE = (byte) 0;
	public static final byte COMMERCIAL = (byte) 32;
	public static final byte MARKETING = (byte) 33;
	public static final byte TREASURY = (byte) 34;
	
	private UdpateAonSmb() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		List<DomainApps> packSuiteList = getPackList(dslContext, AON_ONE);
			
		for (DomainApps  packSuite : packSuiteList) {
			if(packSuite.isActive()) {
				updateDomainApp(dslContext, packSuite.getDomain(), STANDAR);
				updateDomainApp(dslContext, packSuite.getDomain(), INVOICE);
				updateDomainApp(dslContext, packSuite.getDomain(), COMMERCIAL);
				updateDomainApp(dslContext, packSuite.getDomain(), TREASURY);
			}
		}

	}
	
	private List<DomainApps> getPackList(DSLContext dslContext, byte pack) {
		return dslContext.select().from(DomainApplicationModule.DOMAIN_APPLICATION_MODULE).where(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.MODULE.eq(pack))
			.fetch().stream().map(r -> new DomainApps()
				.setId(r.getValue(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.ID))
				.setDomain(r.getValue(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.DOMAIN))
				.setApp(r.getValue(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.MODULE))
				.setActive(true))
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
