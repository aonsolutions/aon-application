package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLWriter;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class SQLSalaryBuilder extends  AbstractSQLSalaryBuilder {

	
	private SQLWriter sqlWriter;
	
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
		int salaryId = sqlWriter.insertSalary(salary);

		for (AbstractSQL.SalaryPayment salaryPayment : salaryPayments) {
			salaryPayment.setSalary(salaryId);
			sqlWriter.insertSalaryPayment(salaryPayment);
		}

		for (AbstractSQL.SalaryDeduction salaryDeduction : salaryDeductions) {
			salaryDeduction.setSalary(salaryId);
			sqlWriter.insertSalaryDeduction(salaryDeduction);
		}
	}

	

}
