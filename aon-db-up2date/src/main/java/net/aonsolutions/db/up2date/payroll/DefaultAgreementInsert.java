package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DefaultAgreementInsert implements Update {
	
	private static final Date EPOCH = new Date(0);
	private static final String ESTATUTO_DE_LOS_TRABAJADORES = "ESTATUTO DE LOS TRABAJADORES";

	public static final DefaultAgreementInsert DEFAULTAGREEMENTINSERT = new DefaultAgreementInsert();
	

	private DefaultAgreementInsert() {
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

		int defAgreemetId =  	
			dslContext
			.select(AGREEMENT.ID)
			.from(AGREEMENT)
			.where(AGREEMENT.DOMAIN.eq(0))
			.and(AGREEMENT.DESCRIPTION.eq(ESTATUTO_DE_LOS_TRABAJADORES))
			.fetchOptional(AGREEMENT.ID)
			.orElse(Integer.MIN_VALUE);
		
		boolean upgraded = defAgreemetId == 0;
		
		if ( upgraded ) 
			return;
		
		
		
		SelectConditionStep<Record1<Integer>> oldAgreementsLevelsIds = 
		dslContext
		.select(AGREEMENT_LEVEL.ID)
		.from(AGREEMENT_LEVEL)
		.where(AGREEMENT_LEVEL.AGREEMENT.eq(defAgreemetId));

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(AGREEMENT)
			.where(AGREEMENT.ID.eq(defAgreemetId))
			.execute()
			;
			
			dslContext
			.delete(AGREEMENT_LEVEL_DATA)
			.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(oldAgreementsLevelsIds))
			.execute()
			;
			 
			dslContext
			.delete(AGREEMENT_LEVEL_CATEGORY)
			.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(oldAgreementsLevelsIds))
			.execute()
			;
			 
			dslContext
			.delete(AGREEMENT_LEVEL)
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(defAgreemetId))
			.execute()
			;
			 
			dslContext
			.delete(AGREEMENT_DATA)
			.where(AGREEMENT_DATA.AGREEMENT.eq(defAgreemetId))
			.execute()
			;
			 
			dslContext
			.delete(AGREEMENT_EXTRA)
			.where(AGREEMENT_EXTRA.AGREEMENT.eq(defAgreemetId))
			.execute()
			;
			 
			dslContext
			.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(defAgreemetId))
			.execute()
			;
			
			// ----------------------------------------------------------------
			
			int defAgreementId =
			dslContext
			.insertInto(AGREEMENT)
			.set(AGREEMENT.DOMAIN, 0)
			.set(AGREEMENT.DESCRIPTION, ESTATUTO_DE_LOS_TRABAJADORES)
			.returning()
			.fetchOne()
			.getId()
			;
			
			dslContext
			.update(AGREEMENT)
			.set(AGREEMENT.ID, 0)
			.where(AGREEMENT.ID.eq(defAgreementId))
			.execute();
			
			String levels [] = {
			"-",
			"I",
			"II",
			"III",
			"IV",
			"V",
			"VI",
			"VII",
			"VIII",
			"IX",
			"X",
			"XI"
			};
			int agreementLevelsIds [] = new int [12];
			for ( int i = 1; i < 12; i++ )
				agreementLevelsIds[i] =
				dslContext
				.insertInto(AGREEMENT_LEVEL)
				.set(AGREEMENT_LEVEL.DOMAIN, 0)
				.set(AGREEMENT_LEVEL.AGREEMENT, 0)
				.set(AGREEMENT_LEVEL.DESCRIPTION, levels[i])
				.returning()
				.fetchOne()
				.getId();
			
			dslContext
			.insertInto(AGREEMENT_LEVEL_CATEGORY)
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[1])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Ingeniero")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[1])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Licenciado")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[1])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Personal de alta direcci\u00F3n")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[2])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Ingeniero T\u00E9cnico")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[2])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Perito")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[2])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Ayudante Titulado")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[3])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Jefe Administrativos")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[3])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Jefe de Taller")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[4])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Ayudante no Titulado")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[5])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Oficial Administrativo")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[6])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Subalterno")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[7])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Auxiliar Administrativo")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[8])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Oficial de primera")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[8])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Oficial de segunda")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[9])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Oficial de tercera")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[9])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Especialista")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[10])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Pe\u00F3n")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[11])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Menor de dieciocho")
			.newRecord()
			.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, 0)
			.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, agreementLevelsIds[11])
			.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, "Cualquiera")
			.execute();
			
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

			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, 0)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, salarioBaseId)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*read-only*/BASE_CGC_MIN / 12 * DIAS_TRABAJADOS / DIAS_MES/**/")
			.newRecord()
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, 0)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraId)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*read-only*/SALARIO_BASE / 12/**/")
			.newRecord()
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, 0)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, pagaExtraId)
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*read-only*/SALARIO_BASE / 12/**/")
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

	
}
