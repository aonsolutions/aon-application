package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jooq.Field;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class InvoiceRecorderDAO {
	
	private static final String DEF_SALES_ACCOUNT = "700000000";				// Ventas de mercaderias.
	private static final String DEF_PURCHASE_ACCOUNT = "600000000";				// Compras de mercaderias. 
	private static final String DEF_OUTPUT_VAT_ACCOUNT = "477000000";			// H.P. IVA REPERCUTIDO
	private static final String DEF_INPUT_VAT_ACCOUNT = "472000000";			// H.P. IVA SOPORTADO
	private static final String DEF_SALES_RET_ACCOUNT = "473000000";			// H.P. RETENCIONES Y PAGOS A CUENTA
	private static final String DEF_NOT_SALES_RET_ACCOUNT = "475100000";		// H.P. ACREEDORA POR RETENCIONES PRACTICADAS
	private static final String DEF_VAT_NEGATIVE_ADJUST_ACCOUNT = "634100000"; 	// AJUSTES NEGATIVOS DE I.V.A. DE ACTIVO CORRIENTE
	private static final String DEF_PREPAYMENT_ACCOUNT = "555900000";			// SUPLIDOS Y PAGOS A CUENTA
	
	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";
	
	private InvoiceRecorderDAO() {
	}

	// ********************************************************
	// ******************************************* [PUBLIC] ***
	// ********************************************************

	public static AccountEntry recordInvoice(AONContext ctx, Integer invoiceId) {
		return getInvoiceEntry(ctx, InvoiceDAO.getFullInvoice(ctx, invoiceId), false);
	}
	public static AccountEntry simulate(AONContext ctx, Integer invoiceId) {
		return getInvoiceEntry(ctx, InvoiceDAO.getFullInvoice(ctx, invoiceId), true);
	}
	
	public static AccountEntry getInvoiceEntry(AONContext ctx, Invoice invoice, boolean dryRun) {
		Context c = new Context(ctx, invoice, dryRun);
		AccountEntry ae = fillAccountEntry( c );
		REGISTRY
			.andThen(INPUT_VAT)
			.andThen(OUTPUT_VAT)
			.andThen(VAT_NEGATIVE_ADJUST)
			.andThen(SALES_WITHHOLDING)
			.andThen(NOT_SALES_WITHHOLDING)
//			.andThen(DIRECT_TAX_ADJUST)
			.andThen(SALES_EXP_ACCOUNT)
			.andThen(PREPAYMENT_ACCOUNT)
			.andThen(PURCHASE_EXP_ACCOUNT)
			.andThen(EXPENSES_EXP_ACCOUNT)
			.andThen(REGISTRY_BALANCING_ACCOUNT)
		.accept(c);
		ae.setDetails(new LinkedList<>());
		ae.getDetails().addAll(c.map.values());
		String concept = obtainConcept(invoice);
		ae.getDetails().stream()
			.forEach( d ->
				d.setConcept(concept)
				 .setDocumentNumber(invoice.getDocumentNumber()));
		return ae;
	}
	
	// *********************************************************
	// ******************************************* [PRIVATE] ***
	// *********************************************************

	private static AccountEntry fillAccountEntry(Context c) {
		if (c.invoice == null) throw new AonCoreException("La factura no puede ser NULL");
		if (c.invoice.getDomain() == null || AonNumberUtils.equals(c.invoice.getDomain(), 0)) {
			throw new AonCoreException("La factura debe tener dominio");
		}
		if (c.invoice.getType() == null) throw new AonCoreException("La factura debe tener tipo");
		if (c.invoice.getIssueDate() == null) throw new AonCoreException("La factura debe tener fecha");
		if (c.invoice.getRegistry() == null) throw new AonCoreException("La factura debe tener titular");
		AccountPeriod period = AccountPeriodDAO.ensurePeriod(c.ctx, c.invoice.getDomain(), c.invoice.getIssueDate());
		AccountEntry accountEntry = new AccountEntry()
			.setPeriod(period.getId())
			.setDomain(c.invoice.getDomain())
			.setConfidential(false)
			.setEntryDate(c.invoice.getIssueDate())
			.setActivity(Optional.ofNullable(c.config.getMainActivity()).map(a -> a.getId()).orElse(null))
			.setComments(c.invoice.getComments())
			.setDirty(false);
		return c.invoice.getType().visit(c.invoice,  new IInvoiceTypeVisitor<AccountEntry>() {
			@Override public AccountEntry visitUndeductible(Invoice i) {return accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);}
			@Override public AccountEntry visitSales(Invoice i) {return accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);}
			@Override public AccountEntry visitPurchase(Invoice i) {return accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);}
			@Override public AccountEntry visitExpenses(Invoice i) {return accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);}
		});
	}
	
	private static String obtainConcept(Invoice invoice) {
		StringBuilder buf = new StringBuilder();
		buf.append( invoice.isSales()? N_FRA : S_FRA );
		if (invoice.getTotal() < 0) {
			buf.append( " " + ABONO );
		}
		buf.append(": ");
		String refCode = AonStringUtils.defaultIfBlank(invoice.getReferenceCode(),"????????");
		buf.append( AonStringUtils.abbreviate(refCode, 64) );
		return buf.toString();
	}
	
	// *********************************************************
	// ***************************************** [CONSUMERS] ***
	// *********************************************************

	// -------------------------------- [ENTRY DETAIL FOR INVOICE REGISTRY ]
	private static final Consumer<Context> REGISTRY = c -> {
		Account account = c.getRegistryAccount().orElse(null);
		double amount = c.invoice.getTotal(); 
		if (c.invoice.isSales()) {
			c.addDebit( account, amount, null );
		} else {
			c.addCredit( account, amount, null );
		}
	};
	
	// -------------------------------------- [ENTRY DETAIL FOR INPUT VAT]
	private static final Consumer<Context> INPUT_VAT = c -> {
		if (c.invoice.isInputVatEnabled()) {
			AonCollectionUtils.stream( AccountInvoiceTaxBreakdown.getMap(c, det -> getInputVatAccount(c, det) ))
				.filter( entry -> AonMathUtils.isNotZero( entry.getValue().getVatQuota(c.invoice) ))  
				.forEach( entry -> c.addDebit( 
						entry.getValue().getAccount()
						, entry.getValue().getVatQuota(c.invoice)
						, c.getRegistryAccount().orElse(null)
					));
		}
	};
	
	// ------------------------------------- [ENTRY DETAIL FOR OUTPUT VAT]
	private static final Consumer<Context> OUTPUT_VAT = c -> {
		if (c.invoice.isOutputVatEnabled()) {
			AonCollectionUtils.stream( AccountInvoiceTaxBreakdown.getMap(c, det -> getOutputVatAccount(c, det) ))
				.filter( entry -> AonMathUtils.isNotZero( entry.getValue().getVatQuota(c.invoice) ))  
				.forEach( entry -> c.addCredit( 
						entry.getValue().getAccount()
						, entry.getValue().getVatQuota(c.invoice)
						, c.getRegistryAccount().orElse(null)
					));
		}
	};
	
	// --------------------------- [ENTRY DETAIL VAT NEGATIVE ADJUSTMENT]
	private static final Consumer<Context> VAT_NEGATIVE_ADJUST = c -> {
		if (c.invoice.isNotSales() && c.invoice.isNotSurcharge() && c.invoice.isVatEnabled() ) {
			AonCollectionUtils.stream( AccountInvoiceTaxBreakdown.getMap(c, det -> getVATNegativeAdjustAccount(c, det) ))
				.filter( entry -> AonMathUtils.isNotZero( entry.getValue().getVatAdjustAmount(c.invoice) ))  
				.forEach( entry -> c.addDebit( 
						entry.getValue().getAccount()
						, entry.getValue().getVatAdjustAmount(c.invoice)
						, c.getRegistryAccount().orElse(null)
					));
		}
	};
	
//		
//	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> DIRECT_TAX_ADJUST = (invoice, map) -> {
//		if (!invoice.isSales() && !invoice.isSurcharge() && invoice.isOutputVatEnabled() != invoice.isInputVatEnabled()) {
//			for (InvoiceDetail det : invoice.getDetails()) {
//				if (det.getInvestAsset().isPresent() && det.getInvestAsset().get().getId() != null) {
//					for (InvoiceTax it : det.getInvoiceTaxes()) {
//						if (AonMathUtils.notEquals(100.0, it.getDirectTaxNoDedExpenses())
//							&& it.getAdjDirectTaxAccount() != null
//							&& it.getAdjDirectTaxAccount().getId() != null) {
//							double amount = it.getDirectTaxNoDedExpenses();
//							Account adjDirectTaxAccount = it.getAdjDirectTaxAccount();
//							if (!isEmpty(adjDirectTaxAccount)) {
//								AccountEntryDetail detail = map.computeIfAbsent(adjDirectTaxAccount.getId()
//									, k -> new AccountEntryDetail()
//										.setAccount(adjDirectTaxAccount.getId())
//										.setAccountCode(adjDirectTaxAccount.getCode())
//										.setAccountDescription(adjDirectTaxAccount.getDescription())
//										.setBalancingAccount(obtainRegistryAccount(invoice))
//										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
//										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
//								);
//								if (invoice.isOutputVatEnabled()) {
//									detail.addCredit( amount );
//								} else {
//									detail.addDebit( amount );
//								}
//								
//								Account expAccount = det.getExpAccount();
//								if (!isEmpty(expAccount)) {
//									detail = map.computeIfAbsent(expAccount.getId()
//										, k -> new AccountEntryDetail()
//											.setAccount(expAccount.getId())
//											.setAccountCode(expAccount.getCode())
//											.setAccountDescription(expAccount.getDescription())
//											.setBalancingAccount(obtainRegistryAccount(invoice))
//											.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
//											.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
//									);
//									if (invoice.isOutputVatEnabled()) {
//										detail.addDebit( amount );
//									} else {
//										detail.addCredit( amount );
//									}
//									
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	};
//
	
	// -------------------------------- [ENTRY DETAIL FOR SALES WITDHOLDING]
	private static final Consumer<Context> SALES_WITHHOLDING = c -> {
		if (c.invoice.isSales() && c.invoice.isWithholding()) {
			c.invoice.getInvoiceWithholding() 
				.filter( w -> AonMathUtils.isNotZero(w.getQuota() - w.getDeductibleQuota())) 
				.ifPresent( w  -> {
					Account account = w.getAccount().orElse( c.getDefaultSalesRetentionAccount() );
					c.map
						.computeIfAbsent( account.getCode()
							, k -> new AccountEntryDetail()
							.setAccount(account)
							.setBalancingAccount(c.getRegistryAccount().orElse(null))					
						.addDebit( w.getQuota() - w.getDeductibleQuota()));
			});
		}
	};
	
	// -------------------------------- [ENTRY DETAIL FOR SALES WITDHOLDING]
	private static final Consumer<Context> NOT_SALES_WITHHOLDING = c -> {
		if (c.invoice.isNotSales() && c.invoice.isWithholding()) {
			c.invoice.getInvoiceWithholding() 
				.filter( w -> AonMathUtils.isNotZero(w.getQuota() - w.getDeductibleQuota())) 
				.ifPresent( w  -> {
					Account account = w.getAccount().orElse( c.getDefaultNotSalesRetentionAccount() );
					c.map
						.computeIfAbsent( account.getCode()
							, k -> new AccountEntryDetail()
							.setAccount(account)
							.setBalancingAccount(c.getRegistryAccount().orElse(null))					
						.addCredit( w.getQuota() - w.getDeductibleQuota()));
			});
		}
	};

	// -------------------------------- [ENTRY DETAIL FOR PURCHASE INVOICE EXP ACCOUNT ]
	private static final Consumer<Context> PREPAYMENT_ACCOUNT = c -> {
		if (c.invoice.isPurchase()) {
			c.invoice.detailStream()
				.filter( det -> det.isPrepayment() )
				.filter( det -> AonMathUtils.isNotZero( det.getTaxableBase()) )
				.map(det -> 
					new Pair<Account,Double>( 
						c.getExpAccount(det).orElse( c.getDefaultPrepaymentAccount() ) 
						, det.getTaxableBase() )
					)
				.forEach( p -> { 
					c.setRegistryBalancingAccount(p.getLeft());
					c.addDebit( p.getLeft(), p.getRight(), c.getRegistryAccount().orElse(null) );
				})
			;
		}
	};

	// -------------------------------- [ENTRY DETAIL FOR SALE INVOICE EXP ACCOUNT ]
	private static final Consumer<Context> SALES_EXP_ACCOUNT = c -> {
		if (c.invoice.isSales()) {
			c.invoice.detailStream()
				.filter( det -> det.isNotPrepayment() )
				.filter( det -> AonMathUtils.isNotZero( det.getTaxableBase()) )
				.map(det -> 
					new Pair<Account,Double>( 
						c.getExpAccount(det).orElse( c.getDefaultSalesAccount() ) 
						, det.getTaxableBase() )
					)
				.forEach( p -> { 
					c.setRegistryBalancingAccount(p.getLeft());
					c.addCredit( p.getLeft(), p.getRight(), c.getRegistryAccount().orElse(null));
				 })
			;
		}
	};

	// -------------------------------- [ENTRY DETAIL FOR PURCHASE INVOICE EXP ACCOUNT ]
	private static final Consumer<Context> PURCHASE_EXP_ACCOUNT = c -> {
		if (c.invoice.isPurchase()) {
			c.invoice.detailStream()
				.filter( det -> det.isNotPrepayment() )
				.filter( det -> AonMathUtils.isNotZero( det.getTaxableBase()) )
				.map(det -> 
					new Pair<Account,Double>( 
						c.getExpAccount(det).orElse( c.getDefaultPurchaseAccount() ) 
						, det.getTaxableBase() )
					)
				.forEach( p -> { 
					c.setRegistryBalancingAccount(p.getLeft());
					c.addDebit( p.getLeft(), p.getRight(), c.getRegistryAccount().orElse(null) );
				})
			;
		}
	};
	
		
	// -------------------------------- [ENTRY DETAIL FOR EXPENSE INVOICE EXP ACCOUNT ]
	private static final Consumer<Context> EXPENSES_EXP_ACCOUNT = c -> {
		if (c.invoice.isExpenses() || c.invoice.isUndeductible()) {
			c.invoice.detailStream()
				.filter( det -> det.isNotPrepayment() )
				.filter( det -> AonMathUtils.isNotZero( det.getTaxableBase()) )
				.map(det -> 
					new Pair<Account,Double>( 
						c.getExpAccount(det)
							.orElseThrow( () -> new AonCoreException(
							"No se ha podido determinar la cuenta de explotaci\u00F3 para el detalle de la factura: " + AonStringUtils.abbreviate(det.getDescription(), 64) ) )
						, det.getTaxableBase() )
					)
				.forEach( p -> { 
					c.setRegistryBalancingAccount(p.getLeft());
					c.addDebit( p.getLeft(), p.getRight(), c.getRegistryAccount().orElse(null) ); 
				}) 
			;
		}
	};

	// -------------------------------- [FILL REGISTRY BALANCING ACCOUNT]
	private static final Consumer<Context> REGISTRY_BALANCING_ACCOUNT = c -> {
		if (c.registryBalancingAccount != null && c.registryBalancingAccount.get() != null) {
			Account balancingAccount = c.registryBalancingAccount.get();
			c.getRegistryAccount()
				.map( Account::getCode )
				.filter( code -> AonStringUtils.isNotBlank(code) )
				.map( code -> c.map.get(code))
				.filter( d -> d != null)
				.ifPresent( aed -> aed.setBalancingAccount(balancingAccount))
			;
		}
	};

	// *****************************************************
	// ************************* [INNER UTILITY CLASSES] ***
	// *****************************************************
	
	private static class AccountInvoiceTaxBreakdown {
		private final Account account;
		private final InvoiceBreakdown ib;
		
		AccountInvoiceTaxBreakdown(Account account, InvoiceTax it) {
			super();
			ib = InvoiceBreakdown.from( it );
			this.account = account;
		}
		String getKey() {
			return account.getCode() + "|" + ib.getPercentage() + "|" + ib.getSurcharge();
		}
		
		Account getAccount() {
			return account;
		}
		
		InvoiceBreakdown getInvoiceBreakdown() {
			return ib;
		}
		
		double getVatQuota( Invoice invoice) {
			return invoice.isVatEnabled()
				? ib.getDeductibleQuota()
				: ib.getQuota();
		}
		double getVatAdjustAmount(Invoice invoice) {
			return invoice.isVatEnabled()
				? AonMathUtils.round(ib.getQuota() + ib.getSurchargeQuota() - ib.getDeductibleQuota())
				: 0.0;
		}
		
		static Map<String, AccountInvoiceTaxBreakdown> getMap(Context c, Function<InvoiceDetail, Account> accountGetter) {
			Map<String, AccountInvoiceTaxBreakdown> vatMap = new LinkedHashMap<>();
			c.invoice.detailStream()
				.forEach(det -> {
					Account account = accountGetter.apply( det );
					if (account == null || account.getId() == null) {
						throw new AonCoreException("No se ha podido determinar la cuenta de IVA repercutido para el detalle de la factura: " + AonStringUtils.abbreviate(det.getDescription(), 64) );
					}
					det.taxStream()
						.filter( it -> it.isVatType() )
						.map( it -> new AccountInvoiceTaxBreakdown( account, it ) )
						.forEach( aitb -> {
							AccountInvoiceTaxBreakdown prev = vatMap.get( aitb.getKey() );
							if (prev == null) {
								vatMap.put( aitb.getKey(), aitb );
							} else {
								prev.getInvoiceBreakdown().add( aitb.getInvoiceBreakdown() );
							}
						});
				});
			AonCollectionUtils.valuesStream( vatMap )
				.map( aitb -> aitb.getInvoiceBreakdown() )
				.forEach( ib -> ib.calculate( c.invoice ) )
			;
			return vatMap;
		}
	}
	
	private static class Context {
		
		private final AONContext ctx;
		private final Integer domainId;
		private final AonConfiguration config;
		private final Invoice invoice;
		private final boolean dryRun;
		LinkedHashMap<String,AccountEntryDetail> map = new LinkedHashMap<>();
		private Account registryAccount = null;
		private AtomicReference<Account> registryBalancingAccount = null;
		
		Context(AONContext ctx, Invoice invoice, boolean dryRun) {
			this.ctx = ctx;
			this.config = ctx.getConfig();
			this.invoice = invoice;
			this.domainId = invoice.getDomain();
			this.dryRun = dryRun;
			checkDefaultAccounts();
		}
		
		public void setRegistryBalancingAccount(Account account) {
			// Solo se puede establecer la cuenta de contrapartida del asiento de registro una vez (una cuenta de explotacion). 
			// Si  hay mas de una cuenta de explotacion, se anula el valor para que no se utilice ninguna.
			if (account == null || AonStringUtils.isBlank(account.getCode())) return;
			if (registryBalancingAccount == null) {
				registryBalancingAccount = new AtomicReference<>( account );
			} else if (AonStringUtils.notEquals( registryBalancingAccount.get().getCode(), account.getCode() )) {
				registryBalancingAccount.set( null );
			}
		}

		private void checkDefaultAccounts() {
			checkDefaultAccount(getDefaultSalesAccount()
				, DEF_SALES_ACCOUNT, AppParam.ACC_DEFAULT_SALES_ACC, config.accounting()::setDefaultSalesAccount);
			checkDefaultAccount(getDefaultPurchaseAccount()
				, DEF_PURCHASE_ACCOUNT, AppParam.ACC_DEFAULT_PURCHASE_ACC, config.accounting()::setDefaultPurchaseAccount);
			checkDefaultAccount(getDefaultOutputVatAccount()
				, DEF_OUTPUT_VAT_ACCOUNT, AppParam.ACC_DEFAULT_CHARGED_VAT_ACC, config.accounting()::setDefaultChargedVatAccount);
			checkDefaultAccount(getDefaultInputVatAccount()
				, DEF_INPUT_VAT_ACCOUNT, AppParam.ACC_DEFAULT_PAID_VAT_ACC, config.accounting()::setDefaultPaidVatAccount);
			checkDefaultAccount(getDefaultSalesRetentionAccount()
				, DEF_SALES_RET_ACCOUNT, AppParam.ACC_DEFAULT_PAID_RET_ACC, config.accounting()::setDefaultPaidRetAccount);
			checkDefaultAccount(getDefaultNotSalesRetentionAccount()
				, DEF_NOT_SALES_RET_ACCOUNT, AppParam.ACC_DEFAULT_PAID_RET_ACC, config.accounting()::setDefaultChargedRetAccount);
			checkDefaultAccount(getDefaultVATNegativeAdjustAccount()
				, DEF_VAT_NEGATIVE_ADJUST_ACCOUNT, AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC, config.accounting()::setVatNegativeAdjustAccount);
			checkDefaultAccount(getDefaultPrepaymentAccount()
				, DEF_PREPAYMENT_ACCOUNT, AppParam.ACC_DEFAULT_PREPAYMENT_ACC, config.accounting()::setDefaultPrepayment);
		}


		private Account checkDefaultAccount(Account a, String code, AppParam params, Consumer<Account> setter) {
			if (a == null || a.getId() == null) {
				a = AccountDAO.get(ctx, code);
				if (a != null && a.getId() != null) {
					AppParamDAO.save(ctx, domainId, params, AonNumberUtils.toString(a.getId()));
					setter.accept(a);
				}
			}
			return a;
		}

		private Account getDefaultSalesAccount() 			{return ensureAccount(config.accounting().getDefaultSalesAccount());}
		private Account getDefaultPurchaseAccount() 		{return ensureAccount(config.accounting().getDefaultPurchaseAccount());}
		private Account getDefaultOutputVatAccount() 		{return ensureAccount(config.accounting().getDefaultChargedVatAccount());}
		private Account getDefaultInputVatAccount() 		{return ensureAccount(config.accounting().getDefaultPaidVatAccount());}
		private Account getDefaultSalesRetentionAccount() 	{return ensureAccount(config.accounting().getDefaultPaidRetAccount());}
		private Account getDefaultNotSalesRetentionAccount(){return ensureAccount(config.accounting().getDefaultChargedRetAccount());}
		private Account getDefaultVATNegativeAdjustAccount(){return ensureAccount(config.accounting().getVatNegativeAdjustAccount());}
		private Account getDefaultPrepaymentAccount() 		{return ensureAccount(config.accounting().getDefaultPrepayment());}
		
		private Account ensureAccount(Account a) {
			return (a != null && a.getId() == null) ? null : a;
		}
		
		Optional<Account> getRegistryAccount() {
			if (registryAccount == null) {
				registryAccount = obtainRegistryAccount().orElse(null);
			}
			return Optional.ofNullable(registryAccount);
		}
		
		private Optional<Account> obtainRegistryAccount() {
			Account account = null;
			if (invoice.getRegistry() != null && invoice.getRegistryAccount() != null) {
				account = ensureAccount(invoice.getRegistryAccount());
			}
			if (dryRun && account == null) {
				String code = obtainRegistryAccountCode(invoice);
				account = new Account()
					.setCode(code)
					.setDescription(invoice.getRegistryName());
			}
			return Optional.ofNullable(account);
		}

		private String obtainRegistryAccountCode(Invoice invoice) {
			String code = "?????????";
			if (invoice.getRegistryAccount() != null) {
				code = invoice.getRegistryAccount().getCode();
				if (AonStringUtils.isBlank(code)) {
					code = getUnknowAccountCode( getRegistryAccountPrefix(invoice) );
				}
			}
			return code;
		}

		private String getRegistryAccountPrefix(Invoice invoice ) {
			return invoice.getType().visit(invoice,  new IInvoiceTypeVisitor<String>() {
				@Override public String visitPurchase(Invoice i) 		{return "4000";}
				@Override public String visitSales(Invoice i) 			{return "4300";}
				@Override public String visitExpenses(Invoice i) 		{return "4100";}
				@Override public String visitUndeductible(Invoice i) 	{return "4100";}
			});
		}

		private String getUnknowAccountCode(String accountPrefix) {
			return AonStringUtils.rightPad(accountPrefix, 9, '?');
		}
		
		Optional<Account> getExpAccount(InvoiceDetail detail) {
			Account account = null;
			if (detail.getAccountId() != null) {
				account = new Account()
					.setId(detail.getAccountId())
					.setCode(detail.getAccountCode())
					.setDescription(detail.getAccountDescription());
			}
			return Optional.ofNullable(account);
		}
		
		private Optional<AccountEntryDetail> get(Account account, Account balAccount) {
			if (account == null) return Optional.empty();
			if (AonStringUtils.isBlank(account.getCode())) return Optional.empty();
			return Optional.of(map.computeIfAbsent(account.getCode(), k -> new AccountEntryDetail().setAccount(account).setBalancingAccount(balAccount)));
		}
		
		void addCredit(Account account, double amount, Account balAccount) {
			if (!check(account, amount)) return;
			get(account,balAccount).ifPresent( detail -> detail.addCredit( amount ));
		}
		
		void addDebit(Account account, double amount, Account balAccount) {
			if (!check(account, amount)) return;
			get(account,balAccount).ifPresent( detail -> detail.addDebit( amount ));
		}
		
		private boolean check(Account account, double amount) {
			if (AonMathUtils.isZero(amount)
			 || account == null
			 || AonStringUtils.isBlank(account.getCode())) {
				return false;
			}
			return true;
		}
	}
	
	// *****************************************************
	// ******************************************* [OLD] ***
	// *****************************************************
	
	/**
	 * Usado en AccountingInvoiceDAO! 
	 */
	public static AccountEntry getEntryBase(AONContext ctx, AonConfiguration aonCtx, Invoice invoice) {
		EnterpriseActivity ea = !invoice.getActivity().isEmpty() ? invoice.getActivity() : aonCtx.getMainActivity();
		Integer activity = (ea==null?null:ea.getId());
		Integer periodId = null;
		if (invoice.getIssueDate() != null) {
			AccountPeriod period = ACCOUNTING.ensurePeriod(ctx, ctx.getDomainId(), invoice.getIssueDate());
			periodId = (period == null? null : period.getId());
		}
		AccountEntry accountEntry = new AccountEntry()
				.setPeriod(periodId)
				.setDomain(invoice.getDomain())
				.setConfidential(false)
				.setEntryDate(invoice.getIssueDate())
				.setActivity(activity)
				.setComments(invoice.getComments())
				.setDirty(false);
		invoice.getType().visit(invoice,  new IInvoiceTypeVisitor<Void>() {
			@Override 
			public Void visitUndeductible(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(true);
				return null;
			}
			@Override 
			public Void visitSales(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);
				return null;
			}
			@Override 
			public Void visitPurchase(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);
				return null;
			}
			@Override 
			public Void visitExpenses(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(false);
				return null;
			}
		});
		return accountEntry;
	}

	private static Account getInputVatAccount(Context c, InvoiceDetail det) {
		return getProductVatAccount(c, det, TAX.PURCHASE_ACCOUNT)
			.orElse( c.getDefaultInputVatAccount() );
	}
	private static Account getOutputVatAccount(Context c, InvoiceDetail det) {
		return getProductVatAccount(c, det, TAX.SALES_ACCOUNT)
			.orElse( c.getDefaultOutputVatAccount() );
	}
	private static Account getVATNegativeAdjustAccount(Context c, InvoiceDetail det) {
		return c.getDefaultVATNegativeAdjustAccount();
	}
	private static Optional<Account> getProductVatAccount(Context c, InvoiceDetail det, Field<Integer> accountField) {
		if (det.getItem() == null || det.getItem().getId() != null) return Optional.empty();
		return c.ctx.getDslContext().select(ACCOUNT.fields())
			.from(ITEM)
			.innerJoin(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.innerJoin(TAX).on(TAX.ID.eq(PRODUCT.VAT))
			.innerJoin(ACCOUNT).on(ACCOUNT.ID.eq( accountField))
			.where(ITEM.ID.eq(det.getItem().getId()))
			.fetch()
			.stream()
			.map( r -> FullAccountFiller.build( r )  )
			.findFirst();
	}

}
