package net.aonsolutions.db.up2date.config;

import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;

import java.sql.Connection;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;


public class InsertTreasuryAndMarketingModule implements Update {

	public static final InsertTreasuryAndMarketingModule INSERT_TREASURY_AND_MARKETING_MODULE = new InsertTreasuryAndMarketingModule();

	private InsertTreasuryAndMarketingModule() {
	
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		getManagementDomainApplicationModules(dslContext)
		.forEach(dam -> 
			dslContext.insertInto(DOMAIN_APPLICATION_MODULE)
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN, dam.getDomain())
				.set(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION, dam.getDomainApplication())
				.set(DOMAIN_APPLICATION_MODULE.MODULE, (byte) 3)
				.execute());
		
//		getCrmDomainApplicationModules(dslContext)
//		.forEach(dam -> 
//			dslContext.insertInto(DOMAIN_APPLICATION_MODULE)
//				.set(DOMAIN_APPLICATION_MODULE.DOMAIN, dam.getDomain())
//				.set(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION, dam.getDomainApplication())
//				.set(DOMAIN_APPLICATION_MODULE.MODULE, (byte) 0)
//				.execute());
	}
	
	private Stream<DomainApplicationModule> getManagementDomainApplicationModules(DSLContext dslContext) {
		return dslContext.select()
			.from(DOMAIN_APPLICATION_MODULE)
			.where(DOMAIN_APPLICATION_MODULE.MODULE.eq((byte) 2))
			.stream().map(r -> new DomainApplicationModule()
				.setId(r.getValue(DOMAIN_APPLICATION_MODULE.ID))
				.setDomain(r.getValue(DOMAIN_APPLICATION_MODULE.DOMAIN))
				.setDomainApplication(r.getValue(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION))
				.setModule(r.getValue(DOMAIN_APPLICATION_MODULE.MODULE)));
	}
	
//	private Stream<DomainApplicationModule> getCrmDomainApplicationModules(DSLContext dslContext) {
//		return dslContext.select()
//			.from(DOMAIN_APPLICATION_MODULE)
//			.where(DOMAIN_APPLICATION_MODULE.MODULE.eq((byte) 1))
//			.stream().map(r -> new DomainApplicationModule()
//				.setId(r.getValue(DOMAIN_APPLICATION_MODULE.ID))
//				.setDomain(r.getValue(DOMAIN_APPLICATION_MODULE.DOMAIN))
//				.setDomainApplication(r.getValue(DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION))
//				.setModule(r.getValue(DOMAIN_APPLICATION_MODULE.MODULE)));
//	}
	
	public class DomainApplicationModule {
		Integer id;
		Integer domain;
		Integer domainApplication;
		byte module;
		
		public Integer getId() {
			return id;
		}
		
		public DomainApplicationModule setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getDomain() {
			return domain;
		}
		
		public DomainApplicationModule setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
		
		public Integer getDomainApplication() {
			return domainApplication;
		}
		
		public DomainApplicationModule setDomainApplication(Integer domainApplication) {
			this.domainApplication = domainApplication;
			return this;
		}
		
		public byte getModule() {
			return module;
		}
		
		public DomainApplicationModule setModule(byte module) {
			this.module = module;
			return this;
		}
		
	}

}
