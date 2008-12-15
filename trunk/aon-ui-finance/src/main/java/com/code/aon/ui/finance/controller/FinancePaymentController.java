package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FinancePaymentController extends BasicController {
		
	private Double payedAmount;
	
	private Date paymentDate;
	
	private RegistryBank registryBank;
	
	private AccountEntryFinanceWriter writer;
	
	public Double getPayedAmount() {
		return payedAmount;
	}

	public void setPayedAmount(Double payedAmount) {
		this.payedAmount = payedAmount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
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

	public void init( Double payerAmount ) {
		setPayedAmount(0.0);
		setPaymentDate(new Date() );
		setRegistryBank(new RegistryBank());
	}
	
	public void onPayment(ActionEvent event) throws ManagerBeanException{
		if(getPayedAmount()==0){
			AonUtil.addInfoMessage("No se puede realizar un pago de importe 0.0");
			throw new AbortProcessingException();
		}
		ResourceBundle bundle = AonUtil.getResourceBundle("financeBundle");
		Finance finance = (Finance)this.getTo();
		if(finance.getPayMethod().getId() == null){
			finance.setPayMethod(null);
		}
		if(finance.getBank().getId() == null){
			finance.setBank(null);
		}
		if(finance.getTotalAmount() != getPayedAmount()){
			createNewFinance(finance, round(finance.getAmount() - payedAmount + finance.getExpenses(), 2));
			finance.setAmount(round(payedAmount - finance.getExpenses(), 2));
			FinanceTrackingWriter.addFinanceTracking(finance, FinanceTrackingType.FRACTIONED, bundle.getString("finance_tracking_fractioned"));
		}
		finance.setFinanceStatus(FinanceStatus.PAID);
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		financeBean.update(finance);
		AccountEntry entry = recordFinance(finance);
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, FinanceTrackingType.RECORDED, bundle.getString("finance_tracking_recorded") + " " + entry.getId());
		insertAccountEntryFinanceTracking(entry, tracking);
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
			recordingTo.setRegistryBank(getRegistryBank());
			recordingTo.setFinanceList(list);
			recordingTo.setDate(getPaymentDate());
			recordingTo.setType((finance.isPayment()?AccountEntryType.PAYMENT:AccountEntryType.COLLECTION));
			recordingTo.setSecurityLevel(finance.getSecurityLevel()==null?SecurityLevel.OFFICIAL:finance.getSecurityLevel());
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

	private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}