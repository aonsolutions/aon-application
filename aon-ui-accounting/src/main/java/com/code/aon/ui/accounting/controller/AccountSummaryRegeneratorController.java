package com.code.aon.ui.accounting.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.event.AccountSummaryManager;
import com.code.aon.accounting.event.IProgressionBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountSummaryRegeneratorController extends BasicController implements
		IProgressionBean {

	private Period period;
	private Long progressionCurrentValue = -1L;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
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

}
