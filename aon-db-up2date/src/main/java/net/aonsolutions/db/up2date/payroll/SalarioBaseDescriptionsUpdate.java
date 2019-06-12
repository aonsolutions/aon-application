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

public class SalarioBaseDescriptionsUpdate implements Update {

	public static final SalarioBaseDescriptionsUpdate SALARIOBASEDESCRIPTIONSUPDATE = new SalarioBaseDescriptionsUpdate();
	
	private SalarioBaseDescriptionsUpdate() {
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
			
			dslContext.alterTable(PAYMENT_CONCEPT)
			.alterColumn(PAYMENT_CONCEPT.DESCRIPTION)
			.set(SQLDataType.VARCHAR(128))
			.execute();

			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "(SALARIO_DIARIO=/*user*/SALARIO_DIARIO/**/) * DIAS_TRABAJADOS")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "SALARIO BASE DIARIO (@{SALARIO_DIARIO} € X @{DIAS_TRABAJADOS} DÍAS)")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("SALARIO_BASE"))
			.and(PAYMENT_CONCEPT.EXPRESSION.like("%SALARIO_DIARIO%"))
			.execute()
			;
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "(SALARIO_HORA=/*user*/SALARIO_HORA/**/) * HORAS_TRABAJADAS")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "SALARIO BASE HORA (@{SALARIO_HORA} € X @{HORAS_TRABAJADAS} HORAS)")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("SALARIO_BASE"))
			.and(PAYMENT_CONCEPT.EXPRESSION.like("%SALARIO_HORA%"))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
