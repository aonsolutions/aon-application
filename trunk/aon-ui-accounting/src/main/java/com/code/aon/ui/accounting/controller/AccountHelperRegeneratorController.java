package com.code.aon.ui.accounting.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.accounting.util.AccountHelperManager;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class AccountHelperRegeneratorController implements IProgression {

	private Long progressionCurrentValue = -1L;
	private boolean progressionEnabled;

	public void onEditSearch(ActionEvent event) {

	}

	public void regenerateAccountHelper(ActionEvent event) {
		try {
			AccountHelperManager manager = new AccountHelperManager();
			setProgressionCurrentValue(0L);
			manager.regenerateAccountHelper(this);
			AonUtil.addInfoMessage("Las ayudas para las contrapartidas de cuentas de han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se han podido regenerar las ayudas para las contrapartidas de cuentas . Causa: "
					+ e.getMessage());
		} finally {
			setProgressionCurrentValue(-1L);
		}
	}

	@Override
	public Long getProgressionCurrentValue() {
		return progressionCurrentValue;
	}

	@Override
	public void setProgressionCurrentValue(Long currentValue) {
		progressionCurrentValue = currentValue;
	}

	@Override
	public boolean isProgressionEnabled() {
		return progressionEnabled;
	}

	@Override
	public void setProgressionEnabled(boolean enabled) {
		this.progressionEnabled = enabled;
	}

}
