package net.aonsolutions.db.up2date.registry;

import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;
import static org.jooq.impl.SQLDataType.TINYINT;
import static org.jooq.impl.SQLDataType.VARCHAR;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RecordDataAlterTable implements Update {

	public static final RecordDataAlterTable RECORD_DATA_ALTER_TABLE = new RecordDataAlterTable();

	private RecordDataAlterTable() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		Field<Byte> type = DSL.field("type", TINYINT, "Tipo de Dato Registral");
		Field<String> irus = DSL.field("irus", VARCHAR, "Codigo IRUS del Dato Registral");
		Field<Byte> commercialRegistryCode = DSL.field("commercial_registry_code", TINYINT, "Codigo de Registro Mercantil");

		// Add `type` NOT NULL DEFAULT 3 existing rows get 3, data migration sets the
		// first record per (domain, registry) to 0 afterwards.
		try {
			dslContext.select(type).from(RECORD_DATA).limit(1).fetch();
		} catch (Exception e) {
			dslContext
				.alterTable(RECORD_DATA)
				.addColumn(type, TINYINT.nullable(false).defaultValue((byte) 3))
				.execute();
		}

		// Data migration: first record per (domain, registry) type=0, rest stay at 3.
		// Uses a JOIN UPDATE because MariaDB does not allow a subquery that references
		// the same table being updated.
		dslContext.execute(
			"UPDATE `record_data` r1 " +
			"JOIN (SELECT MIN(`id`) AS `min_id` FROM `record_data` GROUP BY `domain`, `registry`) r2 " +
			"ON r1.`id` = r2.`min_id` " +
			"SET r1.`type` = 0"
		);

		// Virtual column: 1 when type=0, NULL otherwise.
		// NULL values are ignored by unique indexes in MariaDB, so this enforces
		// "at most one type=0 record per (domain, registry)" without restricting
		// records with other type values.
		dslContext.resultQuery("SHOW COLUMNS FROM `record_data` WHERE `Field` = 'type_0_unique'")
			.fetchOptional()
			.ifPresentOrElse(
				col -> System.out.println("Virtual column 'type_0_unique' already exists in 'record_data'."),
				() -> dslContext.execute(
					"ALTER TABLE `record_data` " +
					"ADD COLUMN `type_0_unique` TINYINT " +
					"GENERATED ALWAYS AS (IF(`type` = 0, 1, NULL)) VIRTUAL"
				)
			);

		// Unique index over (domain, registry, type_0_unique): enforces that only one
		// record per (domain, registry) can have type=0.
		dslContext.resultQuery("SHOW INDEX FROM `record_data` WHERE `Key_name` = 'UNQ_RECORD_DATA_DOMAIN_REGISTRY_TYPE0'")
			.fetchOptional()
			.ifPresentOrElse(
				idx -> System.out.println("Unique index 'UNQ_RECORD_DATA_DOMAIN_REGISTRY_TYPE0' already exists in 'record_data'."),
				() -> dslContext.execute(
					"CREATE UNIQUE INDEX `UNQ_RECORD_DATA_DOMAIN_REGISTRY_TYPE0` " +
					"ON `record_data` (`domain`, `registry`, `type_0_unique`)"
				)
			);

		try {
			dslContext.select(irus).from(RECORD_DATA).limit(1).fetch();
		} catch (Exception e) {
			dslContext
				.alterTable(RECORD_DATA)
				.addColumn(irus, VARCHAR.length(13).nullable(true))
				.execute();
		}

		try {
			dslContext.select(commercialRegistryCode).from(RECORD_DATA).limit(1).fetch();
		} catch (Exception e) {
			dslContext
				.alterTable(RECORD_DATA)
				.addColumn(commercialRegistryCode, TINYINT.nullable(true))
				.execute();
		}
	}

}
