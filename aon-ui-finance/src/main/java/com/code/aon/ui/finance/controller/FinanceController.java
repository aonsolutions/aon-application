package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_FRACTIONED;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_GROUPED;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_SETTLED;
import static com.code.aon.ui.common.ICommonMessages.PAYMENT_INVALID_AMOUNT_ERROR;
import static com.code.aon.ui.common.ICommonMessages.PAYMENT_NOT_MATCH_AMOUNT_ERROR;
import static com.code.aon.ui.common.ICommonMessages.PAYMENT_PAY_METHOD_UNDEFINED_ERROR;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.BankUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.event.FinanceSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class FinanceController extends FinanceListController implements IFinanceController {

	private Company company;
	private boolean payment;
	private boolean payroll;
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
	private boolean showBankManualInput;
	private RegistryBank registryBank;
	private boolean showFinanceGroupWindow;
	private boolean financeGroup;
	private boolean showFinancePayrollWindow;
	private Month payrollMonth;
	private int payrollYear;
	private List<ITransferObject> orderedList;
	
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

	public boolean isPayroll() {
		return payroll;
	}

	public void setPayroll(boolean payroll) {
		this.payroll = payroll;
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

	public boolean isShowBankManualInput() throws ManagerBeanException {
		return showBankManualInput;
	}

	public void setShowBankManualInput(boolean showBankManualInput) {
		this.showBankManualInput = showBankManualInput;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public boolean isShowFinanceGroupWindow() {
		return showFinanceGroupWindow;
	}

	public void setShowFinanceGroupWindow(boolean showFinanceGroupWindow) {
		this.showFinanceGroupWindow = showFinanceGroupWindow;
	}

	public boolean isFinanceGroup() {
		return financeGroup;
	}

	public void setFinanceGroup(boolean financeGroup) {
		this.financeGroup = financeGroup;
	}

	public boolean isShowFinancePayrollWindow() {
		return showFinancePayrollWindow;
	}

	public void setShowFinancePayrollWindow(boolean showFinancePayrollWindow) {
		this.showFinancePayrollWindow = showFinancePayrollWindow;
	}

	public Month getPayrollMonth() {
		return payrollMonth;
	}

	public void setPayrollMonth(Month payrollMonth) {
		this.payrollMonth = payrollMonth;
	}

	public int getPayrollYear() {
		return payrollYear;
	}

	public void setPayrollYear(int payrollYear) {
		this.payrollYear = payrollYear;
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
		if(isFinanceGroup()){
			refreshFinanceGroupList();
			finance.setAmount(0.0);
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
			finance.setBankAccount(new BankAccount());
			finance.setBankAlias(null);
			finance.setBic(null);
			setShowBankManualInput(false);
		}
	}

	public void onRegistryBankChanged(ValueChangeEvent event) {
		Finance finance = (Finance)getTo();
		if (event.getNewValue() != null) {
			RegistryBank rb = (RegistryBank) event.getNewValue();
			finance.setBankAccount(rb.getBankAccount());
			finance.setBankAlias(rb.getBankAlias());
			finance.setBic(rb.getBic());
		} else {
			finance.setBankAccount(null);
			finance.setBankAlias(null);
			finance.setBic(null);
		}
	}

	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		Finance finance = (Finance)getTo();
		if (finance != null && finance.getPayMethod() != null) {
			PayMethod pm = finance.getPayMethod();
			if ((!isPayment() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) || (isPayment() && pm.getType() == PayMethodType.BANK_TRANSFER)) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getAllRegistryBanks(finance.getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getAllCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		Finance finance = (Finance)getTo();
		if (finance != null && finance.getPayMethod() != null) {
			PayMethod pm = finance.getPayMethod();
			if ((!isPayment() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) || (isPayment() && pm.getType() == PayMethodType.BANK_TRANSFER)) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getActiveRegistryBanks(finance.getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getActiveCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}
	public int getActiveBanksCount() throws ManagerBeanException {
		return getActiveBanks().size();
	}
	public int getAllBanksCount() throws ManagerBeanException {
		return getAllBanks().size();
	}
	
	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData((Finance)getTo());
	}

	public boolean isPending() {
    	return ((Finance)this.getTo()).isPending();
    }
    
    public boolean isReturned(){
    	return ((Finance)this.getTo()).isReturned();
    }

    public boolean isPaid(){
    	return ((Finance)this.getTo()).isPaid();
    }
    
    public boolean isGrouped(){
    	return ((Finance)this.getTo()).getFinanceGroup() != null && ((Finance)this.getTo()).getFinanceGroup().getId()!=null;
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
			AonUtil.addErrorMessageFromBundle(PAYMENT_PAY_METHOD_UNDEFINED_ERROR);
			throw new AbortProcessingException();
		} 
		super.accept(null);

		setPaymentDate(finance.getDueDate());
		setPaymentAmount(finance.getTotalAmount());
		if (finance.getPayMethod().getType() != PayMethodType.CASH_BASIS && finance.getPayMethod().getType() != PayMethodType.OTHER) {
			setPaymentRegistryBank(obtainPaymentRegistryBank(getCompany(), finance.getBankAccount()));
			setPaymentPayMethodTypeDetail(null);
		} else {
			setPaymentRegistryBank(null);
			setPaymentPayMethodTypeDetail(obtainPayMethodTypeDetail(finance.getPayMethod().getType()));
		}
		setPaymentRecordable(AonUtil.getRoleManager().isAccountingOperator());
	}

	private RegistryBank obtainPaymentRegistryBank(Registry registry, BankAccount bankAccount) throws ManagerBeanException {
		IManagerBean registryBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		if (bankAccount != null && bankAccount.getIban() != null) {
			criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_BANK_ACCOUNT), bankAccount.getIban());
		}
		Iterator<ITransferObject> iterator = registryBankBean.getList(criteria, 0, 1).iterator();
		if (iterator.hasNext()) {
			return (RegistryBank)iterator.next();
		}
		criteria = new Criteria();
		criteria.addEqualExpression(registryBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
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

	private RegistryBank obtainReturnRegistryBank(Registry registry, Finance finance) throws ManagerBeanException {
		BankAccount bankAccount = finance.getBankAccount();
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.PAID);
		Iterator<?> iterator = fBatchDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			FinanceBatchDetail detail = (FinanceBatchDetail)iterator.next();
			bankAccount = detail.getFinanceBatch().getRegistryBank().getBankAccount();
		}
		return obtainPaymentRegistryBank(registry, bankAccount);
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
		criteria.addEqualExpression(payMethodTypeDetailBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE_DETAIL_TYPE), type);
		criteria.addOrder(payMethodTypeDetailBean.getFieldName(IEntityAlias.PAY_METHOD_TYPE_DETAIL_DESCRIPTION));
		Iterator<?> iter = payMethodTypeDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			PayMethodTypeDetail payMethodTypeDetail = (PayMethodTypeDetail) iter.next();
			SelectItem item = new SelectItem(payMethodTypeDetail, payMethodTypeDetail.getDescription());
			payMethodTypeDetailList.add(item);
		}
	}

	public int getPayMethodTypeDetailsSize() {
		return (getPayMethodTypeDetailList() == null) ? 0 : getPayMethodTypeDetailList().size();
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
			AonUtil.addErrorMessageFromBundle(PAYMENT_INVALID_AMOUNT_ERROR);
			throw new AbortProcessingException();
		}
		if (getPaymentAmount() != finance.getTotalAmount()) {
			double amount = finance.getTotalAmount();

			finance.setAmount(CommonUtil.round(getPaymentAmount() - finance.getExpenses()));
			String message = AonUtil.getMessage(FINANCE_TRACKING_FRACTIONED, 1, 2);
			FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message, amount);

			Finance fraction = getFinanceGenerator().duplicateFinance(finance, CommonUtil.round(amount - getPaymentAmount()));
			message = AonUtil.getMessage(FINANCE_TRACKING_FRACTIONED, 2, 2);
			FinanceTrackingWriter.addFinanceTracking(fraction, new Date(), FinanceTrackingType.FRACTIONED, message, amount);

			AonUtil.addWarningMessageFromBundle(PAYMENT_NOT_MATCH_AMOUNT_ERROR);
		}
		finance.setFinanceStatus(FinanceStatus.PAID);
		super.accept(null);

		AccountEntry entry = null;
		String message = null;
		if (isPaymentRecordable()) {
			entry = getWriter().recordFinance(finance, getPaymentRegistryBank(), getPaymentPayMethodTypeDetail(), getPaymentDate());
			message = AonUtil.getMessage(ICommonMessages.TRACKING_RECORDED) + " " + entry.getId();
		}

		message = (message!=null) ? message : AonUtil.getMessage(ICommonMessages.PENDING);
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
			message = AonUtil.getMessage(ICommonMessages.TRACKING_RECORDED) + " " + entry.getId();
		}

		message = (message!=null) ? message : AonUtil.getMessage(ICommonMessages.PENDING);
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, getReturnDate(), FinanceTrackingType.RETURNED, message,
				getReturnRegistryBank(), getReturnPayMethodTypeDetail(), finance.getTotalAmount(), isReturnRecordable());

		if (isReturnRecordable()) {
			getWriter().insertAccountEntryFinanceTracking(entry, tracking);
		}

		FinanceTrackingController financeTrackingController = (FinanceTrackingController)FormUtil.getController(FINANCE_TRACKING_CONTROLLER_NAME);
		financeTrackingController.onSearch(null);
	}

	private void returnFinanceBatchDetail(Finance finance) throws ManagerBeanException {
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.PAID);
		Iterator<?> iterator = fBatchDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			FinanceBatchDetail detail = (FinanceBatchDetail)iterator.next();
			detail.setStatus(FinanceStatus.RETURNED);
			fBatchDetailBean.update(detail);
		}
	}

	public void onSettleFinance(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)this.getTo();
		finance.setFinanceStatus(FinanceStatus.SETTLED);
		super.accept(null);

		String message = AonUtil.getMessage(FINANCE_TRACKING_SETTLED);
		createFinanceTracking(finance, message);

		FinanceTrackingController financeTrackingController = (FinanceTrackingController)FormUtil.getController(FINANCE_TRACKING_CONTROLLER_NAME);
		financeTrackingController.onSearch(null);
	}
	
	public void createFinanceTracking(Finance finance, String message){
		FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.SETTLED, message);
	}

	public List<?> getOrderedList() {
		return orderedList;
	}

	public void setOrderedList(List<ITransferObject> orderedList) {
		this.orderedList = orderedList;
	}
	
	public void onOrderFinanceList(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByRegistry(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_REGISTRY_NAME));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByDate(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByPayment(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderFinanceListByBank(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_BANK_ACCOUNT));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_DUE_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.FINANCE_INVOICE_REFERENCE_CODE));
		orderedList=getManagerBean().getList(criteria);
	}

	public void onShowInvoice(ActionEvent event) throws ManagerBeanException {
		Finance to = (Finance)this.getTo();
		if (!to.isEmptyInvoice()) {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ID), to.getInvoice().getId());
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

		BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(invoiceControllerName);
		invoiceController.onLoad(event, finance.getInvoice().getId(), FINANCE_FORM_NAME, FINANCE_CONTROLLER_NAME + ".refresh");
	}
	
	public void onShowFinanceGroupWindow(ActionEvent event) throws ManagerBeanException{
		if(!this.isNew()){
			buildFinanceGroupList((Finance) this.getTo());
		}
		refreshFinanceList();
		setShowFinanceGroupWindow(true);
	}
	
	public void onGroupSelected(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeListController = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);        
        FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
        if(!this.isNew()){
        	groupSelected();
        	buildFinanceGroupList((Finance) this.getTo());
        	this.getManagerBean().restoreNullSubPOJOs(this.getTo());
        	this.getManagerBean().update(this.getTo());
        } else {
        	groupListController.getGroupList().addAll(financeListController.getCheckedFinances());
        	refreshFinanceGroupAmount();
        }
        financeListController.clearCheckedFinances();
        refreshFinanceList();
	}
	
	public void onUngroupSelected(ActionEvent event) throws ManagerBeanException{
        FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
        if(!this.isNew()){
        	ungroupSelected();
        	buildFinanceGroupList((Finance) this.getTo());
        	this.getManagerBean().restoreNullSubPOJOs(this.getTo());
        	this.getManagerBean().update(this.getTo());
        } else {
        	groupListController.getGroupList().removeAll(groupListController.getCheckedFinances());
        	refreshFinanceGroupAmount();
        }
        groupListController.clearCheckedFinances();
        refreshFinanceList();
	}
	
	public void buildFinanceGroupList(Finance finance) throws ManagerBeanException{
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);	
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("Finance.financeGroup.id", finance.getId());
		
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
		groupListController.init();
		for(ITransferObject to: financeBean.getList(criteria)){
			Finance f = (Finance) to;
			groupListController.getGroupList().add(f);
		}
	}
	
	private void groupSelected() throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		FinanceListController financeListController = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);        
		Finance financeGroup = (Finance) this.getTo();
		for(Finance finance: financeListController.getCheckedFinances()){
			finance.setFinanceGroup(financeGroup);
			finance.setFinanceStatus(FinanceStatus.SETTLED);
			financeBean.update(finance);
			String message = AonUtil.getMessage(FINANCE_TRACKING_GROUPED);
			createFinanceTracking(finance, message);
			financeGroup.setAmount(financeGroup.getAmount()+finance.getAmount());
		}
	}

	private void ungroupSelected() throws ManagerBeanException {
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
		if(groupListController.getCheckedFinances().size()==groupListController.getGroupList().size()){
			String msg = "Imposible continuar. Se debe agrupar al menos un vencimiento.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		ungroupSelected(groupListController.getCheckedFinances());
    }
	
	public void ungroupSelected(List<?> finances) throws ManagerBeanException{
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Finance financeGroup = (Finance) this.getTo();
		for(Object to : finances){
			Finance finance = (Finance) to;
			finance.setFinanceGroup(null);
			finance.setFinanceStatus((FinanceTrackingWriter.wasFinanceReturned(finance)?FinanceStatus.RETURNED:FinanceStatus.PENDING));
			financeBean.update(finance);
			removeFinanceTracking(finance);
			financeGroup.setAmount(financeGroup.getAmount()-finance.getAmount());
		}
	}
	
	private void removeFinanceTracking(Finance finance) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceTracking.class);
		FinanceTracking tracking = getLastTracking(finance);
		getWriter().removeAccountEntryFinanceTracking(tracking);
		bean.remove(tracking);
	}

	private FinanceTracking getLastTracking(Finance finance) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE), false);
		List<ITransferObject> list = bean.getList(criteria);
		return list.isEmpty()?null:(FinanceTracking)list.get(0);
	}

	private void refreshFinanceGroupList() throws ManagerBeanException {
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
		groupListController.init();
	}
	
	private void refreshFinanceList() throws ManagerBeanException {
		FinanceListController controller = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		controller.clearCheckedFinances();
		controller.clearCriteria();
		controller.getCriteria().addEqualExpression(controller.getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), ((Finance)getTo()).getRegistry().getId());
		Expression expr1 = ExpressionUtilities.getEqualExpression(controller.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		Expression expr2 = ExpressionUtilities.getEqualExpression(controller.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.RETURNED);
		controller.getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		if(getTo()!=null & ((Finance)getTo()).getId()!=null){
			controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.FINANCE_ID), ((Finance)getTo()).getId());
		}
		
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
        for(Finance finance: groupListController.getGroupList()){
        	controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.FINANCE_ID), finance.getId());
        }
        controller.initializeModel();
	}
	
	private void refreshFinanceGroupAmount() {
		FinanceGroupListController groupListController = (FinanceGroupListController) AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_GROUP_LIST_CONTROLLER_NAME);
		Double amount = new Double(0.0);
		for(Finance finance: groupListController.getGroupList()){
			amount += finance.getAmount();
		}
		((Finance)getTo()).setAmount(amount);
	}

	public void onGeneratePayrollFinancesShow(ActionEvent event) {
		setPayrollMonth(Month.getMonthByValue(CommonUtil.getMonth(new Date())));
		setPayrollYear(CommonUtil.getYear(new Date()));
		try {
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.FINANCE_ID), new Integer(0));
			onSearch(event);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onGeneratePayrollFinances(ActionEvent event) {
		Date fromDate = CommonUtil.getDate(getPayrollYear(), getPayrollMonth().getValue(), 1);
		Date toDate = CommonUtil.getMonthLastDay(fromDate);

		PreparedStatement statement = null;
		Connection connection = null;
		try {
			Integer maxId = new Integer(0);
			Criteria criteria = new Criteria();
			ProjectionList projectionList = new ProjectionList(Projection.max(getFieldName(IEntityAlias.FINANCE_ID)));
			for (Object obj : getManagerBean().getList(projectionList, criteria)) {
				maxId = (Integer)obj;
			}

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());

			StringWriter deleteFinances = new StringWriter();
			deleteFinances.append("DELETE FROM finance");
			deleteFinances.append(" WHERE " + DomainManager.getSQLWhereClause("domain"));
			deleteFinances.append(" AND status = " + FinanceStatus.PENDING.ordinal());
			deleteFinances.append(" AND payroll = 1");
			deleteFinances.append(" AND due_date BETWEEN ? AND ?");

			statement = connection.prepareStatement(deleteFinances.toString());
			statement.setDate(1, new java.sql.Date(fromDate.getTime()));
			statement.setDate(2, new java.sql.Date(toDate.getTime()));
			statement.execute();

			StringWriter insertFinances = new StringWriter();
			insertFinances.append("INSERT INTO finance (");
			insertFinances.append("domain, payment, registry, rdocument, rdocument_type, rdocument_country, rname, amount, concept, ");
			insertFinances.append("due_date, pay_method, bank_account, bank_alias, bic, status, security_level, scope, payroll, source_id)");
			insertFinances.append(" SELECT s.domain, 1, c.person, r.document, r.document_type, r.document_country, r.name,");
			insertFinances.append(" ROUND(s.total_liquid, 2) - ROUND(CASE WHEN SUM(f.amount) IS NULL THEN 0 ELSE SUM(f.amount) END, 2) AS total_amount,");
			insertFinances.append(" CONCAT(" + obtainSalaryTypeCondition("s.type") + ", ' - ', DATE_FORMAT(s.issue_date, '%d/%m/%Y')),");
			insertFinances.append(" s.charge_date, rp.pay_method, rb.bank_account, rb.alias, rb.bic, 0, 0, w.scope, 1, s.id");
			insertFinances.append(" FROM registry AS r, workplace AS w");
			insertFinances.append(" LEFT JOIN salary AS s ON " + DomainManager.getSQLWhereClause("s.domain") + " AND s.charge_date BETWEEN ? AND ?");
			insertFinances.append(" LEFT JOIN contract AS c ON c.id = s.contract");
			insertFinances.append(" LEFT JOIN finance AS f ON f.source_id = s.id AND f.payroll = 1");
			insertFinances.append(" LEFT JOIN rpaymethod AS rp ON rp.registry = c.person");
			insertFinances.append(" LEFT JOIN rbank AS rb ON rb.id = rp.rbank");
			insertFinances.append(" WHERE r.id = c.person");
			insertFinances.append(" AND w.id = c.workplace");
			insertFinances.append(" GROUP BY s.id");
			insertFinances.append(" HAVING total_amount <> 0");

			statement = connection.prepareStatement(insertFinances.toString());
			statement.setDate(1, new java.sql.Date(fromDate.getTime()));
			statement.setDate(2, new java.sql.Date(toDate.getTime()));
			statement.execute();

			if (maxId.intValue() > 0) {
				FinanceSearchListener searchListener = (FinanceSearchListener)AonUtil.getRegisteredBean(FINANCE_SEARCH_LISTENER_NAME);
				searchListener.initData();
				criteria = new Criteria();
				criteria.addGreaterThanExpression(getFieldName(IEntityAlias.FINANCE_ID), maxId);
				criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAYROLL), Boolean.TRUE);
				setCriteria(criteria);
				onSearch(null);
			}
		} catch (SQLException ex) {
			addMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		} catch (AonConnectionException ex) {
			addMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		} catch (ManagerBeanException ex) {
			addMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		} finally {
			DatabaseUtil.closeQuietly(statement);
			DatabaseUtil.closeQuietly(connection);
		}
	}

	private String obtainSalaryTypeCondition(String fieldName) {
		String condition = "CASE " + fieldName;
		for (SalaryType salaryType : SalaryType.values()) {
			condition += " WHEN " + salaryType.ordinal() + " THEN '" + salaryType.getName(AonUtil.getCurrentLocale()).toUpperCase() + "'";
		}
		condition += " END";
		return condition;
	}

}