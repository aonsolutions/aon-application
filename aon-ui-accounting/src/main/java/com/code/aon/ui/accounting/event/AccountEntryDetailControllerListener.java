package com.code.aon.ui.accounting.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.controller.AccountEntryController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryDetailControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountEntryDetailControllerListener.class.getName());
	private static final String ENTRY_CONTROLLER = "accountEntry";

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
				detail.setAccount(getBalancingAccount());
				detail.setBalancingAccount(getAccount());
			}
			setBalancingAccount(null);
			setAccount(null);
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
    	AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(ENTRY_CONTROLLER); 
        c.setTotalCredit(null);
        c.setTotalDebit(null);
	}
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
    	AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(ENTRY_CONTROLLER); 
        c.setTotalCredit(null);
        c.setTotalDebit(null);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
    	AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(ENTRY_CONTROLLER); 
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