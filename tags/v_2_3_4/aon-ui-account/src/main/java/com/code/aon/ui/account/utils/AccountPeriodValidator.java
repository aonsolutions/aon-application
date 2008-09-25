package com.code.aon.ui.account.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.account.Period;
import com.code.aon.account.dao.IAccountAlias;
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
		criteria.addGreaterThanOrEqualExpression(periodBean.getFieldName(IAccountAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean.getFieldName(IAccountAlias.PERIOD_INITIATION_DATE), date);
		Iterator iter = periodBean.getList(criteria).iterator();
		if(!iter.hasNext()){
			String msg = "No hay ejercicio contable definido para la fecha indicada";
			LOGGER.log(Level.SEVERE, msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
}
