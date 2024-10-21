package net.aonsolutions.occam.impl.handler;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryFbatch.ACCOUNT_ENTRY_FBATCH;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountPeriod;
import net.aonsolutions.occam.api.model.Filter.FinanceFilter;
import net.aonsolutions.occam.api.model.Finance;
import net.aonsolutions.occam.api.model.FinanceEntry;
import net.aonsolutions.occam.api.model.FinanceRecorder;
import net.aonsolutions.occam.api.model.FinanceTracking;
import net.aonsolutions.occam.api.model.InvoiceHeader;
import net.aonsolutions.occam.api.model.type.AccountEntryType;
import net.aonsolutions.occam.api.model.type.FinanceType;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.FinanceHandler.FinanceFiller;
import net.aonsolutions.occam.impl.handler.FinanceHandler.FinanceOrder;
import net.aonsolutions.occam.impl.handler.FinanceTrackingHandler.FinanceTrackingFiller;

class FinanceEntryHandler {
	
	private static final com.esferalia.aon.jooq.tables.Account RBANK_ACCOUNT = ACCOUNT.as("rbAcc");;
	private static final com.esferalia.aon.jooq.tables.Account PM_TYPE_DETAIL_ACCOUNT = ACCOUNT.as("pmAcc");
	
	private FinanceEntryHandler() {
		
	}

	static Stream<Finance> accountFetch(final AONContext ctx, int domain, FinanceFilter filter, int offset, int numberOfRows, FinanceOrder orderBy) {
		return FinanceHandler.stream(ctx, domain, filter, offset, numberOfRows,orderBy)
			.map(finance -> fillAcccount(ctx,finance));
	}

	static FinanceEntry getFinanceEntry(final AONContext ctx, int domain, Integer accountEntryId) {
		AccountEntry accountEntry = AccountEntryHandler.get(ctx, domain, accountEntryId)
			.orElseThrow(() -> new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage()));
		final FinanceEntry entry = new FinanceEntry();
		entry.setFinanceBatch(ctx.getDslContext()
				.select(ACCOUNT_ENTRY_FBATCH.FBATCH)
				.from(ACCOUNT_ENTRY_FBATCH)
				.where(ACCOUNT_ENTRY_FBATCH.ACCOUNT_ENTRY.eq(accountEntryId))
				.fetch()
				.stream()
				.findFirst()
				.map( rec -> rec.getValue(ACCOUNT_ENTRY_FBATCH.FBATCH))
				.orElse(null)
				);
		
		entry.setAccountEntry(accountEntry);
		accountEntry.getDetails()
			.stream()
			.forEach(detail -> {
				if (AonStringUtils.startsWith(detail.getAccountCode(), "5")) {
					entry.setBankAccount(detail.getAccount().orElse(null));
					entry.setManualConcept(AonStringUtils.substringBetween(detail.getConcept(), AonStringUtils.OPEN_BRACKET, AonStringUtils.CLOSE_BRACKET));
				} else if (AonStringUtils.startsWith(detail.getAccountCode(), "6")) {
					entry.setExpensesAccount(detail.getAccount().orElse(null));
					entry.setExpenses( AonMathUtils.round(detail.getDebit() - detail.getCredit() ));
				}
			});
		if (entry.isFromFinanceBatch()) {
			ctx.getDslContext()
			.select(FINANCE.fields())
			.select(REGISTRY.fields())
			.select(PAY_METHOD.fields())
			.select(SCOPE.fields())
			.select(INVOICE.fields())
				.from(FBATCH_DETAIL)
				.join(FINANCE).on(FINANCE.ID.equal(FBATCH_DETAIL.FINANCE))
				.join(REGISTRY).on(FINANCE.REGISTRY.equal(REGISTRY.ID))
				.join(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
				.where(FBATCH_DETAIL.FBATCH.eq(entry.getFinanceBatch()))
				.fetch()
				.stream()
			.map( new FinanceFiller() )
			.map(finance -> fillAcccount(ctx,finance))
//			.peek(finance -> fillCustomerAcccount(ctx,finance))
//			.peek(finance -> fillSupplierAcccount(ctx,finance))
//			.peek(finance -> fillCreditorAcccount(ctx,finance))
			.forEach( finance -> entry.getTrackings().put(finance.getId(), new FinanceTracking().setFinance(finance)));
		} else {
			ctx.getDslContext()
				.select(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY)
				.select(FINANCE_TRACKING.fields())
				.select(FINANCE.fields())
				.select(REGISTRY.fields())
				.select(PAY_METHOD.fields())
				.select(INVOICE.fields())
				.select(RBANK.fields())
				.select(RBANK_ACCOUNT.fields())
				.select(PM_TYPE_DETAIL.fields())
				.select(PM_TYPE_DETAIL_ACCOUNT.fields())
				.from(ACCOUNT_ENTRY_FINANCE_TRACKING)
				.join(FINANCE_TRACKING).on(FINANCE_TRACKING.ID.equal(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING))
				.join(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
				.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
				.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
				.leftOuterJoin(RBANK).on(FINANCE_TRACKING.RBANK.equal(RBANK.ID))
				.leftOuterJoin(RBANK_ACCOUNT).on(RBANK.ACCOUNT.equal(RBANK_ACCOUNT.ID))
				.leftOuterJoin(PM_TYPE_DETAIL).on(FINANCE_TRACKING.PM_TYPE_DETAIL.equal(PM_TYPE_DETAIL.ID))
				.leftOuterJoin(PM_TYPE_DETAIL_ACCOUNT).on(PM_TYPE_DETAIL.ACCOUNT.equal(PM_TYPE_DETAIL_ACCOUNT.ID))
				.where(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY.eq(accountEntryId))
				.fetch()
				.stream()
				.map( new FinanceTrackingFiller() )
				.map(ft -> ft.setLastTracking(FinanceTrackingHandler.isLastTracking(ctx, ft)))
				.map(ft -> fillAcccount(ctx,ft))
				.forEach( ft -> entry.getTrackings().put(ft.getFinance().getId(), ft));
		}
		return entry;
	}
	private static FinanceTracking fillAcccount(AONContext ctx,FinanceTracking ft) {
		fillAcccount(ctx, ft.getFinance());
		return ft;
	}
	
	private static class RegistryAccountVisitor implements InvoiceTypeVisitor<Optional<Account>> {
		private final AONContext ctx;
		private final Finance finance; 
		
		private RegistryAccountVisitor(AONContext ctx,Finance finance) {
			this.ctx = ctx;
			this.finance = finance; 
		}
		
		@Override
		public Optional<Account> visitPurchase() {
			return SupplierHandler.get(ctx, finance.getDomain(), finance.getRegistry()).flatMap( c -> c.getAccount());
		}

		@Override
		public Optional<Account> visitSales() {
			return CustomerHandler.get(ctx, finance.getDomain(), finance.getRegistry()).flatMap( c -> c.getAccount());
		}

		@Override
		public Optional<Account> visitExpenses() {
			return CreditorHandler.get(ctx, finance.getDomain(), finance.getRegistry()).flatMap( c -> c.getAccount());
		}

		@Override public Optional<Account> visitUndeductible() { return visitExpenses(); }
	}
	
	private static Finance fillAcccount(AONContext ctx,Finance finance) {
		if (finance.getRegistry() != null) {
			finance.getInvoice().ifPresent(i -> {
				Optional<Account> account = i.getType().visit( new RegistryAccountVisitor(ctx,finance));
				if (account.isEmpty()) {
					if (finance.getFinanceType() == FinanceType.PAYMENT) {
						// Se asume acreedor para los pagos.
						// Los proveedores deberian tener factura.
						account = CreditorHandler.get(ctx, finance.getDomain(), finance.getRegistry()).flatMap( c -> c.getAccount());
					} else {
						account = CustomerHandler.get(ctx, finance.getDomain(), finance.getRegistry()).flatMap( c -> c.getAccount());	
					}
				}
				finance.setRegistryAccount( account.orElse(null));
			});
		}
		return finance;
	}

	static AccountEntry[] getReturnFinanceEntry(AONContext ctx, int domain, FinanceTracking tracking) {
		return getEntry(ctx, domain, tracking,
			tracking.getFinance().getFinanceType() == FinanceType.PAYMENT
				?AccountEntryType.RETURNED_PAYMENT
				:AccountEntryType.RETURNED_COLLECTION);	
	}

	static AccountEntry[] getPayFinanceEntry(AONContext ctx, int domain, FinanceTracking tracking) {
		return getEntry(ctx, domain, tracking, AccountEntryType.FINANCE);	
	}

	private static AccountEntry[] getEntry(AONContext ctx, int domain, FinanceTracking tracking, AccountEntryType entryType) {
		AccountPeriod period = AccountPeriodHandler.get(ctx, domain, tracking.getTrackingDate())
			.orElseThrow(() -> new AonCoreException( AonError.WRONG_PERIOD.format( tracking.getTrackingDate() ) ));

		Finance finance = tracking.getFinance();
		AccountEntry ae = new AccountEntry()
			.setDomain(domain)
			.setPeriod( period )
			.setEntryDate(tracking.getTrackingDate())
			.setEntryType(entryType)
			.setActivity(finance.getInvoice().flatMap( i -> i.getActivity()).orElse(null))
			.setConfidential(finance.isConfidential());
		FinanceEntry financeEntry = new FinanceEntry();
		financeEntry.setAccountEntry(ae);
		financeEntry.setBankAccount( tracking.getPayAccount());
		if (finance.getRegistryAccount().isEmpty()) {
			InvoiceHeader invoice = finance.getInvoice().orElse(null);
			if (invoice == null || finance.getRegistry() == null) {
				throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
			}
			if (invoice.getType() == null ) {
				throw new AonCoreException( AonError.INVOICE_EMPTY_TYPE.getMessage() );
			}
			invoice.getType().visit( new RegistryAccountVisitor(ctx,finance))
				.ifPresentOrElse( finance::setRegistryAccount
					, () -> new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() ));
		}
		financeEntry.add(finance);
		return FinanceRecorder.recordFinanceEntry(financeEntry);
	}

//	public static FinanceEntry save(AONContext ctx, FinanceEntry financeEntry) {
//		if (financeEntry.getBankAccount() == null || financeEntry.getBankAccount().getId() == null) {
//			throw new AonCoreException("Debe indicar una cuenta contable como contrapartida");
//		}
//		if (AonCollectionUtils.isEmpty(financeEntry.getTrackings())) {
//			throw new AonCoreException("Debe indicar algún vencmiento para grabar el apunte");
//		}
//		ctx.checkWrite();
//		if (financeEntry.getAccountEntry().getId() == null) {
//			return insert(ctx,financeEntry);
//		} else {
//			return update(ctx,financeEntry);
//		}
//	}

//	public static FinanceEntry update(AONContext ctx, FinanceEntry financeEntry) {
//		ctx.log().info(" ----- START FINANCE ENTRY UPDATE ----- ");
//		try {
//			if (!financeEntry.isMultipleGeneration()) {
//				AccountEntry[] entries = FinanceRecorder.recordFinanceEntry(financeEntry);
//				AccountEntry entry = entries[0];
//				
//				// Se borran (id negativo) las líneas del apunte original, se 
//				// suman al nuevo apunte (que tiene el mismo id) De tal forma, 
//				// la cabecera se modifica y las línea con id negativo se borran. 
//				// Las línea sin id, se añaden.
//				for (AccountEntryDetail original : financeEntry.getAccountEntry().getDetails()) {
//					original.setId( original.getId() * -1);
//					entry.addDetail(original);
//				}
//				Integer entryId = AccountEntryHandler.save(ctx, entry);
//				entry.setId(entryId);
//				for (FinanceTracking tracking : financeEntry.getTrackings().values()) {
//					if (tracking.isDeleted()) {
//						FinanceTrackingHandler.delete(ctx, tracking);
//					} else {
//						if (tracking.getAccountEntry() == null) {
//							pay( ctx,financeEntry,entry,tracking);
//						}
//					}
//				}
//			}
//			return financeEntry;
//		} catch (Exception t) {
//			ctx.log().info(" ----- [ERROR] " + t.getMessage());
//			throw t;
//		} finally {
//			ctx.log().info(" ----- END FINANCE ENTRY UPDATE ----- ");
//		}
//	}
//	
//	private static void pay(AONContext ctx, FinanceEntry financeEntry, AccountEntry entry, FinanceTracking tracking) {
//		tracking.setAmount(tracking.getFinance().getAmount());
//		if (financeEntry.getBankAccount() != null) {
//			tracking
//				.setPayAccount(financeEntry.getBankAccount())
//				.setDescription(AonStringUtils.abbreviate( 
//					tracking.getPayAccount().getFullName(),FINANCE_TRACKING.DESCRIPTION.getDataType().length()));
//		}
//		FinanceTrackingDAO.pay(ctx,tracking,entry);
//	}
//
//	private static FinanceEntry insert(AONContext ctx, FinanceEntry financeEntry) {
//		ctx.log().info(" ----- START FINANCE ENTRY INSERT ----- ");
//		try {
//			if (!financeEntry.isMultipleGeneration()) {
//				AccountEntry[] entries = FinanceRecorder.recordFinanceEntry(financeEntry);
//				if (entries != null && entries.length == 1) {
//					AccountEntry entry = entries[0];
//					Integer entryId = AccountEntryHandler.save(ctx, entry);
//					entry.setId(entryId);
//					financeEntry.setAccountEntry(entry);
//					for (FinanceTracking tracking : financeEntry.getTrackings().values()) {
//						pay( ctx,financeEntry,entry,tracking);
//					}
//				}
//			}
//			return financeEntry;
//		} catch (Exception t) {
//			ctx.log().info(" ----- [ERROR] " + t.getMessage());
//			throw t;
//		} finally {
//			ctx.log().info(" ----- END FINANCE ENTRY INSERT ----- ");
//		}
//	}
//	
//	public static void deleteAccountEntryFinanceTrackings(AONContext ctx, Integer accountEntryId) {
//		ctx.checkWrite();
//		FinanceEntry entry = getFinanceEntry(ctx, accountEntryId);
//		FinanceValidation.validateDelete(ctx, entry);
//		for (FinanceTracking ft : entry.getTrackings().values()) {
//			if (ft.getAccountEntry() != null) {
//				FinanceTrackingDAO.delete(ctx, ft);
//			}
//		}
//	}

}


