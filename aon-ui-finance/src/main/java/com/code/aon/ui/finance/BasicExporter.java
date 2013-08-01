package com.code.aon.ui.finance;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class BasicExporter {
	
	private String[] SKIP_ACCOUNTS = new String[] { "477", "472", "473", "4751" };

	private OutputStream out;
	
	private Invoice invoice;
	
	private AccountEntry accountEntry;
	
	private List<TaxBreakDown> taxBreakDowns;
	
	private AccountEntryDetail registryDetail;
	
	private List<AccountEntryDetail> details;
	
	public void init( Invoice invoice, OutputStream out ) throws ManagerBeanException {
		this.out = out;
		this.invoice = invoice;
		this.accountEntry = obtainAccountEntry();
		InvoicePriceStrategy priceStrategy = new InvoicePriceStrategy();
		this.taxBreakDowns = priceStrategy.getTaxBreakDowns(invoice, invoice);		
		this.details = obtainDetails();
		this.registryDetail = obtainRegistryDetail();
	}
	
	private AccountEntry obtainAccountEntry() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			AccountEntryInvoice aei = (AccountEntryInvoice) list.get(0);
			return aei.getAccountEntry();
		}
		return null;
	}
	
	
	private boolean isSkipAccount( Account account ) {
		for( String preffix : SKIP_ACCOUNTS ) {
			if ( StringUtils.startsWith(account.getCode(), preffix) ) {
				return true;
			}
		}
		return false;
	}	
	
	private List<AccountEntryDetail> obtainDetails() {
		List<AccountEntryDetail> list = new LinkedList<AccountEntryDetail>();
		for( AccountEntryDetail aed : getAccountEntry().getDetail() ) {
			if (! isSkipAccount(aed.getAccount()) ) {
				list.add(aed);
			}
		}
		Comparator<AccountEntryDetail> comparator = new Comparator<AccountEntryDetail>() {

			@Override
			public int compare(AccountEntryDetail o1, AccountEntryDetail o2) {
				return o1.getAccount().getCode().compareTo(o2.getAccount().getCode());
			}
			
		};
		Collections.sort( list, comparator );
		return list;
	}
	
	protected Enterprise getEnterprise() {
		for( InvoiceDetail id : getInvoice().getLines() ) {
			if ( (id.getWorkPlace() != null) && (id.getWorkPlace().getId() != null) ) {
				return id.getWorkPlace().getEnterprise();
			}
		}
		return null;
	}
	
	private boolean isRelated( AccountEntryDetail aed, TaxBreakDown tbd ) {
		double amount = (aed.getCredit() != 0) ? aed.getCredit() : aed.getDebit();
		return (amount == tbd.getBase());
	}
	
	protected List<TaxBreakDown> getTaxes( AccountEntryDetail aed ) {
		List<TaxBreakDown> list = new LinkedList<TaxBreakDown>();
		for( TaxBreakDown tbd : getTaxBreakDowns() ) {
			if ( isRelated(aed, tbd) ) {
				list.add(tbd);
			}
		}
		getTaxBreakDowns().removeAll(list);
		return list;
	}
	
	protected TaxBreakDown getRelatedTax( List<TaxBreakDown> taxList, TaxBreakDown tax ) {
		TaxBreakDown relatedTax = null;
		if (! taxList.isEmpty() ) {
			for( TaxBreakDown tbd : taxList ) {
				if ( (tbd.getBase() == tax.getBase()) &&
						(tbd.getTaxType() != tax.getTaxType()) ) {
					relatedTax = tbd;
					break;
				}
			}
		}
		if ( relatedTax != null ) {
			taxList.remove(relatedTax);
		}		
		return relatedTax;
	}	
	
	protected TaxBreakDown getNextTax( List<TaxBreakDown> taxList ) {
		TaxBreakDown tax = null;
		for( TaxBreakDown tbd : taxList ) {
			if ( tbd.getTaxType() == TaxType.VAT ) {
				tax = tbd;
			}
		}
		if ( tax == null ) {
			tax = taxList.get(0);
		}
		taxList.remove(tax);
		return tax;
	}		
		
	private Account getRegistryAccount() throws ManagerBeanException {
		Account account = null;
		Registry registry = getInvoice().getRegistry();
		switch ( getInvoice().getType() ) {
			case SALES:
				IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
				Customer customer = (Customer) customerBean.get(registry.getId());
				if ( customer != null ) {
					account = customer.getAccount();
				}
				break;
			case PURCHASE:
				IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
				Supplier supplier = (Supplier) supplierBean.get(registry.getId());
				if ( supplier != null ) {
					account = supplier.getAccount();
				}
				break;
			default:
				IManagerBean creditorBean = BeanManager.getManagerBean(Creditor.class);
				Creditor creditor = (Creditor) creditorBean.get(registry.getId());
				if ( creditor != null ) {
					account = creditor.getAccount();
				}
		}
		return account;
	}
	
	protected AccountEntryDetail obtainRegistryDetail() throws ManagerBeanException {
		AccountEntryDetail detail = null;
		Account registryAccount = getRegistryAccount();
		if ( registryAccount != null ) {
			for( AccountEntryDetail aed : getDetails() ) {
				if ( aed.getAccount() == registryAccount ) {
					detail = aed;
					break;
				}
			}
		}
		if ( detail == null ) {
			detail = getDetails().get(0);
		}
		getDetails().remove(detail);
		return detail;
	}
	
	public void write( byte[] data ) throws IOException {
		out.write(data);
	}
	
	public Invoice getInvoice() {
		return invoice;
	}

	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	public List<TaxBreakDown> getTaxBreakDowns() {
		return taxBreakDowns;
	}

	public List<AccountEntryDetail> getDetails() {
		return details;
	}

	public AccountEntryDetail getRegistryDetail() {
		return registryDetail;
	}

	public abstract String getFileName();
	
	public abstract void write() throws IOException, ManagerBeanException;
	
}
