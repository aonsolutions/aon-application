package com.code.aon.ui.accounting.check.modules.account;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckEntryAdapter;
import com.code.aon.ui.util.AonUtil;

public class AccountEnabledCheckEntry extends CheckEntryAdapter {

	private boolean fixed = false;
	private String fixLabel;
	

	@Override
	public void onFix(ActionEvent event) throws AonCheckException{
		try {
			Account account = (Account) getTo();
			IManagerBean bean = BeanManager.getManagerBean(Account.class);
			account.setEntryEnabled(!account.isEntryEnabled());
			bean.update(account);
			fixed = true;
		} catch (ManagerBeanException e) {
			String message = "No se pudo actualizar la cuenta contable. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}

	@Override
	public boolean isFixAvailable() {
		return true;
	}
	@Override
	public boolean isFixed() {
		return fixed ;
	}

	@Override
	public String getMessage() {
		Account account = (Account) getTo();
		return super.getMessage() + "(" + account.getCode() + " " + account.getDescription() + ")";	
	}

	@Override
	public String getFixActionLabel() {
		return fixLabel;
	}
	
	public void setFixActionLabel(String fixLabel) {
		this.fixLabel = fixLabel;
	}
	
}
