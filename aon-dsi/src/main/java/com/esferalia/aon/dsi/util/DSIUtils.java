package com.esferalia.aon.dsi.util;

import static com.esferalia.aon.dsi.jooq.tables.Fnempres.FNEMPRES;

import java.sql.Connection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.dsi.jooq.tables.Fnnominc;
import com.esferalia.aon.dsi.jooq.tables.Fnnominl;
import com.esferalia.aon.dsi.jooq.tables.records.FnempresRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnnomincRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnnominlRecord;

public class DSIUtils {
	

	public static DSLContext getDSLContext(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		return DSL.using(conn, settings);
	}
	
	public static List<FnempresRecord> getEmpress(Connection conn, Condition ...conditions) {
		//@formatter:off
		return getDSLContext(conn)
		.select()
		.from(FNEMPRES)
		.where(conditions)
		.fetchInto(FNEMPRES);
		//@formatter:on
	}
	
	public static List<FnnominlRecord> getNominasL(Connection conn, Condition ...conditions) {
		//@formatter:off
		return getDSLContext(conn)
				.select()
				.from(Fnnominl.FNNOMINL)
				.where(conditions)
				.fetchInto(Fnnominl.FNNOMINL);
		//@formatter:on
	}
	
	public static List<FnnomincRecord> getNominasC(Connection conn, Condition ...conditions) {
		//@formatter:off
		return getDSLContext(conn)
				.select()
				.from(Fnnominc.FNNOMINC)
				.where(conditions)
				.fetchInto(Fnnominc.FNNOMINC);
		//@formatter:on
	}
}
