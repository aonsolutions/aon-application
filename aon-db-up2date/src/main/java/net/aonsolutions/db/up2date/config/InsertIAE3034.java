package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE3034 implements Update {

	public static final InsertIAE3034 INSERT_IAE_3034 = new InsertIAE3034();

	private InsertIAE3034() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		System.out.println("[START IAE 3-034]");
		
		long count = dslContext.select()
			.from(Iae.IAE)
			.where(Iae.IAE.SECTION.eq("3")
			.and(Iae.IAE.EPIGRAPH.eq("034")))
			.fetch()
			.stream()
			.count();
		
		
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "3")
				.set(Iae.IAE.EPIGRAPH, "034")
				.set( Iae.IAE.TITLE, "COMPOSITORES, LETRISTAS, ARREGLISTAS Y ADAPATADORES MUSICALES")
				.execute();
			
			System.out.println("[IAE 3-034 INSERTED] " + c);
		} else {
			System.out.println("[IAE 3-034 NOT INSERTED - no need]");
		}
		
	}

}
