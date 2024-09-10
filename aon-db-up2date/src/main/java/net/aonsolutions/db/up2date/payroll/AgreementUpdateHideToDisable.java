package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class AgreementUpdateHideToDisable implements Update {

	public static final AgreementUpdateHideToDisable AGREEMENTUPDATEHIDETODISABLE = new AgreementUpdateHideToDisable();
	
	private AgreementUpdateHideToDisable() {}
	
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
			
			updateHideMessageAgreementPayments(dslContext);

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

	private void updateHideMessageAgreementPayments(DSLContext dslContext) {
		
		List<AgreementPaymentRecord> agreementPayments = dslContext.selectFrom(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.EXPRESSION.like("HIDE();%"))
			.fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			String expression = agreementPayment.getExpression();
			expression = expression.replaceAll("HIDE\\(.*\\);", "DISABLE();");
			
			agreementPayment.setExpression(expression);
			agreementPayment.update();
		});
		
		System.out.println("AgreementPayment Expression Updates : " + agreementPayments.size());
	}
	
}
