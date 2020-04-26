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

public class GarantizadoFix implements Update {

	public static final GarantizadoFix GARANTIZADOFIX = new GarantizadoFix();
	
	private GarantizadoFix() {
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
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "isdef DIAS_IT ?/*user*/GTZDO(SALARIO_BASE)/**/: REMOVE() ")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("GARANTIZADO"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("MEJORAS PREST.SS.INCAPACIDAD TEMPORAL"))
			.execute()
			;
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "isdef DIAS_ENFERMEDAD_PROFESIONAL ?/*user*/GTZDO(SALARIO_BASE)/**/: REMOVE() ")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("GARANTIZADO"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("MEJORAS PREST.SS.ENFERMEDAD PROFESIONAL"))
			.execute()
			;
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "isdef DIAS_ENFERMEDAD_COMUN ?/*user*/GTZDO(SALARIO_BASE,1,3)/**/: REMOVE()")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "MEJORAS PREST.SS.ENFERMEDAD COMÚN")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("GARANTIZADO"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("MEJORAS PREST.SS.ENFERMEDAD COMÚM"))
			.execute()
			;
		});
	}

}
