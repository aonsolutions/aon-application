package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class ReduccionCgcE02Fix implements Update {

	private static final String REDUCCION_CGC_E_02 = "REDUCCION_CGC_E_02";

	public static final ReduccionCgcE02Fix REDUCCIONCGCE02FIX = new ReduccionCgcE02Fix();

	private ReduccionCgcE02Fix() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set( SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION, "BASE_CGC", "BASE_CGC_E"))
			.where(SYSTEM_DATA.DOMAIN.eq(-107))
			.and(SYSTEM_DATA.NAME.eq(REDUCCION_CGC_E_02))
			.and(SYSTEM_DATA.EXPRESSION.notContains("BASE_CGC_E"))
			.execute()
			;
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
