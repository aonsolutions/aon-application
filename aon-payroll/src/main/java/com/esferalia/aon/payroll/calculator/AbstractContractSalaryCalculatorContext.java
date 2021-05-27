package com.esferalia.aon.payroll.calculator;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext.SQLNoItContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public abstract class AbstractContractSalaryCalculatorContext implements
		IContractSalaryCalculatorContext {

	public static Date prevMonth(Date date) {
		return addMonth2Date(date, -1);
	}

	protected static Date addMonth2Date(Date date, int months) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	protected IListener listener;

	@Override
	public IListener getListener() {
		return listener;
	}

	public void setListener(IListener listener) {
		this.listener = listener;
	}
	
	//public abstract ISalary getSalary(Date date);
	
	public abstract Date getDate(String tableLabel, String columnLabel);
	
	//public abstract IContractSalaryCalculatorContext getContractSalaryCalculatorContext(Date startDate, Date endDate);
	
	
	
/*	private ISalary calcSalary(Date itStartDate) {
		ContractSalaryCalculator calculator = 
				new ContractSalaryCalculator();
		SalaryBuilder builder = new SalaryBuilder();
		calculator.setSalaryBuilder(builder);
		
		try {
			Date endDate = DateUtils.addDays(itStartDate, -1);
			IContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(getStartDate(), endDate);
			return calculator.calculate(ctx);
		} catch (SalaryException e) {
			throw new RuntimeException(e);
		}		
	}*/
	

	
	
}
