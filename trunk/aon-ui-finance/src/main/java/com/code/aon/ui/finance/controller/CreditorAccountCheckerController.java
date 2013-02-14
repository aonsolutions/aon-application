package com.code.aon.ui.finance.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.ui.util.AonUtil;

public class CreditorAccountCheckerController extends CreditorController {

	@Override
	public boolean isAccountSynchronizable() {
		try {
			if (getModel().isRowAvailable()) {
				Creditor creditor = (Creditor)getModel().getRowData();
				return isAccountSynchronizable(creditor);
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
				Creditor creditor = (Creditor)getModel().getRowData();
				onAccountSynchronize(creditor);
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
				Creditor creditor = (Creditor)getModel().getRowData();
				onNewAccount(creditor);
			}
		} catch (ManagerBeanException ex) {
			String msg = "No se pudo crear la Cuenta Contable. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, ex);
		}
	}

}
