package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRecorder {

	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";
	
	private static interface IVisitor {
		void visit( AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map);
	}
	private static interface IFinanceVisitor {
		void visit( AccountingInvoice invoice, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map);
	}
	private static enum FinanceEntryDetailType implements Serializable {
		 CUSTOMER( new IFinanceVisitor() {
			@Override
			public void visit(AccountingInvoice invoice, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry().getType() == AccountingRegistryType.CUSTOMER) {
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						String code = invoice.getRegistry().getAccountCode();
						if (AonStringUtils.isBlank(code)) code = AccountingRegistryType.CUSTOMER.getAccountPrefix() + "?????";
						String description = invoice.getRegistry().getAccountDescription();
						if (AonStringUtils.isBlank(description)) description = invoice.getRegistry().getName();
						detail = new AccountEntryDetail()
							.setAccount(invoice.getRegistry().getAccountId())
							.setAccountCode(code)
							.setAccountDescription(description);
						map.put(invoice.getRegistry().getAccountId(),detail);
					}
					detail.setCredit(invoice.getTotalInvoice());
					
					AccountEntryDetail payDetail = map.get(finance.getPayAccountId());
					if (payDetail == null) {
						String description = finance.getPayAccountDescription();
						if (AonStringUtils.isBlank(description)) description = "COBRO FACTURA";
						payDetail = new AccountEntryDetail()
							.setAccount(finance.getPayAccountId())
							.setAccountCode(finance.getPayAccountCode())
							.setAccountDescription(description);
						map.put(finance.getPayAccountId(),payDetail);
					}
					payDetail.setDebit(invoice.getTotalInvoice());
				}
			}
	 	})
		,SUPPLIER_CREDITOR( new IFinanceVisitor() {
			@Override
			public void visit(AccountingInvoice invoice, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry().getType() == AccountingRegistryType.SUPPLIER 
					||invoice.getRegistry().getType() == AccountingRegistryType.CREDITOR) {
					
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						String code = invoice.getRegistry().getAccountCode();
						if (AonStringUtils.isBlank(code)) code = invoice.getRegistry().getType().getAccountPrefix() + "?????";
						String description = invoice.getRegistry().getAccountDescription();
						if (AonStringUtils.isBlank(description)) description = invoice.getRegistry().getName();
						detail = new AccountEntryDetail()
							.setAccount(invoice.getRegistry().getAccountId())
							.setAccountCode(code)
							.setAccountDescription(description);
						map.put(invoice.getRegistry().getAccountId(),detail);
					}
					detail.setDebit(invoice.getTotalInvoice());
					
					AccountEntryDetail payDetail = map.get(finance.getPayAccountId());
					if (payDetail == null) {
						String description = finance.getPayAccountDescription();
						if (AonStringUtils.isBlank(description)) description = "PAGO FACTURA";
						payDetail = new AccountEntryDetail()
							.setAccount(finance.getPayAccountId())
							.setAccountCode(finance.getPayAccountCode())
							.setAccountDescription(description);
						map.put(finance.getPayAccountId(),payDetail);
					}
					payDetail.setCredit(invoice.getTotalInvoice());
					
				}
			}
	 	})
	 
		;
		private IFinanceVisitor visitor;
		private FinanceEntryDetailType(IFinanceVisitor visitor) {
			this.visitor = visitor;
		}
		
		private static void visit(AccountingInvoice invoice, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map ) {
			for (FinanceEntryDetailType t : FinanceEntryDetailType.values()) {
				t.visitor.visit(invoice, finance, map);
			}
		}
	}

	private static enum InvoiceEntryDetailType implements Serializable {
		 CUSTOMER( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry().getType() == AccountingRegistryType.CUSTOMER) {
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						String code = invoice.getRegistry().getAccountCode();
						if (AonStringUtils.isBlank(code)) code = AccountingRegistryType.CUSTOMER.getAccountPrefix() + "?????";
						String description = invoice.getRegistry().getAccountDescription();
						if (AonStringUtils.isBlank(description)) description = invoice.getRegistry().getName();
						detail = new AccountEntryDetail()
							.setAccount(invoice.getRegistry().getAccountId())
							.setAccountCode(code)
							.setAccountDescription(description);
						map.put(invoice.getRegistry().getAccountId(),detail);
					}
					detail.setDebit(invoice.getTotalInvoice());
				}
			}
	 	})
		,SUPPLIER_CREDITOR( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry().getType() == AccountingRegistryType.SUPPLIER 
					||invoice.getRegistry().getType() == AccountingRegistryType.CREDITOR) {
					
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						String code = invoice.getRegistry().getAccountCode();
						if (AonStringUtils.isBlank(code)) code = invoice.getRegistry().getType().getAccountPrefix() + "?????";
						String description = invoice.getRegistry().getAccountDescription();
						if (AonStringUtils.isBlank(description)) description = invoice.getRegistry().getName();
						detail = new AccountEntryDetail()
							.setAccount(invoice.getRegistry().getAccountId())
							.setAccountCode(code)
							.setAccountDescription(description);
						map.put(invoice.getRegistry().getAccountId(),detail);
					}
					detail.setCredit(invoice.getTotalInvoice());
				}
			}
	 	})
		,INPUT_VAT( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getVats() != null && invoice.isInputVatEnabled()) {
					for (InvoiceVAT vat : invoice.getVats()) {
						if (vat.getInputAccountId() != null) {
							AccountEntryDetail detail = map.get(vat.getInputAccountId());
							if (detail == null) {
								detail = new AccountEntryDetail()
									.setAccount(vat.getInputAccountId())
									.setAccountCode(vat.getInputAccountCode())
									.setAccountDescription(vat.getInputAccountDescription());
								map.put(vat.getInputAccountId(),detail);
							}
							detail.addDebit( vat.getDeductibleQuota() + vat.getSurchargeQuota() );
						}
					}
				}
			}
	 	})
		,OUTPUT_VAT( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getVats() != null && invoice.isOutputVatEnabled()) {
					for (InvoiceVAT vat : invoice.getVats()) {
						if (vat.getOutputAccountId() != null) {
							AccountEntryDetail detail = map.get(vat.getOutputAccountId());
							if (detail == null) {
								detail = new AccountEntryDetail()
										.setAccount(vat.getOutputAccountId())
										.setAccountCode(vat.getOutputAccountCode())
										.setAccountDescription(vat.getOutputAccountDescription());
								map.put(vat.getOutputAccountId(),detail);
							}
							detail.addCredit( vat.getDeductibleQuota() + vat.getSurchargeQuota() );
						}
					}
				}
			}
	 	})
		,VAT_NEGATIVE_ADJUST( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (!invoice.isSales() 
					&& !invoice.isSurcharge() 
					&& invoice.isOutputVatEnabled() != invoice.isInputVatEnabled()
					&& invoice.getVats() != null) {
					for (InvoiceVAT vat : invoice.getVats()) {
						if (vat.getAdjAccountId() != null && vat.getDeductibleQuota() !=  vat.getQuota()) {
							AccountEntryDetail detail = map.get(vat.getAdjAccountId());
							if (detail == null) {
								detail = new AccountEntryDetail()
										.setAccount(vat.getAdjAccountId())
										.setAccountCode(vat.getAdjAccountCode())
										.setAccountDescription(vat.getAdjAccountDescription());
								map.put(vat.getAdjAccountId(),detail);
							}
							double amount = vat.getQuota() - vat.getDeductibleQuota();
							if (invoice.isOutputVatEnabled()) {
								detail.addCredit( amount );
							} else {
								detail.addDebit( amount );
							}
						}
					}
				}
			}
			 
	 	})
		
		,SALES_WITHHOLDING(new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.isSales() 
					&& invoice.isWithholding() 
					&& invoice.getWithholdingData() != null 
					&& invoice.getWithholdingData().getAccountId() != null 
					&& invoice.getWithholdingData().getQuota() !=  0.0) {
					
					AccountEntryDetail detail = map.get(invoice.getWithholdingData().getAccountId());
					if (detail == null) {
						detail = new AccountEntryDetail()
								.setAccount(invoice.getWithholdingData().getAccountId())
								.setAccountCode(invoice.getWithholdingData().getAccountCode())
								.setAccountDescription(invoice.getWithholdingData().getAccountDescription());
						map.put(invoice.getWithholdingData().getAccountId(),detail);
					}
					detail.addDebit( invoice.getWithholdingData().getQuota() );
				}
			}
			 
	 	})
		,NOT_SALES_WITHHOLDING(new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (!invoice.isSales() 
					&& invoice.isWithholding() 
					&& invoice.getWithholdingData() != null 
					&& invoice.getWithholdingData().getAccountId() != null 
					&& invoice.getWithholdingData().getQuota() !=  0.0) {
					
					AccountEntryDetail detail = map.get(invoice.getWithholdingData().getAccountId());
					if (detail == null) {
						detail = new AccountEntryDetail()
								.setAccount(invoice.getWithholdingData().getAccountId())
								.setAccountCode(invoice.getWithholdingData().getAccountCode())
								.setAccountDescription(invoice.getWithholdingData().getAccountDescription());
						map.put(invoice.getWithholdingData().getAccountId(),detail);
					}
					detail.addCredit( invoice.getWithholdingData().getQuota() );
				}
			}
	 	})
		,SALES( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.isSales() && invoice.getVats() != null) {
					for (InvoiceVAT vat : invoice.getVats()) {
						if (vat.getExpAccountId() != null) {
							AccountEntryDetail detail = map.get(vat.getExpAccountId());
							if (detail == null) {
								detail = new AccountEntryDetail()
									.setAccount(vat.getExpAccountId())
									.setAccountCode(vat.getExpAccountCode())
									.setAccountDescription(vat.getExpAccountDescription());
								map.put(vat.getExpAccountId(), detail);
							}
							detail.addCredit( vat.getBase() );		
						}
					}
				}
			}
			 
	 	})
		,PURCHASES_EXPENSES( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (!invoice.isSales() && invoice.getVats() != null) {
					for (InvoiceVAT vat : invoice.getVats()) {
						if (vat.getExpAccountId() != null) {
							AccountEntryDetail detail = map.get(vat.getExpAccountId());
							if (detail == null) {
								detail = new AccountEntryDetail()
									.setAccount(vat.getExpAccountId())
									.setAccountCode(vat.getExpAccountCode())
									.setAccountDescription(vat.getExpAccountDescription());
								map.put(vat.getExpAccountId(),detail);
							}
							detail.addDebit( vat.getBase() );
						}
					}
				}
			}
			 
	 	})
		;
		
		private IVisitor visitor;
		private InvoiceEntryDetailType(IVisitor visitor) {
			this.visitor = visitor;
		}
		
		private static void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map ) {
			for (InvoiceEntryDetailType t : InvoiceEntryDetailType.values()) {
				t.visitor.visit(invoice, map);
			}
		}
	}
	
	private static String obtainConcept(Invoice invoice) {
		String prefix = (invoice.isSales()) ? N_FRA : S_FRA;
		if (invoice.getTotal() < 0) {
			prefix += " " + ABONO;
		}
		prefix += ": ";
		return AonStringUtils.abbreviate(prefix + AonStringUtils.defaultIfBlank(invoice.getReferenceCode(),"????????"), 32);
	}
	
	public static AccountEntry getInvoiceEntry(AccountingInvoice invoice) {
		AccountEntry ae = AccountEntry.clone(invoice.getAccountEntry());
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<Integer, AccountEntryDetail>();
		InvoiceEntryDetailType.visit(invoice,map);	
		ae.setDetails(new LinkedList<AccountEntryDetail>());
		ae.getDetails().addAll(map.values());
		String concept = obtainConcept(invoice.getInvoice());
		for (AccountEntryDetail detail : ae.getDetails()) {
			detail.setConcept(concept)
				.setConcept( AonStringUtils.abbreviate(detail.getConcept(), 32 ))
				.setDocumentNumber(invoice.getInvoice().getDocumentNumber());
		}
		return ae;
	}
	
	public static AccountEntry getFinanceEntry(AccountingInvoice invoice, Finance finance) {
		AccountEntry ae = AccountEntry.clone(invoice.getAccountEntry());
		AccountEntry payEntry = AccountEntry
				.clone(ae)
				.setEntryDate(finance.getDueDate())
				.setEntryType(AccountEntryType.PAYMENT);
		LinkedHashMap<Integer,AccountEntryDetail> payMap = new LinkedHashMap<Integer, AccountEntryDetail>();
		FinanceEntryDetailType.visit(invoice,finance,payMap);	
		payEntry.setDetails(new LinkedList<AccountEntryDetail>());
		payEntry.getDetails().addAll(payMap.values());
		
		String payConcept = invoice.isSales()
				?"Cobro Fra: "+ invoice.getInvoice().getDocumentNumber()
				:"Pago Fra: "+ (AonStringUtils.isBlank( invoice.getInvoice().getReferenceCode())
				?"????????"
				:invoice.getInvoice().getReferenceCode());
		for (AccountEntryDetail detail : payEntry.getDetails()) {
			detail.setConcept(payConcept)
				.setConcept( AonStringUtils.abbreviate(detail.getConcept(), 32 ))
				.setDocumentNumber(invoice.getInvoice().getDocumentNumber());
		}
		return payEntry;
	}
	
	public static AccountEntry[] recordInvoice(AccountingInvoice invoice) {
		AccountEntry ae = getInvoiceEntry(invoice);
		if (invoice.hasFinances() && invoice.isFinanceRecordable()) {
			LinkedList<AccountEntry> entries = new LinkedList<AccountEntry>();
			entries.add(ae);
			for (Finance finance : invoice.getFinances()) {
				AccountEntry payEntry = getFinanceEntry(invoice,finance);
				entries.add(payEntry);
			}
			return entries.toArray(new AccountEntry[entries.size()]);
		}
		return new AccountEntry[]{ae};
	}
}
