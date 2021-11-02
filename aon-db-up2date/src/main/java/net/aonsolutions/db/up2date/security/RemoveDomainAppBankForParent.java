package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.DomainApp;

import net.aonsolutions.db.up2date.Update;

public class RemoveDomainAppBankForParent implements Update {


	public static final RemoveDomainAppBankForParent REMOVE_DOMAIN_APP_BANK_FOR_PARENT = new RemoveDomainAppBankForParent();

	private RemoveDomainAppBankForParent() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LinkedList<Integer> domainIds = dslContext
			.select(Domain.DOMAIN.ID)
			.from(Domain.DOMAIN)
			.where(Domain.DOMAIN.PARENT.isNull().and(Domain.DOMAIN.DOMAINMANAGEMENT.eq((byte) 1)))
			.fetch().stream().map(r -> r.getValue(Domain.DOMAIN.ID)).collect(Collectors.toCollection(LinkedList::new));
		
		dslContext
			.delete(DomainApp.DOMAIN_APP)
			.where(DomainApp.DOMAIN_APP.DOMAIN.in(domainIds))
			.and(DomainApp.DOMAIN_APP.APP.eq((byte) 12))
			.execute();
	}

}
