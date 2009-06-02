package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

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
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the finance maintenance.
 * 
 */
public class FinanceController extends BasicController implements IFinanceConstants {

	private static final Logger LOGGER = Logger.getLogger(FinanceController.class.getName());

	private Company company;

	private boolean payment;

	private Date paymentDate;

	private double paymentAmount;

	private RegistryBank paymentRegistryBank;
	
	private RegistryPayMethod paymentRegistryPayMethod;
	
	private Date returnDate;

	private double returnExpenses;

	private RegistryBank returnRegistryBank;
	
	private FinanceGenerator financeGenerator;
	
	private AccountEntryFinanceWriter writer;

	private boolean showFinancePaymentWindow;

	private boolean showFinanceReturnWindow;
	
	private List orderedList;

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
	
	public void onFinancePaymentShow(ActionEvent event) {
		super.accept();

		Finance finance = (Finance) getTo();
		setPaymentDate(finance.getDueDate());
		setPaymentAmount(finance.getTotalAmount());
		if (finance.getPayMethod().getType() == PayMethodType.CASH_BASIS) {
			setPaymentRegistryBank(null);
		} else {
			setPaymentRegistryBank(obtainPaymentRegistryBank(getCompany(), finance.getBank(), finance.getBankAccount()));
		}
	}

	public void onFinanceReturnShow(ActionEvent event) {
		Finance finance = (Finance) getTo();
		setReturnDate(new Date());
		setReturnExpenses(finance.getExpenses());
		setReturnRegistryBank(obtainReturnRegistryBank(getCompany(), finance));
	}

	private RegistryBank obtainPaymentRegistryBank(Registry registry, Bank bank, BankAccount bankAccount) {
		try {
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
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining registry Banks", e);
		} 
		return null;
	}

	@SuppressWarnings("unchecked")
	private RegistryBank obtainReturnRegistryBank(Registry registry, Finance finance) {
		Bank bank = finance.getBank();
		BankAccount bankAccount = finance.getBankAccount();
		try {
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
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining registry Banks", e);
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

	public List<SelectItem> getBanks() {
		Finance finance = (Finance) getTo();
		if (finance != null && finance.getPayMethod() != null) {
			PayMethod pm = finance.getPayMethod();
			if ((!isPayment() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) || (isPayment() && pm.getType() == PayMethodType.BANK_TRANSFER)) {
				return getRegistryBanks(finance.getRegistry());
			}
			return getRegistryBanks(getCompany());
		}
		return new LinkedList<SelectItem>();
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getRegistryBanks(Registry registry) {
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank, StringUtils.abbreviate(rBank.getBank().getName(), 30)
						+ " [" + rBank.getBankAccount().toString() + "]");
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks", e);
		}
		return rBanks;
	}

	public List<SelectItem> getCompanyRegistryBanks() {
		return getRegistryBanks(getCompany());
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
		if (getPaymentAmount() == 0 || getPaymentAmount() > finance.getTotalAmount()){
			AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_INVALID_AMOUNT_ERROR);
			throw new AbortProcessingException();
		}

		if (getPaymentAmount() != finance.getTotalAmount()) {
			getFinanceGenerator().duplicateFinance(finance, CommonUtil.round(finance.getTotalAmount() - getPaymentAmount(), 2));
			finance.setAmount(CommonUtil.round(getPaymentAmount() - finance.getExpenses(), 2));
			String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_FRACTIONED);
			FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message);
		}
		finance.setFinanceStatus(FinanceStatus.PAID);
		getManagerBean().update(finance);

		AccountEntry entry = getWriter().recordFinance(finance, getPaymentRegistryBank(), getPaymentDate());
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

		AccountEntry entry = getWriter().returnFinance(finance, getReturnRegistryBank(), getReturnDate());
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

	public List getOrderedList() {
		return orderedList;
	}

	public void setOrderedList(List orderedList) {
		this.orderedList = orderedList;
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