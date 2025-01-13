package net.aonsolutions.occam.impl.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.api.model.AccountEntryDetail;
import net.aonsolutions.occam.api.model.AccountPeriod;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceDetail;
import net.aonsolutions.occam.api.model.InvoiceTax;
import net.aonsolutions.occam.api.model.InvoiceWithholding;
import net.aonsolutions.occam.api.model.type.AccountEntryType;
import net.aonsolutions.occam.api.model.type.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.impl.AONContext;

public class InvoiceRecorder {

	private InvoiceRecorder() {
	}
	
	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";

	private static record InvoiceRecorderContext( AONContext ctx, int domain, Invoice invoice, Map<Account,AccountEntryDetail> map) {}
	
	public static AccountEntry getInvoiceEntry(AONContext ctx, int domain, Invoice invoice) {
		ensureInvoiceAccounts( ctx, domain, invoice); 
		
		AccountEntry ae = fillAccountEntry( ctx, invoice);
		Map<Account,AccountEntryDetail> map = new LinkedHashMap<>();
		REGISTRY
			.andThen(INPUT_VAT)
			.andThen(OUTPUT_VAT)
			.andThen(VAT_NEGATIVE_ADJUST)
			.andThen(DIRECT_TAX_ADJUST)
			.andThen(WITHHOLDING)
			.andThen(EXP_ACCOUNT)
			.andThen(PREPAYMENTS)
		.accept(new InvoiceRecorderContext(ctx, domain, invoice, map));
		AonCollectionUtils.stream(map.values()).forEach(ae::addDetail);
		String concept = obtainConcept(invoice);
		ae.detailStream()
			.forEach( d ->
				d.setConcept(concept)
				 .setDocumentNumber(invoice.getDocumentNumber()));
		return ae;
	}

	private static void ensureInvoiceAccounts(AONContext ctx, int domain, Invoice invoice) {
		if ( invoice.isInputVatEnabled() || invoice.isOutputVatEnabled()) {
			invoice.detailStream()
				.flatMap( d -> d.taxStream() )
				.filter( it -> it.hasVatQuota())
				.filter( it -> 
							(invoice.isInputVatEnabled()  && it.getInputAccount().isEmpty())
						 || (invoice.isOutputVatEnabled() && it.getOutputAccount().isEmpty())
					   )
				.forEach( it -> {
					if (invoice.isInputVatEnabled() && it.getInputAccount().isEmpty()) {
						it.setInputAccount(ctx.getApplicationParameters(domain).getInputVatDefaultAccount().orElse(null)); 
					}
					if ( invoice.isOutputVatEnabled() && it.getOutputAccount().isEmpty()) {
						it.setOutputAccount(ctx.getApplicationParameters(domain).getOutputVatDefaultAccount().orElse(null));
					}
				});
		}
	}

	// *********************************************************
	// ******************************************* [PRIVATE] ***
	// *********************************************************
	
	private static class AccountPrefix implements InvoiceTypeVisitor<String> {
		@Override public String visitSales() 		{return "4300";}
		@Override public String visitPurchase() 	{return "4000";}
		@Override public String visitExpenses() 	{return "4100";}
		@Override public String visitUndeductible() {return visitExpenses();}
	}
	
	private static AccountEntry fillAccountEntry(AONContext ctx, Invoice invoice) {
		if (invoice == null) throw new AonCoreException("La factura no puede ser NULL");
		if (invoice.getType() == null) throw new AonCoreException("La factura debe tener tipo");
		if (invoice.getIssueDate() == null) throw new AonCoreException("La factura debe tener fecha");
		if (invoice.getDomain() == null) throw new AonCoreException("La factura debe tener dominio");
		if (invoice.getRegistry() == null) throw new AonCoreException("La factura debe tener titular");
		if (AonNumberUtils.equals(invoice.getDomain(), 0)) throw new AonCoreException("La factura debe tener dominio");
		
		AccountPeriod period = AccountPeriodHandler.ensurePeriod(ctx, invoice.getDomain(), invoice.getIssueDate());
		AccountEntry accountEntry = new AccountEntry()
			.setPeriod(period)
			.setDomain(invoice.getDomain())
			.setConfidential(false)
			.setEntryDate(invoice.getIssueDate())
			.setActivity(ctx.getDefaultActivity(invoice.getDomain()).orElse(null))
			.setComments(invoice.getHeader().getComments())
			.markAsClean();
		return invoice.getType().visit(new InvoiceTypeVisitor<AccountEntry>() {
			@Override public AccountEntry visitUndeductible() {return accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);}
			@Override public AccountEntry visitSales() {return accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);}
			@Override public AccountEntry visitPurchase() {return accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);}
			@Override public AccountEntry visitExpenses() {return accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);}
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
	
	private static void fillBalancingAccount(AccountEntryDetail detail, Invoice invoice) {
		Map<Account,Long> accounts = invoice.detailStream()
			.map(d -> d.getExpAccount().orElse(null))
			.filter( a -> a != null)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		Account account = null;
		if (accounts.size() == 1) {
			account = AonCollectionUtils.stream(accounts.keySet()).findFirst().orElse(null);
		}
		detail.setBalancingAccount(account);
	}
	
	private static final Consumer<InvoiceRecorderContext> REGISTRY = irc -> {
		Account registryAccount = irc.invoice
			.getRegistryAccount()
			.orElse(
				new Account()
					.setCode(irc.invoice.getType().visit(new AccountPrefix()))
					.setDescription(irc.invoice.getRegistryName())
			);
		double amount = irc.invoice.getTotal(); 
		AccountEntryDetail detail = irc.map.get(registryAccount);
		if (detail == null) {
			detail = new AccountEntryDetail()
				.setAccount(registryAccount);
			fillBalancingAccount(detail,irc.invoice);
			irc.map.put(registryAccount,detail);
		}
		if (irc.invoice.isSales()) {
			detail.addDebit( amount );
		} else {
			detail.addCredit( amount );
		}
	};
	
	private static final Consumer<InvoiceRecorderContext> INPUT_VAT = irc -> {
		if (irc.invoice.isInputVatEnabled()) {
			irc.invoice.detailStream()
				.flatMap( d -> d
					.taxStream()
					.filter( it -> it.hasVatQuota() )
					.map( it -> new Pair<InvoiceDetail,InvoiceTax>(d,it)  )
				)
				.forEach( p -> {
					InvoiceDetail det = p.getLeft();
					InvoiceTax it = p.getRight();
					Account inputAccount = it
						.getInputAccount()
						.orElse(det
							.getExpAccount()
							.orElse(null)
					);
					if (inputAccount != null) {
						irc.map.computeIfAbsent(inputAccount
							, k -> new AccountEntryDetail()
								.setAccount(inputAccount)
								.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null)))
						.addDebit( it.getVatFullQuota() );
					}
				});
		}
	};
	
	private static final Consumer<InvoiceRecorderContext> OUTPUT_VAT = irc -> {
		if (irc.invoice.isOutputVatEnabled()) {
			irc.invoice.detailStream()
			.flatMap( d -> d.taxStream() )
			.filter( it -> it.hasVatQuota())
			.forEach( it -> it.getOutputAccount()
				.ifPresent( oa -> irc.map.computeIfAbsent(oa
					, k -> new AccountEntryDetail()
						.setAccount(oa)
						.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null)))
				.addCredit( it.getVatFullQuota() )));
		}
	};

	private static final Consumer<InvoiceRecorderContext> VAT_NEGATIVE_ADJUST = irc -> {
		if (!irc.invoice.isSales() 
			&& !irc.invoice.isSurcharge() 
			&& irc.invoice.isOutputVatEnabled() != irc.invoice.isInputVatEnabled()) {
			irc.invoice.detailStream()
			.flatMap( d -> d
				.taxStream()
				.filter( it -> it.hasVatQuota() )
				.map( it -> new Pair<InvoiceDetail,InvoiceTax>(d,it)  )
			)
			.filter( p -> AonNumberUtils.notEquals(p.getRight().getDeductibleQuota(), p.getRight().getQuota()) )
			.forEach( p -> {
				InvoiceDetail det = p.getLeft();
				InvoiceTax it = p.getRight();
				Account adjAccount = it
						.getAdjAccount()
						.orElse(det
							.getExpAccount()
							.orElse(null)
				);
				if (adjAccount != null) {
					AccountEntryDetail detail = irc.map.computeIfAbsent(adjAccount
						, k -> new AccountEntryDetail()
							.setAccount(adjAccount)
							.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null))
					);
					if (irc.invoice.isOutputVatEnabled()) {
						detail.addCredit( it.getVatNegativeAdjust() );
					} else {
						detail.addDebit( it.getVatNegativeAdjust() );
					}
				}
			});
		}
	};
		
	private static final Consumer<InvoiceRecorderContext> DIRECT_TAX_ADJUST = irc -> {
		if (!irc.invoice.isSales() 
			&& !irc.invoice.isSurcharge() 
			&& irc.invoice.isOutputVatEnabled() != irc.invoice.isInputVatEnabled()) {
			irc.invoice.detailStream()
			.filter(det -> det.getInvestAsset().isPresent())
			.filter(det -> AonMathUtils.notEquals(100.0, det.getDirectTaxPercent()))
			.forEach( det -> 
				det.getAdjDirectTaxAccount()
					.ifPresent( adjDirectTaxAccount -> {
						AccountEntryDetail detail = irc.map.computeIfAbsent(adjDirectTaxAccount
							, k -> new AccountEntryDetail()
								.setAccount(adjDirectTaxAccount)
								.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null))
						);
						if (irc.invoice.isOutputVatEnabled()) {
							detail.addCredit( det.getDirectTaxNoDedExpenses() );
						} else {
							detail.addDebit( det.getDirectTaxNoDedExpenses() );
						}
						
						// Se resta la cantidad no deducible de la cuenta de gastos
						det.getExpAccount().ifPresent( expAccount -> {
							AccountEntryDetail expDetail = irc.map.computeIfAbsent(expAccount
								, k -> new AccountEntryDetail()
									.setAccount(expAccount)
									.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null))
							);
							if (irc.invoice.isOutputVatEnabled()) {
								expDetail.addDebit( det.getDirectTaxNoDedExpenses() );
							} else {
								expDetail.addCredit( det.getDirectTaxNoDedExpenses() );
							}
						});
					}
				)
			);
		}
	};
	
	private static final Consumer<InvoiceRecorderContext> WITHHOLDING = irc -> {
		if (irc.invoice.isWithholding() && irc.invoice.getWithholding().isPresent()) {
			InvoiceWithholding withholding = irc.invoice.getWithholding().get();
			
			withholding.getAccount()
				.or( () -> getWitholdingCreditorDefaultAccount(irc) )
				.ifPresent(withholdingAccount -> {
				if (AonMathUtils.isNotZero(withholding.getQuota())) {
					AccountEntryDetail detail = irc.map.computeIfAbsent( withholdingAccount
						, k -> new AccountEntryDetail()
							.setAccount(withholdingAccount)
							.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null))
					);
					if (irc.invoice.isSales()) {
						detail.addDebit( withholding.getQuota() );
					} else {
						detail.addCredit( withholding.getQuota() );
					}
				}
				
			});
			
		}
	};
	
	private static final Consumer<InvoiceRecorderContext> EXP_ACCOUNT = irc -> {
		// Se obtiene un mapa de las cuentas de explotación utilizadas en la factura.
		LinkedHashMap<Account, Double> bases = fillBasesPerAccountFromInvoiceDetail(irc);
		AonCollectionUtils.stream(bases.entrySet())
			.forEach( e -> {
				AccountEntryDetail detail = irc.map.computeIfAbsent(e.getKey(), k -> new AccountEntryDetail()
						.setAccount(k)
						.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null)));	
				if (irc.invoice.isSales()) {
					detail.addCredit( e.getValue() );
				} else {
					detail.addDebit( e.getValue() );
				}
			});
	};
	
	private static final Consumer<InvoiceRecorderContext> PREPAYMENTS = irc -> {
		LinkedHashMap<Account, Double> bases = fillPrePaymentBasesFromInvoiceDetail(irc);
		AonCollectionUtils.stream(bases.entrySet())
			.forEach( e -> {
				AccountEntryDetail detail = irc.map.computeIfAbsent(e.getKey(), k -> new AccountEntryDetail()
						.setAccount(k)
						.setBalancingAccount(irc.invoice.getRegistryAccount().orElse(null)));	
				if (irc.invoice.isSales()) {
					detail.addCredit( e.getValue() );
				} else {
					detail.addDebit( e.getValue() );
				}
			});
	};
	
	private static LinkedHashMap<Account, Double> fillBasesPerAccountFromInvoiceDetail(InvoiceRecorderContext irc) {
		LinkedHashMap<Account, Double> bases = new LinkedHashMap<>();
		irc.invoice.detailStream()
			.filter( det -> !det.isPrepayment())
			.forEach(det -> det.getExpAccount()
				.or( () -> getItemAccount(irc,det) )
				.or( () -> getSalesAccount(irc,det) )
				.or( () -> getPurchaseAccount(irc,det) )
				.ifPresent( a -> bases.merge(a, det.getTaxableBase(), (oldVal, newVal) -> AonMathUtils.round(oldVal + newVal, 4)))
			);
		roundLast( bases );
		return bases;
	}

	private static LinkedHashMap<Account, Double> fillPrePaymentBasesFromInvoiceDetail(InvoiceRecorderContext irc) {
		LinkedHashMap<Account, Double> bases = new LinkedHashMap<>();
		irc.invoice.detailStream()
			.filter( det -> det.isPrepayment())
			.forEach(det -> getPrepaymentAccount(irc,det) 
				.ifPresent( a -> bases.merge(a, det.getTaxableBase(), (oldVal, newVal) -> AonMathUtils.round(oldVal + newVal, 4)))
			);
		roundLast( bases );
		return bases;
	}

	private static void roundLast(LinkedHashMap<Account, Double> bases) {
		double mainValue = AonMathUtils.round(AonCollectionUtils.stream(bases.values()).mapToDouble(d -> d).sum());
		double gap = AonCollectionUtils.stream( bases.entrySet())
			.map( e -> {e.setValue( AonNumberUtils.zeroIfNull(e.getValue())); return e;})
			.map( e -> {e.setValue( AonMathUtils.round(e.getValue())); return e;})
			.mapToDouble(e -> AonMathUtils.round(mainValue - e.getValue()))
			.sum()
		;
		if (AonMathUtils.isNotZero(gap)) {
			bases
				.keySet()
				.stream()
				.skip(bases.size() - 1)
				.findFirst()
				.ifPresent( a -> bases.merge(a, gap, (oldVal, newVal) -> AonMathUtils.round(oldVal + newVal)));
		}
	}

	private static Optional<Account> getItemAccount(InvoiceRecorderContext irc, InvoiceDetail det) {
//		if (account == null && invoiceDetail.getItem().getProduct().getType() == ProductType.EXPENSE) {
//			throw new AonCoreException("El gasto \"" + invoiceDetail.getDescription() + "\" no tiene cuenta contable asociada.");
//		}
		return Optional.empty();
	}
	private static Optional<Account> getPrepaymentAccount(InvoiceRecorderContext irc, InvoiceDetail det) {
		return (det.isPrepayment())
			?irc.ctx.getApplicationParameters(irc.domain).getPrepaymentDefaultAccount()
			:Optional.empty();
	}
	private static Optional<Account> getWitholdingCreditorDefaultAccount(InvoiceRecorderContext irc) {
		return (!irc.invoice.isSales())
			?irc.ctx.getApplicationParameters(irc.domain).getWitholdingCreditorDefaultAccount()
			:Optional.empty();		
	}
	private static Optional<Account> getSalesAccount(InvoiceRecorderContext irc, InvoiceDetail det) {
		return (irc.invoice.isSales())
			?irc.ctx.getApplicationParameters(irc.domain).getSalesDefaultAccount()
			:Optional.empty();		
	}
	private static Optional<Account> getPurchaseAccount(InvoiceRecorderContext irc, InvoiceDetail det) {
		return (irc.invoice.isPurchase())
			?irc.ctx.getApplicationParameters(irc.domain).getPurchaseDefaultAccount()
			:Optional.empty();
	}

}
