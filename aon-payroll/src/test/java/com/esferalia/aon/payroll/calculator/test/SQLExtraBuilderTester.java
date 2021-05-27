package com.esferalia.aon.payroll.calculator.test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class SQLExtraBuilderTester extends SQLSalaryBuilderTester {

	
	
	public SQLExtraBuilderTester(Connection connection) throws SQLException {
		super(connection, SalaryType.EXTRA);
	}

	protected void test(ResultSet rs ) throws SQLException {
		while ( rs.next() ) {
			Date dbIssueDate =  rs.getDate(SalaryColumns.ISSUE_DATE);
			if ( issueDate.getMonth() == dbIssueDate.getMonth() ) { 
				assertDoubleField(SalaryColumns.TOTAL_PAYMENT, rs);
				assertDoubleField(SalaryColumns.IRPF_BASE, rs);
				assertDoubleField(SalaryColumns.TOTAL_LIQUID, rs);
				System.out.println(String.format("SUCCESS : [%tD-%tD] %f %s %s %s", 
						rs.getDate(SalaryColumns.START_DATE),
						rs.getDate(SalaryColumns.END_DATE),
						rs.getBigDecimal(SalaryColumns.TOTAL_LIQUID),
						rs.getString(SalaryColumns.EMPLOYEE_DOCUMENT),
						rs.getString(SalaryColumns.ENTERPRISE_NAME),
						rs.getString(SalaryColumns.EMPLOYEE_NAME)));
			}
		}
	}

	
}
