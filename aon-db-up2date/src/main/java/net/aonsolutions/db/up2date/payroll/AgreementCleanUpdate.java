package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AgreementCleanUpdate implements Update {

	public static final AgreementCleanUpdate AGREEMENTCLEANUPDATE = new AgreementCleanUpdate();
	
	private AgreementCleanUpdate() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			removeTrashAgreements(dslContext);

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

	private void removeTrashAgreements(DSLContext dslContext) {
		
		List<Integer> agreementIds = dslContext.select(AGREEMENT.ID).from(AGREEMENT)
				.where(AGREEMENT.ID.lt(0)).fetch(AGREEMENT.ID);
		
		SelectConditionStep<Record1<Integer>> agreementLevelId = dslContext
				.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.in(agreementIds));

		// Poner a null todos los contratos que apuntan al convenio borrado
		dslContext.update(CONTRACT)
			.set(CONTRACT.AGREEMENT_LEVEL, DSL.val(null, CONTRACT.AGREEMENT_LEVEL))
			.set(CONTRACT.CATEGORY_DESCRIPTION, DSL.val(null, CONTRACT.CATEGORY_DESCRIPTION))
			.where(CONTRACT.AGREEMENT_LEVEL.in(agreementLevelId))
			.execute();
		
		dslContext.delete(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
				.in(agreementLevelId)).execute();

		dslContext.delete(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL
				.in(agreementLevelId)).execute();

		dslContext.delete(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.in(agreementIds))
				.execute();

		dslContext.delete(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.AGREEMENT.in(agreementIds))
				.execute();

		dslContext.delete(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.in(agreementIds))
				.execute();

		dslContext.delete(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.AGREEMENT.in(agreementIds))
				.execute();

		dslContext.update(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.AGREEMENT, DSL.castNull(PAYROLL_WORKPLACE.AGREEMENT))
				.where(PAYROLL_WORKPLACE.AGREEMENT.in(agreementIds))
				.execute();

		if(!agreementIds.isEmpty())
			for(Integer agreementId : agreementIds)
				dslContext.delete(ENTERPRISE_DATA)
						.where(ENTERPRISE_DATA.NAME.eq(AGREEMENT.getName()))
						.and(ENTERPRISE_DATA.EXPRESSION.in(agreementId.toString()))
						.execute();
			
		dslContext.delete(AGREEMENT).where(AGREEMENT.ID.in(agreementIds))
				.execute();
		
		System.out.println("Agreements deleted = " + agreementIds.size());
		
	}
	
}
