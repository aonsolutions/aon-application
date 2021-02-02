package com.esferalia.aon.gwt.payroll.server;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ReportsServlet extends HttpServlet {

	public static void getPayrolls(Connection connection, PrintStream os, Condition where, SortField<?>... sortFields)
			throws ManagerBeanException {
		try (
				AONContext aonContext = new AONContext(connection);
				DSLContext dslContext = aonContext.getDslContext();
		) {
			

			dslContext.select().from(com.esferalia.aon.jooq.tables.Salary.SALARY)
			.innerJoin(com.esferalia.aon.jooq.tables.Contract.CONTRACT).onKey()
			.innerJoin(com.esferalia.aon.jooq.tables.Workplace.WORKPLACE).onKey()
			.innerJoin(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE).onKey()
			.where(where)
			.orderBy(sortFields)
			.fetch().stream().forEach(record -> {

				SalaryRecord salaryRecord = record.into(com.esferalia.aon.jooq.tables.Salary.SALARY);
				ContractRecord contractRecord = record.into(com.esferalia.aon.jooq.tables.Contract.CONTRACT);
				WorkplaceRecord workplaceRecord = record.into(com.esferalia.aon.jooq.tables.Workplace.WORKPLACE);
				EnterpriseRecord enterpriseRecord = record.into(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE);
				
//				ss: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeSS()(),
//				nif: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeDocument()(),
//				fullname : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeName()(),
//				city : payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeCity()(),
//				address: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAddress()(),
//				category: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAgreementCategory()(),
//				quote_group: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeQuoteGroup()(),
//				professional_group: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeAgreementCategory()(),
//				seniority_date: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeSeniorityDateTime()(),
//				contract_type: payroll.@com.esferalia.aon.js.payroll.client.Reports.Payroll::getEmployeeContractType()()


				os.printf(
				"{\n"
				+ "logoEnterprise'%s'\n"
				+ ",logoEnterprise2'%s'\n"
				+ ",net:%f\n"
				+ ",payment:%f\n"
				+ ",deduction:%f\n"
				+ ",total_accrual:%f\n"
				+ ",total_deductions:%f\n"
				+ ",liquid_perceive:%f\n"
				+ ",place:'%s'\n"
				+ ",date:'%s'\n"
				+ ",reason'%s'\n"

				+ ",enterprise:{\n"
				+ "\tcif:'%s'\n"
				+ "\t,name:'%s'\n"
				+ "\t,city:'%s'\n"
				+ "\t,address:'%s'\n"
				+ "\t,ccc:'%s'\n"
				+ "}\n"

				+ ",employee:{\n"
				+ "\tss:'%s'\n"
				+ "\tnif:'%s'\n"
				+ "\tfullname:'%s'\n"
				+ "\tcity:'%s'\n"
				+ "\taddress:'%s'\n"
				+ "}\n"
				
				+ "}"
				,BLANK_IMAGE
				,BLANK_IMAGE
				,salaryRecord.getTotalLiquid()
				,salaryRecord.getTotalPayment()
				,salaryRecord.getTotalDeduction()
				,0.00 	//TODO:stotalAccrual
				,0.00 	//TODO:totalDeductions
				,salaryRecord.getTotalLiquid()
				,"" 	//TODO:place
				,""		//TODO:date
				,""		//TODO:reason
				
				// Enterprise
				,stringify(salaryRecord.getEnterpriseDocument())
				,stringify(salaryRecord.getEnterpriseName())
				, ""	//TODO:city
				,stringify(salaryRecord.getEnterpriseAddress())
				,stringify(salaryRecord.getCcc())
				
				// Employee
				,stringify(salaryRecord.getSocialSecurityNumber())
				,stringify(salaryRecord.getEmployeeDocument())
				,stringify(salaryRecord.getEmployeeName())
				, ""	//TODO:city
				, ""	//TODO:address
				
				
				
				);
				
				
//				Map<String, List<String>> salaryDatas = dslContext
//				.select()
//				.from(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA)
//				.where(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA.SALARY.eq(salaryRecord.getId()))
//				.fetchGroups(com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA.NAME, com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA.EXPRESSION)
//				;
//				salaryDatas.getOrDefault(ContextVariable.TC2, Collections.emptyList())
//				.stream().findAny().ifPresent( tc2 -> salary.setEmployeeContractType(tc2));
				

			});
		}

	}

	private static String stringify(String str) {
		return AonStringUtils.replace(str, "'", "\\'");
	}
	
	private static String BLANK_IMAGE = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIBAQICAgICAgICAwUDAwMDAwYEBAMFBwYHBwcGBwcICQsJCAgKCAcHCg0KCgsMDAwMBwkODw0MDgsMDAz/2wBDAQICAgMDAwYDAwYMCAcIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAz/wAARCAABAAEDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD9/KKKKAP/2Q==";


}
