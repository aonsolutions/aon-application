package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.SelectWhereStep;

import com.esferalia.aon.jooq.tables.records.FinanceTrackingRecord;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.Finance;
import net.aonsolutions.occam.api.model.FinanceTracking;
import net.aonsolutions.occam.api.model.PayMethodTypeDetail;
import net.aonsolutions.occam.api.model.type.FinanceStatus;
import net.aonsolutions.occam.api.model.type.FinanceTrackingType;
import net.aonsolutions.occam.api.model.type.PayMethodType;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.AccountHandler.AccountFiller;
import net.aonsolutions.occam.impl.handler.FinanceHandler.FinanceFiller;
import net.aonsolutions.occam.impl.handler.RegistryBankHandler.RegistryBankFiller;

class FinanceTrackingHandler {

	private static final com.esferalia.aon.jooq.tables.Account RBANK_ACCOUNT = ACCOUNT.as("rbAcc");
	private static final com.esferalia.aon.jooq.tables.Account PM_TYPE_DETAIL_ACCOUNT = ACCOUNT.as("pmAcc");
	
	private FinanceTrackingHandler() {
		
	}

	// -------------------------------------------------------------
	// ------------------------ LECTURA ----------------------------
	// -------------------------------------------------------------
	static LinkedList<FinanceTracking> getFinanceTrackings(AONContext ctx, Integer financeId) {
		return getFinanceTrackingSelect( ctx )
			.where(FINANCE_TRACKING.FINANCE.eq(financeId))
			.orderBy(FINANCE_TRACKING.ID)
			.fetch()
			.stream()
			.map( new FinanceTrackingFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	static FinanceTracking getFinanceTracking(AONContext ctx, Integer trackingId) {
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

	static boolean isLastTracking(AONContext ctx, FinanceTracking ft) {
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

	static FinanceTracking getLastTracking(AONContext ctx, Integer financeId) {
		LinkedList<FinanceTracking> trackings = getFinanceTrackings(ctx, financeId);
		if ( trackings != null && !trackings.isEmpty()) {
			return trackings.getLast();	
		}
		return null;
	}

	// -------------------------------------------------------------
	// ------------------------ ESCRITURA --------------------------
	// -------------------------------------------------------------
	static Integer insert(AONContext ctx, FinanceTracking ft) {
		ctx.checkWrite();
		FinanceTrackingRecord r = ctx.getDslContext()
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
		ctx.log().debug("INSERT FINANCE_TRACKING id: " + r.getValue(FINANCE_TRACKING.ID) + " finance: " + ft.getFinance().getId());
		return r.getValue(FINANCE_TRACKING.ID); 
	}
	
	private static void updateFinanceStatus(AONContext ctx,Integer financeId,FinanceStatus financeStatus) {
		int i = ctx.getDslContext().update(FINANCE)
			.set(FINANCE.STATUS,financeStatus.value())
			.set(FINANCE.MODIFICATION_USER,ctx.getUser())
			.set(FINANCE.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FINANCE.ID.equal( financeId))
			.execute();
		ctx.log().debug("UPDATE FINANCE  ("+i+") id: " + financeId + " status: " + financeStatus.getDescription());
	}

//	static void delete(AONContext ctx, FinanceTracking tracking) {
//		if (!isLastTracking(ctx,tracking)) {
//			throw new AonCoreException(AonError.FINANCE_TRACKING_LATER_TRACKINGS.getMessage());
//		}
//		
//		
//		List<FiscalModel> models = AlcatrazHandler.isTrackingDeclared(ctx, tracking.getId() );
//		if (models != null && !models.isEmpty()) {
//			throw new AonCoreException(AonError.TRACKING_CANT_DELETE_MODEL.format(
//				models
//					.stream()
//					.map( fm -> MessageFormat.format("[Mod. {0}] ",fm.getModelFullName()))
//					.collect(StringBuilder::new, StringBuilder::append , StringBuilder::append )
//					.toString()
//					));
//		}
//		
//		
//		if (tracking.getBankStatementLink() == null ) {
//			if (tracking.isRecorded()) {
//				if (SecurityDAO.getUser(ctx).hasAccountingRole()) {
//					deleteAccountEntryFinanceTracking(ctx,tracking);
//				} else {
//					throw new AonCoreException(AonError.FINANCE_TRACKING_RECORDED.getMessage());		
//				}
//			}
//			int i = ctx.getDslContext()
//				.delete(FINANCE_TRACKING)
//				.where(FINANCE_TRACKING.ID.equal(tracking.getId()))
//				.execute();
//			ctx.log().debug("DELETE FINANCE_TRACKING  ("+i+") id: " + tracking.getId());
//			FinanceTracking previuosTracking = getLastTracking(ctx, tracking.getFinance().getId());
//			FinanceStatus newStatus = previuosTracking == null ? FinanceStatus.PENDING : previuosTracking.getType().getFinanceStatus(); 
//			updateFinanceStatus(ctx, tracking.getFinance().getId(), newStatus );
//		} else {
//			// TODO El movimiento viene de extracto bancario.
//		}
//	}
	
//	private static void deleteAccountEntryFinanceTracking(AONContext ctx, FinanceTracking ft) {
//		int i = ctx.getDslContext()
//			.delete(ACCOUNT_ENTRY_FINANCE_TRACKING)
//			.where(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING.equal(ft.getId()))
//			.execute();
//		ctx.log().debug("DELETE ACCOUNT_ENTRY_FINANCE_TRACKING ("+i+") Tracking: " + ft.getId());
//	}
	
	
	// -------------------------------------------------------------
	// ----------------- SALDAR UN VENCIMIENTO ---------------------
	// -------------------------------------------------------------
	
//	static Integer settle(AONContext ctx, Integer financeId) {
//		ctx.log().debug(" ----- START FINANCE SETTLE ----- ");
//		try {
//			ctx.checkWrite();
//			Finance finance = FinanceValidation.validateSettleTracking(ctx, financeId);
//			FinanceTracking ft = new FinanceTracking()
//					.setFinance(finance)
//					.setDomain(ctx.getDomainId())
//					.setTrackingDate(new Date())
//					.setType(FinanceTrackingType.SETTLED)
//					.setDescription(FinanceTrackingType.SETTLED.getDescription())
//					.setRegistryBank(null)
//					.setPayMethodTypeDetail(null)
//					.setBankStatementLink(null)
//					.setAmount(finance.getAmount())
//					.setRecorded(false);
//			updateFinanceStatus(ctx,financeId,FinanceStatus.SETTLED);
//			return insert(ctx, ft);
//		} catch (Throwable t) {
//			ctx.log().debug(" ----- [ERROR] " + t.getMessage());
//			throw t;
//		} finally {
//			ctx.log().debug(" ----- END FINANCE SETTLE ----- ");
//		}
//	}
	
	// -------------------------------------------------------------
	// ------------ ELIMINAR MOV SALDADO UN VENCIMIENTO ------------
	// -------------------------------------------------------------
	
//	static void unSettle(AONContext ctx, Integer financeId) {
//		ctx.log().debug(" ----- START FINANCE UNSETTLE ----- ");
//		try {
//			ctx.checkWrite();
//			Finance finance = FinanceValidation.validateUnSettleTracking(ctx, financeId);
//			ctx.getDslContext().delete(FINANCE_TRACKING)
//				.where(FINANCE_TRACKING.DOMAIN.eq(ctx.getDomainId()))
//				.and(FINANCE_TRACKING.FINANCE.eq(finance.getId()))
//				.and(FINANCE_TRACKING.TYPE.eq((byte)FinanceTrackingType.SETTLED.ordinal()))
//				.execute();
//			updateFinanceStatus(ctx,financeId,FinanceStatus.PENDING);
//		} catch (Throwable t) {
//			ctx.log().debug(" ----- [ERROR] " + t.getMessage());
//			throw t;
//		} finally {
//			ctx.log().debug(" ----- END FINANCE UNSETTLE ----- ");
//		}
//	}

	// -------------------------------------------------------------
	// ----------------- DESHACER UN VENCIMIENTO -------------------
	// -------------------------------------------------------------
//	static void undo(AONContext ctx, Integer financeId) {
//		ctx.log().debug(" ----- START FINANCE UNDO ----- ");
//		try {
//			ctx.checkWrite();
//			FinanceTracking financeTracking = FinanceValidation.validateUndoTracking(ctx, financeId);
//			if (financeTracking == null) {
//				// No debe suceder. Si el vencimiento no esta pendiente y no 
//				// tiene movimientos se marca como pendiente.
//				Finance finance = FinanceDAO.getFinance(ctx, financeId);
//				if (!finance.isPending()) {
//					updateFinanceStatus(ctx, finance.getId(), FinanceStatus.PENDING ); 
//				}
//			} else {
//				Integer accountEntryId = ctx.getDslContext()
//						.select(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY)
//						.from(ACCOUNT_ENTRY_FINANCE_TRACKING)
//						.where(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING.eq(financeTracking.getId()))
//						.fetch()
//						.stream()
//						.map( rec -> rec.getValue(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY))
//						.findFirst()
//						.orElse(null);
//				if (accountEntryId != null) {
//					FinanceEntry fe = FinanceEntryHandler.getFinanceEntry(ctx, accountEntryId);
//					if (fe.getTrackings().size() == 1) {
//						AccountEntryHandler.delete(ctx, accountEntryId);
//					} else {
//						fe.getTrackings().get(financeId).setDeleted(true);
//						FinanceEntryHandler.update(ctx, fe);
//					}
//				} else {
//					FinanceTrackingHandler.delete(ctx, financeTracking);
//				}
//			}
//		} catch (Throwable t) {
//			ctx.log().debug(" ----- [ERROR] " + t.getMessage());
//			throw t;
//		} finally {
//			ctx.log().debug(" ----- END FINANCE UNDO ----- ");
//		}
//	}
	
	// -------------------------------------------------------------
	// ----------------- PAGO DEL VENCIMIENTO ----------------------
	// -------------------------------------------------------------
	static FinanceTracking pay(AONContext ctx, int domain, FinanceTracking tracking) {
		ctx.log().debug(" ----- START FINANCE PAY ----- ");
		try {
			ctx.checkWrite();
			Finance finance = tracking.getFinance();
			FinanceValidation.validatePay(ctx, domain, finance);
			AccountEntry entry = null;
			if (tracking.getPayAccount() != null && tracking.getPayAccount().getId() != null) {
				ctx.log().debug(" \t (pay + accounting) ----- ");
				tracking.setDescription(AonStringUtils.abbreviate( 
					tracking.getPayAccount().getFullName(),FINANCE_TRACKING.DESCRIPTION.getDataType().length()));
				AccountEntry[] entries = FinanceEntryHandler.getPayFinanceEntry(ctx, domain, tracking);
				if (entries != null && entries.length == 1) {
					entry = entries[0];	
					Integer entryId = AccountEntryHandler.save(ctx, domain, entry);
					entry.setId(entryId);
					pay(ctx, domain, tracking, entry);
				}
				return tracking.setFinance(FinanceHandler.get(ctx, domain, tracking.getFinance().getId())
						.orElseThrow(() -> new AonCoreException(AonError.FINANCE_NOT_FOUND.getMessage()) ));
			} else {
				tracking.setDomain(finance.getDomain())
				  .setRecorded(false);
				ctx.log().debug(" \t (only pay) ----- ");
				Integer trackingId = doPay(ctx, tracking);		
				return getFinanceTracking(ctx, trackingId);
			}
		} catch (Throwable t) {
			ctx.log().debug(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().debug(" ----- END FINANCE PAY ----- ");
		}
	}
	
	private static Integer doPay(AONContext ctx,FinanceTracking tracking) {
		FinanceValidation.validatePay(ctx, tracking);
		updateFinanceStatus(ctx,tracking.getFinance().getId(),FinanceStatus.PAID);
		tracking.setType(FinanceTrackingType.PAID);
		return insert(ctx, tracking);
	}
	
	static void pay(AONContext ctx,int domain, FinanceTracking tracking,AccountEntry entry) {
		tracking.setDomain(domain)
		  .setTrackingDate(entry.getEntryDate())
		  .setRecorded(true)
		;
		Integer trackingId = doPay(ctx, tracking); 
		ctx.getDslContext()
			.insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,domain)
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY,entry.getId())
			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING,trackingId)
			.execute();
		ctx.log().debug("ACCOUNT_ENTRY_FINANCE_TRACKING account_entry: " + entry.getId() + " tracking: " + trackingId);
	}
	
	// -------------------------------------------------------------
	// ----------------- DEVOLUCION DEL VENCIMIENTO ----------------
	// -------------------------------------------------------------
//	static FinanceTracking returnFinance(AONContext ctx, FinanceTracking tracking) {
//		ctx.log().debug(" ----- START FINANCE RETURN ----- ");
//		try {
//			ctx.checkWrite();
//			FinanceValidation.validateReturn(ctx, tracking);
//			AccountEntry entry = null;
//			if (tracking.getPayAccount() != null && tracking.getPayAccount().getId() != null) {
//				ctx.log().debug(" \t (pay + accounting) ----- ");
//				tracking.setDescription(AonStringUtils.abbreviate( 
//						tracking.getPayAccount().getFullName(),FINANCE_TRACKING.DESCRIPTION.getDataType().length()));
//				AccountEntry[] entries = FinanceEntryHandler.getReturnFinanceEntry(ctx, tracking);
//				if (entries != null && entries.length == 1) {
//					entry = entries[0];	
//					Integer entryId = AccountEntryHandler.save(ctx, entry);
//					entry.setId(entryId);
//					tracking = returnFinance(ctx,tracking, entry);
//				}
//				return tracking.setFinance(FinanceDAO.getFinance(ctx, tracking.getFinance().getId()));
//			} else {
//				ctx.log().debug(" \t (only pay) ----- ");
//				Finance fin = FinanceDAO.getFinance(ctx, tracking.getFinance().getId());
//				tracking.setDomain(fin.getDomain())
//					.setRecorded(false);
//				Integer trackingId = _returnFinance(ctx, tracking);		
//				return getFinanceTracking(ctx, trackingId);
//			}
//		} catch (Throwable t) {
//			ctx.log().debug(" ----- [ERROR] " + t.getMessage());
//			throw t;
//		} finally {
//			ctx.log().debug(" ----- END FINANCE RETURN ----- ");
//		}
//	}
//	
//	private static Integer _returnFinance(AONContext ctx,FinanceTracking ft) {
//		updateFinanceStatus(ctx,ft.getFinance().getId(),FinanceStatus.RETURNED);
//		ft.setType(FinanceTrackingType.RETURNED);
//		Integer trackingId = insert(ctx, ft);
//		return trackingId;
//	}
//	
//	private static FinanceTracking returnFinance(AONContext ctx,FinanceTracking ft,AccountEntry entry) {
//		ft.setTrackingDate(entry.getEntryDate())
//			.setType(FinanceTrackingType.RETURNED)
//			.setRecorded(true)
//		;
//		updateFinanceStatus(ctx,ft.getFinance().getId(),FinanceStatus.RETURNED);
//		Integer trackingId = insert(ctx, ft);
//		ctx.getDslContext()
//			.insertInto(ACCOUNT_ENTRY_FINANCE_TRACKING)
//			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN,ctx.getDomainId())
//			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY,entry.getId())
//			.set(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING,trackingId)
//			.execute();
//		ctx.log().debug("ACCOUNT_ENTRY_FINANCE_TRACKING account_entry: " + entry.getId() + " tracking: " + trackingId);
//		return getFinanceTracking(ctx, trackingId);
//	}
	
	// --------------------------------------------------------------------------
	// ---- GRABACIÓN DEL FRACCIONAMIENTO DE UN VENCIMIENTO  UN VENCIMIENTO -----
	// --------------------------------------------------------------------------
	
//	static Integer fraction(AONContext ctx, Finance finance, String description,double originalAmount) {
//		ctx.log().debug(" ----- START FINANCE FRACTION ----- ");
//		try {
//			ctx.checkWrite();
//			FinanceTracking ft = new FinanceTracking()
//					.setFinance(finance)
//					.setDomain(ctx.getDomainId())
//					.setTrackingDate(new Date())
//					.setType(FinanceTrackingType.FRACTIONED)
//					.setDescription(description)
//					.setRegistryBank(null)
//					.setPayMethodTypeDetail(null)
//					.setBankStatementLink(null)
//					.setAmount(originalAmount)
//					.setRecorded(false);
//			return insert(ctx, ft);
//		} catch (Throwable t) {
//			ctx.log().debug(" ----- [ERROR] " + t.getMessage());
//			throw t;
//		} finally {
//			ctx.log().debug(" ----- END FINANCE SETTLE ----- ");
//		}
//	}

	// -------------------------------------------------------------
	// ---------------------------- MAP ----------------------------
	// -------------------------------------------------------------
	static class PayMethodTypeDetailFiller extends Filler<PayMethodTypeDetail> {
		@Override
		public PayMethodTypeDetail apply(Record r) {
			return build(r);
		}
		
		static PayMethodTypeDetail build(Record r) {
			if (isNull(r, PM_TYPE_DETAIL.ID)) return null;
			return new PayMethodTypeDetail()
				.setId(getValue(r, PM_TYPE_DETAIL.ID))
				.setDomain(getValue(r, PM_TYPE_DETAIL.DOMAIN))
				.setDescription(getValue(r, PM_TYPE_DETAIL.DESCRIPTION))
				.setType(PayMethodType.value( getValue(r, PM_TYPE_DETAIL.TYPE)).orElse(null))
				.setAccount(AccountFiller.build( r, PM_TYPE_DETAIL_ACCOUNT ))
			;
		}
		
	}
	
	static class FinanceTrackingFiller extends Filler<FinanceTracking> {
		@Override
		public FinanceTracking apply(Record r) {
			return build(r);
		}
		
		static FinanceTracking build(Record r) {
			if (isNull(r, FINANCE_TRACKING.ID)) return null;
			return new FinanceTracking()
				.setId(getValue(r, FINANCE_TRACKING.ID))
				.setDomain(getValue(r, FINANCE_TRACKING.DOMAIN))
				.setRegistryBank( RegistryBankFiller.build(r) )
				.setFinance(FinanceFiller.build(r))
				.setPayMethodTypeDetail( PayMethodTypeDetailFiller.build(r))
				.setBankStatementLink(getValue(r, FINANCE_TRACKING.BANK_STATEMENT_LINK))
				.setTrackingDate(getValue(r, FINANCE_TRACKING.TRACKING_DATE))
				.setType( FinanceTrackingType.value(getValue(r, FINANCE_TRACKING.TYPE)).orElse(null))
				.setDescription(getValue(r, FINANCE_TRACKING.DESCRIPTION))
				.setAmount(getValue(r, FINANCE_TRACKING.AMOUNT))
				.setRecorded(AonEnumUtils.getBoolean( getValue(r, FINANCE_TRACKING.RECORDED)))
				.setAccountEntry(getValue(r, ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY))
				.setCreationUser(getValue(r, FINANCE_TRACKING.CREATION_USER))
				.setCreationDate(getValue(r, FINANCE_TRACKING.CREATION_DATE))
				.setModificationUser(getValue(r, FINANCE_TRACKING.MODIFICATION_USER))
				.setModificationDate(getValue(r, FINANCE_TRACKING.MODIFICATION_DATE))
				;
		}
	}
}


