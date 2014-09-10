package com.esferalia.aon.payroll.calculator;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

public abstract class AbstractContractSalaryCalculatorContext implements
		IContractSalaryCalculatorContext {

	protected IListener listener;

	@Override
	public IListener getListener() {
		return listener;
	}

	public void setListener(IListener listener) {
		this.listener = listener;
	}
	
	public abstract ISalary getSalary(Date date);
	
	public abstract IContractSalaryCalculatorContext getContractSalaryCalculatorContext(Date startDate, Date endDate);
	

	public Object br(Date startDate) throws ExpressionException, SQLException,
			SalaryException {


		ISalary salary = getSalary(DateUtils.addMonths(startDate, -1));
		
		if (salary == null)
			salary = calcSalary(startDate);
		
		if (salary == null)
			throw new ExpressionException(); // TODO: Alert somebody that we
												// can't calculate proper BR.

		int days = salary.getTimeUnits();
		return salary.getCommonBase() / days;
	}
	
	private ISalary calcSalary(Date itStartDate) {
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
		
	}
	
	
}
