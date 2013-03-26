package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLWriter;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilderListener;

public class SQLSalaryBuilder extends  AbstractSQLSalaryBuilder {

	
	private ISalaryBuilderListener listener;
	private SQLWriter sqlWriter;
	private int insertedSalaries;
	private static final String FORMAT = "[%s]: %s - %s";
	
	public SQLSalaryBuilder(Connection connection) 
	throws SQLException
	{
		super();
		sqlWriter = new SQLWriter(connection);
	}
	
	@Override
	public ISalary getSalary() {
		try {
			insertSalary();
			if (listener.isDebugEnabled()) {
				String msg = String.format(FORMAT, 
						salary.getEmployeeDocument(),
						salary.getEnterpriseName(),
						salary.getEmployeeName());
				listener.onDebug(msg);
			}
			++insertedSalaries;
		} catch (SQLException e) {
		}
		return null;
	}

	public void begin() throws SQLException{
		sqlWriter.begin();
	}
	
	public void commit() throws SQLException{
		sqlWriter.commit();
	}

	public void rollback() throws SQLException{
		sqlWriter.rollback();
	}

	public void insertSalary() throws SQLException{
		int domainId = salary.getDomain();
		int salaryId = sqlWriter.insertSalary(salary);
		for (AbstractSQL.SalaryCost salaryCost : salaryCosts) {
			salaryCost.setSalary(salaryId);
			salaryCost.setDomain(domainId);
			sqlWriter.insertSalaryCost(salaryCost);
		}

		for (AbstractSQL.SalaryBonus salaryBonus : salaryBonuses) {
			salaryBonus.setSalary(salaryId);
			salaryBonus.setDomain(domainId);
			sqlWriter.insertSalaryBonus(salaryBonus);
		}

		for (AbstractSQL.SalaryEmbargo salaryEmbargo : salaryEmbargos) {
			salaryEmbargo.setSalary(salaryId);
			salaryEmbargo.setDomain(domainId);
			sqlWriter.insertSalaryEmbargo(salaryEmbargo);
		}

		for (AbstractSQL.SalaryPayment salaryPayment : salaryPayments) {
			salaryPayment.setSalary(salaryId);
			salaryPayment.setDomain(domainId);
			sqlWriter.insertSalaryPayment(salaryPayment);
		}

		for (AbstractSQL.SalaryDeduction salaryDeduction : salaryDeductions) {
			salaryDeduction.setSalary(salaryId);
			salaryDeduction.setDomain(domainId);
			sqlWriter.insertSalaryDeduction(salaryDeduction);
		}
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;		
	}
	public ISalaryBuilderListener getListener() {
		return listener;
	}

	public int getInsertedSalaries() {
		return insertedSalaries;
	}

}
