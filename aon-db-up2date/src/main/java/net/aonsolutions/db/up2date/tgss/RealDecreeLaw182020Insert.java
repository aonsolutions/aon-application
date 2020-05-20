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

public class RealDecreeLaw182020Insert implements Update {

	public static RealDecreeLaw182020Insert REALDECREELAW182020INSERT = new RealDecreeLaw182020Insert();

	
	private RealDecreeLaw182020Insert() {
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
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2020);		
		Date _2020StartDate = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 12);
		calendar.set(Calendar.MONTH, Calendar.MAY);		
		Date _May12Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 13);
		Date _May13Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		Date _May31Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JUNE);		
		Date _June1Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 30);
		Date _June30Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		Date _July1Date = new Date(calendar.getTimeInMillis());


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.execute();
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.NAME.eq("PORCENTAJE_REINCORPORACION"))
			.execute();

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_EXONERADO")
			.set(SYSTEM_DATA.END_DATE, _May12Date)
			.set(SYSTEM_DATA.START_DATE, _2020StartDate)
			.set(SYSTEM_DATA.EXPRESSION,"100.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_EXONERADO")
			.set(SYSTEM_DATA.END_DATE, _May31Date)
			.set(SYSTEM_DATA.START_DATE, _May13Date)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 100.00 : 60.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_EXONERADO")
			.set(SYSTEM_DATA.END_DATE, _June30Date)
			.set(SYSTEM_DATA.START_DATE, _June1Date)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 100.00 : 45.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_EXONERADO")
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_DATA.START_DATE, _July1Date)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL; HIDE(\"REAL DECRETO-LEY 18/2020: ...y en ningún caso más allá del 30 de junio de 2020\")")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)

			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_REINCORPORACION")
			.set(SYSTEM_DATA.END_DATE, _May31Date)
			.set(SYSTEM_DATA.START_DATE, _May13Date)
			.set(SYSTEM_DATA.EXPRESSION,"85.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_REINCORPORACION")
			.set(SYSTEM_DATA.END_DATE, _June30Date)
			.set(SYSTEM_DATA.START_DATE, _June1Date)
			.set(SYSTEM_DATA.EXPRESSION,"70.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;
			
			dslContext
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("ERE_FZA_EXONERADO"))
			.fetchOptional(PAYMENT_CONCEPT.ID)
			.ifPresent(ereFzaConceptId -> {
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.EXPRESSION, 
				"isdef COEFICIENTE_ERE_FZA_EXONERADO ? "
				+ "(SELF.addBonus('EXPDTE. REG. DE EMPLEO POR FZA. EXONERADO','_FRACC(CONTEXT,\"CUOTA_EMPRESARIAL\") * COEFICIENTE_ERE_FZA_EXONERADO * (isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO : 100.0)/100.0');0.0)"
				+ ": HIDE()")
				.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(ereFzaConceptId))
				.execute(); 

				dslContext
				.insertInto(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.DOMAIN, 0)
				.set(SYSTEM_PAYMENT.TYPE, (byte)1)
				.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 1)
				.set(SYSTEM_PAYMENT.START_DATE, _May13Date)
				.set(SYSTEM_PAYMENT.END_DATE, _June30Date)
				.set(SYSTEM_PAYMENT.DESCRIPTION, "")
				.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, ereFzaConceptId)
				.set(SYSTEM_PAYMENT.EXPRESSION, 
				"REINCORPORADO_ERE ? (SELF.addBonus('EXPDTE. REG. DE EMPL. FZA. EXONERADO','_FRACC(CONTEXT,\"CUOTA_EMPRESARIAL\") * DIAS_TRABAJADOS/DIAS_COTIZADOS * (isdef PORCENTAJE_REINCORPORACION ? PORCENTAJE_REINCORPORACION : 85.0)/100.0');HIDE()) : HIDE()")
				.execute(); 
			});
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
