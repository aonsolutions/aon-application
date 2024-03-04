package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE848889 implements Update {

	public static final InsertIAE848889 INSERT_IAE_848889 = new InsertIAE848889();

	private InsertIAE848889() {
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
			.where(Iae.IAE.SECTION.eq("1")
			.and(Iae.IAE.EPIGRAPH.eq("848")))
			.fetch()
			.stream()
			.count();
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "1")
				.set(Iae.IAE.EPIGRAPH, "848")
				.set( Iae.IAE.TITLE,"SERVICIOS DE OFICINA FLEXIBLE, COWORKING Y CENTROS DE NEGOCIOS. ACTIVIDADES EMPRESARIALES.")
				.execute();
			
			System.out.println("[IAE 848 INSERTED] " + c);
		} else {
			System.out.println("[IAE 848 NOT INSERTED - no need]");
		}
		
		count = dslContext.select()
				.from(Iae.IAE)
				.where(Iae.IAE.SECTION.eq("2")
				.and(Iae.IAE.EPIGRAPH.eq("889")))
				.fetch()
				.stream()
				.count();
			if(count < 1) {
				int c = dslContext.insertInto(Iae.IAE)
					.set(Iae.IAE.SECTION, "2")
					.set(Iae.IAE.EPIGRAPH, "889")
					.set( Iae.IAE.TITLE,"GUIAS DE MONTAÑA")
					.execute();
				
				System.out.println("[IAE 889 INSERTED] " + c);
			} else {
				System.out.println("[IAE 889 NOT INSERTED - no need]");
			}

	}

}
