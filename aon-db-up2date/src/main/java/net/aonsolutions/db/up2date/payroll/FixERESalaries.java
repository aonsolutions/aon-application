package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SalaryData;

import net.aonsolutions.db.up2date.Update;

public class FixERESalaries implements Update {

	public static final FixERESalaries FIXERESALARIES = new FixERESalaries();
	
	private FixERESalaries() {
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
			
			SalaryData ERE_DAYS = SALARY_DATA.as(DSL.name("ERE_DAYS"));
			SalaryData ERE_FACTOR = SALARY_DATA.as(DSL.name("ERE_FACTOR"));
			SalaryData QUOTE_DAYS =SALARY_DATA.as(DSL.name("QUOTE_DAYS"));
			
			int updated =
			dslContext
			.insertInto(SALARY_DATA)
			.columns(
			SALARY_DATA.DOMAIN,
			SALARY_DATA.SALARY,
			SALARY_DATA.NAME,
			SALARY_DATA.EXPRESSION,
			SALARY_DATA.START_DATE,
			SALARY_DATA.END_DATE
			)
			.select(
			DSL.select(
			ERE_DAYS.DOMAIN,
			ERE_DAYS.SALARY,
			DSL.field("\"DIAS_COTIZADOS\"", String.class),
			ERE_DAYS.EXPRESSION,
			ERE_DAYS.START_DATE,
			ERE_DAYS.END_DATE
			)
			.from(SALARY_DATA.as(ERE_DAYS))
			.innerJoin(SALARY_DATA.as(ERE_FACTOR))
			.on(ERE_DAYS.SALARY.eq(ERE_FACTOR.SALARY)
			.and(ERE_DAYS.START_DATE.eq(ERE_FACTOR.START_DATE))
			.and(ERE_DAYS.END_DATE.eq(ERE_FACTOR.END_DATE))
			.and(ERE_FACTOR.NAME.startsWith("COEFICIENTE_ERE"))
			.and(ERE_FACTOR.EXPRESSION.in("1", "1.0", "1.00")))
			.leftJoin(SALARY_DATA.as(QUOTE_DAYS))
			.on(ERE_DAYS.SALARY.eq(QUOTE_DAYS.SALARY)
			.and(ERE_DAYS.START_DATE.eq(QUOTE_DAYS.START_DATE))
			.and(ERE_DAYS.END_DATE.eq(QUOTE_DAYS.END_DATE))
			.and(QUOTE_DAYS.NAME.eq("DIAS_COTIZADOS")))
			.where(ERE_DAYS.NAME.startsWith("DIAS_ERE"))
			.and(QUOTE_DAYS.ID.isNull())
			)
			.execute()
			;
			
			System.out.print(updated);
			
		});
	}

}
