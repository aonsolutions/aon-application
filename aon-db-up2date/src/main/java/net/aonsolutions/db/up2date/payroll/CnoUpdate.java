package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Cno.CNO;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CnoUpdate implements Update {
	
	public static final CnoUpdate CNOUPDATE = new CnoUpdate();

	private CnoUpdate() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
			
		dslContext.transaction(t -> updateCNODetails(dslContext));
	}

	private void updateCNODetails(DSLContext dslContext) {
		int updates = dslContext.update(CNO)
			.set(CNO.TITLE, " Especialistas en tratamientos de estética, bienestar y afines")
			.where(CNO.CODE.eq("5812"))
			.execute();
		
		System.out.println("CNO Updates : " + updates);
	}

}
