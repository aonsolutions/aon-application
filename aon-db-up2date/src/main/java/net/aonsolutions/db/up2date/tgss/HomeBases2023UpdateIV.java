package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class HomeBases2023UpdateIV implements Update {

	public static final HomeBases2023UpdateIV HOMEBASES2023UPDATEIV = new HomeBases2023UpdateIV();

	private static final String TABLE = "($ in [ "
		+"[291.00,270.00], "
		+"[451.00,386.00], "
		+"[613.00,532.00], "
		+"[775.00,694.00], "
		+"[939.00,858.00], "
		+"[1098.00,1018.00], "
		+"[1260.00,1260.00], "
		+"[Double.MAX_VALUE,BASE_CGC_BRUTA / DIAS_COTIZADOS * DIAS_MES ] ] if $[0] >= ( BASE_CGC_BRUTA / DIAS_COTIZADOS * DIAS_MES ) )[0][1] / DIAS_MES * DIAS_COTIZADOS";

	private static final int DOMAIN = -106;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_CGC_MAX = "BASE_CGC_MAX";
	private static final String BASE_CGP_MAX = "BASE_CGP_MAX";
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Esablish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);
		
		Date start2023Date = new Date(calendar.getTimeInMillis());


		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, TABLE)
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MAX, BASE_CGP_MAX))
			.and(SYSTEM_DATA.START_DATE.eq(start2023Date))
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
