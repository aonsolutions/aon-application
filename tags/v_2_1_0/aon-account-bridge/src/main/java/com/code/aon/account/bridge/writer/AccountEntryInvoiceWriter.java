package com.code.aon.account.bridge.writer;

import java.util.Iterator;
import java.util.Map;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;

/**
 * The Class AccountEntryInvoiceWriter.
 */
public class AccountEntryInvoiceWriter {

	/**
	 * Insertor update account entry.
	 * 
	 * @param entry the entry
	 * @param isNew the is new
	 * 
	 * @return the account entry
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public AccountEntry insertorUpdateAccountEntry(AccountEntry entry, boolean isNew) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		if(isNew){
			entry = (AccountEntry)entryBean.insert(entry);
		}else{
			entry = (AccountEntry)entryBean.update(entry);
		}
		return entry;
	}
	
	/**
	 * Insert entry details.
	 * 
	 * @param retentionTotal the retention total
	 * @param taxQuota the tax quota
	 * @param balancingAccount the balancing account
	 * @param account the account
	 * @param entry the entry
	 * @param invoiceTotal the invoice total
	 * @param taxableBase the taxable base
	 * @param series invoice series
	 * @param number invoice number
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public void insertEntryDetails(AccountEntry entry, Account account, String series, int number, double invoiceTotal, double retentionTotal, double taxQuota, Map basesPerAccount) throws ManagerBeanException {
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		// Primer Apunte
		AccountEntryDetail entryDetail = new AccountEntryDetail();
		entryDetail.setAccount(account);
		entryDetail.setAccountEntry(entry);
		if (basesPerAccount.size() == 1) {
			entryDetail.setBalancingAccount((Account)basesPerAccount.keySet().iterator().next());
		}
		if(entry.getType().equals(AccountEntryType.SALES_INVOICE)){
			entryDetail.setDebit(invoiceTotal);
		}
		if(entry.getType().equals(AccountEntryType.PURCHASE_INVOICE)){
			entryDetail.setCredit(invoiceTotal);
		}
		if(entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)){
			entryDetail.setCredit(invoiceTotal);
		}
		entryDetail.setConcept("N/Fra: " + series + "/" + number);
		entryDetailBean.insert(entryDetail);
		// Segundo Apunte(Mirar si hay q crearlo o no)
		entryDetail = new AccountEntryDetail();
		if(retentionTotal > 0){
			if(entry.getType().equals(AccountEntryType.SALES_INVOICE)){
				entryDetail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT));
				entryDetail.setDebit(retentionTotal);
			}else{
				entryDetail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT));
				entryDetail.setCredit(retentionTotal);
			}
			entryDetail.setAccountEntry(entry);
			entryDetail.setBalancingAccount(account);
			entryDetail.setConcept("N/Fra: " + series + "/" + number);
			entryDetailBean.insert(entryDetail);
		}
		// Tercer Apunte (Si I.V.A. es 0 no se crea)
		if(taxQuota > 0){
			entryDetail = new AccountEntryDetail();
			if(entry.getType().equals(AccountEntryType.SALES_INVOICE)){
				entryDetail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.CHARGE_VAT_ACCOUNT));
				entryDetail.setCredit(taxQuota);
			}else{
				entryDetail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT));
				entryDetail.setDebit(taxQuota);
			}
			entryDetail.setAccountEntry(entry);
			entryDetail.setBalancingAccount(account);
			entryDetail.setConcept("N/Fra: " + series + "/" + number);
			entryDetailBean.insert(entryDetail);
		}
		// Cuarto Apunte (o varios Apuntes en funcion del Mapa de bases por cuenta)
		Iterator iterator = basesPerAccount.keySet().iterator();
		while (iterator.hasNext()) {
			entryDetail = new AccountEntryDetail();
			entryDetail.setAccount((Account)iterator.next());
			entryDetail.setAccountEntry(entry);
			entryDetail.setBalancingAccount(account);
			entryDetail.setConcept("N/Fra: " + series + "/" + number);
			if(entry.getType().equals(AccountEntryType.SALES_INVOICE)){
				entryDetail.setCredit(((Double)basesPerAccount.get(entryDetail.getAccount())).doubleValue());
			}else{
				entryDetail.setDebit(((Double)basesPerAccount.get(entryDetail.getAccount())).doubleValue());
			}
			entryDetailBean.insert(entryDetail);
		}
	}
	
	/**
	 * Insert account entry invoice.
	 * 
	 * @param entry the entry
	 * @param invoice the invoice
	 * 
	 * @return the account entry invoice
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public AccountEntryInvoice insertAccountEntryInvoice(AccountEntry entry, Invoice invoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		AccountEntryInvoice accountEntryInvoice = new AccountEntryInvoice();
		accountEntryInvoice.setInvoice(invoice);
		accountEntryInvoice.setAccountEntry(entry);
		return (AccountEntryInvoice)accountEntryInvoiceBean.insert(accountEntryInvoice);
	}
	
	/**
	 * Unrecord invoice.
	 * 
	 * @param invoice the invoice
	 * @throws ManagerBeanException 
	 */
	public void unrecordInvoice(Invoice invoice) throws ManagerBeanException{
		AccountEntryInvoice accEntryInvoice = obtainAccountEntryInvoice(invoice);
		if(accEntryInvoice != null){
			removeAccountEntryDetails(accEntryInvoice.getAccountEntry());
			removeAccountEntryInvoice(accEntryInvoice);
			removeAccountEntry(accEntryInvoice.getAccountEntry());
		}
	}

	@SuppressWarnings("unchecked")
	private AccountEntryInvoice obtainAccountEntryInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
		Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (AccountEntryInvoice)iter.next();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void removeAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
		Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
		while(iter.hasNext()){
			AccountEntryDetail accEntryDetail = (AccountEntryDetail)iter.next();
			accountEntryDetailBean.remove(accEntryDetail);
		}
	}
	
	private void removeAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
	}
	
	private void removeAccountEntryInvoice(AccountEntryInvoice accEntryInvoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		accountEntryInvoiceBean.remove(accEntryInvoice);
	}
}