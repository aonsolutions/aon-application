package com.code.aon.ui.finance.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.DefaultAccounts;
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
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.finance.recording.RecordingParameters;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.menu.jsf.MenuManager;
import com.code.aon.ui.sales.controller.FeeInvoicingController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRecordingController extends BasicController{
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceRecordingController.class.getName());
	
	private static final String FEE_INVOICING_CONTROLLER_NAME = "feeInvoicing";

	private static final String MENU_MANAGER_NAME = "menuManager";

	private RecordingParameters recordingParams;

	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

	private IPriceStrategy priceStrategy;

	public RecordingParameters getRecordingParams() {
		return recordingParams;
	}

	public void setRecordingParams(RecordingParameters recordingParams) {
		this.recordingParams = recordingParams;
	}

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	@SuppressWarnings("unused")
	public void onInitialize(MenuEvent event) throws ManagerBeanException{
		this.recordingParams = new RecordingParameters();
		this.recordingParams.setInvoiceType(InvoiceType.SALES);
		this.recordingParams.setSecurityLevel(SecurityLevel.OFFICIAL);
	}

	@SuppressWarnings("unchecked")
	public void onRecord(ActionEvent event){
		try {
			Criteria criteria = null;
			Iterator iter = obtainInvoiceList().iterator();
			while(iter.hasNext()){
				Invoice invoice = (Invoice)iter.next();
				recordInvoice(invoice);
				if(criteria == null){
					criteria = new Criteria();
				}
				criteria.addOrExpression(getManagerBean().getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId().toString());
			}
			FeeInvoicingController invoicingController = (FeeInvoicingController)AonUtil.getController(FEE_INVOICING_CONTROLLER_NAME);
			if(criteria == null){
				criteria = new Criteria();
				criteria.addNullExpression(getFieldName(IFinanceAlias.INVOICE_ID));
			}
			invoicingController.setCriteria(criteria);
			invoicingController.onSearch(null);
			updateBreadCrumb();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List obtainInvoiceList() {
		List list = new LinkedList();
		Criteria criteria = new Criteria();
		try {
			criteria.addEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_TYPE), recordingParams.getInvoiceType());
			criteria.addEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
			if(recordingParams.getFromDate() != null){
				criteria.addGreaterThanOrEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), recordingParams.getFromDate());
			}
			if(recordingParams.getToDate() != null){
				criteria.addLessThanOrEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), recordingParams.getToDate());
			}
			if(recordingParams.getSeries() != null && !recordingParams.getSeries().equals("")){
				criteria.addExpression(this.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), recordingParams.getSeries());
			}
			if(recordingParams.getFromNumber() != null){
				criteria.addGreaterThanOrEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_NUMBER), recordingParams.getFromNumber());
			}
			if(recordingParams.getToNumber() != null){
				criteria.addLessThanOrEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_NUMBER), recordingParams.getToNumber());
			}
			if(recordingParams.getRegistryId() != null){
				criteria.addEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), recordingParams.getRegistryId());
			}
			if(recordingParams.getSecurityLevel() != null){
				criteria.addEqualExpression(this.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL), recordingParams.getSecurityLevel());
			}
			list = this.getManagerBean().getList(criteria);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return list;
	}

	private void updateBreadCrumb() {
		MenuManager menuManager = (MenuManager)AonUtil.getRegisteredBean(MENU_MANAGER_NAME);
        menuManager.setCurrentMenu("AON_APP");
        menuManager.getCurrentMenuModel().setSelectedNode("root.aon_administrative_management.aon_invoice_management");
    }

	@SuppressWarnings("unchecked")
	private void recordInvoice(Invoice invoice) {
		AccountEntry entry = new AccountEntry();
		try {
			entry.setAccountPeriod(AccountUtil.obtainPeriod(invoice.getIssueDate()).getId());
			entry.setEntryDate(invoice.getIssueDate());
			entry.setJournal(null);
			entry.setType((invoice.getType().equals(InvoiceType.SALES)?AccountEntryType.SALES_INVOICE:AccountEntryType.PURCHASE_INVOICE));
			entry.setSecurityLevel(invoice.getSecurityLevel());
			entry = getAccountEntryInvoiceWriter().insertorUpdateAccountEntry(entry, true);
			List taxBreakDown = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
			getAccountEntryInvoiceWriter().insertEntryDetails(entry, AccountUtil.obtainCustomerAccount(invoice.getRegistry()), invoice.getSeries(), invoice.getNumber(), getPriceStrategy().getTotalPrice(invoice, invoice), getRetentionTotal(taxBreakDown), getTaxQuota(taxBreakDown), getBasesPerAccount(invoice));
			getAccountEntryInvoiceWriter().insertAccountEntryInvoice(entry, invoice);
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoice.setStatus(InvoiceStatus.SCORED);
			if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
				invoice.setRegistryAddress(null);
			}
			invoiceBean.update(invoice);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
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
	private Map getBasesPerAccount(Invoice invoice) throws ManagerBeanException {
		Map basesPerAccount = new HashMap();
		Iterator<InvoiceDetail> iterator = invoice.getLines().iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = iterator.next();
			Account account = null;
			if (invoiceDetail.getItem() != null) {
				Integer productId = invoiceDetail.getItem().getProduct().getId();
				ProductAccountType accountType = (invoice.getType().equals(InvoiceType.SALES))?ProductAccountType.SALES:ProductAccountType.PURCHASE; 
				IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), productId);
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), accountType);
				Iterator iter = productAccountBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					account = ((ProductAccount)iter.next()).getAccount();
				}
			}
			account = (account==null)?account = obtainDefaultAccount(invoice):account;

			double base = invoiceDetail.getTaxableBase();
			base += (basesPerAccount.containsKey(account))?((Double)basesPerAccount.get(account)).doubleValue():0;
			basesPerAccount.put(account, new Double(base));
		}
		return basesPerAccount;
	}

	@SuppressWarnings("unchecked")
	private Account obtainDefaultAccount(Invoice invoice) throws ManagerBeanException {
		String paramName = (invoice.getType().equals(InvoiceType.SALES)?DefaultAccounts.SALES_ACCOUNT:DefaultAccounts.PURCHASE_ACCOUNT);
		IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamsBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), paramName);
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
}