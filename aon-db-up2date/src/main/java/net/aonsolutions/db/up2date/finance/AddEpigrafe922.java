package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Iae;

import net.aonsolutions.db.up2date.Update;

public class AddEpigrafe922 implements Update {
	
	public static final AddEpigrafe922 ADD_EPIGRAFE_922 = new AddEpigrafe922();

	private AddEpigrafe922() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		
		long count = dslContext.select().from(Iae.IAE).where(Iae.IAE.SECTION.eq("1").and(Iae.IAE.EPIGRAPH.eq("922"))).fetch().stream().count();
		if(count < 1) {
			dslContext.insertInto(Iae.IAE)
			.set(Iae.IAE.SECTION, "1")
			.set(Iae.IAE.EPIGRAPH, "922")
			.set( Iae.IAE.TITLE,"SERVICIOS DE LIMPIEZA");
		}
		
		System.out.println("[END]");
	}

}
