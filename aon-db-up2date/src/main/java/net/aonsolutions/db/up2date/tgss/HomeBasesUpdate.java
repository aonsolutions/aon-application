package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class HomeBasesUpdate implements Update {

	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_CGC_MAX = "BASE_CGC_MAX";
	private static final String BASE_CGP_MAX = "BASE_CGP_MAX";

	public static final HomeBasesUpdate HOMEBASESUPDATE = new HomeBasesUpdate();
	
	private HomeBasesUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION, " BASE_CGC ", " BASE_CGC_BRUTA ")) 
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
			.execute()
			;
			

			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-106))
			.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX, BASE_CGP_MIN, BASE_CGP_MAX))
			.execute()
			;
			
			
			for ( String var : new String[] {BASE_CGC_MAX, BASE_CGP_MIN, BASE_CGP_MAX} )
				dslContext
				.insertInto(SYSTEM_DATA)
				.columns(SYSTEM_DATA.DOMAIN, SYSTEM_DATA.NAME, SYSTEM_DATA.EXPRESSION,  SYSTEM_DATA.START_DATE, SYSTEM_DATA.END_DATE, SYSTEM_DATA.READ_ONLY, SYSTEM_DATA.COMMENTS)
				.select(DSL
					.select(SYSTEM_DATA.DOMAIN, DSL.val(var), SYSTEM_DATA.EXPRESSION,  SYSTEM_DATA.START_DATE, SYSTEM_DATA.END_DATE, SYSTEM_DATA.READ_ONLY, SYSTEM_DATA.COMMENTS)
					.from(SYSTEM_DATA)
					.where(SYSTEM_DATA.DOMAIN.eq(-106))
					.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
				).execute()
				;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
