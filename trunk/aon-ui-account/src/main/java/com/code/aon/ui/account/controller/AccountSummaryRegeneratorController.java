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
    public void regenerateAccountSummary(ActionEvent event) throws ManagerBeanException {
    	IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(periodBean.getFieldName(IAccountAlias.PERIOD_ID), period);
    	Iterator iterator = periodBean.getList(criteria).iterator();
    	if (iterator.hasNext()) {
    		Period accountPeriod = (Period)iterator.next();
    		AccountSummaryManager.regenerateAccountSummary(accountPeriod);
    	}
    }

}
