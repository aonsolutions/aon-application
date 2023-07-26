package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE575576 implements Update {

	public static final InsertIAE575576 INSERT_IAE_755756 = new InsertIAE575576();

	private InsertIAE575576() {
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
			.and(Iae.IAE.EPIGRAPH.eq("755")))
			.fetch()
			.stream()
			.count();
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "1")
				.set(Iae.IAE.EPIGRAPH, "755")
				.set( Iae.IAE.TITLE,"AGENCIAS DE VIAJES")
				.execute();
			
			System.out.println("[IAE 755 INSERTED] " + c);
		} else {
			System.out.println("[IAE 755 NOT INSERTED - no need]");
		}
		
		count = dslContext.select()
				.from(Iae.IAE)
				.where(Iae.IAE.SECTION.eq("1")
				.and(Iae.IAE.EPIGRAPH.eq("756")))
				.fetch()
				.stream()
				.count();
			if(count < 1) {
				int c = dslContext.insertInto(Iae.IAE)
					.set(Iae.IAE.SECTION, "1")
					.set(Iae.IAE.EPIGRAPH, "756")
					.set( Iae.IAE.TITLE,"ACTIVIDADES AUXILIARES Y COMPLEMENTARIAS TRANSPORTE")
					.execute();
				
				System.out.println("[IAE 756 INSERTED] " + c);
			} else {
				System.out.println("[IAE 756 NOT INSERTED - no need]");
			}

	}

}
