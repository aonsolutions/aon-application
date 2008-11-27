package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.InvoiceEntryDetail;
import com.code.aon.accounting.InvoiceEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.BankAccount;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.FinanceGenerator;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.Registry;
import com.code.aon.ui.util.AonUtil;

public class InvoiceEntryController {
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceEntryController.class.getName());
	
//	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private AccountEntryInvoiceWriter writer;
	
	private FinanceGenerator financeGenerator;
	
	private AccountEntryInvoice accountEntryInvoice;
	
	private boolean isNew;
	
	private boolean isNewDetail;
	
	private boolean isNewFinance;
	
	private InvoiceEntryHeader header;

	private DataModel details;
	
	private DataModel finances;
	
	private InvoiceEntryDetail currentDetail;
	
	private Finance currentFinance;
	
	
	public AccountEntryInvoiceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryInvoiceWriter();
		}
		return writer;
	}
	
	public FinanceGenerator getFinanceGenerator() {
		if(financeGenerator == null){
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	public AccountEntryInvoice getAccountEntryInvoice() {
		return accountEntryInvoice;
	}

	public void setAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice) {
		this.accountEntryInvoice = accountEntryInvoice;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isNewDetail() {
		return isNewDetail;
	}

	public void setNewDetail(boolean isNewDetail) {
		this.isNewDetail = isNewDetail;
	}

	public boolean isNewFinance() {
		return isNewFinance;
	}
	
	public boolean isAccountSource() throws ManagerBeanException{
		if(isNew || getAccountEntryInvoice() == null){
			return true;
		}else{
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getAccountEntryInvoice().getInvoice().getId());
			Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InvoiceDetail detail = (InvoiceDetail)iter.next();
				if(!detail.getSource().equals(InvoiceSource.ACCOUNT)){
					return false;
				}
			}
		}
		return true;
	}

	public void setNewFinance(boolean isNewFinance) {
		this.isNewFinance = isNewFinance;
	}

	public InvoiceEntryHeader getHeader() {
		return header;
	}

	public void setHeader(InvoiceEntryHeader header) {
		this.header = header;
	}

	public DataModel getFinances() {
		if(finances == null){
			finances = new ListDataModel(new LinkedList<Finance>());
		}
		return finances;
	}

	public void setFinances(DataModel finances) {
		this.finances = finances;
	}

	public DataModel getDetails() {
		if(details == null){
			details = new ListDataModel(new LinkedList<InvoiceEntryDetail>());
		}
		return details;
	}

	public void setDetails(DataModel details) {
		this.details = details;
	}
	
	public InvoiceEntryDetail getCurrentDetail() {
		return currentDetail;
	}

	public void setCurrentDetail(InvoiceEntryDetail currentDetail) {
		this.currentDetail = currentDetail;
	}

	public Finance getCurrentFinance() {
		return currentFinance;
	}

	public void setCurrentFinance(Finance currentFinance) {
		this.currentFinance = currentFinance;
	}
	

	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.header = new InvoiceEntryHeader();
		initializeHeader();
		header.setType(InvoiceType.SALES);

		this.details = new ListDataModel(new LinkedList<InvoiceEntryDetail>());
		this.currentDetail = null;
		this.setNewDetail(false);

		this.finances = new ListDataModel(new LinkedList<Finance>());
		this.currentFinance = null;
		this.setNewFinance(false);
	}

	private void initializeHeader() {
		Account account = new Account();
		account.setEntryEnabled(true);
		header.setAccount(account);
		header.setRegistry(new Registry());
		header.setDate(new Date());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		header.setTransaction(InvoiceTransactionType.NATIONAL);
		header.setInvestment(false);
	}
	
	public boolean isSales(){
		if(this.header != null){
			return header.getType().equals(InvoiceType.SALES);
		}
		return false;
	}
	
	public boolean isPurchase(){
		if(this.header != null){
			return header.getType().equals(InvoiceType.PURCHASE);
		}
		return false;
	}
	
	public boolean isExpense(){
		if(this.header != null){
			return header.getType().equals(InvoiceType.EXPENSES);
		}
		return false;
	}
	
	public void onNewDetail(ActionEvent event){
		this.isNewDetail = true;
		this.currentDetail = new InvoiceEntryDetail();
		Account a = (header.getAccount()!=null)?header.getAccount():null;
		this.currentDetail.setAccount(a);
	}
	
	public void onSelectDetail(ActionEvent event){
		this.currentDetail = (InvoiceEntryDetail)details.getRowData();
	}
	
	@SuppressWarnings("unchecked")
	public void onAddDetail(ActionEvent event) throws ManagerBeanException{
		if (validateDetail(this.currentDetail)) {
			applySurcharge();
			((List<InvoiceEntryDetail>) this.details.getWrappedData()).add(this.currentDetail);
			this.currentDetail = new InvoiceEntryDetail();
			this.setNewDetail(false);
			onNewDetail(event);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onRemoveDetail(ActionEvent event){
		((LinkedList<InvoiceEntryDetail>)this.details.getWrappedData()).remove(this.currentDetail);
	}

	public void onCancelDetail(ActionEvent event){
		this.currentDetail = new InvoiceEntryDetail();
		this.setNewDetail(false);
	}
	
	@SuppressWarnings("unchecked")
	public void onUpdateDetail(ActionEvent event) throws ManagerBeanException{
		if (validateDetail(this.currentDetail)) {
			applySurcharge();
			int i = ((LinkedList<InvoiceEntryDetail>)this.details.getWrappedData()).indexOf(this.currentDetail);
			((LinkedList<InvoiceEntryDetail>)this.details.getWrappedData()).remove(i);
			((LinkedList<InvoiceEntryDetail>)this.details.getWrappedData()).add(i, this.currentDetail);
			this.currentDetail = new InvoiceEntryDetail();
		}
	}

	private boolean validateDetail(InvoiceEntryDetail detail) {
		if (detail.getAccount()!=null && !detail.getAccount().equals("") && detail.getAccount().getId() != null) {
			try {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), detail.getAccount().getId());
				Iterator<ITransferObject> iterator = accountBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					Account account = (Account)iterator.next();
					if (!account.isEntryEnabled()) {
						AonUtil.addErrorMessage("La Cuenta Contable " + detail.getAccount() + " no permite apuntes.");
						return false;
					}
				} else {
					AonUtil.addErrorMessage("La Cuenta Contable " + detail.getAccount() + " no existe.");
					return false;
				}
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("Error al obtener el pojo de la cuenta con id = " + detail.getAccount());
				return false;
			}
		}
		return true;
	}

	public void onNewFinance(ActionEvent event){
		this.isNewFinance = true;
		this.currentFinance = initializeFinance();
	}
	
	public void onSelectFinance(ActionEvent event) throws ManagerBeanException{
		this.currentFinance = (Finance)finances.getRowData();
	}
	
	private void applySurcharge() throws ManagerBeanException {
		if(isSales()){
			currentDetail.calculateSurcharge((obtainCustomer(getHeader().getRegistry().getId()).isSurcharge()));
		}else if(isPurchase()){
			currentDetail.calculateSurcharge(obtainCompany().isSurcharge());
		}else{
			currentDetail.calculateSurcharge(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onAddFinance(ActionEvent event){
		((List<Finance>)this.finances.getWrappedData()).add(this.currentFinance);
		this.currentFinance = initializeFinance();
		this.setNewFinance(false);
	}
	
	@SuppressWarnings("unchecked")
	public void onRemoveFinance(ActionEvent event){
		((List<Finance>)this.finances.getWrappedData()).remove(this.currentFinance);
	}

	public void onCancelFinance(ActionEvent event){
		this.currentFinance = initializeFinance();
		this.setNewFinance(false);
	}
	
	@SuppressWarnings("unchecked")
	public void onUpdateFinance(ActionEvent event){
		int i = ((List<Finance>)this.finances.getWrappedData()).indexOf(this.currentFinance);
		((List<Finance>)this.finances.getWrappedData()).remove(i);
		((List<Finance>)this.finances.getWrappedData()).add(i, this.currentFinance);
		this.currentFinance = initializeFinance();
	}

	private Finance initializeFinance() {
		Finance finance = new Finance();
		finance.setDueDate(new Date());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setAmount(obtainInitialAmount());
		finance.setBankAccount(new BankAccount());
		return finance;
	}
	
	@SuppressWarnings("unchecked")
	private double obtainInitialAmount() {
		Iterator<InvoiceEntryDetail> iter = ((List<InvoiceEntryDetail>)this.details.getWrappedData()).iterator();
		double detailSum = 0; 
		while(iter.hasNext()){
			InvoiceEntryDetail detail = iter.next();
			detailSum += detail.getTotal();
		}
		Iterator financeIter  = ((LinkedList)this.finances.getWrappedData()).iterator();
		double financeSum = 0;
		while(financeIter.hasNext()){
			Finance finance = (Finance)financeIter.next();
			financeSum += finance.getAmount();
		}
		return round(detailSum - financeSum, 2);
	}

	public void onTypeChanged(ActionEvent event){
		initializeHeader();
	}
	
	public void accept(ActionEvent event) throws ManagerBeanException{
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryInvoice(this.getAccountEntryInvoice());
			deleteInvoice(getAccountEntryInvoice().getInvoice());
			deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
			entry = this.getAccountEntryInvoice().getAccountEntry();
		}
		entry.setAccountPeriod(getHeader().getPeriod().getId());
		entry.setEntryDate(getHeader().getDate());
		entry.setJournal(null);
		entry.setSecurityLevel(getHeader().getSecurityLevel());
		Account account = new Account();
		if(getHeader().getType().equals(InvoiceType.SALES)){
			entry.setType(AccountEntryType.SALES_INVOICE);
			account = AccountUtil.obtainCustomerAccount(getHeader().getRegistry());
		}else{
			if(getHeader().getType().equals(InvoiceType.PURCHASE)){
				entry.setType(AccountEntryType.PURCHASE_INVOICE);
				account = AccountUtil.obtainSupplierAccount(getHeader().getRegistry());
			}else{
				if(getHeader().getType().equals(InvoiceType.EXPENSES)){
					entry.setType(AccountEntryType.EXPENSE_INVOICE);
					account = AccountUtil.obtainCreditorAccount(getHeader().getRegistry());
				}
			}
		}
		Invoice invoice = insertInvoice();
		insertInvoiceDetails(invoice);
		insertFinances(invoice);
		
		entry = getWriter().insertOrUpdateAccountEntry(entry, this.isNew);
		getWriter().insertEntryDetails(entry, account, invoice.getSeries(), invoice.getNumber(), getInvoiceTotal(), obtainTotalRetention(), obtainVATandSurchargeQuota(), obtainBasesPerAccount(details));
		this.setAccountEntryInvoice(getWriter().insertAccountEntryInvoice(entry, invoice));
		
//		this.isNew = false;
//		loadAccountEntryController(entry);

		onReset(event);
	}
	
	/**
	 * Gets the invoice total.
	 * 
	 * @return the invoice total
	 */
	private double getInvoiceTotal() {
		double total = 0.0;
		Iterator<?> iter = ((List<?>)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			InvoiceEntryDetail detail = (InvoiceEntryDetail)iter.next();
			total += detail.getTotal();
		}
		return total;
	}
	
	/**
	 * Obtain VA tand surcharge quota.
	 * 
	 * @return the double
	 */
	private double obtainVATandSurchargeQuota() {
		double total = 0.0;
		Iterator<?> iter = ((List<?>)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			InvoiceEntryDetail detail = (InvoiceEntryDetail)iter.next();
			total += detail.getVatQuota() + detail.getSurcharge();
		}
		return total;
	}

	/**
	 * Get the bases per account map.
	 * 
	 * @return the double
	 */
	private Map<Account, Double> obtainBasesPerAccount(DataModel details) {
		Map<Account, Double> basesPerAccount = new HashMap<Account, Double>();
		Iterator<?> iterator = ((List<?>)details.getWrappedData()).iterator();
		while (iterator.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail)iterator.next();
			Account account = detail.getAccount();
			double base = detail.getTaxableBase();
			base += (basesPerAccount.containsKey(account))?((Double)basesPerAccount.get(account)).doubleValue():0;
			basesPerAccount.put(account, new Double(base));
		}
		return basesPerAccount;
	}

	/**
	 * Obtain total retention.
	 * 
	 * @return the double
	 */
	private double obtainTotalRetention() {
		double total = 0.0;
		Iterator<?> iter = ((List<?>)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			InvoiceEntryDetail detail = (InvoiceEntryDetail)iter.next();
			total += detail.getRetentionQuota();
		}
		return total;
	}
	
	public void onRemove(ActionEvent event){
		deleteAccountEntryInvoice(this.getAccountEntryInvoice());
		deleteInvoice(getAccountEntryInvoice().getInvoice());
		deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
		deleteAccountEntry(getAccountEntryInvoice().getAccountEntry());
	}
	
	private Invoice insertInvoice() {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Invoice invoice = createInvoice();
			return (Invoice)invoiceBean.insert(invoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoice", e);
		}
		return null;
	}
	
	private Invoice createInvoice() throws ManagerBeanException{
		Invoice invoice = new Invoice();
		invoice.setIssueDate(getHeader().getDate());
		invoice.setSeries(getHeader().getSeries());
		if(getHeader().getType().equals(InvoiceType.SALES)){
			invoice.setNumber(calculateNextNumber(getHeader().getSeries(), getHeader().getType()));
		}else{
			invoice.setNumber(getHeader().getNumber());
		}
		invoice.setReferenceCode(getHeader().getReferenceCode());
		invoice.setRegistry(getHeader().getRegistry());
		invoice.setRegistryDocument(getHeader().getDocument());
		invoice.setRegistryName(getHeader().getName());
		invoice.setStatus(InvoiceStatus.SCORED);
		invoice.setType(getHeader().getType());
		invoice.setSecurityLevel(getHeader().getSecurityLevel());
		invoice.setInvestment(getHeader().isInvestment());
		invoice.setTransaction(getHeader().getTransaction());
		return invoice;
	}
	
	private int calculateNextNumber(String series, InvoiceType invoiceType) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), series);
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE), invoiceType);
		Projection projection = Projection.max(invoiceBean.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if(value != null){
			return ((Integer)value).intValue() + 1;
		}
		return 1;
	}
	
	private void insertInvoiceDetails(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Iterator<?> iter = ((List<?>)details.getWrappedData()).iterator();
			while(iter.hasNext()){
				InvoiceEntryDetail detail = (InvoiceEntryDetail)iter.next();
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setDeliveryDetail(null);
				invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setItem(null);
				invoiceDetail.setSource(InvoiceSource.ACCOUNT);
				invoiceDetail.setWorkPlace(obtainWorkPlace());
				invoiceDetail.setTaxableBase(detail.getTaxableBase());
				invoiceDetail.setPrice(detail.getTaxableBase());
				invoiceDetail.setQuantity(1);
				invoiceDetail = (InvoiceDetail) invoiceDetailBean.insert(invoiceDetail);
				insertInvoiceTaxes(invoiceDetail, detail);
				insertInvoiceAccounts(invoiceDetail, detail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoiceDetails for invoice with id= " + invoice.getId(), e);
		}
	}
	
	private WorkPlace obtainWorkPlace() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Iterator<ITransferObject> iter = workPlaceBean.getList(null).iterator();
		if(iter.hasNext()){
			return (WorkPlace)iter.next();
		}
		return null;
	}

	private void insertInvoiceTaxes(InvoiceDetail invoiceDetail, InvoiceEntryDetail detail) {
		try {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			InvoiceTax invoiceTax = new InvoiceTax();
			if(detail.getVat().getPercentage() > 0){
				invoiceTax.setInvoiceDetail(invoiceDetail);
				invoiceTax.setPercentage(detail.getVat().getPercentage());
				invoiceTax.setSurcharge(detail.getVat().getSurcharge());
				invoiceTax.setTaxType(TaxType.VAT);
				invoiceTaxBean.insert(invoiceTax);
			}
			if(detail.getRetention()!= null && detail.getRetention().getPercentage() > 0){
				invoiceTax = new InvoiceTax();
				invoiceTax.setInvoiceDetail(invoiceDetail);
				invoiceTax.setPercentage(detail.getRetention().getPercentage());
				invoiceTax.setSurcharge(detail.getRetention().getSurcharge());
				invoiceTax.setTaxType(TaxType.RETENTION);
				invoiceTaxBean.insert(invoiceTax);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoiceTaxes for InvoiceDetail with id= " + invoiceDetail.getId(), e);
		}
	}

	private void insertInvoiceAccounts(InvoiceDetail invoiceDetail, InvoiceEntryDetail detail) {
		try {
			IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			if (detail.getAccount() != null && !detail.getAccount().equals("")) {
				Account account = detail.getAccount();

				InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
				invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
				invoiceDetailAccount.setAccount(account);
				invoiceAccountBean.insert(invoiceDetailAccount);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoiceAccounts for InvoiceDetail with id= " + invoiceDetail.getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void insertFinances(Invoice invoice) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Iterator<Finance> iter = ((List<Finance>)finances.getWrappedData()).iterator();
			while(iter.hasNext()){
				Finance finance = iter.next();
				finance.setInvoice(invoice);
				finance.setRegistry(invoice.getRegistry());
				finance.setFinanceStatus(FinanceStatus.PENDING);
				financeBean.insert(finance);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting finances for invoice with id= " + invoice.getId(), e);
		}
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException {
		List<Finance> financeList = new LinkedList<Finance>();
		Invoice invoice = createInvoice();
		Registry registry = null;
		if(invoice.getType().equals(InvoiceType.SALES)){
			registry = invoice.getRegistry();
		}else {
			registry = obtainCompany();
		}
		if(registry != null && registry.getId() != null){
			financeList = getFinanceGenerator().generateFinances(invoice, registry, this.getInvoiceTotal(), false);
		}
		this.finances = new ListDataModel(financeList);		
	}
	
	private Company obtainCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
		if(iter.hasNext()){
			return (Company)iter.next();
		}
		return null;
	}
	
	private Customer obtainCustomer(Integer id) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), id);
		Iterator<ITransferObject> iter = customerBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (Customer)iter.next();
		}
		return null;
	}

	private void deleteInvoice(Invoice invoice) {
		deleteFinances(invoice);
		deleteInvoiceDetails(invoice);
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoiceBean.remove(invoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting invoice with id=" + invoice.getId(), e);
		}
	}
	
	private void deleteAccountEntryDetails(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator<ITransferObject> iter = accountEntryDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				accountEntryDetailBean.remove((AccountEntryDetail)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing details related with accountEntry with id= " + accountEntry.getId(), e);
		}
	}
	
	private void deleteAccountEntry(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			accountEntryBean.remove(accountEntry);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting AccountEntry with id=" + accountEntry.getId(), e);
		}
	}
	
	private void deleteInvoiceDetails(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator<ITransferObject> iter = invoiceDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				Criteria taxCriteria = new Criteria();
				taxCriteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<ITransferObject> taxIter = invoiceTaxBean.getList(taxCriteria).iterator();
				while(taxIter.hasNext()){
					invoiceTaxBean.remove((InvoiceTax)taxIter.next());
				}

				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(invoiceAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<ITransferObject> accountIter = invoiceAccountBean.getList(accountCriteria).iterator();
				while(accountIter.hasNext()){
					invoiceAccountBean.remove((InvoiceDetailAccount)accountIter.next());
				}
				invoiceDetailBean.remove(invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error deleting invoiceDetails related with invoice with id= " + invoice.getId(),e);
		}
	}
	
	private void deleteFinances(Invoice invoice) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
			Iterator<ITransferObject> iter = financeBean.getList(criteria).iterator();
			while(iter.hasNext()){
				financeBean.remove(iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting finances related with invoice with id= " + invoice.getId(), e);
		}
	}
	
	private void deleteAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice) {
		try {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			accountEntryInvoiceBean.remove(accountEntryInvoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error deleting accountEntryInvoice with id= " + accountEntryInvoice.getId(), e);
		}
	}
/*	
	private void loadAccountEntryController(AccountEntry entry) {
		try {
			AccountEntryController entryController = (AccountEntryController)AonUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountEntryController", e);
		}
	}
*/	
    private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
    
	public List<SelectItem> getRegistryBanks() throws ManagerBeanException {
		return getRegistryBanks( header.getRegistry());
	}
	
	public List<SelectItem> getRegistryBanks(Registry registry) throws ManagerBeanException {
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		LinkedList<SelectItem> rBanks = new LinkedList<SelectItem>();
		Iterator<?> iter = rBankBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			RegistryBank rBank = (RegistryBank) iter.next();
			SelectItem item = new SelectItem(rBank.getBank(), rBank.getBank().getName() + " [" + rBank.getBankAccount().toString() + "]");
			rBanks.add(item);
		}
		return rBanks;
	}
	
}