package net.aonsolutions.db.up2date.config;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class InsertIAE869 implements Update {

	public static final InsertIAE869 INSERT_IAE_869 = new InsertIAE869();

	private InsertIAE869() {
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
			.and(Iae.IAE.EPIGRAPH.eq("869")))
			.fetch()
			.stream()
			.count();
		
		
		if(count < 1) {
			int c = dslContext.insertInto(Iae.IAE)
				.set(Iae.IAE.SECTION, "2")
				.set(Iae.IAE.EPIGRAPH, "869")
				.set( Iae.IAE.TITLE, "OTROS PROFESIONALES RELACIONADOS CON LAS ACTIVIDADES ARTÍSTICAS Y CULTURALES NO CLASIFICADAS EN LA SECCIÓN TERCERA")
				.execute();
			
			System.out.println("[IAE 869 INSERTED] " + c);
		} else {
			System.out.println("[IAE 869 NOT INSERTED - no need]");
		}
		
	}

}
