package com.esferalia.aon.gwt.payroll.server;

import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

@SuppressWarnings("serial")
@WebServlet(
		name = "CostExporterServlet", 
		urlPatterns = { 
				"/aon_gwt_aio/cost/*" ,
				"/aon_gwt_payroll/cost/*" 
		}
)
public class CostExporterServlet extends SalaryExporterServlet {
	
	
	@Override
	protected String getReportKey(String domain, Integer enterpriseID, SalaryType salaryType) throws SQLException {
		return IPayrollConstants.COST_REPORT;
	}
	
	
}
