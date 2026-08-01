package net.aonsolutions.db.up2date.security;

import static com.esferalia.aon.jooq.tables.DomainApp.DOMAIN_APP;
import static com.esferalia.aon.jooq.tables.UserAppRole.USER_APP_ROLE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

/**
 * Migrates app codes:
 * <ul>
 * <li>{@code domain_app}: app 23 -> 9 (per domain, only if 9 doesn't already exist)</li>
 * <li>{@code user_app_role}: app 41 -> 10, 42 -> 11, 43 -> 12
 * (per domain + user, only if the target app doesn't already exist)</li>
 * </ul>
 */
public class MigrateApp23To9 implements Update {

	public static final MigrateApp23To9 MIGRATE_APP_23_TO_9 = new MigrateApp23To9();

	private MigrateApp23To9() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		dslContext.transaction(config -> {
			DSLContext ctx = config.dsl();

			migrateDomainApp(ctx, (byte) 23, (byte) 9);

			migrateUserAppRole(ctx, (byte) 41, (byte) 10);
			migrateUserAppRole(ctx, (byte) 42, (byte) 11);
			migrateUserAppRole(ctx, (byte) 43, (byte) 12);
		});
	}

	private void migrateDomainApp(DSLContext ctx, byte from, byte to) {
		int updated = 0;

		for (Record record : ctx.select(DOMAIN_APP.ID, DOMAIN_APP.DOMAIN)
				.from(DOMAIN_APP)
				.where(DOMAIN_APP.APP.eq(from))
				.fetch()) {

			Integer id = record.get(DOMAIN_APP.ID);
			Integer domain = record.get(DOMAIN_APP.DOMAIN);

			boolean targetExists = ctx.fetchExists(ctx.selectOne()
				.from(DOMAIN_APP)
				.where(DOMAIN_APP.DOMAIN.eq(domain))
				.and(DOMAIN_APP.APP.eq(to)));

			if (!targetExists) {
				updated += ctx.update(DOMAIN_APP)
					.set(DOMAIN_APP.APP, to)
					.where(DOMAIN_APP.ID.eq(id))
					.execute();
			}
		}

		System.out.printf("domain_app: %d records migrated from app=%d to app=%d%n", updated, from, to);
	}

	private void migrateUserAppRole(DSLContext ctx, byte from, byte to) {
		int updated = 0;

		for (Record record : ctx.select(USER_APP_ROLE.ID, USER_APP_ROLE.DOMAIN, USER_APP_ROLE.USER_ID)
				.from(USER_APP_ROLE)
				.where(USER_APP_ROLE.APP.eq(from))
				.fetch()) {

			Integer id = record.get(USER_APP_ROLE.ID);
			Integer domain = record.get(USER_APP_ROLE.DOMAIN);
			Integer userId = record.get(USER_APP_ROLE.USER_ID);

			boolean targetExists = ctx.fetchExists(ctx.selectOne()
				.from(USER_APP_ROLE)
				.where(USER_APP_ROLE.DOMAIN.eq(domain))
				.and(USER_APP_ROLE.USER_ID.eq(userId))
				.and(USER_APP_ROLE.APP.eq(to)));

			if (!targetExists) {
				updated += ctx.update(USER_APP_ROLE)
					.set(USER_APP_ROLE.APP, to)
					.where(USER_APP_ROLE.ID.eq(id))
					.execute();
			}
		}

		System.out.printf("user_app_role: %d records migrated from app=%d to app=%d%n", updated, from, to);
	}

}
