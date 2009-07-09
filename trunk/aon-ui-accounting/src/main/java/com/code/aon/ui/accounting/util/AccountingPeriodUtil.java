package com.code.aon.ui.accounting.util;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.controller.AccountAppParamsController;
import com.code.aon.ui.util.AonUtil;

public class AccountingPeriodUtil {

	private static final Logger LOGGER = Logger.getLogger(AccountingPeriodUtil.class.getName());
	private static final String ACCOUNT_APP_PARAM_CONTROLLER_NAME = "accAppParams";

	public static Period getDefaultPeriod() throws ManagerBeanException {
		AccountAppParamsController c = (AccountAppParamsController) AonUtil
				.getRegisteredBean(ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		ApplicationParameter param = c.getParameter(DefaultAccounts.DEFAULT_PERIOD);
		if (param != null && param.getValue() != null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			return (Period) periodBean.get(param.getValue());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void validateAccountPeriod(Date date) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(periodBean
				.getFieldName(IAccountingAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean
				.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE), date);
		Iterator iter = periodBean.getList(criteria).iterator();
		if (!iter.hasNext()) {
			String msg = "No hay ejercicio contable definido para la fecha indicada";
			LOGGER.log(Level.SEVERE, msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
}
