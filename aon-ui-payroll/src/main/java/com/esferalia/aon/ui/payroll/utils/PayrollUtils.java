package com.esferalia.aon.ui.payroll.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;


public class PayrollUtils {
	
	
	public ISalary calculateSalary(Contract contract, Date date) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(date);
		startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(date);
		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calculateSalary(contract, startCal.getTime(), endCal.getTime());
	}

	public ISalary calculateSalary(Contract contract, Date startDate, Date endDate) {
		try {
			ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(startDate, endDate, SalaryType.SALARY);
			return ctx.getSalaryProxy().getSalary();
		} catch (SalaryException e) {
			// sigue ...
		}
		return null;
	}
	
	public ISalary getLastSalary(Contract contract) {
		return getBeforeDateSalary(contract, null);
	}
	
	public ISalary getBeforeDateSalary(Contract contract, Date beforeDate) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contract.getId());
			if(beforeDate != null){
				Calendar cal = Calendar.getInstance();
				cal.setTime(beforeDate);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.SALARY_END_DATE), cal.getTime());
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.SALARY_END_DATE), false);
			List<ITransferObject> list = bean.getList(criteria);
			if(list!=null && !list.isEmpty()){
				return (ISalary) list.get(0);
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener la ultima nomina";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		return null;
	}
	
}
