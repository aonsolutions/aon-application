package net.aonsolutions.db.up2date.scope;

import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.sql.Connection;
import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class UserScopeStartDateNotNull implements Update {

    public static final UserScopeStartDateNotNull USER_SCOPE_START_DATE_NOT_NULL = new UserScopeStartDateNotNull();

    private static final Date EPOCH = Date.valueOf("1970-01-01");

    private UserScopeStartDateNotNull() {}

    @Override
    public void upgrade(Connection connection) {

        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

        boolean alreadyNotNull = dslContext.select()
                .from("information_schema.columns")
                .where(DSL.field("table_schema").eq(DSL.currentSchema()))
                .and(DSL.field("table_name").eq(DSL.inline(USER_SCOPE.getName())))
                .and(DSL.field("column_name").eq(DSL.inline("start_date")))
                .and(DSL.field("is_nullable").eq(DSL.inline("NO")))
                .fetchOptional()
                .isPresent();

        if (alreadyNotNull) {
            System.out.println("\tUserScopeStartDateNotNull. start_date already is NOT NULL.");
            return;
        }

        dslContext.transaction(config -> {

            config.dsl()
                    .update(USER_SCOPE)
                    .set(USER_SCOPE.START_DATE, EPOCH)
                    .where(USER_SCOPE.START_DATE.isNull())
                    .execute();

            config.dsl().alterTable(USER_SCOPE).alterColumn(USER_SCOPE.START_DATE)
                    .set(SQLDataType.DATE.nullable(false).defaultValue(DSL.inline("1970-01-01", SQLDataType.DATE)))
                    .execute();
        });

        System.out.println("\tUserScopeStartDateNotNull. start_date UPDATED to NOT NULL DEFAULT '1970-01-01'.");
    }
}
