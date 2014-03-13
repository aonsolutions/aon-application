package com.code.aon.ui.supplier.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.util.AonUtil;

public class SupplierAccountCheckerController extends SupplierController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public boolean isAccountSynchronizable() {
		try {
			if (getModel().isRowAvailable()) {
				Supplier supplier = (Supplier)getModel().getRowData();
				return isAccountSynchronizable(supplier);
			}
			return false;
		} catch (ManagerBeanException ex) {
			String msg = "Error en la lista de Cuentas. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	@Override
	public void onAccountSynchronize(ActionEvent event) {
		try {
			if (getModel().isRowAvailable()) {
				Supplier supplier = (Supplier)getModel().getRowData();
				onAccountSynchronize(supplier);
			}
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo sincronizar la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

	@Override
	public void onNewAccount(ActionEvent event) {
		try {
			if (getModel().isRowAvailable()) {
				Supplier supplier = (Supplier)getModel().getRowData();
				onNewAccount(supplier);
			}
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

}
