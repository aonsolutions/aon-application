package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static org.jooq.impl.SQLDataType.DATE;
import static org.jooq.impl.SQLDataType.VARCHAR;

import java.sql.Connection;
import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import net.aonsolutions.db.up2date.Update;

public class AlterAgreement4Log implements Update {

	public static final AlterAgreement4Log ALTERAGREEMENTLOG = new AlterAgreement4Log();
	
	private AlterAgreement4Log() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Field<String> creationUser = DSL.field(DSL.name("creation_user"), VARCHAR(16).nullable(true), DSL.comment("Usuario de creacion"));
		Field<Date> creationDate = DSL.field(DSL.name("creation_date"), DATE.nullable(true), DSL.comment("Fecha de creacion"));
		
		addUserColumn(dslContext, AGREEMENT, creationUser);
		addDateColumn(dslContext, AGREEMENT, creationDate);
		
		addUserColumn(dslContext, AGREEMENT_LEVEL_DATA, creationUser);
		addDateColumn(dslContext, AGREEMENT_LEVEL_DATA, creationDate);
		
		addUserColumn(dslContext, AGREEMENT_PAYMENT, creationUser);
		addDateColumn(dslContext, AGREEMENT_PAYMENT, creationDate);
		
		Field<String> modificationUser = DSL.field(DSL.name("modification_user"), VARCHAR(16).nullable(true), DSL.comment("Usuario de modificacion"));
		Field<Date> modificationDate = DSL.field(DSL.name("modification_date"), DATE.nullable(true), DSL.comment("Fecha de ultima modificacion"));
		
		addUserColumn(dslContext, AGREEMENT, modificationUser);
		addDateColumn(dslContext, AGREEMENT, modificationDate);
		
		addUserColumn(dslContext, AGREEMENT_LEVEL_DATA, modificationUser);
		addDateColumn(dslContext, AGREEMENT_LEVEL_DATA, modificationDate);
		
		addUserColumn(dslContext, AGREEMENT_PAYMENT, modificationUser);
		addDateColumn(dslContext, AGREEMENT_PAYMENT, modificationDate);
		
	}

	private void addUserColumn(DSLContext dslContext, TableImpl<?> table, Field<String> userField) {
		// Add modificationUser column if not exists
		
		try {
			dslContext.select(userField).from(table).limit(1).fetch();
		} catch ( Exception e ) {
			dslContext.alterTable(table)
				.addColumn(userField)
				.execute();
		}
	}
	
	private void addDateColumn(DSLContext dslContext, TableImpl<?> table, Field<Date> dateFiled) {
		// Add modificationUser column if not exists
		
		try {
			dslContext.select(dateFiled).from(table).limit(1).fetch();
		} catch ( Exception e ) {
			dslContext.alterTable(table)
				.addColumn(dateFiled)
				.execute();
		}
	}

}
