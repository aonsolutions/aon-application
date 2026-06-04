package net.aonsolutions.db.up2date.scope;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class InsertDefaultScope implements Update {

    public static final InsertDefaultScope INSERT_DEAFULT_SCOPE = new InsertDefaultScope();

    private InsertDefaultScope() {}

    @Override
    public void upgrade(Connection connection) {

        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
        
		dslContext.transaction( config -> 
    			config.dsl().insertInto(SCOPE)
    			.columns(SCOPE.DOMAIN, SCOPE.DESCRIPTION)
    			.select(DSL.select(DOMAIN.ID, DSL.inline("EMPRESA"))
    					.from(DOMAIN)
    					.leftJoin(SCOPE).on(SCOPE.DOMAIN.eq(DOMAIN.ID))
    					.where(SCOPE.ID.isNull()).and(DOMAIN.PARENT.isNotNull())).execute()
		);
    }
}

