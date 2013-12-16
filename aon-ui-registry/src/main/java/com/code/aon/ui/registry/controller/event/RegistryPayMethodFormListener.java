package com.code.aon.ui.registry.controller.event;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.BankUtil;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.config.event.BankAccountValidationListener;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryPayMethodFormListener extends RegistryFormListener {
	
	private RegistryPayMethod registryPayMethod;
	private boolean customerMode;

	public RegistryPayMethod getRegistryPayMethod() {
		return registryPayMethod;
	}

	public void setRegistryPayMethod(RegistryPayMethod registryPayMethod) {
		this.registryPayMethod = registryPayMethod;
	}

	public RegistryBank getRegistryBank() {
		return registryPayMethod.getRegistryBank();
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryPayMethod.setRegistryBank(registryBank);
	}
	
	public boolean isCustomerMode() {
		return customerMode;
	}

	public void setCustomerMode(boolean customerMode) {
		this.customerMode = customerMode;
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)	throws ControllerListenerException {
		super.afterBeanCreated(event);
		resetPayMethod();
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanAdded(event);
		checkRegistryBank();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanUpdated(event);
		checkRegistryBank();
	}
	
	protected void resetPayMethod() {
		this.registryPayMethod = new RegistryPayMethod();
		this.registryPayMethod.setPayment(new PayMethod());
		resetRegistryBank();
	}

	private void resetRegistryBank() {
		setRegistryBank(new RegistryBank());
		getRegistryBank().setBankAccount(new BankAccount());
	}

	public void checkRegistryBank() throws ControllerListenerException {
		if ((!isEmpty(registryPayMethod)) && (!isShowCompanyBanks())) {		
			BankAccountValidationListener.checkBankAccount(getRegistryBank(), true);
		}		
	}
	
	@Override
	protected void updateRegistryLines(Registry registry) throws ManagerBeanException {
		super.updateRegistryLines(registry);
		updateRegistryBank(registry);
		updateRegistryPayMethod(registry);
	}	
	
	@Override
	protected void resetRegistryLines() {
		super.resetRegistryLines();
		this.registryPayMethod = null;
	}

	protected void updateRegistryBank(Registry registry) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		if (registryPayMethod != null) {
			if ((!isEmpty(registryPayMethod)) && (!isShowCompanyBanks())) {
				getRegistryBank().setRegistry(registry);
				bean.insertOrUpdate(getRegistryBank());
			}
		}
	}
	
	public boolean isShowCompanyBanks() {
		PayMethodType type = (registryPayMethod.getPayment() != null) ? registryPayMethod.getPayment().getType() : null;
		return (isCustomerMode() && type != PayMethodType.NEGOTIABLE_DOCUMENT) || (!isCustomerMode() && type != PayMethodType.BANK_TRANSFER);
	}

	protected void updateRegistryPayMethod(Registry registry) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryPayMethod.class);
		if (registryPayMethod != null) {
			if (! isEmpty(registryPayMethod)) {
				registryPayMethod.setRegistry(registry);
				bean.insertOrUpdate(registryPayMethod);
			} else if (registryPayMethod.getId() != null) {
				bean.remove(registryPayMethod);
			}
		}
	}

	private boolean isEmpty(RegistryPayMethod payMethod) {
		return (registryPayMethod == null)
			|| (registryPayMethod.getPayment() == null)
			|| (this.registryPayMethod.getPayment().getType() == null);
	}
	
	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			resetRegistryBank();
			this.registryPayMethod.setNumberOfPayments(1);
		}
	}
	
	public boolean isCash() {
		return (registryPayMethod.getPayment() != null) && (registryPayMethod.getPayment().getType() == PayMethodType.CASH_BASIS);
	}
	
	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData(getRegistryBank());
	}

}