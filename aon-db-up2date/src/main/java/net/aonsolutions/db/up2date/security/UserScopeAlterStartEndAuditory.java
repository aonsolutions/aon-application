package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.function.Predicate;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UserScopeAlterStartEndAuditory implements Update {

    public static final UserScopeAlterStartEndAuditory USERSCOPEALTERSTARTENDAUDITORY = new UserScopeAlterStartEndAuditory();

    private UserScopeAlterStartEndAuditory() {}

    @Override
    public void upgrade(Connection connection) {

        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dsl = DSL.using(connection, SQLDialect.MYSQL, settings);

        dsl.transaction(cfg -> {

            DSLContext ctx = DSL.using(cfg);

            // -----------------------------
            // Helpers reutilizables
            // -----------------------------
            Predicate<String> columnExists = col ->
	            ctx.fetchExists(
	                DSL.selectOne()
	                   .from("INFORMATION_SCHEMA.COLUMNS")
	                   .where(DSL.field("TABLE_NAME").eq("user_scope"))
	                   .and(DSL.field("TABLE_SCHEMA").eq(DSL.field("DATABASE()", String.class)))
	                   .and(DSL.field("COLUMN_NAME").eq(col))
	            );
	
	        Predicate<String> indexExists = idx ->
	            ctx.fetchExists(
	                DSL.selectOne()
	                   .from("INFORMATION_SCHEMA.STATISTICS")
	                   .where(DSL.field("TABLE_NAME").eq("user_scope"))
	                   .and(DSL.field("TABLE_SCHEMA").eq(DSL.field("DATABASE()", String.class)))
	                   .and(DSL.field("INDEX_NAME").eq(idx))
	            );
	
	        Predicate<String> fkExists = fk ->
	            ctx.fetchExists(
	                DSL.selectOne()
	                   .from("INFORMATION_SCHEMA.TABLE_CONSTRAINTS")
	                   .where(DSL.field("TABLE_NAME").eq("user_scope"))
	                   .and(DSL.field("TABLE_SCHEMA").eq(DSL.field("DATABASE()", String.class)))
	                   .and(DSL.field("CONSTRAINT_NAME").eq(fk))
	            );


            // -----------------------------
            // Columnas
            // -----------------------------
            addColumnIfMissing(ctx, columnExists,
                "start_date",
                "ALTER TABLE `user_scope` ADD COLUMN `start_date` date DEFAULT NULL COMMENT 'Fecha inicio autorizado'"
            );

            addColumnIfMissing(ctx, columnExists,
                "end_date",
                "ALTER TABLE `user_scope` ADD COLUMN `end_date` date DEFAULT NULL COMMENT 'Fecha fin autorizado'"
            );

            addColumnIfMissing(ctx, columnExists,
                "owner",
                "ALTER TABLE `user_scope` ADD COLUMN `owner` int(11) DEFAULT NULL COMMENT 'Usuario propietario'"
            );

            addColumnIfMissing(ctx, columnExists,
                "creation_user",
                "ALTER TABLE `user_scope` ADD COLUMN `creation_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion'"
            );

            addColumnIfMissing(ctx, columnExists,
                "creation_date",
                "ALTER TABLE `user_scope` ADD COLUMN `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'"
            );

            addColumnIfMissing(ctx, columnExists,
                "modification_user",
                "ALTER TABLE `user_scope` ADD COLUMN `modification_user` varchar(16) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion'"
            );

            addColumnIfMissing(ctx, columnExists,
                "modification_date",
                "ALTER TABLE `user_scope` ADD COLUMN `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion'"
            );

            // -----------------------------
            // Índice owner
            // -----------------------------
            if (!indexExists.test("IDX_USER_SCOPE_OWNER")) {
                System.out.println("[DB] Creando índice IDX_USER_SCOPE_OWNER...");
                ctx.execute("ALTER TABLE `user_scope` ADD KEY `IDX_USER_SCOPE_OWNER` (`owner`)");
            } else {
                System.out.println("[DB] Índice IDX_USER_SCOPE_OWNER ya existe, no se modifica.");
            }

            // -----------------------------
            // Foreign Key owner
            // -----------------------------
            if (!fkExists.test("FK_USER_SCOPE_OWNER")) {
                System.out.println("[DB] Creando foreign key FK_USER_SCOPE_OWNER...");
                ctx.execute(
                    "ALTER TABLE `user_scope` " +
                    "ADD CONSTRAINT `FK_USER_SCOPE_OWNER` FOREIGN KEY (`owner`) REFERENCES `user` (`id`)"
                );
            } else {
                System.out.println("[DB] Foreign key FK_USER_SCOPE_OWNER ya existe, no se modifica.");
            }
        });
    }

    // ---------------------------------------------------------
    // Helper genérico para añadir columnas si no existen
    // ---------------------------------------------------------
    private void addColumnIfMissing(DSLContext ctx,
                                    Predicate<String> existsFn,
                                    String column,
                                    String sql) {

        if (!existsFn.test(column)) {
            System.out.println("[DB] Añadiendo columna '" + column + "'...");
            ctx.execute(sql);
        } else {
            System.out.println("[DB] Columna '" + column + "' ya existe, no se modifica.");
        }
    }
}
