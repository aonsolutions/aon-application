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

public class UnemploymentPercentageFix implements Update {

	private static final String PORCENTAJE_DESMPL_E = "PORCENTAJE_DESMPL_E";
	private static final String PORCENTAJE_DESMPL = "PORCENTAJE_DESMPL";

	public static final UnemploymentPercentageFix UNEMPLOYMENTPERCENTAGEFIX = new UnemploymentPercentageFix();
	
	private UnemploymentPercentageFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESMPL))
		.and(SYSTEM_DATA.EXPRESSION.startsWith("PORCENTAJE"))) > 0;

		if ( upgraded ) 
			return;

		dslContext.transaction( (config) -> {			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, DSL.concat(DSL.concat("PORCENTAJE=(",SYSTEM_DATA.EXPRESSION), "); (PORCENTAJE > 0.00) ? PORCENTAJE : (INDEFINIDO ? 1.55 : 1.60)"))
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESMPL))
			.execute();

			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, DSL.concat(DSL.concat("PORCENTAJE_E=(",SYSTEM_DATA.EXPRESSION), "); (PORCENTAJE_E > 0.00) ? PORCENTAJE_E : (INDEFINIDO ? 5.50 : 6.70)"))
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESMPL_E))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
