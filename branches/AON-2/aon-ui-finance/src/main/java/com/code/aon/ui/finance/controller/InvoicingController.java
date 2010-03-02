package com.code.aon.ui.finance.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

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
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.FinanceGenerator;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.dao.ISupplierAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * The invoicing controller 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InvoicingController extends BasicController {
	
	/**
	 * The logger of the class
	 */
	private static final Logger LOGGER = Logger.getLogger(InvoicingController.class.getName());
	
	/**
	 * Income controller's name
	 */
	private static final String INCOME_CONTROLLER_NAME = "income";

	/**
	 * IncomeDetail controller's name
	 */
	private static final String INVOICING_DETAIL_CONTROLLER_NAME = "invoicingDetail";
	
	/** PurchaseFinance controllers name. */
	private static final String PURCHASE_FINANCE_CONTROLLER_NAME = "purchaseFinance";
	
	/**
	 * Price strategy
	 */
	private IPriceStrategy priceStrategy;
	
	/** The finance generator. */
	private FinanceGenerator financeGenerator;
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	/**
	 * Warehouse ident
	 */
	private Integer warehouseId;
	
	/**
	 * Returns the warehouse ident
	 * 
	 * @return warehouse ident
	 */
	public Integer getWarehouseId() {
		return warehouseId;
	}

	/**
	 * Assigns the warehouse ident
	 * 
	 * @param warehouseId warehouse ident
	 */
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	/**
	 * Resets the controller
	 * 
	 * @param event menu event
	 */
	public void onReset(MenuEvent event){
		super.onReset(null);
	}
	
	public boolean isEditable() {
		if(this.getTo() != null){
			if(((Invoice)this.getTo()).getStatus() == null){
				return true;
			}
			return ((Invoice)this.getTo()).getStatus().equals(InvoiceStatus.PENDING);
		}
		return false;
	}
	
	public boolean isRecorded(){
		Invoice invoice = (Invoice)this.getTo();
		if(invoice.getStatus() != null){
			return invoice.getStatus().equals(InvoiceStatus.SCORED);
		}
		return false;
	}
	
	/**
	 * Recovers the supplier if exists
	 * and creates a new registry with suppliers data 
	 * to assign to the Invoice
	 * 
	 * @param event contains a supplier ident
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public void supplierData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supplierBean.getFieldName(ISupplierAlias.SUPPLIER_ID),event.getNewValue());
			Iterator iter = supplierBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Supplier supplier = (Supplier)iter.next();
				((Invoice)this.getTo()).setRegistry(supplier.getRegistry());
			}
		}
	}
	
	/**
	 * Transfers checked incomes to invoicedetails, and updates incomes as closed
	 * 
	 * @param event the action event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public void onInvoice(ActionEvent event) throws ManagerBeanException{
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		Iterator iter = ((List)incomeController.getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			Income income = (Income)iter.next();
			if(incomeController.isChecked(income)){
				IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
				insertInvoiceDetails((Invoice)this.getTo(), income);
				income.setIncomeStatus(IncomeStatus.CLOSED);
				incomeBean.update(income);
			}
		}
		generateFinances(null);
		incomeController.clearCheckList();
		InvoicingDetailController invoicingDetailController = (InvoicingDetailController) AonUtil.getController(INVOICING_DETAIL_CONTROLLER_NAME);
		invoicingDetailController.onSearch(null);
	}
	
	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException, ExpressionException{
		Invoice invoice = (Invoice)this.getTo();
		recordInvoice(invoice);
	}
	
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
		entry.setType((invoice.getType().equals(InvoiceType.SALES)?AccountEntryType.SALES_INVOICE:AccountEntryType.PURCHASE_INVOICE));
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
	
	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
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
				ProductAccountType accountType = (invoice.getType().equals(InvoiceType.SALES))?ProductAccountType.SALES:ProductAccountType.PURCHASE; 
				IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				criteria = new Criteria();
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

			insertInvoiceDetailAccount(invoiceDetail, account);
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
	
	private void insertInvoiceDetailAccount(InvoiceDetail invoiceDetail, Account account) throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
		invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
		invoiceDetailAccount.setAccount(account);
		invoiceAccountBean.insert(invoiceDetailAccount);
	}

	/**
	 * Updates the income as pending, and removes the invoicedetails
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void onRemoveIncome(ActionEvent event) throws ManagerBeanException{
		IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
		Income income = (Income)incomeController.getModel().getRowData();
		removeInvoiceDetails(income.getId());
		IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
		income.setIncomeStatus(IncomeStatus.PENDING);
		incomeBean.update(income);
		generateFinances(null);
		InvoicingDetailController invoicingDetailController = (InvoicingDetailController) AonUtil.getController(INVOICING_DETAIL_CONTROLLER_NAME);
		invoicingDetailController.onSearch(null);
		incomeController.clearCheckList();
	}

	/**
	 * Recovers all IncomeDetails of this income and transfers to 
	 * the invoice as invoicedetails
	 * 
	 * @param invoice
	 * @param income
	 */
	@SuppressWarnings("unchecked")
	private void insertInvoiceDetails(Invoice invoice, Income income) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setSourceId(incomeDetail.getId());
				invoiceDetail.setDescription(incomeDetail.getDescription());
				invoiceDetail.setDiscountExpression(incomeDetail.getDiscountExpression());
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setItem(incomeDetail.getItem());
				invoiceDetail.setPrice(incomeDetail.getPrice());
				invoiceDetail.setQuantity(incomeDetail.getQuantity());
				invoiceDetail.setSource(InvoiceSource.INCOME);
				invoiceDetail.setTaxes(incomeDetail.getTaxes());
				invoiceDetail.setTaxableBase(obtainTaxableBase(invoiceDetail));
				invoiceDetail.setWorkPlace(obtainWorkPlace());
				invoiceDetailBean.insert(invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error inserting invoiceDetails for invoice with id= " + invoice.getId(), e);
		}
	}
	
	/**
	 * Returns workplace.
	 * 
	 * @return workplace
	 */
	private WorkPlace obtainWorkPlace() {
		try {
			IManagerBean wpBean = BeanManager.getManagerBean(WorkPlace.class);
			List<ITransferObject> wpLst = wpBean.getList(null);
			if (wpLst.size() > 0) {
				WorkPlace wp = (WorkPlace)wpLst.get(0);
				return wp;
			}
		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
		}
		return null;
	}

	/**
	 * Asks the price strategy the taxable base
	 * 
	 * @param invoiceDetail the InvoiceDetail line
	 * @return the taxable base
	 */
	private double obtainTaxableBase(InvoiceDetail invoiceDetail) {
		return getPriceStrategy().getBasePrice(invoiceDetail);
	}
	
	/**
	 * Ask price strategy the total price of the transfer object invoice
	 * 
	 * @return the total price
	 * @throws ManagerBeanException
	 */
	public double getToInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	
	/**
	 * Ask price strategy the total price of the selected row invoice
	 * 
	 * @return the total price
	 * @throws ManagerBeanException
	 */
	public double getInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	
	/**
	 * Returns price strategy
	 * 
	 * @return price strategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	/**
	 * Gets the finance generator.
	 * 
	 * @return the finance generator
	 */
	public FinanceGenerator getFinanceGenerator() {
		if(financeGenerator == null){
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}
	
	@SuppressWarnings("unchecked")
	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IController purchaseFinanceController = AonUtil.getController(PURCHASE_FINANCE_CONTROLLER_NAME);
			Iterator iter = ((List)purchaseFinanceController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				financeBean.remove(finance);
			}
			Company company = obtainCompany();
			getFinanceGenerator().generateFinances(invoice, company, getPriceStrategy().getTotalPrice(invoice, invoice));
			purchaseFinanceController.onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error generating finances for invoice with id= " + invoice.getId(),e);
		}
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

	/**
	 * Recovers all incomedetails with this income ident
	 * and removes all invoicedetail linked to this
	 * 
	 * @param incomeId the income ident
	 */
	@SuppressWarnings("unchecked")
	public void removeInvoiceDetails(Integer incomeId) {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), incomeId);
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_TYPE), InvoiceType.PURCHASE);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), incomeDetail.getId());
				Iterator iterator = invoiceDetailBean.getList(criteria).iterator();
				while(iterator.hasNext()){
					invoiceDetailBean.remove((InvoiceDetail)iterator.next());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing invoiceDetails linked with income with id= " + incomeId, e);
		}
	}
	
	/**
	 * Returns invoice collection
	 * 
	 * @see com.code.aon.ui.form.BasicController#getCollection()
	 */
	@SuppressWarnings("unchecked")
	public Collection getCollection(){
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		l.add(obtainInvoice(((Invoice)this.getTo()).getId()));
		return l;
	}
	
	/**
	 * Returns the invoice of this invoice ident
	 * 
	 * @param invoiceId invoice ident
	 * @return related invoice
	 */
	@SuppressWarnings("unchecked")
	private ITransferObject obtainInvoice(Integer invoiceId) {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ID), invoiceId);
			Iterator iter = invoiceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Invoice)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining invoice with id= " + invoiceId, e);
		}
		return null;
	}
	
	/**
	 * Adds to criteria the invoice date. Greater than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addIssuedate1Expression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to criteria the invoice date. Less than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addIssuedate2Expression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to the criteria the supplier
	 * 
	 * @param event contains supplier's registry ident
	 */
	public void addSupplierEqualExpression(ValueChangeEvent event){
		if(event.getNewValue() != null && !((String)event.getNewValue()).trim().equals("")){
			try{
				Integer id = new Integer((String)event.getNewValue());
				Criteria criteria = getCriteria();
				criteria.addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), id);
				setCriteria(criteria);
			} catch (Exception e) {
			}
		}
	}

    /**
     * Sets default parameters to report.
     * 
     * @param event that launched report
     */
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("invoice");
        manager.setOutputFormat(OutputFormat.PDF);
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
