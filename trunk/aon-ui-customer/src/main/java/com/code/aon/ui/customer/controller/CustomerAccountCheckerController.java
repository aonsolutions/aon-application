package com.code.aon.ui.customer.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.util.AonUtil;

public class CustomerAccountCheckerController extends CustomerController {

	@Override
	public boolean isAccountSynchronizable() {
		try {
			if (getModel().isRowAvailable()) {
				Customer customer = (Customer)getModel().getRowData();
				return isAccountSynchronizable(customer);
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
				Customer customer = (Customer)getModel().getRowData();
				onAccountSynchronize(customer);
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
				Customer customer = (Customer)getModel().getRowData();
				onNewAccount(customer);
			}
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

}
