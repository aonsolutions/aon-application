package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static org.jooq.impl.SQLDataType.TINYINT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterAgreement4Owner implements Update {

	public static final AlterAgreement4Owner ALTER_AGREEMENT_OWNER = new AlterAgreement4Owner();
	
	private AlterAgreement4Owner() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		Field<Byte> owner = DSL.field("owner", TINYINT, "Creador convenio, 0 = AonSolutions, 1 = ServiConvenios");
		
		// Add owner column if not exists
		
		try {
			dslContext.select(owner).from(AGREEMENT).limit(1).fetch();
		} catch ( Exception e ) {
			dslContext
			.alterTable(AGREEMENT)
			.addColumn(owner, TINYINT.length(2).defaultValue((byte)0))
			.execute()
			;
		}
	}

}
