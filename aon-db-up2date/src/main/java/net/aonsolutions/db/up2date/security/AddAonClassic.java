package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.DomainApp;

import net.aonsolutions.db.up2date.Update;


public class AddAonClassic implements Update {

	public static final AddAonClassic ADD_AON_CLASSIC = new AddAonClassic();

	public static final byte AON_CLASSIC = (byte) 7;
	
	private AddAonClassic() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LocalDateTime fechaHora = LocalDateTime.of(2025, 7, 1, 0, 0, 0);
	    Timestamp timestamp = Timestamp.valueOf(fechaHora);

	    dslContext.select(Domain.DOMAIN.ID)
		.from(Domain.DOMAIN)
		.where(Domain.DOMAIN.CREATION_DATE.le(timestamp))
		.fetch().stream().map(r -> r.getValue(Domain.DOMAIN.ID))
		.forEach(domain -> saveDomainApp(dslContext, domain, AON_CLASSIC));
	}
	
	private void saveDomainApp(DSLContext dslContext, Integer domain, byte app) {
		Integer id = getDomainApp(dslContext, domain, app);
		if(id != null) {
			updateDomainApp(dslContext, domain, app, id);
		} else { 
			createDomainApp(dslContext, domain, app);
		} 
	}
	
	private Integer getDomainApp(DSLContext dslContext, Integer domain, byte app) {
		return dslContext.select(DomainApp.DOMAIN_APP.ID)
		.from(DomainApp.DOMAIN_APP)
		.where(DomainApp.DOMAIN_APP.DOMAIN.eq(domain)
			.and(DomainApp.DOMAIN_APP.APP.eq(app)))
		.fetch().stream().map(r -> r.getValue(DomainApp.DOMAIN_APP.ID)).findFirst().orElse(null);
		
	}
	
	private void createDomainApp(DSLContext dslContext, Integer domain,  byte app) {
		dslContext.insertInto(DomainApp.DOMAIN_APP)
		.set(DomainApp.DOMAIN_APP.DOMAIN, domain)
		.set(DomainApp.DOMAIN_APP.APP, app)
		.set(DomainApp.DOMAIN_APP.ACTIVE, (byte) 1)
		.execute();	
	}
	
	private void updateDomainApp(DSLContext dslContext, Integer domain,  byte app, Integer id) {
		dslContext.update(DomainApp.DOMAIN_APP)
		.set(DomainApp.DOMAIN_APP.DOMAIN, domain)
		.set(DomainApp.DOMAIN_APP.APP, app)
		.set(DomainApp.DOMAIN_APP.ACTIVE, (byte) 1)
		.where(DomainApp.DOMAIN_APP.DOMAIN.eq(domain)
			.and(DomainApp.DOMAIN_APP.ID.eq(id)))
		.execute();	
	}
}
