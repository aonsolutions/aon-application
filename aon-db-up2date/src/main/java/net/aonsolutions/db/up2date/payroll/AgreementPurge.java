package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectHavingConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.PayrollWorkplace;

import net.aonsolutions.db.up2date.Update;

public class AgreementPurge implements Update {

	public static final AgreementPurge AGREEMENTPURGE = new AgreementPurge();
	
	private AgreementPurge() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.transaction( config -> {
		    	int deleted = 0;
			Integer [] unusedAgreement ; 
			
		    	DSLContext dsl = config.dsl();		
			
			do {
			    	unusedAgreement = dslContext
        		    	.select(AGREEMENT_LEVEL.AGREEMENT)
        			.from(AGREEMENT_LEVEL)
        			.leftJoin(CONTRACT).onKey()
        			.where(AGREEMENT_LEVEL.AGREEMENT.lt(0))
        			.groupBy(AGREEMENT_LEVEL.AGREEMENT)
        			.having(DSL.count(CONTRACT.ID).eq(0))
        			.limit(50)
        			.fetchArray(AGREEMENT_LEVEL.AGREEMENT);
        
        			dsl.update(PAYROLL_WORKPLACE)
        			.set(PAYROLL_WORKPLACE.AGREEMENT, DSL.castNull(PAYROLL_WORKPLACE.AGREEMENT))
        			.where(PAYROLL_WORKPLACE.AGREEMENT.in(unusedAgreement));

        			dsl.delete(AGREEMENT_LEVEL_DATA).using(AGREEMENT_LEVEL_DATA.innerJoin(AGREEMENT_LEVEL).onKey()).where(AGREEMENT_LEVEL.AGREEMENT.in(unusedAgreement)).execute();
        			dsl.delete(AGREEMENT_LEVEL_CATEGORY).using(AGREEMENT_LEVEL_CATEGORY.innerJoin(AGREEMENT_LEVEL).onKey()).where(AGREEMENT_LEVEL.AGREEMENT.in(unusedAgreement)).execute();
        
        			dsl.delete(AGREEMENT_DATA).where(AGREEMENT_DATA.AGREEMENT.in(unusedAgreement)).execute();
        			dsl.delete(AGREEMENT_EXTRA).where(AGREEMENT_EXTRA.AGREEMENT.in(unusedAgreement)).execute();
        			dsl.delete(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.AGREEMENT.in(unusedAgreement)).execute();
        			dsl.delete(AGREEMENT_PAYMENT).where(AGREEMENT_PAYMENT.AGREEMENT.in(unusedAgreement)).execute();
        			deleted += dsl.delete(AGREEMENT).where(AGREEMENT.ID.in(unusedAgreement)).execute();
			} while ( unusedAgreement.length > 0 );
			
			System.out.println("Deleted " + deleted +" unused agreements.");
			
			
		});
	}

}
