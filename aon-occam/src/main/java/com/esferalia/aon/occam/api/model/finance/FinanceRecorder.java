package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceRecorder {
	
	private static final String CHAR = "Cobro";
	private static final String PAYM = "Pago";
	private static final String REFU = "Abono";
	private static final String RETU = "Dev.";
	private static final String INVO = "Fra";

	private static interface IFinanceVisitor {
		void visit( FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map);
	}
	private static enum FinanceEntryDetailType implements Serializable {
		 COLLECTION( new IFinanceVisitor() {
			@Override
			public void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (!finance.isPayment()) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String document = finance.getInvoice()!=null?finance.getInvoice().getDocumentNumber():"";
						String code = finance.getRegistryAccountCode();
						
						String prefix = (finance.getAmount() < 0) ? REFU: CHAR;
						prefix = prefix + " " + INVO + ": ";
						String concept = (!finance.isEmptyInvoice()) ? prefix + finance.getInvoice().getReferenceCode() : finance.getConcept();								
						
						if (AonStringUtils.isBlank(code)) code = AccountingRegistryType.CUSTOMER.getAccountPrefix() + "?????";
						String description = finance.getRegistryAccountDescription();
						if (AonStringUtils.isBlank(description)) description = finance.getRegistryName();
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccountId())
							.setAccountCode(code)
							.setAccountDescription(description)
							.setConcept(concept)
							.setDocumentNumber(document)
							.setBalancingAccount(financeEntry.getBankAccount().getId())
							.setBalancingAccountCode(financeEntry.getBankAccount().getCode())
							.setBalancingAccountDescription(financeEntry.getBankAccount().getDescription())
							;
						map.put(finance.getId(),detail);
					}
					detail.setCredit(finance.getAmount());
				}
			}
	 	})
		,PAYMENT( new IFinanceVisitor() {
			@Override
			public void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (finance.isPayment()) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String prefix = (finance.getAmount() < 0) ? RETU : PAYM;
						prefix = prefix + " " + INVO + ": ";
						String concept = (!finance.isEmptyInvoice()) ? prefix + finance.getInvoice().getReferenceCode() : finance.getConcept();								
						String code = AonStringUtils.defaultIfBlank(finance.getRegistryAccountCode(),AccountingRegistryType.CUSTOMER.getAccountPrefix() + "?????");
						String description = AonStringUtils.defaultIfBlank(finance.getRegistryAccountDescription(), finance.getRegistryName());
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccountId())
							.setAccountCode(code)
							.setAccountDescription(description)
							.setConcept(concept)
							.setDocumentNumber(finance.getInvoice()!=null?finance.getInvoice().getDocumentNumber():"")
							.setBalancingAccount(financeEntry.getBankAccount().getId())
							.setBalancingAccountCode(financeEntry.getBankAccount().getCode())
							.setBalancingAccountDescription(financeEntry.getBankAccount().getDescription());
						map.put(finance.getId(),detail);
					}
					detail.setDebit(finance.getAmount());
				}
			}
	 	})
	 
		;
		private IFinanceVisitor visitor;
		private FinanceEntryDetailType(IFinanceVisitor visitor) {
			this.visitor = visitor;
		}
		
		private static void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map ) {
			for (FinanceEntryDetailType t : FinanceEntryDetailType.values()) {
				t.visitor.visit(financeEntry, finance, map);
			}
		}
	}

	public static AccountEntry[] recordFinanceEntry(FinanceEntry financeEntry) {
		AccountEntry ae = AccountEntry.clone(financeEntry.getAccountEntry());
		
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<Integer, AccountEntryDetail>();
		for (Finance finance: financeEntry.getFinances().values()) {
			FinanceEntryDetailType.visit(financeEntry,finance,map);	
		}
		ae.setDetails(new LinkedList<AccountEntryDetail>());
		ae.getDetails().addAll(map.values());
		if ( financeEntry.getExpenses() != 0.0
			&& financeEntry.getExpensesAccount() != null 
			&& financeEntry.getExpensesAccount().getId() != null) {
			AccountEntryDetail detail = new AccountEntryDetail()
					.setAccount(financeEntry.getExpensesAccount().getId())
					.setAccountCode(financeEntry.getExpensesAccount().getCode())
					.setAccountDescription(financeEntry.getExpensesAccount().getDescription())
					.setConcept("GASTOS") // TODO
					.setBalancingAccount(financeEntry.getBankAccount().getId())
					.setBalancingAccountCode(financeEntry.getBankAccount().getCode())
					.setBalancingAccountDescription(financeEntry.getBankAccount().getDescription());
			detail.addDebit( financeEntry.getExpenses()  );
			ae.getDetails().add(detail);
		}
		if ( financeEntry.getBankAccount() != null && financeEntry.getBankAccount().getId() != null) {
			AccountEntryDetail detail = new AccountEntryDetail()
				.setAccount(financeEntry.getBankAccount().getId())
				.setAccountCode(financeEntry.getBankAccount().getCode())
				.setConcept("Apunte Tesorer\u00EDa") // TODO
				.setAccountDescription(financeEntry.getBankAccount().getDescription());
			double debit = 0.0;
			double credit = 0.0;
			for ( AccountEntryDetail d : ae.getDetails() ) {
				debit = AonMathUtils.round(debit + d.getDebit());
				credit = AonMathUtils.round(credit + d.getCredit());
			}
			detail.setDebit( AonMathUtils.round( credit  - debit ));
			ae.getDetails().add(detail);
		}
		
		return new AccountEntry[]{ae};
	}
	

}
