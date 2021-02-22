package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqCost;
import com.esferalia.aon.gwt.payroll.shared.MainCost;
import com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV;
import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayroll;
import com.esferalia.aon.jooq.tables.Workplace;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.ibm.icu.util.Calendar;

//@SuppressWarnings("serial")
//@WebServlet(
//		name = "Cost-Excel", 
//		urlPatterns = { 
//				"/aon_gwt_aio/cost_excel/*" ,
//				"/aon_gwt_payroll/cost_excel/*" 
//		}
//)
public class CostCSVServlet extends HttpServlet {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String _domainName = request.getServerName();
		String _month = request.getParameter("month");
		String _year = request.getParameter("year");
		String _enterpriseId = request.getParameter("enterpriseId");
		String _workplaceId = request.getParameter("workplaceId");
		String _salary = request.getParameter("salary");
		String _extra = request.getParameter("extra");
		String _settle = request.getParameter("settle");
		String _delay = request.getParameter("delay");
		
		System.out.println(
				"DomainName : " + _domainName + "\n" +
				"Month : " + _month + "\n" +
				"Year : " + _year + "\n" +
				"EnterpriseId : " + _enterpriseId + "\n" +
				"WorkplaceId : " + _workplaceId + "\n" 
		);
		
//		Date startDate = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(_year));
		calendar.set(Calendar.MONTH, Integer.parseInt(_month));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Date startDate = calendar.getTime();
		
//		startDate.setYear(Integer.parseInt(_year));
//		startDate.setMonth(Integer.parseInt(_month));
//		startDate.setDate(1);
		
		Date endDate = DateUtils.getLastDayOfMonth(startDate);
		


		DSLContext ctx;
		
//		response.setContentType(MimeType.MS_EXCEL.getName());
//		response.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
		
		response.setContentType(MimeType.CSV.getName());
		response.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.CSV.getExtension()+ "\";");
		
		try (ServletOutputStream sos = response.getOutputStream();
			Connection connection = AonServletUtils.getConnection(_domainName);
			AONContext aonContext = new AONContext(connection) ) {
			ctx = aonContext.getDslContext();
			
			Condition condition = 
					DSL.year(SALARY.ISSUE_DATE).eq(Integer.valueOf(_year))
					.and(DSL.month(SALARY.ISSUE_DATE).eq(Integer.valueOf(_month)+1))
					;

			if ( _workplaceId != null &&  Integer.parseInt(_workplaceId) != 0)
				condition = condition.and(WORKPLACE.ID.eq(Integer.parseInt(_workplaceId)));
			if ( _enterpriseId != null )
				condition = condition.and(WORKPLACE.ENTERPRISE.eq(Integer.parseInt(_enterpriseId)));
			
//			List<Byte> _types = new ArrayList<Byte>();
//			_types.add((_salary.equals("1")) ? (byte) 0 : (byte) -1);
//			_types.add((_extra.equals("1")) ? (byte) 1 : (byte) -1);
//			_types.add((_settle.equals("1")) ? (byte) 2 : (byte) -1);
//			_types.add((_delay.equals("1")) ? (byte) 3 : (byte) -1);
			
//			Stream<EnterprisePayroll> stream = EnterprisePayrollExcel.getEnterprisePayrolls(ctx, condition);
			
			Stream<com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV.EnterprisePayroll> stream = EnterprisePayrollCSV.getEnterprisePayrolls(ctx, condition);

			
			List<IEnterprisePayroll> list = stream.collect(Collectors.toList());
//			EnterprisePayrollExcel.write(sos, list, Optional.empty());
			EnterprisePayrollCSV.write(sos, list);

			sos.flush();
			response.flushBuffer();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
}