package com.esferalia.aon.in.payroll.servlets;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV;
import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayroll;

public class ExcelServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("type", "excel");

		if (req.getAttribute("type").equals("csv")) {
			downloadCSV(req, resp);
		} else {
			downloadExcel(req, resp);
		}

	}

	private void downloadExcel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		Settings settings = new Settings();
		settings.setRenderSchema(false);

		DSLContext ctx;
		try (ServletOutputStream sos = resp.getOutputStream()) {
			ctx = DSL.using(getConnection(), SQLDialect.MARIADB, settings);
			Condition condition = DSL.year(SALARY.ISSUE_DATE).eq(2020);
			Condition con = DSL.condition(true);
			Stream<EnterprisePayroll> stream = EnterprisePayrollExcel.getEnterprisePayrolls(ctx, con);

			sos.flush();
			List<IEnterprisePayroll> list = stream.collect(Collectors.toList());
			EnterprisePayrollExcel.write(sos, list, Optional.empty());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void downloadCSV(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/csv");
		Settings settings = new Settings();
		settings.setRenderSchema(false);

		DSLContext ctx;
		try (ServletOutputStream sos = resp.getOutputStream()) {
			ctx = DSL.using(getConnection(), SQLDialect.MARIADB, settings);
			Condition condition = DSL.year(SALARY.ISSUE_DATE).eq(2020);
			Condition con = DSL.condition(true);
			Stream<com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV.EnterprisePayroll> stream = EnterprisePayrollCSV
					.getEnterprisePayrolls(ctx, con);
			List<IEnterprisePayroll> list = stream.collect(Collectors.toList());
			EnterprisePayrollCSV.write(sos, list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
	}
}
