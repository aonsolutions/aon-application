package com.code.aon.ui.accounting.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountEntryDetailControllerListener.class.getName());

	private String concept;
	private Account balancingAccount;
	private Account account;

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Account getBalancingAccount() {
		return balancingAccount;
	}

	public void setBalancingAccount(Account balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) event.getController().getTo();
			List<AccountEntryDetail> list = (List <AccountEntryDetail>) event.getController().getModel().getWrappedData();
			if (list.size()>0){
				double imp = getBalance(list);
				if (imp > 0) {
					detail.setCredit(imp);	
				} else {
					detail.setDebit(CommonUtil.round(imp*(-1)));
				}
				detail.setConcept(getConcept());
				if (getBalancingAccount() != null && getBalancingAccount().getId() != null) {
					detail.setAccount(getBalancingAccount());
				}else {
					detail.setAccount((Account) BeanManager.getManagerBean(Account.class).createNewTo());
				}
				detail.setBalancingAccount(getAccount());
			}
			setBalancingAccount((Account) BeanManager.getManagerBean(Account.class).createNewTo());
			setAccount((Account) BeanManager.getManagerBean(Account.class).createNewTo());
			setConcept(null);
		} catch (ManagerBeanException e) {
			LOGGER.error("Unable to initialize pojo. " + e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AccountEntryDetail detail = (AccountEntryDetail) event.getController().getTo();
		setConcept(detail.getConcept());
		setBalancingAccount(detail.getBalancingAccount());
		setAccount(detail.getAccount());
    	AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME); 
        c.setTotalCredit(null);
        c.setTotalDebit(null);
	}
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
    	AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME); 
        c.setTotalCredit(null);
        c.setTotalDebit(null);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
    	AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME); 
        c.setTotalCredit(null);
        c.setTotalDebit(null);
	}
	
	private double getBalance(List<AccountEntryDetail> list) {
		double imp = 0;
		for (AccountEntryDetail detail: list) {
			imp = CommonUtil.round(imp + detail.getDebit());
			imp = CommonUtil.round(imp - detail.getCredit());
			setConcept(detail.getConcept());
			setBalancingAccount(detail.getBalancingAccount());
			setAccount(detail.getAccount());
		}
		return imp;
	}

}