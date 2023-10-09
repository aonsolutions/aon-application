package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE474 implements Update {

	public static final InsertIAE474 INSERT_IAE_474 = new InsertIAE474();

	private InsertIAE474() {
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
			.and(Iae.IAE.EPIGRAPH.eq("474")))
			.fetch()
			.stream()
			.count();
		
		
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "1")
				.set(Iae.IAE.EPIGRAPH, "474")
				.set( Iae.IAE.TITLE, "ARTES GRÁFICAS")
				.execute();
			
			System.out.println("[IAE 4746 INSERTED] " + c);
		} else {
			System.out.println("[IAE 474 NOT INSERTED - no need]");
		}
		
	}

}
