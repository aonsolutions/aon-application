package com.code.aon.ui.finance.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.invoicing.FinanceGenerator;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.dao.ISupplierAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class PurchaseDirectInvoicingController extends BasicController {
	
	private static final String PURCHASE_DIRECT_FINANCE_CONTROLLER_NAME = "purchaseDirectFinance";

	private IPriceStrategy priceStrategy;
	
	private FinanceGenerator financeGenerator;
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	private List<SelectItem> addresses;

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public FinanceGenerator getFinanceGenerator() {
		if(financeGenerator == null){
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	public boolean isEditable() {
		if (this.getTo() != null) {
			if (((Invoice) this.getTo()).getStatus() == null) {
				return true;
			}
			return ((Invoice) this.getTo()).getStatus().equals(
					InvoiceStatus.PENDING);
		}
		return false;
	}

	public boolean isRecorded() {
		Invoice invoice = (Invoice) this.getTo();
		if (invoice.getStatus() != null) {
			return invoice.getStatus().equals(InvoiceStatus.SCORED);
		}
		return false;
	}

	public double getToInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice) this.getTo();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}

	public double getInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice) this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}
	
	@SuppressWarnings({"unused","unchecked"})
	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IController purchaseDirectFinanceController = AonUtil.getController(PURCHASE_DIRECT_FINANCE_CONTROLLER_NAME);
			List financeList = ((List)purchaseDirectFinanceController.getModel().getWrappedData());
			if(existFinanceTrackings(financeList)){
				AonUtil.addInfoMessage("No se puede generar vencimientos automaticamente. Alguno de ellos tiene operaciones anteriores.");
				throw new AbortProcessingException();
			}else{
				Iterator iter = financeList.iterator();
				while(iter.hasNext()){
					Finance finance = (Finance)iter.next();
					financeBean.remove(finance);
				}
				Company company = obtainCompany();
				getFinanceGenerator().generateFinances(invoice, company, getPriceStrategy().getTotalPrice(invoice, invoice));
				purchaseDirectFinanceController.onSearch(null);
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error generating finances for invoice with id= " + invoice.getId(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean existFinanceTrackings(List financeList) throws ManagerBeanException {
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Iterator iter = financeList.iterator();
		while(iter.hasNext()){
			Criteria criteria = new Criteria();
			Finance finance = (Finance)iter.next();
			criteria.addEqualExpression(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
			if(financeTrackingBean.getCount(criteria) > 0){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException, ExpressionException{
		Invoice invoice = (Invoice)this.getTo();
		recordInvoice(invoice);
	}
	
	@SuppressWarnings("unused")
	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException, ExpressionException{
		Invoice invoice = (Invoice)this.getTo();
		removeInvoiceDetailAccount(invoice);
		getAccountEntryInvoiceWriter().unrecordInvoice(invoice);
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.PENDING);
		if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);
	}
	
	@SuppressWarnings("unchecked")
	private void removeInvoiceDetailAccount(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iterator = invoiceAccountBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)iterator.next();
			invoiceAccountBean.remove(invoiceDetailAccount);
		}
	}

	@SuppressWarnings("unchecked")
	private void recordInvoice(Invoice invoice) throws ManagerBeanException, ExpressionException {
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(AccountUtil.obtainPeriod(invoice.getIssueDate()).getId());
		entry.setEntryDate(invoice.getIssueDate());
		entry.setJournal(null);
		entry.setType(AccountEntryType.PURCHASE_INVOICE);
		entry.setSecurityLevel(invoice.getSecurityLevel());
		entry = getAccountEntryInvoiceWriter().insertorUpdateAccountEntry(entry, true);
		List taxBreakDown = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
		getAccountEntryInvoiceWriter().insertEntryDetails(entry, AccountUtil.obtainSupplierAccount(invoice.getRegistry()), invoice.getSeries(), invoice.getNumber(), getPriceStrategy().getTotalPrice(invoice, invoice), getRetentionTotal(taxBreakDown), getTaxQuota(taxBreakDown), obtainBasesPerAccount(invoice));
		getAccountEntryInvoiceWriter().insertAccountEntryInvoice(entry, invoice);
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.SCORED);
		if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);
	}

	@SuppressWarnings("unchecked")
	private double getTaxQuota(List taxBreakDownList) {
		Iterator iter = taxBreakDownList.iterator();
		double taxQuota = 0;
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
				taxQuota = taxQuota + taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota();
			}
		}
		return taxQuota;
	}

	@SuppressWarnings("unchecked")
	private double getRetentionTotal(List taxBreakDownList) {
		Iterator iter = taxBreakDownList.iterator();
		double retentionQuota = 0;
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.RETENTION)){
				retentionQuota = retentionQuota + taxBreakDown.getTaxQuota();
			}
		}
		return retentionQuota;
	}
	
	@SuppressWarnings("unchecked")
	private Map obtainBasesPerAccount(Invoice invoice) throws ManagerBeanException {
		Map basesPerAccount = new HashMap();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			Account account = null;
			if (invoiceDetail.getItem() != null) {
				Integer productId = invoiceDetail.getItem().getProduct().getId();
				IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				criteria = new Criteria();
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), productId);
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), ProductAccountType.PURCHASE);
				Iterator iter = productAccountBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					account = ((ProductAccount)iter.next()).getAccount();
				}
			}
			account = (account==null)?account = obtainDefaultAccount():account;

			double base = invoiceDetail.getTaxableBase();
			base += (basesPerAccount.containsKey(account))?((Double)basesPerAccount.get(account)).doubleValue():0;
			basesPerAccount.put(account, new Double(base));

			insertInvoiceDetailAccount(invoiceDetail, account);
		}
		return basesPerAccount;
	}

	@SuppressWarnings("unchecked")
	private Account obtainDefaultAccount() throws ManagerBeanException {
		IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamsBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), DefaultAccounts.PURCHASE_ACCOUNT);
		Iterator iter = appParamsBean.getList(criteria).iterator();
		if(iter.hasNext()){
			ApplicationParameter param = (ApplicationParameter)iter.next();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria accountCriteria = new Criteria();
			accountCriteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), param.getValue());
			Iterator accountIter = accountBean.getList(accountCriteria).iterator();
			if(accountIter.hasNext()){
				return (Account)accountIter.next();
			}
		}
		return null;
	}

	private void insertInvoiceDetailAccount(InvoiceDetail invoiceDetail, Account account) throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
		invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
		invoiceDetailAccount.setAccount(account);
		invoiceAccountBean.insert(invoiceDetailAccount);
	}

	@SuppressWarnings("unchecked") 
	public Company obtainCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Iterator iter = companyBean.getList(null, 0, 1).iterator();
		if(iter.hasNext()){
			return (Company)iter.next();
		}
		return null;
	}

    public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public int getAddressCount() {
		if(addresses != null){
			return addresses.size();
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public void loadAddresses(Integer id) throws ManagerBeanException {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = address.getAddress() + " " + address.getAddress2() + " " + address.getAddress3();
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
				SelectItem item = new SelectItem(address.getId(), addressLabel);
				addresses.add(item);
			}
		}
		this.addresses = addresses;
	}

	@SuppressWarnings("unchecked")
	public void supplierData(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supplierBean.getFieldName(ISupplierAlias.SUPPLIER_ID), event.getNewValue());
			Iterator iter = supplierBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				Supplier supplier = (Supplier) iter.next();
				((Invoice)this.getTo()).setRegistryName(supplier.getRegistry().getName() + " " + supplier.getRegistry().getSurname());
				((Invoice)this.getTo()).setRegistryDocument(supplier.getRegistry().getDocument());
				((Invoice) this.getTo()).setRegistry(supplier.getRegistry());
			}
			loadAddresses(new Integer(event.getNewValue().toString()));
		}
	}

	public void addSupplierEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !((String) event.getNewValue()).trim().equals("")) {
			Integer id = new Integer((String) event.getNewValue());
			Criteria criteria = getCriteria();
			criteria.addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), id);
			setCriteria(criteria);
		}
	}

	public void addIssuedateFromExpression(ValueChangeEvent event)throws ManagerBeanException {
		if (event.getNewValue() != null) {
			Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	public void addIssuedateToExpression(ValueChangeEvent event)	throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}

	@SuppressWarnings("unchecked")
	public Integer getAccountEntryId() throws ManagerBeanException {
    	Invoice invoice = (Invoice)this.getTo();
		if (invoice != null && invoice.getId() != null) {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
			Iterator iterator = accountEntryInvoiceBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iterator.next();
				return accountEntryInvoice.getAccountEntry().getId();
			}
		}
    	return null;
	}
}