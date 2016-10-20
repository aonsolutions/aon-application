package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRecorder {
	
	private static interface IVisitor {
		void visit( AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map);
	}

	private static enum AccountEntryDetailType implements Serializable {
		 CUSTOMER( new IVisitor() {

			@Override
			public void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map) {
				if (invoice.getRegistry().getType() == AccountingRegistryType.CUSTOMER) {
					AccountEntryDetail detail = map.get(invoice.getRegistry().getAccountId());
					if (detail == null) {
						String code = invoice.getRegistry().getAccountCode();
						if (AonStringUtils.isBlank(code)) code = AccountingRegistryType.CUSTOMER.getAccountPrefix() + "??????";
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
						if (AonStringUtils.isBlank(code)) code = invoice.getRegistry().getType().getAccountPrefix() + "??????";
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
		private AccountEntryDetailType(IVisitor visitor) {
			this.visitor = visitor;
		}
		
		private static void visit(AccountingInvoice invoice, LinkedHashMap<Integer,AccountEntryDetail> map ) {
			for (AccountEntryDetailType t : AccountEntryDetailType.values()) {
				t.visitor.visit(invoice, map);
			}
		}
	}
	

	
	public static AccountEntry[] recordInvoice(AccountingInvoice invoice) {
		AccountEntry ae = invoice.getAccountEntry();
		LinkedHashMap<Integer,AccountEntryDetail> map = new LinkedHashMap<Integer, AccountEntryDetail>();
		AccountEntryDetailType.visit(invoice,map);	
		ae.setDetails(new LinkedList<AccountEntryDetail>());
		ae.getDetails().addAll(map.values());
		
		
		// TODO 
		// ------- MEJORAR!!
		String document = FinanceUtil.getDocumentNumber(invoice.getInvoice().getType()
				, invoice.getInvoice().getSeries()
				, invoice.getInvoice().getNumber());
		for (AccountEntryDetail detail : ae.getDetails()) {
			detail.setConcept(invoice.isSales()
					?"N/Fra: "+document
					:"S/Fra: "+ (AonStringUtils.isBlank( invoice.getInvoice().getReferenceCode())
						?"????????"
						:invoice.getInvoice().getReferenceCode()));
			detail.setConcept( AonStringUtils.abbreviate(detail.getConcept(), 32 ));
			detail.setDocumentNumber(document);
		}
		return new AccountEntry[]{ae};
	}
	

}
