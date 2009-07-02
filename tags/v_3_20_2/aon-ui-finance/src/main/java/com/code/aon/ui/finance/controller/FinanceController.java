package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.dao.IAccountAlias;
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
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the finance maintenance.
 * 
 */
public class FinanceController extends BasicController implements IFinanceConstants {

	private Company company;

	private boolean payment;

	private Date paymentDate;

	private double paymentAmount;

	private Account paymentCashAccount;
	
	private RegistryBank paymentRegistryBank;
	
	private RegistryPayMethod paymentRegistryPayMethod;
	
	private Date returnDate;

	private double returnExpenses;

	private int returnDeposit;

	private Account returnCashAccount;
	
	private RegistryBank returnRegistryBank;
	
	private FinanceGenerator financeGenerator;
	
	private AccountEntryFinanceWriter writer;

	private boolean showFinancePaymentWindow;

	private boolean showFinanceReturnWindow;
	
	private List<SelectItem> cashAccountList;

	private List<?> orderedList;
	
	private Double totalFinanceAmount;
	
	private Integer numFinance;

	public Integer getNumFinance() {
		return numFinance;
	}

	public void setNumFinance(Integer numFinance) {
		this.numFinance = numFinance;
	}

	public Double getTotalFinanceAmount() {
		return totalFinanceAmount;
	}

	public void setTotalFinanceAmount(Double totalFinanceAmount) {
		this.totalFinanceAmount = totalFinanceAmount;
	}

	/**
	 * A list of finances currently checked
	 */
	private ArrayList<Finance> checks= new ArrayList<Finance>();

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

	public Account getPaymentCashAccount() {
		return paymentCashAccount;
	}

	public void setPaymentCashAccount(Account paymentCashAccount) {
		this.paymentCashAccount = paymentCashAccount;
	}

	public RegistryBank getPaymentRegistryBank() {
		return paymentRegistryBank;
	}

	public void setPaymentRegistryBank(RegistryBank paymentRegistryBank) {
		this.paymentRegistryBank = paymentRegistryBank;
	}

	public RegistryPayMethod getPaymentRegistryPayMethod() {
		return paymentRegistryPayMethod;
	}

	public void setPaymentRegistryPayMethod(RegistryPayMethod paymentRegistryPayMethod) {
		this.paymentRegistryPayMethod = paymentRegistryPayMethod;
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

	public Account getReturnCashAccount() {
		return returnCashAccount;
	}

	public void setReturnCashAccount(Account returnCashAccount) {
		this.returnCashAccount = returnCashAccount;
	}

	public RegistryBank getReturnRegistryBank() {
		return returnRegistryBank;
	}

	public void setReturnRegistryBank(RegistryBank returnRegistryBank) {
		this.returnRegistryBank = returnRegistryBank;
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
	
	public void onFinancePaymentShow(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Finance finance = (Finance) getTo();
		if (finance.getPayMethod() == null || finance.getPayMethod().getId() == null) {
			AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_PAY_METHOD_UNDEFINED_ERROR);
			throw new AbortProcessingException();
		} else {
			super.accept();
		}

		setPaymentDate(finance.getDueDate());
		setPaymentAmount(finance.getTotalAmount());
		if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {
			setPaymentRegistryBank(null);
		} else {
			setPaymentRegistryBank(obtainPaymentRegistryBank(getCompany(), finance.getBank(), finance.getBankAccount()));
		}
		if (getCashAccountsSize() < 2) {
			//No se renderiza la lista de Cajas, por lo tanto se le asigna el valor por defecto.
			setPaymentCashAccount(AccountUtil.obtainCashAccount());
		} else {
			//Se resetea el valor.
			setPaymentCashAccount(null);
		}
	}

	public void onFinanceReturnShow(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Finance finance = (Finance) getTo();
		setReturnDate(new Date());
		setReturnExpenses(finance.getExpenses());
		if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {
			setReturnDeposit(1);
			setReturnRegistryBank(null);
		} else {
			setReturnDeposit(0);
			setReturnRegistryBank(obtainReturnRegistryBank(getCompany(), finance));
		}
		if (getCashAccountsSize() < 2) {
			//No se renderiza la lista de Cajas, por lo tanto se le asigna el valor por defecto.
			setReturnCashAccount(AccountUtil.obtainCashAccount());
		} else {
			//Se resetea el valor.
			setReturnCashAccount(null);
		}
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
		} else {
			criteria = new Criteria();
			criteria.addEqualExpression(registryBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
			iterator = registryBankBean.getList(criteria, 0, 1).iterator();
			if (iterator.hasNext()) {
				return (RegistryBank)iterator.next();
			}
		}
		return null;
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

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			Finance finance = (Finance) getTo();
			finance.setBank(new Bank());
			finance.setBankAccount(new BankAccount());
		}
	}

	public void onBankChanged(LookupChangeEvent event) {
		Finance finance = (Finance) getTo();
		finance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Bank bank = (Bank) event.getNewValue();
			finance.getBankAccount().setEntity(bank.getCode());			
		}
	}
	
	public void onRegistryBankChanged(ValueChangeEvent event) {
		Finance finance = (Finance) getTo();
		if (event.getNewValue() != null) {
			RegistryBank rb = (RegistryBank) event.getNewValue();
			finance.setBank(rb.getBank());
			finance.setBankAccount(rb.getBankAccount());
		} else {
			finance.setBank(null);
			finance.setBankAccount(null);
		}
	}

	public List<SelectItem> getCashAccounts() throws ManagerBeanException, ExpressionException {
		if (cashAccountList == null) {
			cashAccountList = new LinkedList<SelectItem>();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "570*");
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
			criteria.addOrder(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID));
			Iterator<?> iter = accountBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				Account account = (Account) iter.next();
				SelectItem item = new SelectItem(account, account.getFullDescription());
				cashAccountList.add(item);
			}
		}
		return cashAccountList;
	}

	public int getCashAccountsSize() throws ManagerBeanException, ExpressionException {
		return getCashAccounts().size();
	}

	public List<SelectItem> getBanks() throws ManagerBeanException {
		Finance finance = (Finance) getTo();
		if (finance != null && finance.getPayMethod() != null) {
			PayMethod pm = finance.getPayMethod();
			if ((!isPayment() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) || (isPayment() && pm.getType() == PayMethodType.BANK_TRANSFER)) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getRegistryBanks(finance.getRegistry());
			} else {
				CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getCompanyBanks();
			}
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

	public void onFinancePayment(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)this.getTo();
		if (getPaymentAmount() == 0) {
			AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_INVALID_AMOUNT_ERROR);
			throw new AbortProcessingException();
		}
		if (getPaymentAmount() != finance.getTotalAmount()) {
			AonUtil.addWarningMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_NOT_MATCH_AMOUNT_ERROR);
		}

		if (getPaymentAmount() != finance.getTotalAmount()) {
			getFinanceGenerator().duplicateFinance(finance, CommonUtil.round(finance.getTotalAmount() - getPaymentAmount(), 2));
			finance.setAmount(CommonUtil.round(getPaymentAmount() - finance.getExpenses(), 2));
			String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_FRACTIONED);
			FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message);
		}
		finance.setFinanceStatus(FinanceStatus.PAID);
		getManagerBean().update(finance);

		Account paymentAccount = (getPaymentRegistryBank() != null)?AccountUtil.obtainRBankAccount(getPaymentRegistryBank()):paymentCashAccount;
		AccountEntry entry = getWriter().recordFinance(finance, paymentAccount, getPaymentDate());
		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_RECORDED) + " " + entry.getId();
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, entry.getEntryDate(), FinanceTrackingType.RECORDED, message);
		getWriter().insertAccountEntryFinanceTracking(entry, tracking);

		FinanceTrackingController financeTrackingController = (FinanceTrackingController)FormUtil.getController(FINANCE_TRACKING_CONTROLLER_NAME);
		financeTrackingController.onSearch(null);
	}
	
	public void onFinanceReturn(ActionEvent event) throws ManagerBeanException {
		Finance finance = (Finance)this.getTo();
		finance.setExpenses(getReturnExpenses());
		finance.setFinanceStatus(FinanceStatus.RETURNED);
		getManagerBean().update(finance);
		returnFinanceBatchDetail(finance);

		Account returnAccount = (getReturnDeposit() == 0)?AccountUtil.obtainRBankAccount(getReturnRegistryBank()):returnCashAccount;
		AccountEntry entry = getWriter().returnFinance(finance, returnAccount, getReturnDate());
		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_RECORDED) + " " + entry.getId();
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, entry.getEntryDate(), FinanceTrackingType.RETURNED, message);
		getWriter().insertAccountEntryFinanceTracking(entry, tracking);

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
		getManagerBean().update(finance);

		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_SETTLED);
		FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.SETTLED, message);

		FinanceTrackingController financeTrackingController = (FinanceTrackingController)FormUtil.getController(FINANCE_TRACKING_CONTROLLER_NAME);
		financeTrackingController.onSearch(null);
	}

	/**
	 * CHECK LIST CONTROL 
	 */

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Finance to = (Finance) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Finance to = (Finance) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Finance to = (Finance) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return checks;
	}
	
	public void clearCheckedFinances() {
		checks = new ArrayList<Finance>();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		Iterator iterator = this.getManagerBean().getList(this.getCriteria()).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (!checks.contains(finance)) {
				checks.add(finance);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}

	@SuppressWarnings("unchecked")
	public List getOrderedList() {
		return orderedList;
	}

	@SuppressWarnings("unchecked")
	public void setOrderedList(List orderedList) {
		this.orderedList = orderedList;
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onSearch(event);
		try {
			getFinanceAmount();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
		
	public void getFinanceAmount() throws ManagerBeanException  {
		totalFinanceAmount=0.0;
		numFinance=0;
		List<ITransferObject> financeList;		
		IManagerBean bean;
		bean = BeanManager.getManagerBean(Finance.class);
		financeList = bean.getList(this.getCriteria());
		
		for (ITransferObject to : financeList) {
			Finance f = (Finance) to;
			totalFinanceAmount +=f.getTotalAmount();
			numFinance++;
		}
		
		}
	
	public void onOrderFinanceList(ActionEvent event) throws ManagerBeanException {
		Criteria cr = new Criteria();
		cr=this.getCriteria();
		cr.setOrderByList(null);
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		String date = bean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE);
		String invoiceseries = bean.getFieldName(IFinanceAlias.FINANCE_INVOICE_SERIES);
		String invoicenumber = bean.getFieldName(IFinanceAlias.FINANCE_INVOICE_NUMBER);
		cr.addOrder(date,true);
		cr.addOrder(invoiceseries,true);
		cr.addOrder(invoicenumber,true);
		orderedList=bean.getList(cr);
	}
	
	public void onOrderFinanceListByRegistry(ActionEvent event) throws ManagerBeanException {
		Criteria cr = new Criteria();
		cr=this.getCriteria();
		cr.setOrderByList(null);
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		String registry = bean.getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID);		
		cr.addOrder(registry,true);
		orderedList=bean.getList(cr);
	}
	
	public void onOrderFinanceListByDate(ActionEvent event) throws ManagerBeanException {
		Criteria cr = new Criteria();
		cr=this.getCriteria();
		cr.setOrderByList(null);
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		String date = bean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE);
		String id = bean.getFieldName(IFinanceAlias.FINANCE_ID);
		cr.addOrder(date,true);
		cr.addOrder(id,true);
		orderedList=bean.getList(cr);
	}
	
	public void onOrderFinanceListByPayment(ActionEvent event) throws ManagerBeanException {
		Criteria cr = new Criteria();
		cr=this.getCriteria();
		cr.setOrderByList(null);
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		String paymethod = bean.getFieldName(IFinanceAlias.FINANCE_PAY_METHOD_ID);
		String id = bean.getFieldName(IFinanceAlias.FINANCE_ID);
		cr.addOrder(paymethod,true);
		cr.addOrder(id,true);
		orderedList=bean.getList(cr);
	}

}