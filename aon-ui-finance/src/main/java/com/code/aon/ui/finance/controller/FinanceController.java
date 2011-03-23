package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.event.FinanceSearchListener;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class FinanceController extends FinanceListController implements IFinanceConstants {

	private Company company;
	private boolean payment;
	private Date paymentDate;
	private double paymentAmount;
	private RegistryBank paymentRegistryBank;
	private PayMethodTypeDetail paymentPayMethodTypeDetail;
	private boolean paymentRecordable;
	private Date returnDate;
	private double returnExpenses;
	private int returnDeposit;
	private RegistryBank returnRegistryBank;
	private PayMethodTypeDetail returnPayMethodTypeDetail;
	private boolean returnRecordable;
	private FinanceGenerator financeGenerator;
	private AccountEntryFinanceWriter writer;
	private boolean showFinancePaymentWindow;
	private boolean showFinanceReturnWindow;
	private List<SelectItem> payMethodTypeDetailList;
	private Double totalFinanceAmount;
	private String invoiceViewer;
	private boolean purchase;
	private List<?> orderedList;

	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			setCompany( companyController.obtainCompany() );
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public RegistryBank getPaymentRegistryBank() {
		return paymentRegistryBank;
	}

	public void setPaymentRegistryBank(RegistryBank paymentRegistryBank) {
		this.paymentRegistryBank = paymentRegistryBank;
	}

	public PayMethodTypeDetail getPaymentPayMethodTypeDetail() {
		return paymentPayMethodTypeDetail;
	}

	public void setPaymentPayMethodTypeDetail(PayMethodTypeDetail paymentPayMethodTypeDetail) {
		this.paymentPayMethodTypeDetail = paymentPayMethodTypeDetail;
	}

	public boolean isPaymentRecordable() {
		return paymentRecordable;
	}

	public void setPaymentRecordable(boolean paymentRecordable) {
		this.paymentRecordable = paymentRecordable;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public double getReturnExpenses() {
		return returnExpenses;
	}

	public void setReturnExpenses(double returnExpenses) {
		this.returnExpenses = returnExpenses;
	}

	public int getReturnDeposit() {
		return returnDeposit;
	}

	public void setReturnDeposit(int returnDeposit) {
		this.returnDeposit = returnDeposit;
	}

	public RegistryBank getReturnRegistryBank() {
		return returnRegistryBank;
	}

	public void setReturnRegistryBank(RegistryBank returnRegistryBank) {
		this.returnRegistryBank = returnRegistryBank;
	}

	public PayMethodTypeDetail getReturnPayMethodTypeDetail() {
		return returnPayMethodTypeDetail;
	}

	public void setReturnPayMethodTypeDetail(PayMethodTypeDetail returnPayMethodTypeDetail) {
		this.returnPayMethodTypeDetail = returnPayMethodTypeDetail;
	}

	public boolean isReturnRecordable() {
		return returnRecordable;
	}

	public void setReturnRecordable(boolean returnRecordable) {
		this.returnRecordable = returnRecordable;
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	public AccountEntryFinanceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public boolean isShowFinancePaymentWindow() {
		return showFinancePaymentWindow;
	}

	public void setShowFinancePaymentWindow(boolean value) {
		this.showFinancePaymentWindow = value;
	}
	
	public boolean isShowFinanceReturnWindow() {
		return showFinanceReturnWindow;
	}

	public void setShowFinanceReturnWindow(boolean value) {
		this.showFinanceReturnWindow = value;
	}

	public List<SelectItem> getPayMethodTypeDetailList() {
		return payMethodTypeDetailList;
	}

	public void setPayMethodTypeDetailList(List<SelectItem> payMethodTypeDetailList) {
		this.payMethodTypeDetailList = payMethodTypeDetailList;
	}

	public Double getTotalFinanceAmount() {
		return totalFinanceAmount;
	}

	public void setTotalFinanceAmount(Double totalFinanceAmount) {
		this.totalFinanceAmount = totalFinanceAmount;
	}

	public String getInvoiceViewer() {
		return invoiceViewer;
	}

	public void setInvoiceViewer(String invoiceViewer) {
		this.invoiceViewer = invoiceViewer;
	}

	public boolean isPurchase() {
		return purchase;
	}

	public void setPurchase(boolean purchase) {
		this.purchase = purchase;
	}

	public void registryData(LookupChangeEvent event) throws ManagerBeanException {
		Finance finance = (Finance)getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			IRegistry registry = (IRegistry)event.getNewValue();
			finance.setRegistry(registry.getRegistry());
			finance.setRegistryName(registry.getRegistry().getFullName());
			finance.setRegistryDocument(registry.getRegistry().getDocument());
			finance.setRegistryDocumentType(registry.getRegistry().getDocumentType());
			finance.setRegistryDocumentCountry(registry.getRegistry().getDocumentCountry());
		}
	}

	public void onPaymentTypeChanged(ValueChangeEvent event) {
		((Finance)getTo()).setRegistry(new Registry());
		((Finance)getTo()).setRegistryName(null);
		((Finance)getTo()).setRegistryDocument(null);
	}

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			Finance finance = (Finance)getTo();
			finance.setBank(new Bank());
			finance.setBankAccount(new BankAccount());
		}
	}

	public void onBankChanged(LookupChangeEvent event) {
		Finance finance = (Finance)getTo();
		finance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Bank bank = (Bank) event.getNewValue();
			finance.getBankAccount().setEntity(bank.getCode());			
		}
	}
	
	public void onRegistryBankChanged(ValueChangeEvent event) {
		Finance finance = (Finance)getTo();
		if (event.getNewValue() != null) {
			RegistryBank rb = (RegistryBank) event.getNewValue();
			finance.setBank(rb.getBank());
			finance.setBankAccount(rb.getBankAccount());
		} else {
			finance.setBank(null);
			finance.setBankAccount(null);
		}
	}

	public List<SelectItem> getBanks() throws ManagerBeanException {
		Finance finance = (Finance)getTo();
		if (finance != null && finance.getPayMethod() != null) {
			PayMethod pm = finance.getPayMethod();
			if ((!isPayment() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) || (isPayment() && pm.getType() == PayMethodType.BANK_TRANSFER)) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getRegistryBanks(finance.getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}

	public boolean isPending() {
    	return ((Finance)this.getTo()).getFinanceStatus().equals(FinanceStatus.PENDING);
    }
    
    public boolean isReturned(){
    	return ((Finance)this.getTo()).getFinanceStatus().equals(FinanceStatus.RETURNED);
    }

    public boolean isPaid(){
    	return ((Finance)this.getTo()).getFinanceStatus().equals(FinanceStatus.PAID);
    }

	public void onEditSearchCharge(ActionEvent event) {
		setPayment(false);
		super.onEditSearch(event);
	}

	public void onEditSearchPayment(ActionEvent event) {
		setPayment(true);
		super.onEditSearch(event);
	}

	public void onFinancePaymentShow(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)getTo();
		if (finance.getPayMethod() == null || finance.getPayMethod().getId() == null) {
			AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_PAY_METHOD_UNDEFINED_ERROR);
			throw new AbortProcessingException();
		} 
		super.accept(null);

		setPaymentDate(finance.getDueDate());
		setPaymentAmount(finance.getTotalAmount());
		if (finance.getPayMethod().getType() != PayMethodType.CASH_BASIS && finance.getPayMethod().getType() != PayMethodType.OTHER) {
			setPaymentRegistryBank(obtainPaymentRegistryBank(getCompany(), finance.getBank(), finance.getBankAccount()));
			setPaymentPayMethodTypeDetail(null);
		} else {
			setPaymentRegistryBank(null);
			setPaymentPayMethodTypeDetail(obtainPayMethodTypeDetail(finance.getPayMethod().getType()));
		}
		setPaymentRecordable(AonUtil.getRoleManager().isAccountingOperator());
	}

	private RegistryBank obtainPaymentRegistryBank(Registry registry, Bank bank, BankAccount bankAccount) throws ManagerBeanException {
		IManagerBean registryBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(registryBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		if (bank != null && bank.getId() != null) {
			criteria.addEqualExpression(registryBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_BANK_ID), bank.getId());
		}
		if (bankAccount != null && bankAccount.getValue() != null) {
			criteria.addEqualExpression(registryBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_BANK_ACCOUNT), bankAccount);
		}
		Iterator<ITransferObject> iterator = registryBankBean.getList(criteria, 0, 1).iterator();
		if (iterator.hasNext()) {
			return (RegistryBank)iterator.next();
		}
		criteria = new Criteria();
		criteria.addEqualExpression(registryBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		iterator = registryBankBean.getList(criteria, 0, 1).iterator();
		if (iterator.hasNext()) {
			return (RegistryBank)iterator.next();
		}
		return null;
	}

	public void onFinanceReturnShow(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)getTo();
		setReturnDate(new Date());
		setReturnExpenses(finance.getExpenses());
		if (finance.getPayMethod().getType() != PayMethodType.CASH_BASIS && finance.getPayMethod().getType() != PayMethodType.OTHER) {
			setReturnDeposit(0);
			setReturnRegistryBank(obtainReturnRegistryBank(getCompany(), finance));
			setReturnPayMethodTypeDetail(null);
		} else if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {
			setReturnDeposit(1);
			setReturnRegistryBank(null);
			setReturnPayMethodTypeDetail(obtainPayMethodTypeDetail(PayMethodType.CASH_BASIS));
		} else if (finance.getPayMethod().getType() == PayMethodType.OTHER) {
			setReturnDeposit(2);
			setReturnRegistryBank(null);
			setReturnPayMethodTypeDetail(obtainPayMethodTypeDetail(PayMethodType.OTHER));
		}
		setReturnRecordable(AonUtil.getRoleManager().isAccountingOperator());
	}

	@SuppressWarnings("unchecked")
	private RegistryBank obtainReturnRegistryBank(Registry registry, Finance finance) throws ManagerBeanException {
		Bank bank = finance.getBank();
		BankAccount bankAccount = finance.getBankAccount();
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.PAID);
		Iterator iterator = fBatchDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			FinanceBatchDetail detail = (FinanceBatchDetail)iterator.next();
			bank = detail.getFinanceBatch().getRegistryBank().getBank();
			bankAccount = detail.getFinanceBatch().getRegistryBank().getBankAccount();
		}
		return obtainPaymentRegistryBank(registry, bank, bankAccount);
	}

	private PayMethodTypeDetail obtainPayMethodTypeDetail(PayMethodType type) throws ManagerBeanException {
		refreshPayMethodTypeDetailList(type);

		PayMethodTypeDetail payMethodTypeDetail = null;
		if (getPayMethodTypeDetailsSize() == 1) {
			SelectItem selectItem = getPayMethodTypeDetailList().get(0);
			payMethodTypeDetail = (PayMethodTypeDetail)selectItem.getValue();
		}
		return payMethodTypeDetail;
	}

	private void refreshPayMethodTypeDetailList(PayMethodType type) throws ManagerBeanException {
		payMethodTypeDetailList = new LinkedList<SelectItem>();
		IManagerBean payMethodTypeDetailBean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(payMethodTypeDetailBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE_DETAIL_TYPE), type);
		criteria.addOrder(payMethodTypeDetailBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE_DETAIL_DESCRIPTION));
		Iterator<?> iter = payMethodTypeDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			PayMethodTypeDetail payMethodTypeDetail = (PayMethodTypeDetail) iter.next();
			SelectItem item = new SelectItem(payMethodTypeDetail, payMethodTypeDetail.getDescription());
			payMethodTypeDetailList.add(item);
		}
	}

	public int getPayMethodTypeDetailsSize() {
		return getPayMethodTypeDetailList().size();
	}

	public void onDepositChanged(ValueChangeEvent event) throws ManagerBeanException {
		int deposit = ((Integer)event.getNewValue()).intValue();
		if (deposit == 1) {
			setReturnPayMethodTypeDetail(obtainPayMethodTypeDetail(PayMethodType.CASH_BASIS));
		} else if (deposit == 2) {
			setReturnPayMethodTypeDetail(obtainPayMethodTypeDetail(PayMethodType.OTHER));
		}
	}

	public void onFinancePayment(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)this.getTo();
		if (getPaymentAmount() == 0) {
			AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_INVALID_AMOUNT_ERROR);
			throw new AbortProcessingException();
		}
		if (getPaymentAmount() != finance.getTotalAmount()) {
			double amount = finance.getTotalAmount();

			finance.setAmount(CommonUtil.round(getPaymentAmount() - finance.getExpenses(), 2));
			String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_FRACTIONED, 1, 2);
			FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message, amount);

			Finance fraction = getFinanceGenerator().duplicateFinance(finance, CommonUtil.round(amount - getPaymentAmount(), 2));
			message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_FRACTIONED, 2, 2);
			FinanceTrackingWriter.addFinanceTracking(fraction, new Date(), FinanceTrackingType.FRACTIONED, message, amount);

			AonUtil.addWarningMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_NOT_MATCH_AMOUNT_ERROR);
		}
		finance.setFinanceStatus(FinanceStatus.PAID);
		super.accept(null);

		AccountEntry entry = null;
		String message = null;
		if (isPaymentRecordable()) {
			entry = getWriter().recordFinance(finance, getPaymentRegistryBank(), getPaymentPayMethodTypeDetail(), getPaymentDate());
			message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_RECORDED) + " " + entry.getId();
		}

		message = (message!=null) ? message : AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_PENDING);
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, getPaymentDate(), FinanceTrackingType.PAID, message, 
				getPaymentRegistryBank(), getPaymentPayMethodTypeDetail(), finance.getTotalAmount(), isPaymentRecordable());

		if (isPaymentRecordable()) {
			getWriter().insertAccountEntryFinanceTracking(entry, tracking);
		}

		FinanceTrackingController financeTrackingController = (FinanceTrackingController)FormUtil.getController(FINANCE_TRACKING_CONTROLLER_NAME);
		financeTrackingController.onSearch(null);
	}

	public void onFinanceReturn(ActionEvent event) throws ManagerBeanException {
		setReturnRegistryBank((getReturnDeposit()==0) ? getReturnRegistryBank() : null);
		setReturnPayMethodTypeDetail((getReturnDeposit()!=0) ? getReturnPayMethodTypeDetail() : null);

		Finance finance = (Finance)this.getTo();
		finance.setExpenses(getReturnExpenses());
		finance.setFinanceStatus(FinanceStatus.RETURNED);
		super.accept(null);
		returnFinanceBatchDetail(finance);

		AccountEntry entry = null;
		String message = null;
		if (isReturnRecordable()) {
			entry = getWriter().returnFinance(finance, getReturnRegistryBank(), getReturnPayMethodTypeDetail(), getReturnDate());
			message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_RECORDED) + " " + entry.getId();
		}

		message = (message!=null) ? message : AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_PENDING);
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, getReturnDate(), FinanceTrackingType.RETURNED, message,
				getReturnRegistryBank(), getReturnPayMethodTypeDetail(), finance.getTotalAmount(), isReturnRecordable());

		if (isReturnRecordable()) {
			getWriter().insertAccountEntryFinanceTracking(entry, tracking);
		}

		FinanceTrackingController financeTrackingController = (FinanceTrackingController)FormUtil.getController(FINANCE_TRACKING_CONTROLLER_NAME);
		financeTrackingController.onSearch(null);
	}

	@SuppressWarnings("unchecked")
	private void returnFinanceBatchDetail(Finance finance) throws ManagerBeanException {
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.PAID);
		Iterator iterator = fBatchDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			FinanceBatchDetail detail = (FinanceBatchDetail)iterator.next();
			detail.setStatus(FinanceStatus.RETURNED);
			fBatchDetailBean.update(detail);
		}
	}

	public void onSettleFinance(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)this.getTo();
		finance.setFinanceStatus(FinanceStatus.SETTLED);
		super.accept(null);

		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_SETTLED);
		FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.SETTLED, message);

		FinanceTrackingController financeTrackingController = (FinanceTrackingController)FormUtil.getController(FINANCE_TRACKING_CONTROLLER_NAME);
		financeTrackingController.onSearch(null);
	}

	@SuppressWarnings("unchecked")
	public List getOrderedList() {
		return orderedList;
	}

	@SuppressWarnings("unchecked")
	public void setOrderedList(List orderedList) {
		this.orderedList = orderedList;
	}
	
	public void onOrderFinanceList(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByRegistry(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_REGISTRY_NAME));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByDate(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByPayment(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_PAY_METHOD_ID));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByBank(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_BANK_ID));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}

	public void onShowInvoice(ActionEvent event) throws ManagerBeanException {
		Finance to = (Finance)this.getTo();
		if (!to.isEmptyInvoice()) {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ID), to.getInvoice().getId());
			FormUtil.getController(INVOICE_PRINTER_CONTROLLER).setCriteria(criteria);
		}
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)this.getTo();
		String invoiceControllerName = "";
		if (finance.getInvoice().getType() == InvoiceType.SALES) {
			invoiceControllerName = SALE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(SALE_INVOICE_FORM_NAME);
		} else if (finance.getInvoice().getType() == InvoiceType.PURCHASE) {
			invoiceControllerName = PURCHASE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(PURCHASE_INVOICE_FORM_NAME);
		} else if (finance.getInvoice().getType() == InvoiceType.EXPENSES) {
			invoiceControllerName = EXPENSE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(EXPENSE_INVOICE_FORM_NAME);
		} else if (finance.getInvoice().getType() == InvoiceType.UNDEDUCTIBLE) {
			invoiceControllerName = UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
			setInvoiceViewer(UNDEDUCTIBLE_INVOICE_FORM_NAME);
		}

		InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
		invoiceController.onLoadInvoice(event, finance.getInvoice(), FINANCE_FORM_NAME, invoiceControllerName);
	}

	public void onLoadFinance(ActionEvent event, Finance finance, String backAction) throws ManagerBeanException {
		FinanceSearchListener financeSearch = (FinanceSearchListener)AonUtil.getRegisteredBean(FINANCE_SEARCH_LISTENER_NAME);

		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IFinanceAlias.FINANCE_ID), finance.getId());
		financeSearch.setFinanceStatuses(null);
		onSearch(event);
		getModel().setRowIndex(0);
		onSelect(event);

		setBackAction(backAction);
		setBackActionListener(FINANCE_CONTROLLER_NAME + ".onBack");
	}

}