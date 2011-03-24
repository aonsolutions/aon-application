package com.esferalia.aon.payroll.calculator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import com.code.aon.common.AonException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalaryProxy;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public class ContractSalaryCalculatorContext implements IContractSalaryCalculatorContext{

	private SQLContractSalaryCalculatorContext ctx;
	private Contract contract;
	
	public ContractSalaryCalculatorContext(Contract contract, Date startDate, Date endDate, Date issueDate) throws SalaryException {
		try {
			this.contract = contract;
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(Contract.class.getName());
			Connection c = HibernateUtil.getSQLConnection(sessionFactoryName);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression("contract.id", contract.getId());
			this.ctx = new SQLContractSalaryCalculatorContext(c, 
						startDate, 
						endDate,
						issueDate,
						criteria );
			if (!this.ctx.next()) {
				throw new SalaryException("¿?");	
			}
		} catch (ExpressionException e) {
			throw new SalaryException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new SalaryException(e.getMessage(),e);
		}
	}
	
	@Override
	public Date getIssueDate() {
		return ctx.getIssueDate();
	}

	@Override
	public Date getStartDate() {
		return ctx.getStartDate();
	}

	@Override
	public Date getEndDate() {
		return ctx.getEndDate();
	}

	@Override
	public ISalaryProxy getSalaryProxy() {
		return this.contract;
	}

	@Override
	public ExpressionContext getExpressionContext() {
		return ctx.getExpressionContext();
	}

	@Override
	public String getCcc() {
		return ctx.getCcc();
	}

	@Override
	public String getEnterpriseName() {
		return ctx.getEnterpriseName();
	}

	@Override
	public String getEnterpriseAddress() {
		return ctx.getEnterpriseAddress();
	}

	@Override
	public String getEnterpriseDocument() {
		return ctx.getEnterpriseDocument();
	}

	@Override
	public SSRegimeType getSSRegime() {
		return ctx.getSSRegime();
	}

	@Override
	public String getCategory() {
		return ctx.getCategory();
	}
	
	@Override
	public String getQuoteGroup() {
		return ctx.getQuoteGroup();
	}
	
	@Override
	public String getEmployeeName() {
		return ctx.getEmployeeName();
	}

	@Override
	public String getEmployeeDocument() {
		return ctx.getEmployeeDocument();
	}

	@Override
	public String getSocialSecurityNumber() {
		return ctx.getSocialSecurityNumber();
	}

	@Override
	public Integer getRegistration() {
		return ctx.getRegistration();
	}

	@Override
	public Date getSeniorityDate() {
		return ctx.getSeniorityDate();
	}

	@Override
	public Collection<IContractPayment> getContractPayments() throws AonException {
		return ctx.getContractPayments();
	}

	@Override
	public Collection<IContractDeduction> getContractDeductions() throws AonException {
		return ctx.getContractDeductions();
	}
	
}
