package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE863 implements Update {

	public static final InsertIAE863 INSERT_IAE_863 = new InsertIAE863();

	private InsertIAE863() {
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
			.where(Iae.IAE.SECTION.eq("2")
			.and(Iae.IAE.EPIGRAPH.eq("863")))
			.fetch()
			.stream()
			.count();
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "2")
				.set(Iae.IAE.EPIGRAPH, "863")
				.set( Iae.IAE.TITLE,"PERIODISTAS Y OTROS PROFESIONALES DE LA INFORMACI\u00D3N Y LA COMUNICACI\u00D3N")
				.execute();
			
			System.out.println("[IAE 863 INSERTED] " + c);
		} else {
			System.out.println("[IAE 863 NOT INSERTED - no need]");
		}
		

	}

}
