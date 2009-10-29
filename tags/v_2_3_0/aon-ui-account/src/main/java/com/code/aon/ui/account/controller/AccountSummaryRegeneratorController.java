package com.code.aon.ui.account.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.account.Period;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.event.AccountSummaryManager;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountSummaryRegeneratorController extends BasicController {
	
    private String period;
	
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public void onEditSearch(ActionEvent event) {
        this.setPeriod(null);
    }

	@SuppressWarnings({"unused", "unchecked"})
    public void regenerateAccountSummary(ActionEvent event) {
		try {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(periodBean.getFieldName(IAccountAlias.PERIOD_ID), period);
			Iterator iterator = periodBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				Period accountPeriod = (Period)iterator.next();
				AccountSummaryManager.regenerateAccountSummary(accountPeriod);
			}

			AonUtil.addInfoMessage("Los Acumulados de Cuentas se han regenerado correctamente.");
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se han podido regenerar los Acumulados de Cuentas. Causa: " + e.getMessage());
		}
    }

}
