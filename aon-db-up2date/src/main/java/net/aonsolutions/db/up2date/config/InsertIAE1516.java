package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE1516 implements Update {

	public static final InsertIAE1516 INSERT_IAE_1516 = new InsertIAE1516();

	private InsertIAE1516() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		System.out.println("[START IAE]");
		
		long count = dslContext.select()
			.from(Iae.IAE)
			.where(Iae.IAE.SECTION.eq("1")
			.and(Iae.IAE.EPIGRAPH.eq("151.6")))
			.fetch()
			.stream()
			.count();
		
		
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "1")
				.set(Iae.IAE.EPIGRAPH, "151.6")
				.set( Iae.IAE.TITLE,"COMERCIALIZACIÓN DE ENERGÍA ELÉCTRICA")
				.execute();
			
			System.out.println("[IAE 151.6 INSERTED] " + c);
		} else {
			System.out.println("[IAE 151.6 NOT INSERTED - no need]");
		}
		
	}

}
