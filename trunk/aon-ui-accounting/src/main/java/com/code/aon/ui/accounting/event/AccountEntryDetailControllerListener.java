package com.code.aon.ui.accounting.event;

import java.util.List;
import java.util.logging.Logger;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountEntryDetailControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger
			.getLogger(AccountEntryDetailControllerListener.class.getName());

	private String concept;
	private Account balancingAccount;
	private Account account;

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		// ESTA LINEA SE DESCOMENTA PORQUE NO LE ENCONTRAMOS SENTIDO.
		// event.getController().setModel(null);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AccountEntryDetail detail = (AccountEntryDetail) event.getController().getTo();
		setConcept(detail.getConcept());
		setBalancingAccount(detail.getBalancingAccount());
		setAccount(detail.getAccount());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) event.getController().getTo();
			List<AccountEntryDetail> list = (List <AccountEntryDetail>) event.getController().getModel().getWrappedData();
			double imp = getBalance(list);
			if (imp > 0) {
				detail.setCredit(imp);	
			} else {
				detail.setDebit(CommonUtil.round(imp*(-1)));
			}
			detail.setConcept(getConcept());
			detail.setAccount(getBalancingAccount());
			detail.setBalancingAccount(getAccount());
			setBalancingAccount(null);
			setAccount(null);
			setConcept(null);
		} catch (ManagerBeanException e) {
			LOGGER.severe("Unable to initialize pojo. " + e.getMessage());
		}
	}

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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}