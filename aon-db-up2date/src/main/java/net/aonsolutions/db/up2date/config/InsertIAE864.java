package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE864 implements Update {

	public static final InsertIAE864 INSERT_IAE_864 = new InsertIAE864();

	private InsertIAE864() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		System.out.println("[START IAE]");
		
		long count = dslContext.select()
			.from(Iae.IAE)
			.where(Iae.IAE.SECTION.eq("2")
			.and(Iae.IAE.EPIGRAPH.eq("864")))
			.fetch()
			.stream()
			.count();
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "2")
				.set(Iae.IAE.EPIGRAPH, "864")
				.set( Iae.IAE.TITLE,"ESCRITORES Y GUIONISTAS.")
				.execute();
			
			System.out.println("[IAE 864 INSERTED] " + c);
		} else {
			System.out.println("[IAE 864 NOT INSERTED - no need]");
		}
		
	}

}
