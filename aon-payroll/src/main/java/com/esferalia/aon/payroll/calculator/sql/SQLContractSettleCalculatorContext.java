package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.irpf.IIrpfCalculatorContext;
import com.esferalia.aon.payroll.irpf.IrpfCalculator;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

public class SQLContractSettleCalculatorContext 
	extends SQLContractSalaryCalculatorContext{

	
	
	
	public SQLContractSettleCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, null);
	}

	public SQLContractSettleCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, criteria, getPaymentsCriteria(SalaryType.SETTLE));
	}
	
	public SQLContractSettleCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, null,criteria, paymentsCriteria );
	}

	public SQLContractSettleCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate, criteria, getPaymentsCriteria(SalaryType.SETTLE) );
	}

	public SQLContractSettleCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate, criteria, paymentsCriteria );
	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.SETTLE;
	}
	
	
	// ------------------------------------------------------------------------
	
	
	@Override
	public double getIrpf() {
		
		Date endDate = getEndDate();
		Date startDate = getFirstDayOfYear(endDate);
		
		
		Criteria contractCriteria = getContractCriteria();

		IIrpfCalculatorContext irpfCalculatorContext = getIrpfCalculatorContext(
				connection, startDate, endDate, contractCriteria);

		IrpfOutcome irpfOutcome = IrpfCalculator
				.calculateIrpf(irpfCalculatorContext);

		onIrpf(irpfOutcome);

		return irpfOutcome.getIrpfResult().getIrpf();
	}
	
}