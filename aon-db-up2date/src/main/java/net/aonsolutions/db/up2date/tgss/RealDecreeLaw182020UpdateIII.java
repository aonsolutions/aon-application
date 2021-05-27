package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

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

import com.esferalia.aon.jooq.tables.SystemDeduction;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class RealDecreeLaw182020UpdateIII implements Update {

	public static RealDecreeLaw182020UpdateIII REALDECREELAW182020UPDATEIII = new RealDecreeLaw182020UpdateIII();

	
	private RealDecreeLaw182020UpdateIII() {
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 13);
		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.YEAR, 2020);		
		Date _May13Date = new Date(calendar.getTimeInMillis());


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 100.00 : (TRAMO(DIA(CONTEXT.getCurrentBindings().getPeriod().getStart(),-1)); 60.00 )")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.and(SYSTEM_DATA.START_DATE.eq(_May13Date))
			.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
