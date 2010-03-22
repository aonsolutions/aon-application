package com.code.aon.ui.registry.controller.event;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.config.event.BankAccountValidationListener;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class RegistryPayMethodFormListener extends RegistryFormListener {
	
	private RegistryPayMethod registryPayMethod;
	
	private PayMethodType registryPayMethodType = PayMethodType.BANK_TRANSFER;	
	

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
		this.registryPayMethod.setRegistryBank( registryBank );
	}
	
	public PayMethodType getRegistryPayMethodType() {
		return registryPayMethodType;
	}

	public void setRegistryPayMethodType(PayMethodType registryPayMethodType) {
		this.registryPayMethodType = registryPayMethodType;
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanCreated(event);
		this.registryPayMethod = new RegistryPayMethod();
		this.registryPayMethod.setPayment( new PayMethod() );
		resetRegistryBank();
	}
	
	private void resetRegistryBank() {
		this.registryPayMethod.setRegistryBank( new RegistryBank() );
		this.registryPayMethod.getRegistryBank().setBank( new Bank() );
		this.registryPayMethod.getRegistryBank().setBankAccount( new BankAccount() );		
	}

	private boolean isEmpty( RegistryPayMethod payMethod ) {
		return (registryPayMethod.getPayment() == null) || (this.registryPayMethod.getPayment().getType() == null);
	}
	
	private void updateRegistryPayMethod( Registry registry ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryPayMethod.class);
		if (! isEmpty(registryPayMethod) ) {
			registryPayMethod.setRegistry(registry);
			bean.insertOrUpdate(registryPayMethod);
		} else if ( registryPayMethod.getId() != null ) {
			bean.remove(registryPayMethod);
		}
	}

	private void updateRegistryBank( Registry registry ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		if ( (! isEmpty(registryPayMethod)) && (! isShowCompanyBanks())  ) {
			getRegistryBank().setRegistry(registry);
			bean.insertOrUpdate(getRegistryBank());
		}
	}
	
	@Override
	protected void updateRegistryLines(Registry registry) throws ManagerBeanException {
		super.updateRegistryLines(registry);
		updateRegistryBank(registry);
		updateRegistryPayMethod(registry);
	}	
	
	public void checkRegistryBank() throws ControllerListenerException {
		if ( (! isEmpty(registryPayMethod)) && (! isShowCompanyBanks())  ) {		
			BankAccountValidationListener.checkBankAccount(getRegistryBank(), true);
		}		
	}
	
	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			resetRegistryBank();
		}
	}
	
	public boolean isShowCompanyBanks() {
		return (registryPayMethod.getPayment() != null)
			&& (registryPayMethod.getPayment().getType() != registryPayMethodType);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanAdded(event);
		checkRegistryBank();
	}		
	
}