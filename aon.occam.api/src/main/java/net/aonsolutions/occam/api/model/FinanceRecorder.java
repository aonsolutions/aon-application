package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.AccountEntryType;
import net.aonsolutions.occam.api.model.type.FinanceType;

public class FinanceRecorder {
	
	private static final String CHAR = "Cobro";
	private static final String PAYM = "Pago";
	private static final String REFU = "Abono";
	private static final String RETU = "Dev.";
	private static final String INVO = "Fra";

	private enum FinanceEntryDetailType implements Serializable {
		 // COBRO
		 COLLECTION() {
			@Override
			public void accept(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (finance.getFinanceType() == FinanceType.COLLECTION && entryType != AccountEntryType.RETURNED_COLLECTION ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
//						String code = obtainAccountCode(finance);
//						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccount().orElse(null))
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount())
							;
						map.put(finance.getId(),detail);
					}
					detail.setCredit(finance.getAmount());
				}
			}

	 	}
		 // DEVOLUCION DE PAGO
		,RETURNED_COLLECTION() {
			@Override
			public void accept(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (finance.getFinanceType() == FinanceType.COLLECTION && entryType == AccountEntryType.RETURNED_COLLECTION ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
//						String code = obtainAccountCode(finance);
//						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccount().orElse(null))
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount())
							;
						map.put(finance.getId(),detail);
					}
					detail.setCredit(finance.getAmount());
				}
			}
	 	}
		 // PAGO
		,PAYMENT() {
			@Override
			public void accept(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (finance.getFinanceType() == FinanceType.PAYMENT && entryType != AccountEntryType.RETURNED_PAYMENT ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
//						String code = obtainAccountCode(finance); 
//						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccount().orElse(null))
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount())
							;
						map.put(finance.getId(),detail);
					}
					detail.setDebit(finance.getAmount());
				}
			}
		}
		 // DEVOLUCION DE PAGO
		,RETURNED_PAYMENT() {
			@Override
			public void accept(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				AccountEntryType entryType = financeEntry.getAccountEntry().getEntryType();
				if (finance.getFinanceType() == FinanceType.PAYMENT && financeEntry.getAccountEntry().getEntryType() == AccountEntryType.RETURNED_PAYMENT ) {
					AccountEntryDetail detail = map.get(finance.getId());
					if (detail == null) {
						String concept = obtainConcept(finance,entryType,financeEntry.getManualConcept());
//						String code = obtainAccountCode(finance); 
//						String description = obtainAccountDescription(finance);
						detail = new AccountEntryDetail()
							.setAccount(finance.getRegistryAccount().orElse(null))
							.setConcept(concept)
							.setDocumentNumber(obtainDocumentNumber(finance))
							.setBalancingAccount(financeEntry.getBankAccount())
						;
						map.put(finance.getId(),detail);
					}
					detail.setCredit(finance.getAmount());
				}
			}
	 	}
		;

		private FinanceEntryDetailType() {
		}
		abstract void accept( FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map);
		
		private static void visit(FinanceEntry financeEntry, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map ) {
			for (FinanceEntryDetailType t : FinanceEntryDetailType.values()) {
				t.accept(financeEntry, finance, map);
			}
		}
	}

	private static String obtainDocumentNumber(Finance finance) {
		return  finance.getInvoice()
			.map(i -> i.getDocumentNumber())
			.orElse("");
	}
	
	private static String obtainConcept(Finance finance, AccountEntryType entryType,String manualConcept) {
		String prefix = null;
		if (finance.getFinanceType() == FinanceType.COLLECTION) {
			prefix = (finance.getAmount() < 0 || entryType == AccountEntryType.RETURNED_COLLECTION ) ? REFU: CHAR;
		} else {
			prefix = (finance.getAmount() < 0 || entryType == AccountEntryType.RETURNED_PAYMENT) ? RETU : PAYM;
		}
		String pref = prefix + " " + INVO + ": ";
		String concept = finance.getInvoice()
			.map( i -> pref + i.getReferenceCode())
			.orElseGet( finance::getConcept);
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
	
//	private static String obtainAccountCode(Finance finance) {
//		String code = finance.getRegistryAccount().map(Account::getCode).orElse(null);
//		if (AonStringUtils.isBlank(code)) {
//			if (finance.getFinanceType() == FinanceType.COLLECTION) {
//				code = "430?????";
//			} else {
//				code = finance.getInvoice()
//					.map(i -> i.getType() == InvoiceType.PURCHASE ?"400?????":"410?????")
//					.orElse("410?????");
//			}
//		}		
//		return code;
//	}
//	private static String obtainAccountDescription(Finance finance) {
//		String description = finance.getRegistryAccount().map(Account::getDescription).orElse(null);
//		if (AonStringUtils.isBlank(description)) description = finance.getRegistryName();
//		return description;
//	}

	public static AccountEntry[] recordFinanceEntry(FinanceEntry financeEntry) {
		AccountEntry ae = AccountEntry.clone(financeEntry.getAccountEntry());
		Finance uniqueFinance = null;
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<>();
		int validFinances = 0;
		for (FinanceTracking tracking : financeEntry.getTrackings().values()) {
			if (!tracking.isDeleted()) {
				FinanceEntryDetailType.visit(financeEntry,tracking.getFinance(),map);
				++validFinances;
				uniqueFinance = (validFinances == 1)?tracking.getFinance():null;
			}
		}
		
		
		AonCollectionUtils.stream(map.values())
			.forEach(aed -> ae.addDetail(aed));
		
		if ( financeEntry.getExpenses() != 0.0
			&& financeEntry.getExpensesAccount() != null 
			&& financeEntry.getExpensesAccount().getId() != null) {
			String concept = AonStringUtils.defaultIfBlank(financeEntry.getManualConcept(),"GASTOS");
			if (uniqueFinance != null) {
				concept = obtainConcept(uniqueFinance,ae.getEntryType(),financeEntry.getManualConcept());
			} 
			AccountEntryDetail detail = new AccountEntryDetail()
					.setAccount(financeEntry.getExpensesAccount())
					.setConcept(concept)
					.setBalancingAccount(financeEntry.getBankAccount())
			;
			detail.addDebit( financeEntry.getExpenses()  );
			ae.addDetail(detail);
		}
		if ( financeEntry.getBankAccount() != null && financeEntry.getBankAccount().getId() != null) {
			String concept = AonStringUtils.defaultIfBlank(financeEntry.getManualConcept(),"Apunte Tesorer\u00EDa");
			Account balancingAccount = null;
			String documentNumber = null;
			if (uniqueFinance != null) {
				documentNumber = obtainDocumentNumber(uniqueFinance);
				concept = obtainConcept(uniqueFinance,ae.getEntryType(),financeEntry.getManualConcept());
				balancingAccount = uniqueFinance.getRegistryAccount().orElse(null);
			}
			AccountEntryDetail detail = new AccountEntryDetail()
				.setAccount(financeEntry.getBankAccount())
				.setConcept(concept)
				.setBalancingAccount(balancingAccount)
				.setDocumentNumber(documentNumber)
				;
			MutableDouble debit = new MutableDouble(0.0);
			MutableDouble  credit = new MutableDouble(0.0);
			ae.detailStream()
				.forEach(d -> {
					debit.setValue( AonMathUtils.round(debit.getValue() + d.getDebit()));
					credit.setValue( AonMathUtils.round(credit.getValue() + d.getCredit()));
				});
			detail.setDebit( AonMathUtils.round( credit.getValue()  - debit.getValue() ));
			ae.addDetail(detail);
		}
		
		return new AccountEntry[]{ae};
	}
	

}
