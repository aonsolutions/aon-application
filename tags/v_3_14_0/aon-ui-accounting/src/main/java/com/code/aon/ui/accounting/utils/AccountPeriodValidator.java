package com.code.aon.ui.accounting.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class AccountPeriodValidator {
	
	private static final Logger LOGGER = Logger.getLogger(AccountPeriodValidator.class.getName());

	@SuppressWarnings("unchecked")
	public static void validateAccountPeriod(Date date) throws ManagerBeanException{
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE), date);
		Iterator iter = periodBean.getList(criteria).iterator();
		if(!iter.hasNext()){
			String msg = "No hay ejercicio contable definido para la fecha indicada";
			LOGGER.log(Level.SEVERE, msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public static String getValidAccountPeriod(Date date) throws ManagerBeanException{
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE), date);
		Iterator iter = periodBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((Period)iter.next()).getId();
		}
		return null;
	}
}
