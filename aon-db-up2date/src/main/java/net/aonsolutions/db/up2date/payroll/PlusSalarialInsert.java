package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
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

import com.esferalia.aon.jooq.tables.PaymentConcept;

import net.aonsolutions.db.up2date.Update;

public class PlusSalarialInsert implements Update {

	public static final PlusSalarialInsert PLUSSALARIALINSERT = new PlusSalarialInsert();
	
	private PlusSalarialInsert() {
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
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.like("PLUS_SALARIAL"))
		) > 2;
		
		if ( upgraded )
			return;

		Integer plusSalarialConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PLUS_SALARIAL"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		Integer plusExtraSalarialConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PLUS_EXTRA_SALARIAL"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DESCRIPTION, "PLUS SALARIAL MENSUAL")
			.where(PAYMENT_CONCEPT.ID.eq(plusSalarialConceptId))
			.execute()
			;
			
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "PLUS_SALARIAL")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.EXPRESSION, "FRACCIONAR(/*user*/0.00/**/)")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "PLUS SALARIAL FIJO")
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "PLUS_SALARIAL")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.EXPRESSION, "(PLUS_HORA=/*user*/0.00/**/) * HORAS_TRABAJADAS")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "PLUS SALARIAL HORA (@{PLUS_HORA} \u20AC X @{HORAS_TRABAJADAS} HORAS)")
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "PLUS_SALARIAL")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.EXPRESSION, "(PLUS_DIARIO=/*user*/0.00/**/) * DIAS_TRABAJADOS")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "PLUS SALARIAL DIARIO (@{PLUS_DIARIO} \u20AC X @{DIAS_TRABAJADOS} D\u00CDAS)")
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "PLUS_SALARIAL")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.EXPRESSION, "(PLUS_DIARIO=/*user*/0.00/**/) * DIAS_EFECTIVOS")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "PLUS SALARIAL DIAS REALES (@{PLUS_DIARIO} \u20AC X @{DIAS_EFECTIVOS} D\u00CDAS)")
			.execute()
			;

			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DESCRIPTION, "PLUS EXTRA SALARIAL")
			.set(PAYMENT_CONCEPT.EXPRESSION, "FRACCIONAR(/*user*/0.00/**/)")
			.where(PAYMENT_CONCEPT.ID.eq(plusExtraSalarialConceptId))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
