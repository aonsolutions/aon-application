package net.aonsolutions.db.up2date.management;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;
import static com.esferalia.aon.jooq.tables.BankStatementLink.BANK_STATEMENT_LINK;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class KaldeviFinanceFix implements Update {

	private static final Logger LOGGER  = Logger.getLogger(KaldeviFinanceFix.class.getName());

	public static final KaldeviFinanceFix KALDEVIFINANCEFIX = new KaldeviFinanceFix();
	
	private class Counters {
		int fRead;
		int fUpdated;
		int bsUpdated;
		int ftDeleted;
		int bslDeleted;
	}

	private enum FinanceStatus {
		PENDING,
		BATCHED,
		RETURNED,
		PAID,
		SETTLED;

		public Byte value() {
			return (byte) this.ordinal();
		}
	}
	
	private enum FinanceTrackingType {
		BATCHED ( FinanceStatus.BATCHED),
		PAID ( FinanceStatus.PAID),
		RETURNED ( FinanceStatus.RETURNED),
	    FRACTIONED ( FinanceStatus.PENDING),
	    SETTLED( FinanceStatus.SETTLED);
		
		private FinanceStatus financeStatus;
		private FinanceTrackingType (FinanceStatus financeStatus) {
			this.financeStatus = financeStatus;
		}
		
		public FinanceStatus getFinanceStatus() {
			return financeStatus;
		}
	}	
	public static final int KALDEVI_DOMAIN = 8353;
	public static final int KALDEVI_BANK_STATEMENT_ID = 2433681; 
	
	public static final KaldeviFinanceFix ALTER_FS_MODEL_200_2018 = new KaldeviFinanceFix();

	private KaldeviFinanceFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		LOGGER.info("[START]");
		LOGGER.info( "Arreglo vencimientos de Kaldevi" );
		
		final Counters c = new Counters();
		
		Field<Integer> count1 = DSL.count(BANK_STATEMENT_LINK.ID); 
		Record1<Integer> count2 = dslContext.select( DSL.count() )
			.from(BANK_STATEMENT_LINK)
			.where(BANK_STATEMENT_LINK.DOMAIN.eq(KALDEVI_DOMAIN))
			.and(BANK_STATEMENT_LINK.BANK_STATEMENT.eq(KALDEVI_BANK_STATEMENT_ID))
			.limit(1)
			.fetch()
			.stream()
			.findFirst()
			.orElse(null);
		int counted = (count2 != null) ? count2.get(count1) : 0; 
		if  (counted>4000) {
			
			dslContext.transaction(config -> {
				dslContext.select(BANK_STATEMENT_LINK.ID,BANK_STATEMENT_LINK.SOURCE_ID)
					.from(BANK_STATEMENT_LINK)
					.where(BANK_STATEMENT_LINK.DOMAIN.eq(KALDEVI_DOMAIN))
					.and(BANK_STATEMENT_LINK.BANK_STATEMENT.eq(KALDEVI_BANK_STATEMENT_ID))
					.fetch()
					.stream()
					.forEach( rec -> {
						int bslId = rec.get(BANK_STATEMENT_LINK.ID);
						int ftId = rec.get(BANK_STATEMENT_LINK.SOURCE_ID);
						
						c.ftDeleted += dslContext.delete(FINANCE_TRACKING)
								.where(FINANCE_TRACKING.DOMAIN.eq(KALDEVI_DOMAIN))
								.and(FINANCE_TRACKING.BANK_STATEMENT_LINK.eq(bslId))
								.execute();
						c.bslDeleted += dslContext.delete(BANK_STATEMENT_LINK)
								.where(BANK_STATEMENT_LINK.DOMAIN.eq(KALDEVI_DOMAIN))
								.and(BANK_STATEMENT_LINK.ID.eq(bslId))
								.execute();
					});
				c.bsUpdated = dslContext.update(BANK_STATEMENT)
					.set(BANK_STATEMENT.STATUS,(byte) 0)
					.where(BANK_STATEMENT.DOMAIN.eq(KALDEVI_DOMAIN))
					.and(BANK_STATEMENT.ID.eq(KALDEVI_BANK_STATEMENT_ID))
					.execute();
				
				dslContext.select(FINANCE.ID)
				.from(FINANCE)
				.where(FINANCE.DOMAIN.eq(KALDEVI_DOMAIN))
				.and(FINANCE.STATUS.eq(FinanceStatus.PAID.value()))
				.fetch()
				.stream()
				.map( rec -> rec.get(FINANCE.ID))
				.forEach( id -> {
					Record1<Byte> financeTrackingType = dslContext
						.select(FINANCE_TRACKING.TYPE)
						.from(FINANCE_TRACKING)
						.where(FINANCE_TRACKING.DOMAIN.eq(KALDEVI_DOMAIN))
						.and(FINANCE_TRACKING.FINANCE.eq(id))
						.orderBy(FINANCE_TRACKING.ID.desc()) // FINANCE_TRACKING.TRACKING_DATE.desc(), 
						.limit(1)
						.fetch()
						.stream()
						.findFirst()
						.orElse(null);
					FinanceStatus status = FinanceStatus.PENDING;				
					if (financeTrackingType != null) {
						Byte byteType = financeTrackingType.get(FINANCE_TRACKING.TYPE);
						FinanceTrackingType type = FinanceTrackingType.values()[byteType];
						status = type.getFinanceStatus();
					}
					if ( status != FinanceStatus.PAID) {
						int updated = dslContext.update(FINANCE)
							.set(FINANCE.STATUS,status.value())
							.where(FINANCE.DOMAIN.eq(KALDEVI_DOMAIN))
							.and(FINANCE.ID.eq(id))
							.execute();
						c.fUpdated += updated;
					}
					c.fRead++;
					if (c.fRead % 100 == 0) {
						LOGGER.info(".");
					}
					if (c.fRead % 5000 == 0) {
						LOGGER.info (" " + c.fRead + "(" + c.fUpdated + " updated)");
					}
				});
	
			})
			;
			LOGGER.info (" ");
			String msg = " [END] \n "
					+ " \t FINANCE_TRACKING:" + c.ftDeleted+ " deleted \n"
				+ " \t BANK_STATEMENT_LINK :" + c.bslDeleted + " deleted \n"
				+ " \t BANK_STATEMENT :" + c.bsUpdated  + " updated \n"
				+ " \t FINANCE:" + c.fRead + "(" + c.fUpdated + " updated) \n";
			LOGGER.info (msg);
		} else {
			LOGGER.info (" ");
			String msg2 = " [END] Nothing done ("+ counted +")";
			LOGGER.info (msg2);
		}
	
	}

}
