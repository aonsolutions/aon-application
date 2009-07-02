package com.code.aon.ui.accounting.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.event.AccountSummaryManager;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class AccountSummaryRegeneratorController implements IProgression {

	private Period period;
	private Long progressionCurrentValue = -1L;
	private boolean progressionEnabled;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public void onEditSearch(ActionEvent event) {
		this.setPeriod(null);
	}

	public void regenerateAccountSummary(ActionEvent event) {
		try {
			setProgressionCurrentValue(0L);
			AccountSummaryManager.regenerateAccountSummary(period, this);
			setProgressionCurrentValue(-1L);
			AonUtil.addInfoMessage("Los Acumulados de Cuentas se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se han podido regenerar los Acumulados de Cuentas. Causa: "
					+ e.getMessage());
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
