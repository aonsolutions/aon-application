package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
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
		 // COBRO
		 COLLECTION( new IFinanceVisitor() {
			@Override
			public void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (!finance.isPayment() && entryType != AccountEntryType.RETURNED_COLLECTION ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
						String code = obtainAccountCode(finance);
						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccountId())
							.setAccountCode(code)
							.setAccountDescription(description)
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getId())
							.setBalancingAccountCode(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getCode())
							.setBalancingAccountDescription(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getDescription())
							;
						map.put(finance.getId(),detail);
					}
					detail.setCredit(finance.getAmount());
				}
			}

	 	})
		 // DEVOLUCION DE PAGO
		,RETURNED_COLLECTION( new IFinanceVisitor() {
			@Override
			public void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (!finance.isPayment() && entryType == AccountEntryType.RETURNED_COLLECTION ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
						String code = obtainAccountCode(finance);
						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccountId())
							.setAccountCode(code)
							.setAccountDescription(description)
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getId())
							.setBalancingAccountCode(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getCode())
							.setBalancingAccountDescription(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getDescription())
							;
						map.put(finance.getId(),detail);
					}
					detail.setCredit(finance.getAmount());
				}
			}
	 	})
		 // PAGO
		,PAYMENT( new IFinanceVisitor() {
			@Override
			public void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (finance.isPayment() && entryType != AccountEntryType.RETURNED_PAYMENT ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
						String code = obtainAccountCode(finance); 
						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccountId())
							.setAccountCode(code)
							.setAccountDescription(description)
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getId())
							.setBalancingAccountCode(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getCode())
							.setBalancingAccountDescription(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getDescription());
						map.put(finance.getId(),detail);
					}
					detail.setDebit(finance.getAmount());
				}
			}
		})
		 // DEVOLUCION DE PAGO
		,RETURNED_PAYMENT( new IFinanceVisitor() {
			@Override
			public void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (finance.isPayment() && financeEntry.getAccountEntry().getEntryType() == AccountEntryType.RETURNED_PAYMENT ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
						String code = obtainAccountCode(finance); 
						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccountId())
							.setAccountCode(code)
							.setAccountDescription(description)
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getId())
							.setBalancingAccountCode(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getCode())
							.setBalancingAccountDescription(financeEntry.getBankAccount()==null?null:financeEntry.getBankAccount().getDescription());
						map.put(finance.getId(),detail);
					}
					detail.setCredit(finance.getAmount());
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

	private static String obtainDocumentNumber(Finance finance) {
		return finance.getInvoice()!=null?finance.getInvoice().getDocumentNumber():"";
	}
	private static String obtainConcept(Finance finance, AccountEntryType entryType,String manualConcept) {
		String prefix = null;
		if (!finance.isPayment()) {
			prefix = (finance.getAmount() < 0 || entryType == AccountEntryType.RETURNED_COLLECTION ) ? REFU: CHAR;
		} else {
			prefix = (finance.getAmount() < 0 || entryType == AccountEntryType.RETURNED_PAYMENT) ? RETU : PAYM;
		}
		prefix = prefix + " " + INVO + ": ";
		String concept = (!finance.isEmptyInvoice()) ? prefix + finance.getInvoice().getReferenceCode() : finance.getConcept();
		concept = AonStringUtils.abbreviate(concept, 32);
		if (AonStringUtils.isNotBlank(manualConcept) && (AonStringUtils.length(concept) < 30)) {
			concept = concept + " [" + manualConcept;
			if (AonStringUtils.length(concept) > 31) {
				concept = AonStringUtils.substring(concept, 0, 30);
			}
			concept = concept + "]";
		}
		return concept;
		
	}
	private static String obtainAccountCode(Finance finance) {
		String code = finance.getRegistryAccountCode();
		if (AonStringUtils.isBlank(code)) {
			if (!finance.isPayment()) {
				code = AccountingRegistryType.CUSTOMER.getAccountPrefix() + "?????";
			} else {
				if (!finance.isEmptyInvoice() && finance.getInvoice().getType() != null) {
					if (finance.getInvoice().getType() == InvoiceType.PURCHASE) {
						code = AccountingRegistryType.SUPPLIER.getAccountPrefix() + "?????";	
					} else {
						code = AccountingRegistryType.CREDITOR.getAccountPrefix() + "?????";
					}
				} else {
					code = AccountingRegistryType.CREDITOR.getAccountPrefix() + "?????";	
				}
			}
		}		
		return code;
	}
	private static String obtainAccountDescription(Finance finance) {
		String description = finance.getRegistryAccountDescription();
		if (AonStringUtils.isBlank(description)) description = finance.getRegistryName();
		return description;
	}

	public static AccountEntry[] recordFinanceEntry(FinanceEntry financeEntry) {
		AccountEntry ae = AccountEntry.clone(financeEntry.getAccountEntry());
		Finance uniqueFinance = null;
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<Integer, AccountEntryDetail>();
		int validFinances = 0;
		for (FinanceTracking tracking : financeEntry.getTrackings().values()) {
			if (!tracking.isDeleted()) {
				FinanceEntryDetailType.visit(financeEntry,tracking.getFinance(),map);
				++validFinances;
				uniqueFinance = (validFinances == 1)?tracking.getFinance():null;
			}
		}
		
		ae.setDetails(new LinkedList<AccountEntryDetail>());
		ae.getDetails().addAll(map.values());
		if ( financeEntry.getExpenses() != 0.0
			&& financeEntry.getExpensesAccount() != null 
			&& financeEntry.getExpensesAccount().getId() != null) {
			String concept = AonStringUtils.defaultIfBlank(financeEntry.getManualConcept(),"GASTOS");
			if (uniqueFinance != null) {
				concept = obtainConcept(uniqueFinance,ae.getEntryType(),financeEntry.getManualConcept());
			} 
			AccountEntryDetail detail = new AccountEntryDetail()
					.setAccount(financeEntry.getExpensesAccount().getId())
					.setAccountCode(financeEntry.getExpensesAccount().getCode())
					.setAccountDescription(financeEntry.getExpensesAccount().getDescription())
					.setConcept(concept)
					.setBalancingAccount(financeEntry.getBankAccount().getId())
					.setBalancingAccountCode(financeEntry.getBankAccount().getCode())
					.setBalancingAccountDescription(financeEntry.getBankAccount().getDescription());
			detail.addDebit( financeEntry.getExpenses()  );
			ae.getDetails().add(detail);
		}
		if ( financeEntry.getBankAccount() != null && financeEntry.getBankAccount().getId() != null) {
			String concept = AonStringUtils.defaultIfBlank(financeEntry.getManualConcept(),"Apunte Tesorer\u00EDa");
			Integer balancingAccount = null;
			String balancingAccountCode = null;
			String balancingAccountDescription = null;
			String documentNumber = null;
			if (uniqueFinance != null) {
				documentNumber = obtainDocumentNumber(uniqueFinance);
				concept = obtainConcept(uniqueFinance,ae.getEntryType(),financeEntry.getManualConcept());
				balancingAccount = uniqueFinance.getRegistryAccountId();
				balancingAccountCode = obtainAccountCode(uniqueFinance); 
				balancingAccountDescription = obtainAccountDescription(uniqueFinance);
			}
			AccountEntryDetail detail = new AccountEntryDetail()
				.setAccount(financeEntry.getBankAccount().getId())
				.setAccountCode(financeEntry.getBankAccount().getCode())
				.setAccountDescription(financeEntry.getBankAccount().getDescription())
				.setConcept(concept)
				.setBalancingAccount(balancingAccount)
				.setBalancingAccountCode(balancingAccountCode)
				.setBalancingAccountDescription(balancingAccountDescription)
				.setDocumentNumber(documentNumber)
				;
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
