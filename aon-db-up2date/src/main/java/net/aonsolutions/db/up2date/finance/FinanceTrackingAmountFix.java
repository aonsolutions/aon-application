package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;

import java.sql.Connection;
import java.sql.Timestamp;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FinanceTrackingAmountFix implements Update {

	public static final FinanceTrackingAmountFix FINANCE_TRACKING_AMOUNT_FIX = new FinanceTrackingAmountFix();
	
	private FinanceTrackingAmountFix() {
		
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		System.out.println("\tFinanceTrackingAmountFix. [START]");
		int[] count = {0};
		dslContext.select(
				 FINANCE_TRACKING.ID
				,FINANCE_TRACKING.DOMAIN
				,FINANCE.AMOUNT)
			.from(FINANCE_TRACKING)
			.innerJoin(FINANCE).on(FINANCE_TRACKING.FINANCE.eq(FINANCE.ID))
			.where(FINANCE_TRACKING.AMOUNT.eq(0.0))
			.and(FINANCE_TRACKING.AMOUNT.ne(FINANCE.AMOUNT))
			.fetch()
			.stream()
			.forEach( rec -> {
				Integer financeTrackingId = rec.getValue(FINANCE_TRACKING.ID); 
				Integer financeTrackingDomain = rec.getValue(FINANCE_TRACKING.DOMAIN);
				Double financeAmount = rec.getValue(FINANCE.AMOUNT);
				int c = dslContext.update(FINANCE_TRACKING)
					.set(FINANCE_TRACKING.AMOUNT, financeAmount)
					.set(FINANCE_TRACKING.MODIFICATION_USER,"FT_AmountFix")
					.set(FINANCE_TRACKING.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.where(FINANCE_TRACKING.ID.eq(financeTrackingId))
					.and(FINANCE_TRACKING.DOMAIN.eq(financeTrackingDomain))
					.execute();
				count[0] = count[0] + c;
			});
		System.out.println("\tFinanceTrackingAmountFix. [END] " + count[0] + " rows updated." );
	}
}
