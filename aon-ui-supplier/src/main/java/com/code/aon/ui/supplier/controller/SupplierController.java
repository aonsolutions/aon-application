package com.code.aon.ui.supplier.controller;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;

public class SupplierController extends RegistryController {

    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";
    private static final String MSG_KEY_PREFIX = "aon_supplier_report";
	
	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Supplier)getTo());
	}

	protected boolean isAccountSynchronizable(Supplier supplier) {
		Account account = supplier.getAccount();
		return (account != null && account.getId() != null && !supplier.getRegistry().getFullName().equals(account.getDescription()));
	}

	public void onAccountSynchronize(ActionEvent event) {
		onAccountSynchronize((Supplier)getTo());
	}

	protected void onAccountSynchronize(Supplier supplier) {
		try {
			supplier.getAccount().setDescription(supplier.getRegistry().getFullName());
			supplier.getAccount().setAlias(supplier.getRegistry().getAlias());
			BeanManager.getManagerBean(Account.class).update(supplier.getAccount());
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public void onNewAccount(ActionEvent event) {
		onNewAccount((Supplier)getTo());
	}

	protected void onNewAccount(Supplier supplier) {
		try {
			AccountBridgeUtil accountBridgeUtil = new AccountBridgeUtil();
			supplier.setAccount(accountBridgeUtil.obtainNewSupplierAccount(supplier));
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	public String getReportTitle(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX);
	}

}