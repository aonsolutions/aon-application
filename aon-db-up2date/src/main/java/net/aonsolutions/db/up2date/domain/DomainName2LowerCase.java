package net.aonsolutions.db.up2date.domain;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.util.mysql.MySQLDataType;

import com.esferalia.aon.jooq.tables.Domain;

import net.aonsolutions.db.up2date.Update;

public class DomainName2LowerCase implements Update {

	public static final DomainName2LowerCase DOMAINNAME2LOWERCASE = new DomainName2LowerCase();
	
	private DomainName2LowerCase() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(DOMAIN.ID)
		.from(DOMAIN)
		.where(DSL.cast(DOMAIN.NAME, SQLDataType.BINARY).ne(DSL.cast(DSL.lower(DOMAIN.NAME), SQLDataType.BINARY)))
		) == 0;
		
		if ( upgraded )
			return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.update(DOMAIN)
			.set(DOMAIN.NAME, DSL.lower(DOMAIN.NAME))
			.where(DSL.cast(DOMAIN.NAME, SQLDataType.BINARY).ne(DSL.cast(DSL.lower(DOMAIN.NAME), SQLDataType.BINARY)))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
		
//		dslContext.alterTable(DOMAIN)
//		.add(DSL.constraint("FORCE_NAME_LOWER_CASE")
//		.check(DSL.cast(DOMAIN.NAME, SQLDataType.BINARY).eq(DSL.cast(DSL.lower(DOMAIN.NAME), SQLDataType.BINARY))))
//		.execute()
//		;
	}

}
