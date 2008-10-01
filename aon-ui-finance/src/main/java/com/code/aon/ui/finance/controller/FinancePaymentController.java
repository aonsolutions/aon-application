package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.AccountEntry;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.FinanceTrackingWriter;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class FinancePaymentController extends BasicController {
		
	private static final Logger LOGGER = Logger.getLogger(FinancePaymentController.class.getName());
	
	private Boolean payment;
	
	private Integer registryId;
	
	private String registryName;
	
	private String series;
	
	private Integer number;
	
	private double payedAmount;
	
	private Date paymentDate;
	
	private Date fromDate;
	
	private Date toDate;
	
	private RegistryBank registryBank;
	
	private AccountEntryFinanceWriter writer;
	
	public Boolean getPayment() {
		return payment;
	}

	public void setPayment(Boolean payment) {
		this.payment = payment;
	}
	
	public Integer getRegistryId() {
		return registryId;
	}

	public void setRegistryId(Integer registryId) {
		this.registryId = registryId;
	}

	public String getRegistryName() {
		return registryName;
	}

	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public double getPayedAmount() {
		return payedAmount;
	}

	public void setPayedAmount(double payedAmount) {
		this.payedAmount = payedAmount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public AccountEntryFinanceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public void onEditSearch(MenuEvent event) throws ManagerBeanException {
		this.onEditSearch((ActionEvent)event);
		initializeSearch();
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		initializeSearch();
	}
	
	private void initializeSearch() {
		try {
			((PageDataModel)this.getModel()).resize(0);
			payment = new Boolean(false);
			registryId = null;
			registryName = "";
			series = "";
			number = null;
			payedAmount = 0.0; 
			paymentDate = new Date();
			fromDate = null;
			toDate = null;
			registryBank = new RegistryBank();
			getCriteria().addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.FINANCE_PAYMENT), payment );
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing search", e);
		}
	}
	
	@SuppressWarnings("unused")
	public void onPayment(ActionEvent event) throws ManagerBeanException{
		if(getPayedAmount()==0){
			AonUtil.addInfoMessage("No se puede realizar un pago de importe 0.0");
			throw new AbortProcessingException();
		}
		ResourceBundle bundle = ResourceBundle.getBundle(AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle"),FacesContext.getCurrentInstance().getViewRoot().getLocale());
		Finance finance = (Finance)this.getTo();
		if(finance.getPayMethod().getId() == null){
			finance.setPayMethod(null);
		}
		if(finance.getBank().getId() == null){
			finance.setBank(null);
		}
		if(finance.getTotalAmount() != getPayedAmount()){
			createNewFinance(finance, (finance.getAmount() - payedAmount) + finance.getExpenses());
			finance.setAmount(payedAmount - finance.getExpenses());
			FinanceTrackingWriter.addFinanceTracking(finance, FinanceTrackingType.FRACTIONED, bundle.getString("aon_finance_tracking_fractioned"));
		}
		finance.setFinanceStatus(FinanceStatus.PAID);
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		financeBean.update(finance);
		AccountEntry entry = recordFinance(finance);
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, FinanceTrackingType.RECORDED, bundle.getString("aon_finance_tracking_recorded") + " " + entry.getId());
		insertAccountEntryFinanceTracking(entry, tracking);
		initializeSearch();
	}
	
	private void createNewFinance(Finance finance, double newAmount) throws ManagerBeanException {
		Finance newFinance = new Finance();
		newFinance.setAmount(newAmount);
		newFinance.setBank(finance.getBank());
		newFinance.setBankAccount(finance.getBankAccount());
		newFinance.setConcept(finance.getConcept());
		newFinance.setDueDate(finance.getDueDate());
		newFinance.setExpenses(0.0);
		newFinance.setFinanceStatus(FinanceStatus.PENDING);
		newFinance.setInvoice(finance.getInvoice());
		newFinance.setPayment(finance.isPayment());
		newFinance.setPayMethod(finance.getPayMethod());
		newFinance.setRegistry(finance.getRegistry());
		newFinance.setSecurityLevel(finance.getSecurityLevel());
		IManagerBean financeBean  = BeanManager.getManagerBean(Finance.class);
		financeBean.insert(newFinance);
	}
	
	private AccountEntry recordFinance(Finance finance) throws ManagerBeanException {
		try {
			FinanceRecordingTo recordingTo = new FinanceRecordingTo();
			List<Finance> list = new LinkedList<Finance>();
			list.add(finance);
			recordingTo.setRegistryBank((getRegistryBank().getId()== null?null:getRegistryBank()));
			recordingTo.setFinanceList(list);
			recordingTo.setDate(getPaymentDate());
			recordingTo.setType((finance.isPayment()?AccountEntryType.PAYMENT:AccountEntryType.COLLECTION));
			AccountEntry entry = getWriter().recordFinances(recordingTo);
			return entry;
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error Recording Finance with id= " + finance.getId() , e);
		}
	}
	
	private void insertAccountEntryFinanceTracking(AccountEntry entry, FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean accountEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		AccountEntryFinanceTracking accEntryTracking = new AccountEntryFinanceTracking();
		accEntryTracking.setAccountEntry(entry);
		accEntryTracking.setFinanceTracking(tracking);
		accountEntryFinanceTrackingBean.insert(accEntryTracking);
	}
}