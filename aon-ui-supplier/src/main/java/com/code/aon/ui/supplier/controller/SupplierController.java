package com.code.aon.ui.supplier.controller;

import static com.code.aon.ui.common.ICommonMessages.SUPPLIER_REPORT;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;

public class SupplierController extends RegistryController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showAuditInfoWindow;
	
	public void onWithholdingChanged(ValueChangeEvent event) {
		Boolean value = (Boolean)event.getNewValue();
		if (!value) {
			((Supplier)getTo()).setWithholdingFarmer(false);
		}
	}

	public void onWithholdingFarmerChanged(ValueChangeEvent event) {
		Boolean value = (Boolean)event.getNewValue();
		if (value) {
			((Supplier)getTo()).setWithholding(true);
		}
	}

	public boolean isAccountSynchronizable() {
		return isAccountSynchronizable((Supplier)getTo());
	}

	protected boolean isAccountSynchronizable(Supplier supplier) {
		Account account = supplier.getAccount();
		return account != null 
			&& account.getId() != null 
			&& account.getDomain() == supplier.getDomain()
			&& !supplier.getRegistry().getFullName().equals(account.getDescription());
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
	   return AonUtil.getMessage(SUPPLIER_REPORT);
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