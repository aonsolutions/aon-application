package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DefaultAgreementUpdate implements Update {
	
	private static final Date EPOCH = new Date(0);
	private static final String ESTATUTO_DE_LOS_TRABAJADORES = "ESTATUTO DE LOS TRABAJADORES";

	public static final DefaultAgreementUpdate DEFAULTAGREEMENTUPDATE = new DefaultAgreementUpdate();
	
	private static final String WARNNING = "/*default*/HIDE(\""
	+"<div>Este trabajador no tiene asignado ning&uacute;n convenio ni concepto propio."
	+"Por defecto se le asigna un convenio que asegura las Bases M&iacute;nimas.</div>"
	+"<div>Si desea no utilizar este convenio, asigne un convenio y/o concepto a este trabajador.</div>"
	+"<div>&nbsp;</div><div class='aon-text-right'>Atentamente, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
	;

	private DefaultAgreementUpdate() {
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

		boolean upgraded = false;
		
		if ( upgraded ) 
			return;
		
		int salarioBaseId = 
		dslContext
		.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("SALARIO_BASE"))
		.and(PAYMENT_CONCEPT.DESCRIPTION.eq("SALARIO BASE MENSUAL"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		
		int pagaExtraId = 
		dslContext
		.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		
		int plusSalarialId = 
		dslContext
		.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PLUS_SALARIAL"))
		.and(PAYMENT_CONCEPT.DESCRIPTION.eq("PLUS SALARIAL MENSUAL"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			// ----------------------------------------------------------------

			dslContext
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*default*//*read-only*/isdef DIAS_TRABAJADOS ? BASE_CGC_MIN * 12 / 14: REMOVE()/**/")
			.where(AGREEMENT_PAYMENT.DOMAIN.eq(0))
			.and(AGREEMENT_PAYMENT.AGREEMENT.eq(0))
			.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(salarioBaseId))
			.execute();
			
			dslContext
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*default*//*read-only*/SALARIO_BASE / 12/**/")
			.where(AGREEMENT_PAYMENT.DOMAIN.eq(0))
			.and(AGREEMENT_PAYMENT.AGREEMENT.eq(0))
			.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(pagaExtraId))
			.execute();

			dslContext.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.DOMAIN.eq(0))
			.and(AGREEMENT_PAYMENT.AGREEMENT.eq(0))
			.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(plusSalarialId))
			.execute()
			;

			dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN,0)
			.set(AGREEMENT_PAYMENT.AGREEMENT,0)
			.set(AGREEMENT_PAYMENT.TYPE, (byte) 1)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*default*//*read-only*/PLUS_MENSUAL * DIAS_TRABAJADOS / DIAS_MES /**/")
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT,plusSalarialId)
			.execute();

			dslContext.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.DOMAIN.eq(0))
			.and(AGREEMENT_PAYMENT.AGREEMENT.eq(0))
			.and(AGREEMENT_PAYMENT.DESCRIPTION.eq("WARNNING"))
			.execute()
			;

			dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN,0)
			.set(AGREEMENT_PAYMENT.AGREEMENT,0)
			.set(AGREEMENT_PAYMENT.TYPE, (byte) 1)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.EXPRESSION, WARNNING)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "WARNNING")
			.execute();

			dslContext.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.DOMAIN.eq(0))
			.and(AGREEMENT_PAYMENT.AGREEMENT.eq(0))
			.and(AGREEMENT_PAYMENT.TYPE.eq((byte) 55))
			.execute()
			;

			dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN,0)
			.set(AGREEMENT_PAYMENT.AGREEMENT,0)
			.set(AGREEMENT_PAYMENT.TYPE, (byte) 55)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "MEJORAS PREST.SS.IT EC")
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*default*//*read-only*/isdef DIAS_ENFERMEDAD_COMUN ? GTZDO(TODO) : HIDE()/**/")
			.execute();

			dslContext.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN,0)
			.set(AGREEMENT_PAYMENT.AGREEMENT,0)
			.set(AGREEMENT_PAYMENT.TYPE, (byte) 55)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "MEJORAS PREST.SS.IT AT/EP")
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*default*//*read-only*/isdef DIAS_ENFERMEDAD_PROFESIONAL ? GTZDO(TODO) : HIDE()/**/")
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

	
}
