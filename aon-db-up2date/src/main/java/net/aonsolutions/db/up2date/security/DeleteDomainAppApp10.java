package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DomainApp;

import net.aonsolutions.db.up2date.Update;

public class DeleteDomainAppApp10 implements Update {

	public static final DeleteDomainAppApp10 DELETE_DOMAIN_APP_APP10 = new DeleteDomainAppApp10();

	private DeleteDomainAppApp10() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		dslContext.transaction(config -> {
			int deleted = config.dsl()
				.deleteFrom(DomainApp.DOMAIN_APP)
				.where(DomainApp.DOMAIN_APP.APP.eq((byte) 10))
				.execute();

			System.out.printf("%d domain_app records with app=10 deleted\r\n", deleted);
		});
	}

}
