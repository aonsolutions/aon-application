package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class PrestITDescriptionsUpdateII implements Update {

	public static final PrestITDescriptionsUpdateII PRESTITDESCRIPTIONSUPDATEII = new PrestITDescriptionsUpdateII();
	
	private PrestITDescriptionsUpdateII() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Integer prestItConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PREST_IT"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		
		Integer mtndadConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("MTNAD"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		Integer pagoDirectoConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PAGO_DIRECTO"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.alterTable(SYSTEM_PAYMENT)
			.alterColumn(SYSTEM_PAYMENT.DESCRIPTION)
			.set(SQLDataType.VARCHAR(128))
			.execute();
			
			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1001]@{DIAS_ENFERMEDAD_COMUN_1_3} DÍAS DE IT POR EC DEL 1º AL 3º DÍA")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_COMUN_1_3%"))
			.execute()
			;
			
			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1004]@{DIAS_ENFERMEDAD_COMUN_4_15} DÍAS DE IT POR EC DEL 4º AL 15º DÍA")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_COMUN_4_15%"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1016]@{DIAS_ENFERMEDAD_COMUN_16_20} DÍAS DE IT POR EC DEL 16º AL 20º DÍA")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_COMUN_16_20%"))
			.execute()
			;
			
			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1021]@{DIAS_ENFERMEDAD_COMUN_21} DÍAS DE IT POR EC A PARTIR DE 21º DÍA")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_COMUN_21%"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1001]@{DIAS_ENFERMEDAD_COMUN_CARENCIA} DÍAS DE IT POR EC PERIODO DE CARENCIA")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_COMUN_CARENCIA%"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1001]@{DIAS_ENFERMEDAD_PROFESIONAL} DÍAS DE IT POR AT/EP")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_PROFESIONAL%"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1001]@{DIAS_MATERNIDAD} DÍAS DE IT POR MATERNIDAD")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(mtndadConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_MATERNIDAD%"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1001]@{DIAS_PATERNIDAD} DÍAS DE IT POR PATERNIDAD")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(mtndadConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_PATERNIDAD%"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1366]@{DIAS_ENFERMEDAD_COMUN_366} DÍAS DE IT POR EC PAGO DIRECTO")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(pagoDirectoConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_COMUN_366%"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "[1366]@{DIAS_ENFERMEDAD_PROFESIONAL_366} DÍAS DE IT POR AT/EP PAGO DIRECTO")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(pagoDirectoConceptId))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_ENFERMEDAD_PROFESIONAL_366%"))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
