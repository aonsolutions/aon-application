package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

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

public class AyudaTWarnDelete implements Update {
	
	private static final double DELTA = 0.01;

	public static final AyudaTWarnDelete AYUDATWARNDELETE = new AyudaTWarnDelete();
	
	private static final Date EPOCH = new Date(0);
	private static final Date FOREVER = null;
	
	private static final String WARNNING = "HIDE(\""
	+"<div>Este convenio ha sido modificado en la &uacute;ltima actualizaci&oacute;n."
	+"El convenio original est&aacute; disponible en la papelera.</div>"
	+"<div>Se ha detectado que el salario base y las pagas extras son iguales."
	+"Se han igualado las pagas al salario base eliminado los importes 'duplicados' de las pagas.</div>"
	+"<div>Si desea recuperar el convenio original, envie este convenio a la papelera.</div>"
	+"<div>&nbsp;</div><div class='aon-text-right'>Disculpe las molestias, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\");"
	;
	

	private AyudaTWarnDelete() {
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
			
		boolean ayudaT = dslContext.fetchCount(
		dslContext
		.select()
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq("ayudat.aonsolutions.net"))
		) > 0;
		
		if ( !ayudaT )
			return;
		
		

		
		dslContext.transaction( (config) -> {
			SelectConditionStep<Record1<Integer>> warnAgreements = 
			DSL
			.select(AGREEMENT_PAYMENT.AGREEMENT.mul(-1))
			.from(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.gt(0))
			.and(AGREEMENT_PAYMENT.DOMAIN.gt(0))
			.and(AGREEMENT_PAYMENT.EXPRESSION.eq(WARNNING))
			.and(AGREEMENT_PAYMENT.DESCRIPTION.eq("WARNNING"))	;
			SelectConditionStep<Record1<Integer>> warnAgreementsLevels = 
			DSL
			.select(AGREEMENT_LEVEL.ID)
			.from(AGREEMENT_LEVEL)
			.where(AGREEMENT_LEVEL.AGREEMENT.in(warnAgreements));
			
			dslContext.delete(AGREEMENT_EXTRA)
			.where(AGREEMENT_EXTRA.ID.lt(0))
			.and(AGREEMENT_EXTRA.AGREEMENT.in(warnAgreements))
			.execute();
			dslContext.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.ID.lt(0))
			.and(AGREEMENT_PAYMENT.AGREEMENT.in(warnAgreements))
			.execute();
			dslContext.delete(AGREEMENT_DATA)
			.where(AGREEMENT_DATA.ID.lt(0))
			.and(AGREEMENT_DATA.AGREEMENT.in(warnAgreements))
			.execute();
			dslContext.delete(AGREEMENT_LEVEL_DATA)
			.where(AGREEMENT_LEVEL_DATA.ID.lt(0))
			.and(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(warnAgreementsLevels))
			.execute();
			dslContext.delete(AGREEMENT_LEVEL_CATEGORY)
			.where(AGREEMENT_LEVEL_CATEGORY.ID.lt(0))
			.and(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.in(warnAgreementsLevels))
			.execute();
			dslContext.delete(AGREEMENT_LEVEL)
			.where(AGREEMENT_LEVEL.ID.lt(0))
			.and(AGREEMENT_LEVEL.AGREEMENT.in(warnAgreements))
			.execute();
			dslContext.delete(AGREEMENT)
			.where(AGREEMENT.ID.in(warnAgreements))
			.execute();
	
			dslContext
			.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.gt(0))
			.and(AGREEMENT_PAYMENT.DOMAIN.gt(0))
			.and(AGREEMENT_PAYMENT.EXPRESSION.eq(WARNNING))
			.and(AGREEMENT_PAYMENT.DESCRIPTION.eq("WARNNING"))	
			.execute();
		});
	}
	
	
	
}
