package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
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

import com.esferalia.aon.jooq.tables.DeductionConcept;
import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.jooq.tables.SystemCost;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class ExtraHoursSwap implements Update {

	private static final String PORCENTAJE_EXTR = "PORCENTAJE_EXTR";
	private static final String PORCENTAJE_EXTR_E = "PORCENTAJE_EXTR_E";
	private static final String PORCENTAJE_NEXTR = "PORCENTAJE_NEXTR";
	private static final String PORCENTAJE_NEXTR_E = "PORCENTAJE_NEXTR_E";

	public static final ExtraHoursSwap EXTRAHOURSSWAP = new ExtraHoursSwap();
	
	private ExtraHoursSwap() {
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
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_EXTR))
		.and(SYSTEM_DATA.EXPRESSION.equal("4.70"))) > 0;

		if ( upgraded ) 
			return;

		dslContext.transaction( (config) -> {			
			
			

			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "4.70")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_EXTR))
			.execute();

			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DESCRIPTION, "4.70 %")
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(0))
			.and(SYSTEM_DEDUCTION.EXPRESSION.contains(PORCENTAJE_EXTR))
			.execute();

			dslContext.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DESCRIPTION, "HORAS EXTRAORDINARIAS")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte)2))
			.and(PAYMENT_CONCEPT.CODE.eq("HORAS_EXTRAS"))
			.execute();

			dslContext.update(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Resto horas extraordinarias")
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("EXTR"))
			.execute();

			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "23.60")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_EXTR_E))
			.execute();

			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.DESCRIPTION, "23.60 %")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.EXPRESSION.contains(PORCENTAJE_EXTR_E))
			.execute();


			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "2.00")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_NEXTR))
			.execute();

			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DESCRIPTION, "2.00 %")
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(0))
			.and(SYSTEM_DEDUCTION.EXPRESSION.contains(PORCENTAJE_NEXTR))
			.execute();

			dslContext.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DESCRIPTION, "HORAS EXTRAORDINARIAS FUERZA MAYOR")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte)3))
			.and(PAYMENT_CONCEPT.CODE.eq("HORAS_EXTRAS"))
			.execute();


			dslContext.update(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "Horas extraordinarias fuerza mayor")
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("NEXTR"))
			.execute();

			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "12.00")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_NEXTR_E))
			.execute();

			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.DESCRIPTION, "12.00 %")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.EXPRESSION.contains(PORCENTAJE_NEXTR_E))
			.execute();
		});
	}

}
