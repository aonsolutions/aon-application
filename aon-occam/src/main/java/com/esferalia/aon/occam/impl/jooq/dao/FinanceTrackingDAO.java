package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.SelectWhereStep;

import com.esferalia.aon.jooq.tables.records.FinanceTrackingRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.PayMethodTypeDetail;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO.FullFinanceFiller;
import com.esferalia.aon.occam.impl.jooq.validation.FinanceValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceTrackingDAO {

	private static final com.esferalia.aon.jooq.tables.Account RBANK_ACCOUNT = ACCOUNT.as("rbAcc");;
	private static final com.esferalia.aon.jooq.tables.Account PM_TYPE_DETAIL_ACCOUNT = ACCOUNT.as("pmAcc");

	// -------------------------------------------------------------
	// ------------------------ LECTURA ----------------------------
	// -------------------------------------------------------------
	public static LinkedList<FinanceTracking> getFinanceTrackings(AONContext ctx, Integer financeId) {
		return getFinanceTrackingSelect( ctx )
			.where(FINANCE_TRACKING.FINANCE.eq(financeId))
			.orderBy(FINANCE_TRACKING.ID)
			.fetch()
			.stream()
			.map( new FinanceTrackingFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static FinanceTracking getFinanceTracking(AONContext ctx, Integer trackingId) {
		return getFinanceTrackingSelect( ctx )
			.where(FINANCE_TRACKING.ID.eq(trackingId))
			.fetch()
			.stream()
			.map( new FinanceTrackingFiller() )
			.findFirst()
			.orElse(null);
	}

	private static SelectWhereStep<Record> getFinanceTrackingSelect(AONContext ctx) {
		return ctx.getDslContext()
			.select(FINANCE_TRACKING.fields())
			.select(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY)
			.select(RBANK.fields())
			.select(RBANK_ACCOUNT.fields())
			.select(PM_TYPE_DETAIL.fields())
			.select(PM_TYPE_DETAIL_ACCOUNT.fields())
			.from(FINANCE_TRACKING)
			.leftOuterJoin(ACCOUNT_ENTRY_FINANCE_TRACKING).on(FINANCE_TRACKING.ID.equal(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING))
			.leftOuterJoin(RBANK).on(FINANCE_TRACKING.RBANK.equal(RBANK.ID))
			.leftOuterJoin(RBANK_ACCOUNT).on(RBANK.ACCOUNT.equal(RBANK_ACCOUNT.ID))
			.leftOuterJoin(PM_TYPE_DETAIL).on(FINANCE_TRACKING.PM_TYPE_DETAIL.equal(PM_TYPE_DETAIL.ID))
			.leftOuterJoin(PM_TYPE_DETAIL_ACCOUNT).on(PM_TYPE_DETAIL.ACCOUNT.equal(PM_TYPE_DETAIL_ACCOUNT.ID))
		;
	}

	public static boolean isLastTracking(AONContext ctx, FinanceTracking ft) {
		return !ctx.getDslContext()
			.select()
			.from(FINANCE_TRACKING)
			.where(FINANCE_TRACKING.FINANCE.eq(ft.getFinance().getId()))
			.and(FINANCE_TRACKING.ID.gt(ft.getId()))
			.fetch()
			.stream()
			.findAny()
			.isPresent();
	}

	public static FinanceTracking getLastTracking(AONContext ctx, Integer financeId) {
		LinkedList<FinanceTracking> trackings = getFinanceTrackings(ctx, financeId);
		if ( trackings != null && !trackings.isEmpty()) {
			return trackings.getLast();	
		}
		return null;
	}

	// -------------------------------------------------------------
	// ------------------------ ESCRITURA --------------------------
	// -------------------------------------------------------------
	public static Integer insert(AONContext ctx, FinanceTracking ft) {
		ctx.checkWrite();
		FinanceTrackingRecord record = ctx.getDslContext()
			.insertInto(FINANCE_TRACKING)
				.set(FINANCE_TRACKING.DOMAIN,ft.getDomain())
				.set(FINANCE_TRACKING.FINANCE,ft.getFinance().getId())
				.set(FINANCE_TRACKING.TRACKING_DATE,AonDateUtils.toSql(ft.getTrackingDate()))
				.set(FINANCE_TRACKING.TYPE,ft.getType().value())
				.set(FINANCE_TRACKING.DESCRIPTION,ft.getDescription())
				.set(FINANCE_TRACKING.PM_TYPE_DETAIL,ft.getPayMethodTypeDetail()==null?null:ft.getPayMethodTypeDetail().getId())
				.set(FINANCE_TRACKING.RBANK,ft.getRegistryBank()==null?null:ft.getRegistryBank().getId())
				.set(FINANCE_TRACKING.BANK_STATEMENT_LINK,ft.getBankStatementLink())
				.set(FINANCE_TRACKING.AMOUNT,ft.getAmount())
				.set(FINANCE_TRACKING.RECORDED, AonEnumUtils.getByte(ft.isRecorded()))
				.set(FINANCE_TRACKING.CREATION_USER,ctx.getUser())
				.set(FINANCE_TRACKING.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.returning(FINANCE_TRACKING.ID)
				.fetchOne();
		ctx.log().info("INSERT FINANCE_TRACKING id: " + record.getValue(FINANCE_TRACKING.ID));
		return record.getValue(FINANCE_TRACKING.ID); 
	}
	
	private static void updateFinanceStatus(AONContext ctx,Integer financeId,FinanceStatus financeStatus) {
		int i = ctx.getDslContext().update(FINANCE)
				.set(FINANCE.STATUS,financeStatus.value())
				.set(FINANCE.MODIFICATION_USER,ctx.getUser())
				.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.where(FINANCE.ID.equal( financeId))
				.execute();
		ctx.log().info("UPDATE FINANCE  ("+i+") id: " + financeId + " status: " + financeStatus.getDescription());
	}

	public static void delete(AONContext ctx, FinanceTracking tracking) {
		if (!isLastTracking(ctx,tracking)) {
			throw new AonCoreException(AonError.FINANCE_TRACKING_LATER_TRACKINGS.getMessage());
		}
		
		if (tracking.getBankStatementLink() == null ) {
			if (tracking.isRecorded()) {
				if (SecurityDAO.getUser(ctx).hasAccountingRole()) {
					deleteAccountEntryFinanceTracking(ctx,tracking);
				} else {
					throw new AonCoreException(AonError.FINANCE_TRACKING_RECORDED.getMessage());		
				}
			}
			int i = ctx.getDslContext()
				.delete(FINANCE_TRACKING)
				.where(FINANCE_TRACKING.ID.equal(tracking.getId()))
				.execute();
			ctx.log().info("DELETE FINANCE_TRACKING  ("+i+") id: " + tracking.getId());
			FinanceTracking previuosTracking = getLastTracking(ctx, tracking.getFinance().getId());
			FinanceStatus newStatus = previuosTracking == null ? FinanceStatus.PENDING : previuosTracking.getType().getFinanceStatus(); 
			updateFinanceStatus(ctx, tracking.getFinance().getId(), newStatus );
		} else {
			// TODO El movimiento viene de extracto bancario.
		}
	}
	
	private static void deleteAccountEntryFinanceTracking(AONContext ctx, FinanceTracking ft) {
		int i = ctx.getDslContext()
			.delete(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.where(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING.equal(ft.getId()))
			.execute();
		ctx.log().info("DELETE ACCOUNT_ENTRY_FINANCE_TRACKING ("+i+") Tracking: " + ft.getId());
	}
	
	
	// -------------------------------------------------------------
	// ----------------- SALDAR UN VENCIMIENTO ---------------------
	// -------------------------------------------------------------
	
	public static Integer settle(AONContext ctx, Integer financeId) {
		ctx.log().info(" ----- START FINANCE SETTLE ----- ");
		try {
			ctx.checkWrite();
			Finance finance = FinanceValidation.validateSettleTracking(ctx, financeId);
			FinanceTracking ft = new FinanceTracking()
					.setFinance(finance)
					.setDomain(ctx.getDomainId())
					.setTrackingDate(new Date())
					.setType(FinanceTrackingType.SETTLED)
					.setDescription(FinanceTrackingType.SETTLED.getDescription())
					.setRegistryBank(null)
					.setPayMethodTypeDetail(null)
					.setBankStatementLink(null)
					.setAmount(finance.getAmount())
					.setRecorded(false);
			updateFinanceStatus(ctx,financeId,FinanceStatus.SETTLED);
			return insert(ctx, ft);
		} catch (Throwable t) {
			ctx.log().info(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().info(" ----- END FINANCE SETTLE ----- ");
		}
	}

	// -------------------------------------------------------------
	// ----------------- DESHACER UN VENCIMIENTO -------------------
	// -------------------------------------------------------------
	public static void undo(AONContext ctx, Integer financeId) {
		ctx.log().info(" ----- START FINANCE UNDO ----- ");
		try {
			ctx.checkWrite();
			FinanceTracking financeTracking = FinanceValidation.validateUndoTracking(ctx, financeId);
			Integer accountEntryId = ctx.getDslContext()
				.select(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY)
					.from(ACCOUNT_ENTRY_FINANCE_TRACKING)
					.where(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING.eq(financeTracking.getId()))
				.fetch()
				.stream()
				.map( rec -> rec.getValue(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY))
				.findFirst()
				.orElse(null);
			if (accountEntryId != null) {
				FinanceEntry fe = FinanceEntryDAO.getFinanceEntry(ctx, accountEntryId);
				if (fe.getTrackings().size() == 1) {
					AccountEntryDAO.delete(ctx, accountEntryId);
				} else {
					fe.getTrackings().get(financeId).setDeleted(true);
					FinanceEntryDAO.update(ctx, fe);
				}
			} else {
				FinanceTrackingDAO.delete(ctx, financeTracking);
			}
		} catch (Throwable t) {
			ctx.log().info(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().info(" ----- END FINANCE UNDO ----- ");
		}
	}
	
	// -------------------------------------------------------------
	// ----------------- PAGO DEL VENCIMIENTO ----------------------
	// -------------------------------------------------------------
	public static FinanceTracking pay(AONContext ctx, FinanceTracking tracking) {
		ctx.log().info(" ----- START FINANCE PAY ----- ");
		try {
			ctx.checkWrite();
			Finance finance = tracking.getFinance(); 
			FinanceValidation.validatePay(ctx, finance);
			AccountEntry entry = null;
			if (tracking.getPayAccount() != null && tracking.getPayAccount().getId() != null) {
				tracking.setDescription(AonStringUtils.abbreviate( 
					tracking.getPayAccount().getFullName(),FINANCE_TRACKING.DESCRIPTION.getDataType().length()));
				AccountEntry[] entries = FinanceEntryDAO.getPayFinanceEntry(ctx, tracking);
				if (entries != null && entries.length == 1) {
					entry = entries[0];	
					Integer entryId = AccountEntryDAO.save(ctx, entry);
					entry.setId(entryId);
					pay(ctx, tracking, entry);
				}
				return tracking;
			} 
			// TODO ¿Si no se quiere contabilizar el pago?
			throw new AonCoreException(AonError.FINANCE_TRACKING_NO_BANK_ACCOUNT.getMessage());
		} catch (Throwable t) {
			ctx.log().info(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().info(" ----- END FINANCE PAY ----- ");
		}
	}
	
	public static void pay(AONContext ctx,FinanceTracking tracking,AccountEntry entry) {
		tracking.setDomain(ctx.getDomainId())
		  .setTrackingDate(entry.getEntryDate())
		  .setType(FinanceTrackingType.PAID)
		  .setRecorded(true)
		;
		updateFinanceStatus(ctx,tracking.getFinance().getId(),FinanceStatus.PAID);
		Integer trackingId = insert(ctx, tracking);
		ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY,entry.getId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING,trackingId)
			.execute();
		ctx.log().info("ACCOUNT_ENTRY_FINANCE_TRACKING account_entry: " + entry.getId() + " tracking: " + trackingId);
	}
	
	// -------------------------------------------------------------
	// ----------------- DEVOLUCION DEL VENCIMIENTO ----------------
	// -------------------------------------------------------------
	public static FinanceTracking returnFinance(AONContext ctx, FinanceTracking tracking) {
		ctx.log().info(" ----- START FINANCE RETURN ----- ");
		try {
			ctx.checkWrite();
			FinanceValidation.validateReturn(ctx, tracking);
			AccountEntry entry = null;
			if (tracking.getPayAccount() != null && tracking.getPayAccount().getId() != null) {
				tracking.setDescription(AonStringUtils.abbreviate( 
						tracking.getPayAccount().getFullName(),FINANCE_TRACKING.DESCRIPTION.getDataType().length()));
				AccountEntry[] entries = FinanceEntryDAO.getReturnFinanceEntry(ctx, tracking);
				if (entries != null && entries.length == 1) {
					entry = entries[0];	
					Integer entryId = AccountEntryDAO.save(ctx, entry);
					entry.setId(entryId);
					tracking = returnFinance(ctx,tracking, entry);
				}
				return tracking;
			}
			// TODO ¿Si no se quiere contabilizar el pago?
			throw new AonCoreException(AonError.FINANCE_TRACKING_NO_BANK_ACCOUNT.getMessage());
		} catch (Throwable t) {
			ctx.log().info(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().info(" ----- END FINANCE RETURN ----- ");
		}
	}
	
	private static FinanceTracking returnFinance(AONContext ctx,FinanceTracking ft,AccountEntry entry) {
		ft.setTrackingDate(entry.getEntryDate())
		  .setType(FinanceTrackingType.RETURNED)
		  .setRecorded(true)
		;
		updateFinanceStatus(ctx,ft.getFinance().getId(),FinanceStatus.RETURNED);
		Integer trackingId = insert(ctx, ft);
		ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY,entry.getId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING,trackingId)
			.execute();
		ctx.log().info("ACCOUNT_ENTRY_FINANCE_TRACKING account_entry: " + entry.getId() + " tracking: " + trackingId);
		return getFinanceTracking(ctx, trackingId);
	}
	
	// -------------------------------------------------------------
	// ---------------------------- MAP ----------------------------
	// -------------------------------------------------------------
	public static class FullFinanceTrackingFiller  implements Function<Record,FinanceTracking> {
		@Override
		public FinanceTracking apply(Record record) {
			return new FinanceTracking()
				.setId(record.getValue(FINANCE_TRACKING.ID))
				.setDomain(record.getValue(FINANCE_TRACKING.DOMAIN))
				.setRegistryBank( new RegistryBank().setId(record.getValue(FINANCE_TRACKING.RBANK)) )
				.setBankStatementLink(record.getValue(FINANCE_TRACKING.BANK_STATEMENT_LINK))
				.setFinance(new FullFinanceFiller().apply(record))
				.setPayMethodTypeDetail(new PayMethodTypeDetail().setId( record.getValue(FINANCE_TRACKING.PM_TYPE_DETAIL)))
				.setTrackingDate(record.getValue(FINANCE_TRACKING.TRACKING_DATE))
				.setType( FinanceTrackingType.safeValueOf(record.getValue(FINANCE_TRACKING.TYPE)))
				.setDescription(record.getValue(FINANCE_TRACKING.DESCRIPTION))
				.setAmount(record.getValue(FINANCE_TRACKING.AMOUNT))
				.setRecorded(AonEnumUtils.getBoolean( record.getValue(FINANCE_TRACKING.RECORDED)))
				.setAccountEntry(record.getValue(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY))
				.setCreationUser(record.getValue(FINANCE_TRACKING.CREATION_USER))
				.setCreationDate(record.getValue(FINANCE_TRACKING.CREATION_DATE))
				.setModificationUser(record.getValue(FINANCE_TRACKING.MODIFICATION_USER))
				.setModificationDate(record.getValue(FINANCE_TRACKING.MODIFICATION_DATE))
				;
		}
	}

	public static class FinanceTrackingFiller  implements Function<Record,FinanceTracking> {
		@Override
		public FinanceTracking apply(Record record) {
			return new FinanceTracking()
				.setId(record.getValue(FINANCE_TRACKING.ID))
				.setDomain(record.getValue(FINANCE_TRACKING.DOMAIN))
				.setRegistryBank(new RegistryBank()
					.setId(record.getValue(RBANK.ID))
					.setRegistry(record.getValue(RBANK.REGISTRY))
					.setDomain(record.getValue(RBANK.DOMAIN))
					.setAccount(record.getValue(RBANK.ACCOUNT))
					.setAccountCode(record.getValue(RBANK_ACCOUNT.CODE))
					.setAccountDescription(record.getValue(RBANK_ACCOUNT.DESCRIPTION))
					.setActive(AonEnumUtils.getBoolean( record.getValue(RBANK.ACTIVE)))
					.setAlias(record.getValue(RBANK.ALIAS))
					.setBankAccount(new BankAccount(record.getValue(RBANK.BANK_ACCOUNT)))
					.setBic(record.getValue(RBANK.BIC))
					.setSuffix(record.getValue(RBANK.SUFIX)))
				.setPayMethodTypeDetail(new PayMethodTypeDetail()
						.setId(record.getValue(PM_TYPE_DETAIL.ID))
						.setDomain(record.getValue(PM_TYPE_DETAIL.DOMAIN))
						.setDescription(record.getValue(PM_TYPE_DETAIL.DESCRIPTION))
						.setType(PayMethodType.safeValueOf( record.getValue(PM_TYPE_DETAIL.TYPE)))
						.setAccount(new Account()
							.setId(record.getValue(PM_TYPE_DETAIL_ACCOUNT.ID))
							.setCode(record.getValue(PM_TYPE_DETAIL_ACCOUNT.CODE))
							.setDescription(record.getValue(PM_TYPE_DETAIL_ACCOUNT.DESCRIPTION))))
				.setBankStatementLink(record.getValue(FINANCE_TRACKING.BANK_STATEMENT_LINK))
				.setFinance(new Finance().setId(record.getValue(FINANCE_TRACKING.FINANCE)))
				.setTrackingDate(record.getValue(FINANCE_TRACKING.TRACKING_DATE))
				.setType( FinanceTrackingType.safeValueOf(record.getValue(FINANCE_TRACKING.TYPE)))
				.setDescription(record.getValue(FINANCE_TRACKING.DESCRIPTION))
				.setAmount(record.getValue(FINANCE_TRACKING.AMOUNT))
				.setRecorded(AonEnumUtils.getBoolean( record.getValue(FINANCE_TRACKING.RECORDED)))
				.setAccountEntry(record.getValue(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY))
				
				.setCreationUser(record.getValue(FINANCE_TRACKING.CREATION_USER))
				.setCreationDate(record.getValue(FINANCE_TRACKING.CREATION_DATE))
				.setModificationUser(record.getValue(FINANCE_TRACKING.MODIFICATION_USER))
				.setModificationDate(record.getValue(FINANCE_TRACKING.MODIFICATION_DATE))
				;
		}
	}
}


