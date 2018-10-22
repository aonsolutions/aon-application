package com.esferalia.aon.gwt.payroll.server;

import java.sql.SQLException;

import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

@SuppressWarnings("serial")
public class CostExporterServlet extends SalaryExporterServlet {
	
	
	@Override
	protected String getReportKey(String domain, Integer enterpriseID, SalaryType salaryType) throws SQLException {
		return IPayrollConstants.COST_REPORT;
	}
	
	
}
