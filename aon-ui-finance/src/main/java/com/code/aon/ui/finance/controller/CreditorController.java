package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.CREDITOR_REPORT;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;

public class CreditorController extends RegistryController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showAuditInfoWindow;
	
	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Creditor)getTo());
	}

	protected boolean isAccountSynchronizable(Creditor creditor) {
		Account account = creditor.getAccount();
		System.out.println(account.getDomain() +" --- "+ creditor.getDomain());
		return (account != null 
			&& account.getId() != null 
			&& account.getDomain() == creditor.getDomain()
			&& !creditor.getRegistry().getFullName().equals(account.getDescription()));
	}

	public void onAccountSynchronize(ActionEvent event) {
		onAccountSynchronize((Creditor)getTo());
	}

	protected void onAccountSynchronize(Creditor creditor) {
		try {
			creditor.getAccount().setDescription(creditor.getRegistry().getFullName());
			creditor.getAccount().setAlias(creditor.getRegistry().getAlias());
			BeanManager.getManagerBean(Account.class).update(creditor.getAccount());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onNewAccount(ActionEvent event) {
		onNewAccount((Creditor)getTo());
	}

	protected void onNewAccount(Creditor creditor) {
		try {
			AccountBridgeUtil accountBridgeUtil = new AccountBridgeUtil();
			creditor.setAccount(accountBridgeUtil.obtainNewCreditorAccount(creditor));
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

    public String getReportTitle(){
    	return AonUtil.getMessage(CREDITOR_REPORT);
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
    
}