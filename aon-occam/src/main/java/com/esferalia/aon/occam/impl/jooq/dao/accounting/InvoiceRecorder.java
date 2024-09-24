package com.esferalia.aon.occam.impl.jooq.dao.accounting;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRecorder {

	private InvoiceRecorder() {
	}
	
	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";

	public static AccountEntry getInvoiceEntry(AONContext ctx, Invoice invoice) {
		AccountEntry ae = fillAccountEntry( ctx, invoice);
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<>();
		REGISTRY
			.andThen(INPUT_VAT)
			.andThen(OUTPUT_VAT)
			.andThen(VAT_NEGATIVE_ADJUST)
			.andThen(DIRECT_TAX_ADJUST)
			.andThen(WITHHOLDING)
			.andThen(EXP_ACCOUNT)
		.accept(invoice, map);
		
		ae.setDetails(new LinkedList<>());
		ae.getDetails().addAll(map.values());
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

	private static AccountEntry fillAccountEntry(AONContext ctx, Invoice invoice) {
		if (invoice == null) throw new AonCoreException("La factura no puede ser NULL");
		if (invoice.getType() == null) throw new AonCoreException("La factura debe tener tipo");
		if (invoice.getIssueDate() == null) throw new AonCoreException("La factura debe tener fecha");
		if (invoice.getDomain() == null) throw new AonCoreException("La factura debe tener dominio");
		if (invoice.getRegistry() == null) throw new AonCoreException("La factura debe tener titular");
		if (AonNumberUtils.equals(invoice.getDomain(), 0)) throw new AonCoreException("La factura debe tener dominio");
		
		Integer activityId = Optional.ofNullable(ctx.getConfiguration().getMainActivity()).map(a -> a.getId()).orElse(null);
		AccountPeriod period = AccountPeriodDAO.ensurePeriod(ctx, invoice.getDomain(), invoice.getIssueDate());
		AccountEntry accountEntry = new AccountEntry()
			.setPeriod(period.getId())
			.setDomain(invoice.getDomain())
			.setConfidential(false)
			.setEntryDate(invoice.getIssueDate())
			.setActivity(activityId)
			.setComments(invoice.getComments())
			.setDirty(false);
		return invoice.getType().visit(invoice,  new IInvoiceTypeVisitor<AccountEntry>() {
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

	private static Integer obtainRegistryAccount(Invoice invoice) {
		return (invoice.getRegistry() != null && invoice.getRegistryAccount() != null)
			?invoice.getRegistryAccount().getId() 
			:null;
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
	
	private static String obtainRegistryAccountDescription(Invoice invoice) {
		String description = "";
		if ( invoice.getRegistryAccount() != null ) {
			description = invoice.getRegistryAccount().getDescription();
			if (AonStringUtils.isBlank(description)) {
				description = invoice.getRegistryName();
			}
		}
		return description;
	}
	
	private static boolean isEmpty(Account account) {
		return account == null || account.getId() == null;
	}

	private static void fillBalancingAccount(AccountEntryDetail detail, Invoice invoice) {
		Account account = null;
		if (invoice.getDetails() != null) {
			for (InvoiceDetail det : invoice.getDetails()) {
				if (account == null) {
					account = det.getExpAccount();
				}
				if (account == null
				 || det.getExpAccount() == null
				 || !AonNumberUtils.equals(account.getId(),det.getExpAccount().getId())) {
					account = null;
					break;
				}
			}
		}
		detail.setBalancingAccount(AonObjectUtils.ifNotNullGet(account, Account::getId ) )
			.setBalancingAccountCode(AonObjectUtils.ifNotNullGet(account, Account::getCode ) )
			.setBalancingAccountDescription(AonObjectUtils.ifNotNullGet(account, Account::getDescription ) )
			;
	}
	
	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> REGISTRY = (invoice, map) -> {
		Integer registryAccountId = obtainRegistryAccount(invoice);
		double amount = invoice.getTotal(); 
		AccountEntryDetail detail = map.get(registryAccountId);
		if (detail == null) {
			detail = new AccountEntryDetail()
				.setAccount(registryAccountId)
				.setAccountCode(obtainRegistryAccountCode(invoice))
				.setAccountDescription(obtainRegistryAccountDescription(invoice));
			fillBalancingAccount(detail,invoice);
			map.put(registryAccountId,detail);
		}
		if (invoice.isSales()) {
			detail.addDebit( amount );
		} else {
			detail.addCredit( amount );
		}
	};
	
	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> INPUT_VAT = (invoice, map) -> {
		if (invoice.isInputVatEnabled()) {
			for (InvoiceDetail det : invoice.getDetails()) {
				for (InvoiceTax it : det.getInvoiceTaxes()) {
					if (it.isVatType()) {
						double amount = it.getDeductibleQuota() + it.getSurchargeQuota();
						if (AonMathUtils.isNotZero(amount)) {
							Account inputAccount = isEmpty(it.getInputAccount())?det.getExpAccount():it.getInputAccount();
							if (!isEmpty(inputAccount)) {
								map.computeIfAbsent(inputAccount.getId()
									, k -> new AccountEntryDetail()
										.setAccount(inputAccount.getId())
										.setAccountCode(inputAccount.getCode())
										.setAccountDescription(inputAccount.getDescription())
										.setBalancingAccount(obtainRegistryAccount(invoice))
										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
								).addDebit( amount );
							}
						}
					}
				}
			}
		}
	};
	
	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> OUTPUT_VAT = (invoice, map) -> {
		if (invoice.isOutputVatEnabled()) {
			for (InvoiceDetail det : invoice.getDetails()) {
				for (InvoiceTax it : det.getInvoiceTaxes()) {
					if (it.isVatType()) {
						double amount = it.getDeductibleQuota() + it.getSurchargeQuota();
						if (AonMathUtils.isNotZero(amount)) {
							Account outputAccount = it.getOutputAccount();
							if (!isEmpty(outputAccount)) {
								map.computeIfAbsent(outputAccount.getId()
									, k -> new AccountEntryDetail()
										.setAccount(outputAccount.getId())
										.setAccountCode(outputAccount.getCode())
										.setAccountDescription(outputAccount.getDescription())
										.setBalancingAccount(obtainRegistryAccount(invoice))
										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
								).addCredit( amount );
								
							}
						}
					}
				}
			}
		}
	};

	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> VAT_NEGATIVE_ADJUST = (invoice, map) -> {
		if (!invoice.isSales() && !invoice.isSurcharge() && invoice.isOutputVatEnabled() != invoice.isInputVatEnabled()) {
			for (InvoiceDetail det : invoice.getDetails()) {
				for (InvoiceTax it : det.getInvoiceTaxes()) {
					if (it.isVatType() && AonNumberUtils.notEquals(it.getDeductibleQuota(), it.getQuota())) {
						double amount = it.getQuota() - it.getDeductibleQuota();
						if (AonMathUtils.isNotZero(amount)) {
							Account adjAccount = isEmpty(it.getAdjAccount())?det.getExpAccount():it.getAdjAccount();
							if (!isEmpty(adjAccount)) {
								AccountEntryDetail detail = map.computeIfAbsent(adjAccount.getId()
									, k -> new AccountEntryDetail()
										.setAccount(adjAccount.getId())
										.setAccountCode(adjAccount.getCode())
										.setAccountDescription(adjAccount.getDescription())
										.setBalancingAccount(obtainRegistryAccount(invoice))
										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
								);
								if (invoice.isOutputVatEnabled()) {
									detail.addCredit( amount );
								} else {
									detail.addDebit( amount );
								}
							}
						}
					}
				}
			}
		}
	};
		
	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> DIRECT_TAX_ADJUST = (invoice, map) -> {
		if (!invoice.isSales() && !invoice.isSurcharge() && invoice.isOutputVatEnabled() != invoice.isInputVatEnabled()) {
			for (InvoiceDetail det : invoice.getDetails()) {
				if (det.getInvestAsset().isPresent() && det.getInvestAsset().get().getId() != null) {
					for (InvoiceTax it : det.getInvoiceTaxes()) {
						if (AonMathUtils.notEquals(100.0, it.getDirectTaxNoDedExpenses())
							&& it.getAdjDirectTaxAccount() != null
							&& it.getAdjDirectTaxAccount().getId() != null) {
							double amount = it.getDirectTaxNoDedExpenses();
							Account adjDirectTaxAccount = it.getAdjDirectTaxAccount();
							if (!isEmpty(adjDirectTaxAccount)) {
								AccountEntryDetail detail = map.computeIfAbsent(adjDirectTaxAccount.getId()
									, k -> new AccountEntryDetail()
										.setAccount(adjDirectTaxAccount.getId())
										.setAccountCode(adjDirectTaxAccount.getCode())
										.setAccountDescription(adjDirectTaxAccount.getDescription())
										.setBalancingAccount(obtainRegistryAccount(invoice))
										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
								);
								if (invoice.isOutputVatEnabled()) {
									detail.addCredit( amount );
								} else {
									detail.addDebit( amount );
								}
								
								Account expAccount = det.getExpAccount();
								if (!isEmpty(expAccount)) {
									detail = map.computeIfAbsent(expAccount.getId()
										, k -> new AccountEntryDetail()
											.setAccount(expAccount.getId())
											.setAccountCode(expAccount.getCode())
											.setAccountDescription(expAccount.getDescription())
											.setBalancingAccount(obtainRegistryAccount(invoice))
											.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
											.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
									);
									if (invoice.isOutputVatEnabled()) {
										detail.addDebit( amount );
									} else {
										detail.addCredit( amount );
									}
									
								}
							}
						}
					}
				}
			}
		}
	};
	
	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> WITHHOLDING = (invoice, map) -> {
		if (invoice.isWithholding() && invoice.getWithholding().isPresent()) {
			InvoiceWithholding withholding = invoice.getWithholding().get();
			if (withholding.getAccount() != null && AonMathUtils.isNotZero(withholding.getQuota())) {
				AccountEntryDetail detail = map.computeIfAbsent( withholding.getAccount().getId()
					, k -> new AccountEntryDetail()
						.setAccount(withholding.getAccount().getId())
						.setAccountCode(withholding.getAccount().getCode())
						.setAccountDescription(withholding.getAccount().getDescription())
						.setBalancingAccount(obtainRegistryAccount(invoice))
						.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
						.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
				);
				if (invoice.isSales()) {
					detail.addDebit( withholding.getQuota() );
				} else {
					detail.addCredit( withholding.getQuota() );
				}
			}
		}
	};
	
	private static final BiConsumer<Invoice, LinkedHashMap<Integer,AccountEntryDetail>> EXP_ACCOUNT = (invoice, map) -> {
		for (InvoiceDetail det : invoice.getDetails()) {
			double amount = det.getTaxableBase();
			if (AonMathUtils.isNotZero(amount)) {
				Account expAccount = det.getExpAccount();
				if (!isEmpty(expAccount)) {
					AccountEntryDetail detail = map.computeIfAbsent(expAccount.getId()
						, k -> new AccountEntryDetail()
							.setAccount(expAccount.getId())
							.setAccountCode(expAccount.getCode())
							.setAccountDescription(expAccount.getDescription())
							.setBalancingAccount(obtainRegistryAccount(invoice))
							.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
							.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice))
					);
					if (invoice.isSales()) {
						detail.addCredit( amount );
					} else {
						detail.addDebit( amount );
					}
				}
			}
		}
	};
}
