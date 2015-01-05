package com.esferalia.aon.dsi.util;

import static com.esferalia.aon.dsi.jooq.tables.Fnempres.FNEMPRES;
import static com.esferalia.aon.dsi.jooq.tables.Fnnominc.FNNOMINC;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.sql.Connection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectOnConditionStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.dsi.jooq.tables.records.FnempresRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnnomincRecord;
import com.esferalia.aon.jooq.tables.Salary;

public class DSIUtils {
	
	public static DSLContext getDSLContext(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		return DSL.using(conn, settings);
	}
	
	public static List<Record> getEmpress(Connection conn, Condition ...conditions) {
		
		//@formatter:off
//		return getDSLContext(conn)
//		.select()
//		.from(FNEMPRES)
//		.where(conditions)
//		.orderBy(FNEMPRES.F20RSOCIAL.asc())
//		.fetchInto(FNEMPRES);
		//@formatter:on
		
		DSLContext create = getDSLContext(conn);

		try {

			SelectOnConditionStep<Record> select = create.select()
			.from(FNEMPRES)
			.leftOuterJoin(FNNOMINC)
			.on(FNEMPRES.F20SSNUM.eq(FNNOMINC.F30SSNUMEM)
					.and(FNEMPRES.F20SSCOD.eq(FNNOMINC.F30SSCODEM)));

			List<Record> empres = create.select()
					.from(FNEMPRES, FNNOMINC)
					.whereExists(select)
					.orderBy(FNEMPRES.F20RSOCIAL.asc())
					.fetch();

			return empres;
		} catch ( Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static List<FnnomincRecord> getNominasC(Connection conn, Condition ...conditions) {
		//@formatter:off
		return getDSLContext(conn)
				.select()
				.from(FNNOMINC)
				.where(conditions)
				.fetchInto(FNNOMINC);
		//@formatter:on
	}
	
	public static List<Record> getInfo (Connection conn, Condition ...conditions) {
		
		DSLContext create = getDSLContext(conn);
		
		try {
			
			Condition nominc2empres = 
					FNNOMINC.F30SSCODEM.eq(FNEMPRES.F20SSCOD)
					.and(FNNOMINC.F30SSNUMEM.eq(FNEMPRES.F20SSNUM));
			
			List<Record> record = create.select()
					.from(FNNOMINC)
					.join(FNEMPRES)
					.on(nominc2empres)
					.orderBy(FNNOMINC.F30SSCODEM, 
							FNNOMINC.F30SSNUMEM, 
							FNNOMINC.F30SSCOD, 
							FNNOMINC.F30SSNUM, 
							FNNOMINC.F30MES, 
							FNNOMINC.F30TIPO)
					.fetch();

			return record;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		

	}
}
