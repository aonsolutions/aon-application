package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonNumberUtils;
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
					Integer registryAccount = invoice.getRegistry().getAccountId();
					String registryAccountCode = invoice.getRegistry().getAccountCode();
					if (AonStringUtils.isBlank(registryAccountCode)) registryAccountCode = AccountingRegistryType.CUSTOMER.getAccountPrefix() + "?????";
					String registryAccountDescription = invoice.getRegistry().getAccountDescription();
					if (AonStringUtils.isBlank(registryAccountDescription)) registryAccountDescription = invoice.getRegistry().getName();
					
					Integer payAccount = finance.getPayAccountId();
					String payAccountCode = finance.getPayAccountCode();
					String payAccountDescription = finance.getPayAccountDescription();
					if (AonStringUtils.isBlank(payAccountDescription)) payAccountDescription = "COBRO FACTURA";
					
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						detail = new AccountEntryDetail()
							.setAccount(registryAccount)
							.setAccountCode(registryAccountCode)
							.setAccountDescription(registryAccountDescription)
							.setBalancingAccount(payAccount)
							.setBalancingAccountCode(payAccountCode)
							.setBalancingAccountDescription(payAccountDescription);
						map.put(invoice.getRegistry().getAccountId(),detail);
					}
					detail.setCredit(invoice.getTotalInvoice());
					
					AccountEntryDetail payDetail = map.get(finance.getPayAccountId());
					if (payDetail == null) {
						payDetail = new AccountEntryDetail()
							.setAccount(payAccount)
							.setAccountCode(payAccountCode)
							.setAccountDescription(payAccountDescription)
							.setBalancingAccount(registryAccount)
							.setBalancingAccountCode(registryAccountCode)
							.setBalancingAccountDescription(registryAccountDescription);
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
					
					Integer registryAccount = invoice.getRegistry().getAccountId();
					String registryAccountCode = invoice.getRegistry().getAccountCode();
					if (AonStringUtils.isBlank(registryAccountCode)) registryAccountCode 
						= invoice.getRegistry().getType().getAccountPrefix() + "?????";
					String registryAccountDescription = invoice.getRegistry().getAccountDescription();
					if (AonStringUtils.isBlank(registryAccountDescription)) registryAccountDescription = invoice.getRegistry().getName();
					
					Integer payAccount = finance.getPayAccountId();
					String payAccountCode = finance.getPayAccountCode();
					String payAccountDescription = finance.getPayAccountDescription();
					if (AonStringUtils.isBlank(payAccountDescription)) payAccountDescription = "PAGO FACTURA";
					
					
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						detail = new AccountEntryDetail()
							.setAccount(registryAccount)
							.setAccountCode(registryAccountCode)
							.setAccountDescription(registryAccountDescription)
							.setBalancingAccount(payAccount)
							.setBalancingAccountCode(payAccountCode)
							.setBalancingAccountDescription(payAccountDescription);
						map.put(invoice.getRegistry().getAccountId(),detail);
					}
					detail.setDebit(invoice.getTotalInvoice());
					
					AccountEntryDetail payDetail = map.get(finance.getPayAccountId());
					if (payDetail == null) {
						payDetail = new AccountEntryDetail()
							.setAccount(payAccount)
							.setAccountCode(payAccountCode)
							.setAccountDescription(payAccountDescription)
							.setBalancingAccount(registryAccount)
							.setBalancingAccountCode(registryAccountCode)
							.setBalancingAccountDescription(registryAccountDescription);
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
	
	private static Integer obtainRegistryAccount(AccountingInvoice invoice) {
		return invoice.getRegistry().getAccountId();
	}
	private static String obtainRegistryAccountCode(AccountingInvoice invoice) {
		String code = invoice.getRegistry().getAccountCode();
		if (AonStringUtils.isBlank(code)) code = invoice.getRegistry().getType().getAccountPrefix() + "?????";
		return code;
	}
	private static String obtainRegistryAccountDescription(AccountingInvoice invoice) {
		String description = invoice.getRegistry().getAccountDescription();
		if (AonStringUtils.isBlank(description)) description = invoice.getRegistry().getName();
		return description;
	}
	private static void fillBalancingAccount(AccountEntryDetail detail, AccountingInvoice invoice) {
		Integer account = null;
		String code = null;
		String description = null;
		for (InvoiceVAT vat : invoice.getVats()) {
			if (account == null) {
				account = vat.getExpAccountId();
				code = vat.getExpAccountCode();
				description = vat.getExpAccountDescription();
			}
			if (!AonNumberUtils.equals(account,vat.getExpAccountId())) {
				account = null;
				code = null;
				description = null;
				break;
			}
		}
		detail.setBalancingAccount(account)
			.setBalancingAccountCode(code)
			.setBalancingAccountDescription(description);
	}

	private static enum InvoiceEntryDetailType implements Serializable {
		 CUSTOMER( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry().getType() == AccountingRegistryType.CUSTOMER) {
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						Integer account = obtainRegistryAccount(invoice); 
						detail = new AccountEntryDetail()
							.setAccount(account)
							.setAccountCode(obtainRegistryAccountCode(invoice))
							.setAccountDescription(obtainRegistryAccountDescription(invoice));
						fillBalancingAccount(detail,invoice);
						map.put(account,detail);
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
						Integer account = obtainRegistryAccount(invoice);
						detail = new AccountEntryDetail()
							.setAccount(account)
							.setAccountCode(obtainRegistryAccountCode(invoice))
							.setAccountDescription(obtainRegistryAccountDescription(invoice));
						fillBalancingAccount(detail,invoice);
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
						Integer id = null;
						String code = null;
						String description = null;
						if (vat.getInputAccountId() == null) {
							id = vat.getExpAccountId();
							code = vat.getExpAccountCode();
							description = vat.getExpAccountDescription();
						} else {
							id = vat.getInputAccountId();
							code = vat.getInputAccountCode();
							description = vat.getInputAccountDescription();
						}
						if (id != null) {
							AccountEntryDetail detail = map.get(id);
							if (detail == null) {
								detail = new AccountEntryDetail()
									.setAccount(id)
									.setAccountCode(code)
									.setAccountDescription(description)
									.setBalancingAccount(obtainRegistryAccount(invoice))
									.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
									.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
									;
								map.put(id,detail);
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
						Integer id = null;
						String code = null;
						String description = null;
						if (vat.getOutputAccountId() == null) {
							id = vat.getExpAccountId();
							code = vat.getExpAccountCode();
							description = vat.getExpAccountDescription();
						} else {
							id = vat.getOutputAccountId();
							code = vat.getOutputAccountCode();
							description = vat.getOutputAccountDescription();
						}
						if (id != null) {
							AccountEntryDetail detail = map.get(id);
							if (detail == null) {
								detail = new AccountEntryDetail()
										.setAccount(id)
										.setAccountCode(code)
										.setAccountDescription(description)
										.setBalancingAccount(obtainRegistryAccount(invoice))
										.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
										.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
										;
								map.put(id,detail);
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
						if (vat.getDeductibleQuota() !=  vat.getQuota()) {
							Integer id = null;
							String code = null;
							String description = null;
							if (vat.getAdjAccountId() == null) {
								id = vat.getExpAccountId();
								code = vat.getExpAccountCode();
								description = vat.getExpAccountDescription();
							} else {
								id = vat.getAdjAccountId();
								code = vat.getAdjAccountCode();
								description = vat.getAdjAccountDescription();
							}
							AccountEntryDetail detail = map.get(id);
							if (detail == null) {
								detail = new AccountEntryDetail()
									.setAccount(id)
									.setAccountCode(code)
									.setAccountDescription(description)
									.setBalancingAccount(obtainRegistryAccount(invoice))
									.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
									.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
								map.put(id,detail);
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
								.setAccountDescription(invoice.getWithholdingData().getAccountDescription())
								.setBalancingAccount(obtainRegistryAccount(invoice))
								.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
								.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
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
								.setAccountDescription(invoice.getWithholdingData().getAccountDescription())
								.setBalancingAccount(obtainRegistryAccount(invoice))
								.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
								.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
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
									.setAccountDescription(vat.getExpAccountDescription())
									.setBalancingAccount(obtainRegistryAccount(invoice))
									.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
									.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
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
									.setAccountDescription(vat.getExpAccountDescription())
									.setBalancingAccount(obtainRegistryAccount(invoice))
									.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
									.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
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
			for (Finance finance : invoice.getFinances()) {
				AccountEntry payEntry = getFinanceEntry(invoice,finance);
				entries.add(payEntry);
			}
			entries.add(ae);
			return entries.toArray(new AccountEntry[entries.size()]);
		}
		return new AccountEntry[]{ae};
	}
}
