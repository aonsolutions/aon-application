package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.DomainApp;
import com.esferalia.aon.jooq.tables.DomainApplication;
import com.esferalia.aon.jooq.tables.DomainApplicationModule;

import net.aonsolutions.db.up2date.Update;


public class UdpateAonProfessional implements Update {


	public static final UdpateAonProfessional UPDATE_AON_PROFESSIONAL = new UdpateAonProfessional();

	public static final byte CONSULTANCY = (byte) 1;
	
	public static final byte AON_FINANCE = (byte) 23;
	public static final byte AON_ONE = (byte) 19;
	
	public static final byte BASIC = (byte) 24;
	public static final byte STANDAR = (byte) 25;
	public static final byte PROFESSIONAL = (byte) 26;
	
	
	private UdpateAonProfessional() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		List<Integer> domainsWithUsers = getMyAonDomainsWithUsers(dslContext);
		for (Integer domain : domainsWithUsers) {
			updateDomainApp(dslContext, domain, PROFESSIONAL);
		}
		
		List<Integer> domainsWithoutUsers = getMyAonDomainsWithoutUsers(dslContext);
		for (Integer domain : domainsWithoutUsers) {
			createDomainModule(dslContext, domain, AON_FINANCE);
		}
	}
	
	private List<Integer> getMyAonDomainsWithUsers(DSLContext dslContext) {
		return dslContext.select(Domain.DOMAIN.ID)
			.from(Domain.DOMAIN)
			.where(Domain.DOMAIN.TYPE.ne(CONSULTANCY))
			.and(Domain.DOMAIN.MAXDEFINEDUSERS.gt(0))
			.and(Domain.DOMAIN.ID.notIn(
				dslContext.select(DomainApp.DOMAIN_APP.DOMAIN)
				.from(DomainApp.DOMAIN_APP)
				.where(DomainApp.DOMAIN_APP.APP.in(BASIC, STANDAR, PROFESSIONAL))
			))
			.and(Domain.DOMAIN.ID.notIn( 
				dslContext.select(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.DOMAIN)
				.from(DomainApplicationModule.DOMAIN_APPLICATION_MODULE)
				.where(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.MODULE.in(AON_FINANCE, AON_ONE))
			))
			.fetch().stream().map(r -> r.getValue(Domain.DOMAIN.ID))
			.toList();
	}
	
	private List<Integer> getMyAonDomainsWithoutUsers(DSLContext dslContext) {
		return dslContext.select(Domain.DOMAIN.ID)
			.from(Domain.DOMAIN)
			.where(Domain.DOMAIN.TYPE.ne(CONSULTANCY))
			.and(Domain.DOMAIN.MAXDEFINEDUSERS.eq(0))
			.and(Domain.DOMAIN.ID.notIn(
				dslContext.select(DomainApp.DOMAIN_APP.DOMAIN)
				.from(DomainApp.DOMAIN_APP)
				.where(DomainApp.DOMAIN_APP.APP.in(BASIC, STANDAR, PROFESSIONAL))
			))
			.and(Domain.DOMAIN.ID.notIn( 
				dslContext.select(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.DOMAIN)
				.from(DomainApplicationModule.DOMAIN_APPLICATION_MODULE)
				.where(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.MODULE.in(AON_FINANCE, AON_ONE))
			))
			.fetch().stream().map(r -> r.getValue(Domain.DOMAIN.ID))
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
	
	private void createDomainModule(DSLContext dslContext, Integer domain,  byte app) {
		Integer domainApplication = dslContext.select(DomainApplication.DOMAIN_APPLICATION.ID)
		.from(DomainApplication.DOMAIN_APPLICATION)
		.where(DomainApplication.DOMAIN_APPLICATION.DOMAIN.eq(domain))
		.and(DomainApplication.DOMAIN_APPLICATION.APPLICATION.eq(28))
		.fetch().stream().map(r -> r.getValue(DomainApplication.DOMAIN_APPLICATION.ID))
		.findFirst().orElse(null);
		
		if(domainApplication != null) {
			dslContext.insertInto(DomainApplicationModule.DOMAIN_APPLICATION_MODULE)
			.set(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.DOMAIN, domain)
			.set(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.MODULE, app)
			.set(DomainApplicationModule.DOMAIN_APPLICATION_MODULE.DOMAIN_APPLICATION, domainApplication)
			.execute();
		}
	}
	
}
