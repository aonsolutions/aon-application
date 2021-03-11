package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayroll;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.ibm.icu.util.Calendar;

@SuppressWarnings("serial")
@WebServlet(
		name = "Cost-Excel", 
		urlPatterns = { 
				"/aon_gwt_aio/cost_excel/*" ,
				"/aon_gwt_payroll/cost_excel/*" 
		}
)
public class CostExcelServlet extends HttpServlet {
	
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
		
		response.setContentType(MimeType.MS_EXCEL.getName());
		response.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
		
//		response.setContentType(MimeType.CSV.getName());
//		response.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.CSV.getExtension()+ "\";");
		
		try (ServletOutputStream sos = response.getOutputStream();
//			Connection connection = AonServletUtils.getConnection(_domainName);
//			AONContext aonContext = new AONContext(connection);
			AONContext aonContext = AONContext.getAONContext(_domainName, "")) {
			ctx = aonContext.getDslContext();
			
			Condition condition = 
					DSL.year(SALARY.ISSUE_DATE).eq(Integer.valueOf(_year))
					.and(DSL.month(SALARY.ISSUE_DATE).eq(Integer.valueOf(_month)+1))
					;

			if ( _workplaceId != null &&  Integer.parseInt(_workplaceId) != 0)
				condition = condition.and(WORKPLACE.ID.eq(Integer.parseInt(_workplaceId)));
			
			Integer enterpriseId = null;
			try {
				enterpriseId = Integer.parseInt(_enterpriseId);
			} catch (NumberFormatException | NullPointerException e) {}
			if (enterpriseId == null || enterpriseId == 0)
				enterpriseId = AON.getWorkplace(aonContext.getDomainName()
						, aonContext.getDomainId()
						, aonContext.getUser()
						, w -> w.getIdProperty().eq(Integer.parseInt(_workplaceId)))
						.getEnterprise();
			
			condition = condition.and(WORKPLACE.ENTERPRISE.eq(enterpriseId));
			
//			List<Byte> _types = new ArrayList<Byte>();
//			_types.add((_salary.equals("1")) ? (byte) 0 : (byte) -1);
//			_types.add((_extra.equals("1")) ? (byte) 1 : (byte) -1);
//			_types.add((_settle.equals("1")) ? (byte) 2 : (byte) -1);
//			_types.add((_delay.equals("1")) ? (byte) 3 : (byte) -1);
			
			Stream<EnterprisePayroll> stream = EnterprisePayrollExcel.getEnterprisePayrolls(ctx, condition);
			
//			Stream<com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV.EnterprisePayroll> stream = EnterprisePayrollCSV.getEnterprisePayrolls(ctx, condition);
			String dateString = "";
			try {
				Integer month = Integer.parseInt(_month);
			if (month != null)
				month++;
			Integer year = Integer.parseInt(_year);
			dateString = getDateString(month, year);
			} catch (NumberFormatException | NullPointerException e) {}
			
			List<IEnterprisePayroll> list = stream.collect(Collectors.toList());
			String entName = EnterprisePayrollExcel.getEnterpriseName(_domainName, enterpriseId, Integer.parseInt(_workplaceId));
			EnterprisePayrollExcel.write(sos, list, Optional.empty(), entName, dateString);
//			EnterprisePayrollExcel.writeDiff(sos, list, Optional.empty());
//			EnterprisePayrollCSV.write(sos, list);

			sos.flush();
			response.flushBuffer();

		}

	}
	
	
	private static String getDateString (Integer month, Integer year) {
		if (month == null || year == null)
			return "";
		else {
			switch (month) {
			case 1:
				return "Enero de "+year;
			case 2:
				return "Febrero de "+year;
			case 3:
				return "Marzo de "+year;
			case 4:
				return "Abril de "+year;
			case 5:
				return "Mayo de "+year;
			case 6:
				return "Junio de "+year;
			case 7:
				return "Julio de "+year;
			case 8:
				return "Agosto de "+year;
			case 9:
				return "Septiembre de "+year;
			case 10:
				return "Octubre de "+year;
			case 11:
				return "Noviembre de "+year;
			case 12:
				return "Diciembre de "+year;
			default:
				return "";
			}
		}
	}
	
	
}