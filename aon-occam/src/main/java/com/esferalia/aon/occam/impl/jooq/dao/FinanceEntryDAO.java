package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.AccountEntryFbatch.ACCOUNT_ENTRY_FBATCH;
import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceFilter;
import com.esferalia.aon.occam.api.model.finance.FinanceRecorder;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO.FinanceOrder;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO.FullFinanceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceTrackingDAO.FullFinanceTrackingFiller;
import com.esferalia.aon.occam.impl.jooq.validation.FinanceValidation;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceEntryDAO {
	
	private FinanceEntryDAO() {
		
	}

	public static Stream<Finance> accountFetch(final AONContext ctx, FinanceFilter filter, int offset, int numberOfRows, FinanceOrder orderBy) {
		return FinanceDAO.fetch(ctx, filter, offset, numberOfRows,orderBy)
			.map(finance -> fillAcccount(ctx,finance));
//			.map(finance -> fillCustomerAcccount(ctx,finance))
//			.map(finance -> fillSupplierAcccount(ctx,finance))
//			.filter(finance -> 
//					finance.hasInvoice()						// Si viene de factura debe pasar
//																// o
//					|| (!finance.hasInvoice()					// Si no viene de factura, 
//					&& finance.isPayment()			 			// y es un pago 
//					&& finance.getRegistryAccountId() == null))	// y no hay cuenta (el paso anterior no ha rellenado la cuenta)
//			.map(finance -> fillCreditorAcccount(ctx,finance))
//		;
	}

	public static FinanceEntry getFinanceEntry(final AONContext ctx, Integer accountEntryId) {
		AccountEntry accountEntry = AccountEntryDAO.getAccountEntry(ctx, accountEntryId);
		if (accountEntry == null) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage());
		}
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
					entry.setBankAccount(AccountDAO.get(ctx, detail.getAccountId()));
					entry.setManualConcept(AonStringUtils.substringBetween(detail.getConcept(), AonStringUtils.OPEN_BRACKET, AonStringUtils.CLOSE_BRACKET));
				} else if (AonStringUtils.startsWith(detail.getAccountCode(), "6")) {
						entry.setExpensesAccount(AccountDAO.get(ctx, detail.getAccountId()));
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
			.map( new FullFinanceFiller() )
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
				.select(SCOPE.fields())
				.select(INVOICE.fields())
					.from(ACCOUNT_ENTRY_FINANCE_TRACKING)
					.join(FINANCE_TRACKING).on(FINANCE_TRACKING.ID.equal(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING))
					.join(FINANCE).on(FINANCE.ID.equal(FINANCE_TRACKING.FINANCE))
					.join(REGISTRY).on(FINANCE.REGISTRY.equal(REGISTRY.ID))
					.join(SCOPE).on(FINANCE.SCOPE.equal(SCOPE.ID))
					.leftOuterJoin(PAY_METHOD).on(FINANCE.PAY_METHOD.equal(PAY_METHOD.ID))
					.leftOuterJoin(INVOICE).on(FINANCE.INVOICE.equal(INVOICE.ID))
					.where(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY.eq(accountEntryId))
					.fetch()
					.stream()
				.map( new FullFinanceTrackingFiller() )
				.map(ft -> ft.setLastTracking(FinanceTrackingDAO.isLastTracking(ctx, ft)))
				.map(ft -> fillAcccount(ctx,ft))
//				.peek(ft -> fillCustomerAcccount(ctx,ft.getFinance()))
//				.peek(ft -> fillSupplierAcccount(ctx,ft.getFinance()))
//				.peek(ft -> fillCreditorAcccount(ctx,ft.getFinance()))
				.forEach( ft -> entry.getTrackings().put(ft.getFinance().getId(), ft));
		}
		return entry;
	}
	private static FinanceTracking fillAcccount(AONContext ctx,FinanceTracking ft) {
		fillAcccount(ctx, ft.getFinance());
		return ft;
	}
	private static Finance fillAcccount(AONContext ctx,Finance finance) {
		if (finance.hasRegistry()) {
			Account account = null;
			if (finance.isFromSalesInvoice()) {
				account = CustomerDAO.getCustomerAccount(ctx,finance.getRegistry().getId());	
			} else if (finance.isFromPurchaseInvoice()) {
				account = SupplierDAO.getSupplierAccount(ctx,finance.getRegistry().getId());	
			} else if (finance.isFromExpensesInvoice() || finance.isFromUndeductibleInvoice()) {
				account = CreditorDAO.getCreditorAccount(ctx,finance.getRegistry().getId());	
			}
			if (account == null ) {
				if (!finance.isPayment()) {
					account = CustomerDAO.getCustomerAccount(ctx,finance.getRegistry().getId());	
				} else {
					// Se asume acreedor para los pagos.
					// Los proveedores deberian tener factura.
					account = CreditorDAO.getCreditorAccount(ctx,finance.getRegistry().getId());
				}
			}
			fillRegistryAccountData(finance,account);
		}
		return finance;
	}

//	private static Finance fillCustomerAcccount(AONContext ctx,Finance finance) {
//		if (finance.getRegistry() != null	 
//			&& (finance.isFromSalesInvoice()					// Es factura de Venta 
//			|| (!finance.hasInvoice() && !finance.isPayment())) // Cobro sin factura
//			) {
//			Account account = CustomerDAO.getCustomerAccount(ctx,finance.getRegistry().getId());
//			fillRegistryAccountData(finance,account);
//		}
//		return finance;
//	}
//	private static Finance fillSupplierAcccount(AONContext ctx,Finance finance) {
//		if (finance.getRegistry() != null 
//			&& (finance.isFromPurchaseInvoice()					// Es factura de Compra 
//			|| (!finance.hasInvoice() && finance.isPayment()))  // Pago sin factura
//			) {
//			Account account = SupplierDAO.getSupplierAccount(ctx,finance.getRegistry().getId());
//			fillRegistryAccountData(finance,account);
//		}
//		return finance;
//	}
//	private static Finance fillCreditorAcccount(AONContext ctx,Finance finance) {
//		if (finance.getRegistry() != null && 
//			(finance.isFromExpensesInvoice()					// Es factura de Gastos 
//			|| finance.isFromUndeductibleInvoice() 				// Es factura de Gastos No Ded.
//			|| (!finance.hasInvoice() && finance.isPayment()))  // Pago sin factura 
//			) {
//			Account account = CreditorDAO.getCreditorAccount(ctx,finance.getRegistry().getId());
//			fillRegistryAccountData(finance,account);
//		}
//		return finance;
//	}
	
	private static void fillRegistryAccountData(Finance finance, Account account) {
		if (account == null) {
			finance.setRegistryAccountId(null);
			finance.setRegistryAccountCode(null);
			finance.setRegistryAccountDescription(null);
		} else {
			finance.setRegistryAccountId(account.getId());
			finance.setRegistryAccountCode(account.getCode());
			finance.setRegistryAccountDescription(account.getDescription());
		}
	}

	public static AccountEntry[] getReturnFinanceEntry(AONContext ctx, FinanceTracking tracking) {
		return getEntry(ctx, tracking,
				tracking.getFinance().isPayment()
					?AccountEntryType.RETURNED_PAYMENT
					:AccountEntryType.RETURNED_COLLECTION);	
	}

	public static AccountEntry[] getPayFinanceEntry(AONContext ctx, FinanceTracking tracking) {
		return getEntry(ctx, tracking, AccountEntryType.FINANCE);	
	}

	private static AccountEntry[] getEntry(AONContext ctx, FinanceTracking tracking, AccountEntryType entryType) {
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, tracking.getTrackingDate());
		if (period == null) {
			throw new AonCoreException( AonError.WRONG_PERIOD.format( tracking.getTrackingDate() ) );
		}
		Finance finance = tracking.getFinance();
		Integer act = Optional.ofNullable(finance.getInvoice())
			.flatMap( Invoice::optActivity )
			.map( ac -> ac.getId() )
			.orElse(null)
		;
		AccountEntry ae = new AccountEntry()
			.setDomain(ctx.getDomainId())
			.setPeriod(period.getId())
			.setEntryDate(tracking.getTrackingDate())
			.setEntryType(entryType)
			.setActivity(act)
			.setSecurityLevel(finance.getSecurityLevel());
		FinanceEntry financeEntry = new FinanceEntry();
		financeEntry.setAccountEntry(ae);
		financeEntry.setBankAccount( tracking.getPayAccount());
		if (finance.getRegistryAccountId() == null) {
			Invoice invoice = finance.getInvoice();
			if (invoice != null) {
				if (finance.getRegistry() == null || finance.getRegistry().getId() == null) {
					throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
				}
				if (invoice.getType() == null ) {
					throw new AonCoreException( AonError.INVOICE_EMPTY_TYPE.getMessage() );
				}
				invoice.getType().visit(invoice, new IInvoiceTypeVisitor<Void>() {
					private void fill( Account acc) {
						if (acc == null) {
							throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
						}
						finance.setRegistryAccountId(acc.getId());
						finance.setRegistryAccountCode(acc.getCode());
						finance.setRegistryAccountDescription(acc.getDescription());
					}
					
					@Override
					public Void visitSales(Invoice invoice) {
						fill( CustomerDAO.getCustomerAccount(ctx, finance.getRegistry().getId()));
						return null;
					}
					
					@Override
					public Void visitPurchase(Invoice invoice) {
						fill( SupplierDAO.getSupplierAccount(ctx, finance.getRegistry().getId()));
						return null;
					}
					
					@Override
					public Void visitExpenses(Invoice invoice) {
						fill( CreditorDAO.getCreditorAccount(ctx, finance.getRegistry().getId()));
						return null;
					}
					@Override
					public Void visitUndeductible(Invoice invoice) {
						visitExpenses(invoice);
						return null;
					}
					
				});
			} else {
				Registry registry = finance.getRegistry();
				if (registry == null) {
					throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
				}
				if (finance.isPayment()) {
					Account acc = SupplierDAO.getSupplierAccount(ctx, registry.getId());
					if (acc == null) {
						acc = CreditorDAO.getCreditorAccount(ctx, registry.getId());
					}
					if (acc == null) {
						throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
					}
					finance.setRegistryAccountId(acc.getId());
					finance.setRegistryAccountCode(acc.getCode());
					finance.setRegistryAccountDescription(acc.getDescription());
				} else {
					Account acc = CustomerDAO.getCustomerAccount(ctx, registry.getId());
					if (acc == null) {
						acc = CustomerDAO.ensureAccount(ctx, registry.getId());
					}
					if (acc == null) {
						throw new AonCoreException( AonError.FINANCE_TRACKING_NO_REGISTRY_ACCOUNT.getMessage() );	
					}
					finance.setRegistryAccountId(acc.getId());
					finance.setRegistryAccountCode(acc.getCode());
					finance.setRegistryAccountDescription(acc.getDescription());
				}
			}
		}
		financeEntry.add(finance);
		return FinanceRecorder.recordFinanceEntry(financeEntry);
	}

	public static FinanceEntry save(AONContext ctx, FinanceEntry financeEntry) {
		if (financeEntry.getBankAccount() == null || financeEntry.getBankAccount().getId() == null) {
			throw new AonCoreException("Debe indicar una cuenta contable como contrapartida");
		}
		if (AonCollectionUtils.isEmpty(financeEntry.getTrackings())) {
			throw new AonCoreException("Debe indicar algún vencmiento para grabar el apunte");
		}
		ctx.checkWrite();
		if (financeEntry.getAccountEntry().getId() == null) {
			return insert(ctx,financeEntry);
		} else {
			return update(ctx,financeEntry);
		}
	}

	public static FinanceEntry update(AONContext ctx, FinanceEntry financeEntry) {
		ctx.log().info(" ----- START FINANCE ENTRY UPDATE ----- ");
		try {
			if (!financeEntry.isMultipleGeneration()) {
				AccountEntry[] entries = FinanceRecorder.recordFinanceEntry(financeEntry);
				AccountEntry entry = entries[0];
				
				// Se borran (id negativo) las líneas del apunte original, se 
				// suman al nuevo apunte (que tiene el mismo id) De tal forma, 
				// la cabecera se modifica y las línea con id negativo se borran. 
				// Las línea sin id, se añaden.
				for (AccountEntryDetail original : financeEntry.getAccountEntry().getDetails()) {
					original.setId( original.getId() * -1);
					entry.addDetail(original);
				}
				Integer entryId = AccountEntryDAO.save(ctx, entry);
				entry.setId(entryId);
				for (FinanceTracking tracking : financeEntry.getTrackings().values()) {
					if (tracking.isDeleted()) {
						FinanceTrackingDAO.delete(ctx, tracking);
					} else {
						if (tracking.getAccountEntry() == null) {
							pay( ctx,financeEntry,entry,tracking);
						}
					}
				}
			}
			return financeEntry;
		} catch (Exception t) {
			ctx.log().info(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().info(" ----- END FINANCE ENTRY UPDATE ----- ");
		}
	}
	
	private static void pay(AONContext ctx, FinanceEntry financeEntry, AccountEntry entry, FinanceTracking tracking) {
		tracking.setAmount(tracking.getFinance().getAmount());
		if (financeEntry.getBankAccount() != null) {
			tracking
				.setPayAccount(financeEntry.getBankAccount())
				.setDescription(AonStringUtils.abbreviate( 
					tracking.getPayAccount().getFullName(),FINANCE_TRACKING.DESCRIPTION.getDataType().length()));
		}
		FinanceTrackingDAO.pay(ctx,tracking,entry);
	}

	private static FinanceEntry insert(AONContext ctx, FinanceEntry financeEntry) {
		ctx.log().info(" ----- START FINANCE ENTRY INSERT ----- ");
		try {
			if (!financeEntry.isMultipleGeneration()) {
				AccountEntry[] entries = FinanceRecorder.recordFinanceEntry(financeEntry);
				if (entries != null && entries.length == 1) {
					AccountEntry entry = entries[0];
					Integer entryId = AccountEntryDAO.save(ctx, entry);
					entry.setId(entryId);
					financeEntry.setAccountEntry(entry);
					for (FinanceTracking tracking : financeEntry.getTrackings().values()) {
						pay( ctx,financeEntry,entry,tracking);
					}
				}
			}
			return financeEntry;
		} catch (Exception t) {
			ctx.log().info(" ----- [ERROR] " + t.getMessage());
			throw t;
		} finally {
			ctx.log().info(" ----- END FINANCE ENTRY INSERT ----- ");
		}
	}
	
	public static void deleteAccountEntryFinanceTrackings(AONContext ctx, Integer accountEntryId) {
		ctx.checkWrite();
		FinanceEntry entry = getFinanceEntry(ctx, accountEntryId);
		FinanceValidation.validateDelete(ctx, entry);
		for (FinanceTracking ft : entry.getTrackings().values()) {
			if (ft.getAccountEntry() != null) {
				FinanceTrackingDAO.delete(ctx, ft);
			}
		}
	}

}


