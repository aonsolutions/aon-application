package com.code.aon.ui.accounting.util;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.IDefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.controller.AccountAppParamsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountingPeriodUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountingPeriodUtil.class.getName());
	private static final String ACCOUNT_APP_PARAM_CONTROLLER_NAME = "accAppParams";

	public static Period getDefaultPeriod() throws ManagerBeanException {
		AccountAppParamsController c = (AccountAppParamsController) AonUtil
				.getRegisteredBean(ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		ApplicationParameter param = c.getParameter(IDefaultAccounts.DEFAULT_PERIOD);
		if (param != null && param.getValue() != null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			try {
				Integer periodId =  Integer.parseInt(param.getValue());
				return (Period) periodBean.get(periodId);
			} catch (NumberFormatException e) {
				// Nada.
			}
		}
		return null;
	}

	public static void validateAccountPeriod(Date date) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(periodBean
				.getFieldName(IEntityAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean
				.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE), date);
		Iterator<?> iter = periodBean.getList(criteria).iterator();
		if (!iter.hasNext()) {
			String msg = "No hay ejercicio contable definido para la fecha indicada";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public Period getPreviousPeriod(Period period) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		String deadlineAlias = periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE);
		criteria.addLessThanOrEqualExpression(deadlineAlias, period.getInitiationDate());
		criteria.addOrder(deadlineAlias, false);
		Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (Period) iter.next();
		}
		return null;
	}
}
