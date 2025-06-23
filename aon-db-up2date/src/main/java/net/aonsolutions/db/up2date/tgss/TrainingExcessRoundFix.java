package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
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

import com.esferalia.aon.jooq.tables.SystemData;

import net.aonsolutions.db.up2date.Update;

public class TrainingExcessRoundFix implements Update {

	private static final int DOMAIN = -101;

	public static final TrainingExcessRoundFix TRAININGEXCESSROUNDFIX = new TrainingExcessRoundFix();

	private TrainingExcessRoundFix() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.like("BASE_EXCESO"))
			.and(SYSTEM_DATA.EXPRESSION.like("%ROUND(BASE_CGC - BASE_CGP_MIN,2)%"))
			) > 0;


		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION,"__EXCESO = MAX(0.00, ROUND(BASE_CGC - BASE_CGP_MIN,2)); __EXCESO > 0.00 ? __EXCESO : UNDEFINED(\"BASE_EXCESO\")")
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.like("BASE_EXCESO"))
			.execute();
			;

			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
