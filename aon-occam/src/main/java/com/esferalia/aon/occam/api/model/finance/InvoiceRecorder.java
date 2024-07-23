package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
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
				// double amount = invoice.getTotalInvoice();
				double amount = finance.getAmount();
				if (invoice.getRegistry().getType() == AccountingRegistryType.CUSTOMER) {
					Integer registryAccount = invoice.getRegistry().getAccountId();
					String registryAccountCode = invoice.getRegistry().getAccountCode();
					if (AonStringUtils.isBlank(registryAccountCode)) registryAccountCode = AccountingRegistryType.CUSTOMER.getAccountPrefix() + "?????";
					String registryAccountDescription = invoice.getRegistry().getAccountDescription();
					if (AonStringUtils.isBlank(registryAccountDescription)) registryAccountDescription = invoice.getRegistry().getName();
					
					Integer payAccount = invoice.getPayAccountId();
					String payAccountCode = invoice.getPayAccountCode();
					String payAccountDescription = invoice.getPayAccountDescription();
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
					detail.setCredit(amount);
					
					AccountEntryDetail payDetail = map.get(invoice.getPayAccountId());
					if (payDetail == null) {
						payDetail = new AccountEntryDetail()
							.setAccount(payAccount)
							.setAccountCode(payAccountCode)
							.setAccountDescription(payAccountDescription)
							.setBalancingAccount(registryAccount)
							.setBalancingAccountCode(registryAccountCode)
							.setBalancingAccountDescription(registryAccountDescription);
						map.put(invoice.getPayAccountId(),payDetail);
					}
					payDetail.setDebit(amount);
				}
			}
	 	})
		,SUPPLIER_CREDITOR( new IFinanceVisitor() {
			@Override
			public void visit(AccountingInvoice invoice, Finance finance,LinkedHashMap<Integer,AccountEntryDetail> map) {
				// double amount = invoice.getTotalInvoice();
				double amount = finance.getAmount();
				if ( (invoice.getRegistry().getType() == AccountingRegistryType.SUPPLIER 
					||invoice.getRegistry().getType() == AccountingRegistryType.CREDITOR
					||invoice.getRegistry().getType() == AccountingRegistryType.UNDED_CREDITOR)) {
					
					Integer registryAccount = invoice.getRegistry().getAccountId();
					String registryAccountCode = invoice.getRegistry().getAccountCode();
					if (AonStringUtils.isBlank(registryAccountCode)) registryAccountCode 
						= invoice.getRegistry().getType().getAccountPrefix() + "?????";
					String registryAccountDescription = invoice.getRegistry().getAccountDescription();
					if (AonStringUtils.isBlank(registryAccountDescription)) registryAccountDescription = invoice.getRegistry().getName();
					
					Integer payAccount = invoice.getPayAccountId();
					String payAccountCode = invoice.getPayAccountCode();
					String payAccountDescription = invoice.getPayAccountDescription();
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
					detail.setDebit(amount);
					
					AccountEntryDetail payDetail = map.get(invoice.getPayAccountId());
					if (payDetail == null) {
						payDetail = new AccountEntryDetail()
							.setAccount(payAccount)
							.setAccountCode(payAccountCode)
							.setAccountDescription(payAccountDescription)
							.setBalancingAccount(registryAccount)
							.setBalancingAccountCode(registryAccountCode)
							.setBalancingAccountDescription(registryAccountDescription);
						map.put(invoice.getPayAccountId(),payDetail);
					}
					payDetail.setCredit(amount);
					
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
		return (invoice.getRegistry() != null)?invoice.getRegistry().getAccountId() : null;
	}
	private static String obtainRegistryAccountCode(AccountingInvoice invoice) {
		String code = "?????????";
		if ( invoice.getRegistry() != null ) {
			code = invoice.getRegistry().getAccountCode();
			if (AonStringUtils.isBlank(code)) {
				code = invoice.getRegistry().getType().getAccountPrefix() + "?????";
			}
		}
		return code;
	}
	private static String obtainRegistryAccountDescription(AccountingInvoice invoice) {
		String description = "";
		if ( invoice.getRegistry() != null ) {
			description = invoice.getRegistry().getAccountDescription();
			if (AonStringUtils.isBlank(description)) {
				description = invoice.getRegistry().getName();
			}
		}
		return description;
	}
	private static void fillBalancingAccount(AccountEntryDetail detail, AccountingInvoice invoice) {
		Account account = null;
		if (invoice.getVats() != null) {
			for (InvoiceVAT vat : invoice.getVats()) {
				if (account == null) {
					account = vat.getExpAccount();
				}
				if (account == null
				 || vat.getExpAccount() == null
				 || !AonNumberUtils.equals(account.getId(),vat.getExpAccount().getId())) {
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

	private enum InvoiceEntryDetailType implements Serializable {
		 CUSTOMER( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry() != null && invoice.getRegistry().getType() == AccountingRegistryType.CUSTOMER) {
					double amount = invoice.getTotalInvoice(); 
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
					detail.setDebit(amount);
				}
			}

	 	})
		,SUPPLIER_CREDITOR( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry() != null 
					&& (invoice.getRegistry().getType() == AccountingRegistryType.SUPPLIER 
					|| invoice.getRegistry().getType() == AccountingRegistryType.CREDITOR
					|| invoice.getRegistry().getType() == AccountingRegistryType.UNDED_CREDITOR)) {
					
					double amount = invoice.getTotalInvoice();
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
					detail.setCredit(amount);
				}
			}
	 	})
		,INPUT_VAT( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getVats() != null && invoice.isInputVatEnabled()) {
					for (InvoiceVAT vat : invoice.getVats()) {
						double amount = vat.getDeductibleQuota() + vat.getSurchargeQuota();
						if (AonMathUtils.isNotZero(amount)) {
							Integer id = null;
							String code = null;
							String description = null;
							if (vat.getInputAccount() == null || vat.getInputAccount().getId() == null) {
								if (vat.getExpAccount() != null) {
									id = vat.getExpAccount().getId();
									code = vat.getExpAccount().getCode();
									description = vat.getExpAccount().getDescription();
								}
							} else {
								id = vat.getInputAccount().getId();
								code = vat.getInputAccount().getCode();
								description = vat.getInputAccount().getDescription();
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
								detail.addDebit( amount );
							}
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
						double amount = vat.getDeductibleQuota() + vat.getSurchargeQuota();
						if (AonMathUtils.isNotZero(amount)) {
							Integer id = null;
							String code = null;
							String description = null;
							if (vat.getOutputAccount() != null) {
								id = vat.getOutputAccount().getId();
								code = vat.getOutputAccount().getCode();
								description = vat.getOutputAccount().getDescription();
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
								detail.addCredit( amount );
							}
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
							double amount = vat.getQuota() - vat.getDeductibleQuota();
							if (AonMathUtils.isNotZero(amount)) {
								Integer id = null;
								String code = null;
								String description = null;
								if (vat.getAdjAccount() == null || vat.getAdjAccount().getId() == null) {
									if (vat.getExpAccount() != null) {
										id = vat.getExpAccount().getId();
										code = vat.getExpAccount().getCode();
										description = vat.getExpAccount().getDescription();
									}
								} else {
									id = vat.getAdjAccount().getId();
									code = vat.getAdjAccount().getCode();
									description = vat.getAdjAccount().getDescription();
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
	 	})
		
		,DIRECT_TAX_ADJUST( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (!invoice.isSales() 
					&& !invoice.isSurcharge() 
					&& invoice.isOutputVatEnabled() != invoice.isInputVatEnabled()
					&& invoice.getVats() != null) {
					for (InvoiceVAT vat : invoice.getVats()) {
						if (vat.getInvestAsset() != null
							&& AonMathUtils.notEquals(100.0, vat.getDirectTaxNoDedExpenses())
							&& vat.getAdjDirectTaxAccount() != null
							&& vat.getAdjDirectTaxAccount().getId() != null) {
							double amount = vat.getDirectTaxNoDedExpenses();
							Integer id = vat.getAdjDirectTaxAccount().getId();
							String code = vat.getAdjDirectTaxAccount().getCode();
							String description = vat.getAdjDirectTaxAccount().getDescription();
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
							if (invoice.isOutputVatEnabled()) {
								detail.addCredit( amount );
							} else {
								detail.addDebit( amount );
							}
							
							if (vat.getExpAccount() != null) {
								id = vat.getExpAccount().getId();
								code = vat.getExpAccount().getCode();
								description = vat.getExpAccount().getDescription();
							}
							detail = map.get(id);
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
							if (invoice.isOutputVatEnabled()) {
								detail.addDebit( amount );
							} else {
								detail.addCredit( amount );
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
					&& AonMathUtils.isNotZero(invoice.getWithholdingData().getQuota())) {
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
					&& AonMathUtils.isNotZero(invoice.getWithholdingData().getQuota())) {
					
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
						double amount = vat.getBase();
						if (vat.getExpAccount() != null 
							&& vat.getExpAccount().getId() != null
							&& AonMathUtils.isNotZero(amount)) {
							AccountEntryDetail detail = map.get(vat.getExpAccount().getId());
							if (detail == null) {
								detail = new AccountEntryDetail()
									.setAccount(vat.getExpAccount().getId())
									.setAccountCode(vat.getExpAccount().getCode())
									.setAccountDescription(vat.getExpAccount().getDescription())
									.setBalancingAccount(obtainRegistryAccount(invoice))
									.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
									.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
								map.put(vat.getExpAccount().getId(), detail);
							}
							detail.addCredit( amount );		
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
						double amount = vat.getBase();
						if (vat.getExpAccount() != null 
							&& vat.getExpAccount().getId() != null
							&& AonMathUtils.isNotZero(amount)) {
							AccountEntryDetail detail = map.get(vat.getExpAccount().getId());
							if (detail == null) {
								detail = new AccountEntryDetail()
									.setAccount(vat.getExpAccount().getId())
									.setAccountCode(vat.getExpAccount().getCode())
									.setAccountDescription(vat.getExpAccount().getDescription())
									.setBalancingAccount(obtainRegistryAccount(invoice))
									.setBalancingAccountCode(obtainRegistryAccountCode(invoice))
									.setBalancingAccountDescription(obtainRegistryAccountDescription(invoice));
								map.put(vat.getExpAccount().getId(),detail);
							}
							detail.addDebit( amount );
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
	
	private static String obtainConcept(Invoice invoice, String manualConcept) {
		String prefix = (invoice.isSales()) ? N_FRA : S_FRA;
		if (invoice.getTotal() < 0) {
			prefix += " " + ABONO;
		}
		prefix += ": ";
		String concept = AonStringUtils.abbreviate(prefix + AonStringUtils.defaultIfBlank(invoice.getReferenceCode(),"????????"), 64);
		if (AonStringUtils.isNotBlank(manualConcept)) {
			concept = concept + " [" + manualConcept;
			if (AonStringUtils.length(concept) > 64) {
				concept = AonStringUtils.abbreviate(concept,63);
			}
			concept = concept + "]";
		}
		return concept;
	}
	
	public static AccountEntry getInvoiceEntry(AccountingInvoice invoice) {
		AccountEntry ae = AccountEntry.clone(invoice.getAccountEntry());
		if (invoice.getRegistry() != null && invoice.getRegistry().getType() != null) {
			ae.setEntryType( invoice.getRegistry().getType().getAccountEntryType() );
		}
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<Integer, AccountEntryDetail>();
		InvoiceEntryDetailType.visit(invoice,map);	
		ae.setDetails(new LinkedList<>());
		ae.getDetails().addAll(map.values());
		String concept = obtainConcept(invoice.getInvoice(), invoice.getManualConcept());
		for (AccountEntryDetail detail : ae.getDetails()) {
			detail.setConcept(concept)
				.setConcept( AonStringUtils.abbreviate(detail.getConcept(), 64 ))
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
				.setConcept( AonStringUtils.abbreviate(detail.getConcept(), 64 ))
				.setDocumentNumber(invoice.getInvoice().getDocumentNumber());
		}
		return payEntry;
	}
	
	public static AccountEntry[] recordInvoice(AccountingInvoice invoice) {
		AccountEntry ae = getInvoiceEntry(invoice);
		if (invoice.getInvoice().hasFinances()) {
			LinkedList<AccountEntry> entries = new LinkedList<AccountEntry>();
			entries.add(ae);
			for (Finance finance : invoice.getInvoice().getFinances()) {
				if (finance.isRecordable() && !finance.isRemoved()) {
					AccountEntry payEntry = getFinanceEntry(invoice,finance);
					entries.add(payEntry);
				}
			}
			
			return entries.toArray(new AccountEntry[entries.size()]);
		}
		return new AccountEntry[]{ae};
	}
}
