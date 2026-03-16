package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DomainApp;

import net.aonsolutions.db.up2date.Update;

public class DeleteDomainAppApp7 implements Update {

	public static final DeleteDomainAppApp7 DELETE_DOMAIN_APP_APP7 = new DeleteDomainAppApp7();

	private DeleteDomainAppApp7() {
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
				.where(DomainApp.DOMAIN_APP.APP.eq((byte) 7))
				.execute();

			System.out.printf("%d domain_app records with app=7 deleted\r\n", deleted);
		});
	}

}
