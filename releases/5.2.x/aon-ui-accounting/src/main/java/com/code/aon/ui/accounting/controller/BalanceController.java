package com.code.aon.ui.accounting.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.balance.BalanceDefaults;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class BalanceController extends BasicController {

	public void onLoadDefaults(ActionEvent event) {
		BalanceDefaults defaults = new BalanceDefaults();
		Balance balance = (Balance) getTo();
		Integer id = balance.getId();
		try {
			if (id >= 1 && id <= 5) {
				defaults.reloadBalance(balance);
			} else {
				String msg = "No existe un balance preconfigurado para el código " + id;
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
			super.onEditSearch(event);
			super.onSearch(event);
			for (int i = 0; i < getModel().getRowCount(); i++) {
				getModel().setRowIndex(i);
				Balance b = (Balance) getModel().getRowData();
				if (id.equals(b.getId())) {
					onSelect(event);
					break;
				}
			}
			
		} catch (ManagerBeanException e) {
			String msg = "Error al recargar el balance preconfigurado para el código " + id;
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		
	}
}
