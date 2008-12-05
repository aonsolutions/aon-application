package com.code.aon.ui.sales.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Series;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.ConsoleInvoicingFeedBack;
import com.code.aon.finance.invoicing.CustomerFeeInvoicingDAO;
import com.code.aon.finance.invoicing.CustomerFeeInvoicingEngine;
import com.code.aon.finance.invoicing.FinanceGenerator;
import com.code.aon.finance.invoicing.IInvoicingEngine;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.finance.invoicing.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

@Deprecated
public class FeeInvoicingController extends BasicController {
	
	private static final String FEE_INVOICING_ADDRESS_CONTROLLER_NAME = "feeInvoicingAddress";
	private static final String FEE_INVOICING_DETAIL_CONTROLLER_NAME = "feeInvoicingDetail";
	private static final String FEE_FINANCE_CONTROLLER_NAME = "feeFinance";

	private InvoicingParameters invoicingParams;

	private IInvoicingEngine engine;
	
	private IPriceStrategy priceStrategy;
	
	private FinanceGenerator financeGenerator;
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	private List<SelectItem> addresses;
	
	private String seriesDescripition;

	public FeeInvoicingController() {
		this.invoicingParams = new InvoicingParameters();
	}

	public InvoicingParameters getInvoicingParams() {
		return invoicingParams;
	}

	public String getSeriesDescripition() {
		return seriesDescripition;
	}

	public void setSeriesDescripition(String seriesDescripition) {
		this.seriesDescripition = seriesDescripition;
	}

	public void setInvoicingParams(InvoicingParameters invoicingParams) {
		this.invoicingParams = invoicingParams;
	}
	
	public void onInitialize(ActionEvent event) throws ManagerBeanException{
		this.invoicingParams = new InvoicingParameters();
		this.invoicingParams.setNumber(obtainMaxNumber(null));
		this.invoicingParams.setSecurityLevel(SecurityLevel.OFFICIAL);
		this.invoicingParams.setInvoiceDate(new Date());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		invoicingParams.setMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		invoicingParams.setYear(calendar.get(Calendar.YEAR));
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), seriesId);
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.SALES);
		Projection projection = Projection.max(invoiceBean.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if(value != null){
			return ((Integer)value).intValue() + 1;
		}
		return 1;
	}
	
	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException{
		if (event.getPhaseId() == PhaseId.ANY_PHASE) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
		}
		if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
			int number = obtainMaxNumber((String)event.getNewValue());
			SecurityLevel securityLevel = obtainSeriesSecurityLevel((String)event.getNewValue());
			if(this.getTo() != null){
				((Invoice)this.getTo()).setNumber(number);
				((Invoice)this.getTo()).setSecurityLevel(securityLevel);
			}
			if(getInvoicingParams() != null){
				getInvoicingParams().setNumber(number);
				getInvoicingParams().setSecurityLevel(securityLevel);
				getInvoicingParams().setWorkPlaceId(obtainSeriesWorkPlace((String)event.getNewValue()));
			}
		}
	}

	public void onSeriesChangedInvoicing(ValueChangeEvent event) throws ManagerBeanException{
		if (event.getPhaseId() == PhaseId.ANY_PHASE) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION );
			event.queue();
		}
		if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
			getInvoicingParams().setNumber(obtainMaxNumber((String)event.getNewValue()));	
		}
	}

	@SuppressWarnings("unchecked")
	private SecurityLevel obtainSeriesSecurityLevel(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), seriesId);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if(iter.hasNext()){
			Series series = (Series)iter.next(); 
			if(series.getSecurityLevel() != null){
				return series.getSecurityLevel();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Integer obtainSeriesWorkPlace(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), seriesId);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if(iter.hasNext()){
			Series series = (Series)iter.next(); 
			if(series.getWorkPlace() != null){
				return series.getWorkPlace().getId();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void onInvoice(ActionEvent event) throws InvoicingException, ManagerBeanException, ExpressionException {
	    IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
        Criteria criteria = new Criteria();
        criteria.addLessThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE), invoicingParams.getInvoiceDate());
        criteria.addGreaterThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_DEADLINE), invoicingParams.getInvoiceDate());
        if (periodBean.getList(criteria).size() == 0) {
            addMessage("There is no Account Period defined for this Invoicing Date");
        } else {
            InvoicingEngineFactory.register("customerFeeEngine", new CustomerFeeInvoicingEngine());
    		engine = InvoicingEngineFactory.getInvoicingEngine("customerFeeEngine");
    		engine.setInvoicingDAO(new CustomerFeeInvoicingDAO());
    		engine.setInvoicingFeedBack(new ConsoleInvoicingFeedBack());
    		engine.invoice(invoicingParams);
    		List invoicedList = (List) engine.getInvoicingDAO().getCollection();
    		criteria = new Criteria();
    		if(invoicedList.size() == 0){
    			criteria.addNullExpression(getManagerBean().getFieldName(IFinanceAlias.INVOICE_ID));
    		}else{
    			Iterator iter = invoicedList.iterator();
    			while(iter.hasNext()){
    				Invoice invoice = (Invoice)iter.next();
    				criteria.addOrExpression(getManagerBean().getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId().toString());
    				recordInvoice(invoice);
    			}
    		}
    		setCriteria(criteria);
    		this.onSearch(null);
        }
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

	public String getAddress() throws ManagerBeanException {
		RegistryAddress rAddress = ((Invoice)this.getTo()).getRegistryAddress();
		String address = (rAddress!=null)?rAddress.getAddress()+" "+rAddress.getAddress2()+" "+rAddress.getAddress3():"";
		BasicController addressController = (BasicController)FormUtil.getController(FEE_INVOICING_ADDRESS_CONTROLLER_NAME);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			InvoiceAddress invoiceAddress = (InvoiceAddress)addressController.getTo();
			address = invoiceAddress.getAddress() + " " + invoiceAddress.getAddress2();
		}
		return address;
	}

	public String getCity() throws ManagerBeanException {
		RegistryAddress rAddress = ((Invoice)this.getTo()).getRegistryAddress();
		String city = (rAddress!=null)?rAddress.getCity():"";
		BasicController addressController = (BasicController)FormUtil.getController(FEE_INVOICING_ADDRESS_CONTROLLER_NAME);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			InvoiceAddress invoiceAddress = (InvoiceAddress)addressController.getTo();
			city = invoiceAddress.getCity();
		}
		return city;
	}

	@SuppressWarnings("unchecked")
	public void customerData(ValueChangeEvent event) throws ManagerBeanException, ExpressionException{
		if(event.getNewValue() != null && !event.getNewValue().equals("")){
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), event.getNewValue().toString());
			Iterator iter = customerBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Customer customer = (Customer)iter.next();
				((Invoice)this.getTo()).setRegistryName(customer.getRegistry().getName() + " " + customer.getRegistry().getSurname());
				((Invoice)this.getTo()).setRegistryDocument(customer.getRegistry().getDocument());
				((Invoice)this.getTo()).setRegistry(customer.getRegistry());
			}
			loadAddresses(new Integer(event.getNewValue().toString()));
		}
	}
	
	public double getToInvoiceTotalPrice(){
		Invoice invoice = (Invoice)this.getTo();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}

	/**
	 * Adds registry to the criteria
	 * 
	 * @param event
	 *            contains criteria ident
	 * @throws ManagerBeanException
	 */
	public void addCustomerExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		if (event.getNewValue() != null
				&& !((String) event.getNewValue()).trim().equals("")) {
			try {
				Integer id = new Integer((String) event.getNewValue());
				Criteria criteria = getCriteria();
				criteria.addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), id);
				setCriteria(criteria);
			} catch (Exception e) {
			}
		}
	}

	public void addFromIssueDateExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		if (event.getNewValue() != null) {
			Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}

	public void addToIssueDateExpression(ValueChangeEvent event)
			throws ManagerBeanException {
		if (event.getNewValue() != null) {
			Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	
	public FinanceGenerator getFinanceGenerator() {
		if(financeGenerator == null){
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}
	
	@SuppressWarnings("unchecked")
	public Customer getCustomer() throws ManagerBeanException{
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		if((Invoice)this.getTo() != null){
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), ((Invoice)this.getTo()).getRegistry().getId());
			Iterator iter = customerBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Customer)iter.next();
			}
			
		}
		return null;
	}
	
	/*
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("feeInvoice");
        manager.setOutputFormat(OutputFormat.PDF);
    }
    */
	
	public boolean isRemovable(){
		FeeInvoicingDetailController feeInvoicingDetailController = (FeeInvoicingDetailController)FormUtil.getController(FEE_INVOICING_DETAIL_CONTROLLER_NAME);
		Invoice invoice = (Invoice)this.getTo();
		if(feeInvoicingDetailController.getTo() == null && invoice.getStatus().equals(InvoiceStatus.PENDING)){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IController feeFinanceController = FormUtil.getController(FEE_FINANCE_CONTROLLER_NAME);
			List financeList = ((List)feeFinanceController.getModel().getWrappedData());
			if(existFinanceTrackings(financeList)){
				AonUtil.addInfoMessage("No se puede generar vencimientos automaticamente. Alguno de ellos tiene operaciones anteriores.");
				throw new AbortProcessingException();
			}else{
				Iterator iter = financeList.iterator();
				while(iter.hasNext()){
					Finance finance = (Finance)iter.next();
					financeBean.remove(finance);
				}
				getFinanceGenerator().generateFinances(invoice, invoice.getRegistry(), getPriceStrategy().getTotalPrice(invoice, invoice));
				feeFinanceController.onSearch(null);
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
		entry.setType(AccountEntryType.SALES_INVOICE);
		entry.setSecurityLevel(invoice.getSecurityLevel());
		entry = getAccountEntryInvoiceWriter().insertOrUpdateAccountEntry(entry, true);
		List taxBreakDown = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
		getAccountEntryInvoiceWriter().insertEntryDetails(entry, AccountUtil.obtainCustomerAccount(invoice.getRegistry()), invoice.getSeries(), invoice.getNumber(), getPriceStrategy().getTotalPrice(invoice, invoice), getRetentionTotal(taxBreakDown), getTaxQuota(taxBreakDown), obtainBasesPerAccount(invoice));
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
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), ProductAccountType.SALES);
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
		criteria.addEqualExpression(appParamsBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), DefaultAccounts.SALES_ACCOUNT);
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

	public boolean isRecorded(){
		Invoice invoice = (Invoice)this.getTo();
		if(invoice.getStatus() != null){
			return invoice.getStatus().equals(InvoiceStatus.SCORED);
		}
		return false;
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