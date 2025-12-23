package com.esferalia.aon.occam.impl.jooq.dao.invoice;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRecorderDAO {

	private InvoiceRecorderDAO() {
	}
	
	private static class Context {
		
		private static final String _600000000 = "600000000";
		private final AONContext ctx;
		private final Integer domainId;
		private final AonConfiguration config;
		private final Invoice invoice;
		private final boolean dryRun;
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<>();
		private Account registryAccount = null;
		
		Context(AONContext ctx, Invoice invoice, boolean dryRun) {
			this.ctx = ctx;
			this.config = ctx.getConfig();
			this.invoice = invoice;
			this.domainId = invoice.getDomain();
			this.dryRun = dryRun;
			checkDefaultAccounts();
		}
		
		private void checkDefaultAccounts() {
			checkDefaultSalesAccount();
		}

		private void checkDefaultSalesAccount() {
			Account a = getDefaultSalesAccount();
			if (a == null) {
				a = AccountDAO.get(ctx, _600000000);
				if (a != null && a.getId() != null) {
					AppParamDAO.save(ctx, domainId, AppParam.ACC_DEFAULT_SALES_ACC, AonNumberUtils.toString(a.getId()));
					config.accounting().setDefaultSalesAccount(a);
				}
			}
		}

		Optional<Account> getRegistryAccount() {
			if (registryAccount == null) {
				registryAccount = obtainRegistryAccount().orElse(null);
			}
			return Optional.ofNullable(registryAccount);
		}
		
		Optional<Account> obtainRegistryAccount() {
			Account account = null;
			if (invoice.getRegistry() != null && invoice.getRegistryAccount() != null) {
				account = invoice.getRegistryAccount();
			}
			if (dryRun && account == null) {
				String code = obtainRegistryAccountCode(invoice);
				String description = obtainRegistryAccountDescription();
				account = new Account()
					.setCode(code)
					.setDescription(description);
			}
			return Optional.ofNullable(account);
		}

		private String obtainRegistryAccountDescription() {
			return getRegistryAccount()
				.map( acc -> acc.getDescription() )
				.orElse(invoice.getRegistryName());
		}

		Optional<Account> getExpAccount(InvoiceDetail detail) {
			Account account = null;
			if (detail.getAccountId() != null) {
				account = new Account()
					.setId(detail.getAccountId())
					.setCode(detail.getAccountCode())
					.setDescription(detail.getAccountDescription());
			}
			if (account == null && invoice.isSales()) {
				 account = getDefaultSalesAccount();
			}
			if (account == null && invoice.isPurchase()) {
				account = getDefaultPurchaseAccount();
			}
			return Optional.ofNullable(account);
		}
		
		private Account getDefaultSalesAccount() {
			return config.accounting().getDefaultSalesAccount();
		}
		private Account getDefaultPurchaseAccount() {
			return config.accounting().getDefaultPurchaseAccount();
		}

		private Optional<AccountEntryDetail> get(Account expAccount) {
			if (expAccount == null) return Optional.empty();
			if (expAccount.getId() == null) return Optional.empty();
			return Optional.of(map.computeIfAbsent(expAccount.getId(), k -> new AccountEntryDetail().setAccount(expAccount)));
		}
		
		void addCredit(Account expAccount, double amount) {
			if (!check(expAccount, amount)) return;
			get(expAccount).ifPresent( detail -> detail.addCredit( amount ));
		}
		
		void addDebit(Account expAccount, double amount) {
			if (!check(expAccount, amount)) return;
			get(expAccount).ifPresent( detail -> detail.addDebit( amount ));
		}
		
		private boolean check(Account expAccount, double amount) {
			if (AonMathUtils.isZero(amount)
			 || expAccount == null
			 || expAccount.getId() == null) {
				return false;
			}
			return true;
		}
	}
	
	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";

	// ********************************************************
	// ******************************************* [PUBLIC] ***
	// ********************************************************

	public static AccountEntry getInvoiceEntry(AONContext ctx, Invoice invoice) {
		return getInvoiceEntry(ctx, invoice, false);
	}
	
	public static AccountEntry getInvoiceEntry(AONContext ctx, Invoice invoice, boolean dryRun) {
		Context c = new Context(ctx, invoice, dryRun);
		AccountEntry ae = fillAccountEntry( c );
		FILL_REGISTRY_ACCOUNT
			.andThen(REGISTRY)
//			.andThen(INPUT_VAT)
//			.andThen(OUTPUT_VAT)
//			.andThen(VAT_NEGATIVE_ADJUST)
//			.andThen(DIRECT_TAX_ADJUST)
//			.andThen(WITHHOLDING)
			.andThen(EXP_ACCOUNT)
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
	
	private static String getUnknowAccountCode(String accountPrefix) {
		return AonStringUtils.rightPad(accountPrefix, 9, '?');
	}

	private static String obtainRegistryAccountCode(Invoice invoice) {
		String code = "?????????";
		if (invoice.getRegistryAccount() != null) {
			code = invoice.getRegistryAccount().getCode();
			if (AonStringUtils.isBlank(code)) {
				code = getUnknowAccountCode( getRegistryAccountPrefix(invoice) );
			}
		}
		return code;
	}

	private static String getRegistryAccountPrefix(Invoice invoice ) {
		return invoice.getType().visit(invoice,  new IInvoiceTypeVisitor<String>() {
			@Override public String visitPurchase(Invoice i) 		{return "4000";}
			@Override public String visitSales(Invoice i) 			{return "4300";}
			@Override public String visitExpenses(Invoice i) 		{return "4100";}
			@Override public String visitUndeductible(Invoice i) 	{return "4100";}
		});
	}
	
	private static boolean isEmpty(Integer id) {
		return id == null;
	}

	private static void fillBalancingAccount(AccountEntryDetail detail, Invoice invoice) {
		Account account = new Account();
		invoice.detailStream()
			.filter( d -> d.getAccountId() != null )
			.findFirst()
			.ifPresent( d -> {
				account.setId(d.getAccountId());
				account.setCode(d.getAccountCode());
				account.setDescription(d.getAccountDescription());
			}
		);
		detail.setBalancingAccountId(AonObjectUtils.ifNotNullGet(account, Account::getId ) )
			.setBalancingAccountCode(AonObjectUtils.ifNotNullGet(account, Account::getCode ) )
			.setBalancingAccountDescription(AonObjectUtils.ifNotNullGet(account, Account::getDescription ) )
		;
	}
	
	// *********************************************************
	// ***************************************** [CONSUMERS] ***
	// *********************************************************

	private static final Consumer<Context> FILL_REGISTRY_ACCOUNT = c -> {
		
	};
	
	private static final Consumer<Context> REGISTRY = c -> {
		Account account = c.obtainRegistryAccount().orElse(null);
		double amount = c.invoice.getTotal(); 
		if (c.invoice.isSales()) {
			c.addDebit( account, amount );
		} else {
			c.addCredit( account, amount );
		}
	};
	
//	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> INPUT_VAT = (invoice, map) -> {
//		if (invoice.isInputVatEnabled()) {
//			for (InvoiceDetail det : invoice.getDetails()) {
//				for (InvoiceTax it : det.getInvoiceTaxes()) {
//					if (it.isVatType()) {
//						double amount = it.getDeductibleQuota() + it.getSurchargeQuota();
//						if (AonMathUtils.isNotZero(amount)) {
//							Integer inputAccountId = isEmpty(it.getAccount())?det.getAccountId():it.getAccount();
//							if (!isEmpty(inputAccount)) {
//								map.computeIfAbsent(inputAccount
//									, k -> new AccountEntryDetail()
//										.setAccountId(inputAccount.getId())
//										.setAccountCode(inputAccount.getCode())
//										.setAccountDescription(inputAccount.getDescription())
//										.setBalancingAccount(obtainRegistryAccount(invoice))
//										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
//										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
//								).addDebit( amount );
//							}
//						}
//					}
//				}
//			}
//		}
//	};
//	
//	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> OUTPUT_VAT = (invoice, map) -> {
//		if (invoice.isOutputVatEnabled()) {
//			for (InvoiceDetail det : invoice.getDetails()) {
//				for (InvoiceTax it : det.getInvoiceTaxes()) {
//					if (it.isVatType()) {
//						double amount = it.getDeductibleQuota() + it.getSurchargeQuota();
//						if (AonMathUtils.isNotZero(amount)) {
//							Account outputAccount = it.getOutputAccount();
//							if (!isEmpty(outputAccount)) {
//								map.computeIfAbsent(outputAccount.getId()
//									, k -> new AccountEntryDetail()
//										.setAccount(outputAccount.getId())
//										.setAccountCode(outputAccount.getCode())
//										.setAccountDescription(outputAccount.getDescription())
//										.setBalancingAccount(obtainRegistryAccount(invoice))
//										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
//										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
//								).addCredit( amount );
//								
//							}
//						}
//					}
//				}
//			}
//		}
//	};
//
//	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> VAT_NEGATIVE_ADJUST = (invoice, map) -> {
//		if (!invoice.isSales() && !invoice.isSurcharge() && invoice.isOutputVatEnabled() != invoice.isInputVatEnabled()) {
//			for (InvoiceDetail det : invoice.getDetails()) {
//				for (InvoiceTax it : det.getInvoiceTaxes()) {
//					if (it.isVatType() && AonNumberUtils.notEquals(it.getDeductibleQuota(), it.getQuota())) {
//						double amount = it.getQuota() - it.getDeductibleQuota();
//						if (AonMathUtils.isNotZero(amount)) {
//							Account adjAccount = isEmpty(it.getAdjAccount())?det.getExpAccount():it.getAdjAccount();
//							if (!isEmpty(adjAccount)) {
//								AccountEntryDetail detail = map.computeIfAbsent(adjAccount.getId()
//									, k -> new AccountEntryDetail()
//										.setAccount(adjAccount.getId())
//										.setAccountCode(adjAccount.getCode())
//										.setAccountDescription(adjAccount.getDescription())
//										.setBalancingAccount(obtainRegistryAccount(invoice))
//										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
//										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
//								);
//								if (invoice.isOutputVatEnabled()) {
//									detail.addCredit( amount );
//								} else {
//									detail.addDebit( amount );
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	};
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
//	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> WITHHOLDING = (invoice, map) -> {
//		if (invoice.isWithholding() && invoice.getWithholding().isPresent()) {
//			InvoiceWithholding withholding = invoice.getWithholding().get();
//			if (withholding.getAccount() != null && AonMathUtils.isNotZero(withholding.getQuota())) {
//				AccountEntryDetail detail = map.computeIfAbsent( withholding.getAccount().getId()
//					, k -> new AccountEntryDetail()
//						.setAccount(withholding.getAccount().getId())
//						.setAccountCode(withholding.getAccount().getCode())
//						.setAccountDescription(withholding.getAccount().getDescription())
//						.setBalancingAccount(obtainRegistryAccount(invoice))
//						.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
//						.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
//				);
//				if (invoice.isSales()) {
//					detail.addDebit( withholding.getQuota() );
//				} else {
//					detail.addCredit( withholding.getQuota() );
//				}
//			}
//		}
//	};
	
	private static final Consumer<Context> EXP_ACCOUNT = c -> {
		for (InvoiceDetail det : c.invoice.getDetails()) {
			double amount = det.getTaxableBase();
			if (AonMathUtils.isNotZero(amount)) {
				Account expAccount = c.getExpAccount(det)
					.orElseThrow( () -> new AonCoreException(
						"No se ha podido determinar la cuenta de gasto para el detalle de la factura: " + AonStringUtils.abbreviate(det.getDescription(), 64) ) );
				if (c.invoice.isSales()) {
					c.addCredit( expAccount, amount );
				} else {
					c.addDebit( expAccount, amount );
				}
			}
		}
	};
}
