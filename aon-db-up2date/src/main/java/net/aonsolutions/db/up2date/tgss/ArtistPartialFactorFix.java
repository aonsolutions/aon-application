package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep7;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class ArtistPartialFactorFix implements Update {
	
	public static ArtistPartialFactorFix ARTISTPARTIALFACTORFIX = new ArtistPartialFactorFix();

	private ArtistPartialFactorFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean upgraded = dslContext.fetchCount(
		dslContext.select().from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-108))
			.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN"))
			.and(SYSTEM_DATA.EXPRESSION.contains("COEFICIENTE_PARCIALIDAD"))
		) >= 1;
		
		if ( upgraded )
			return;

		UpdateConditionStep<SystemDataRecord> fixPartialFactorArtist = 
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, DSL.concat(SYSTEM_DATA.EXPRESSION, " * COEFICIENTE_PARCIALIDAD" ))
		.where(SYSTEM_DATA.DOMAIN.eq(-108))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN"))
		.and(SYSTEM_DATA.EXPRESSION.notContains("COEFICIENTE_PARCIALIDAD"))
		;

		dslContext.transaction( config -> {
			fixPartialFactorArtist.execute();
		});
	}

}
