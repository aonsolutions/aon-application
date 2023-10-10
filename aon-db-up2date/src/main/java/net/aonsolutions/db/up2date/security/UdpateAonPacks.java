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

import net.aonsolutions.db.up2date.Update;


public class UdpateAonPacks implements Update {


	public static final UdpateAonPacks UPDATE_AON_PACKS = new UdpateAonPacks();

	public static final byte CONSULTANCY = (byte) 1;
	
	public static final byte PACK_SUITE = (byte) 15;
	public static final byte PACK_PORTAL = (byte) 16;
	public static final byte PACK_PAYROLL = (byte) 17;
	public static final byte PACK_FISCAL_ACCOUNTING = (byte) 18;
	
	
	private UdpateAonPacks() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		List<Integer> domainsWithUsers = getDomainsWithPackSuite(dslContext);
		for (Integer domain : domainsWithUsers) {
			updateDomainApp(dslContext, domain, PACK_PORTAL);
			updateDomainApp(dslContext, domain, PACK_PAYROLL);
			updateDomainApp(dslContext, domain, PACK_FISCAL_ACCOUNTING);
		}
	}
	
	private List<Integer> getDomainsWithPackSuite(DSLContext dslContext) {
		return dslContext.select(Domain.DOMAIN.ID)
			.from(Domain.DOMAIN)
			.join(DomainApp.DOMAIN_APP).on(Domain.DOMAIN.ID.eq(DomainApp.DOMAIN_APP.DOMAIN))
			.where(Domain.DOMAIN.TYPE.ne(CONSULTANCY))
			.and(DomainApp.DOMAIN_APP.APP.eq(PACK_SUITE))
			.and(DomainApp.DOMAIN_APP.ACTIVE.eq((byte) 1))
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
}
