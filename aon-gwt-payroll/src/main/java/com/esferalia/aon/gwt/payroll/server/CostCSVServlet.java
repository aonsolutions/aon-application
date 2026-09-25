package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.DOMAIN;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.ENTERPRISE;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.FILTER;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.MONTH;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.USER;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.WORKPLACE;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.YEAR;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV;
import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(
		name = "Cost-CSV", 
		urlPatterns = { 
				"/aon_gwt_aio/cost_csv/*" ,
				"/aon_gwt_payroll/cost_csv/*" 
		}
)
public class CostCSVServlet extends HttpServlet {
	private SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer enterpriseId;
		Integer workplaceId;
		
		String enterpriseName;
		String workplaceName;
		
		Integer month;
		Integer year;
		
		Integer monthEnd = null;
		Integer yearEnd = null;
		
		String domainName;
		String user;
		Collection<Integer> types;
		
		//Picking up the parameters
		{
			if (req.getParameterValues(FILTER.getName()) != null)
				types = Arrays.stream(req.getParameterValues(FILTER.getName()))
				.map(str -> Integer.parseInt(str))
				.collect(Collectors.toList());
			else
				types = Collections.unmodifiableList(new ArrayList<Integer>());
			
			enterpriseId = AonStringUtils.isNotBlank(req.getParameter(ENTERPRISE.getName())) ? Integer.parseInt(req.getParameter(ENTERPRISE.getName())) : null;
			workplaceId = AonStringUtils.isNotBlank(req.getParameter(WORKPLACE.getName())) ? Integer.parseInt(req.getParameter(WORKPLACE.getName())) : null;
			
			enterpriseName = req.getParameter("enterpriseName");
			workplaceName = req.getParameter("workplaceName");
			
			month = Integer.parseInt(req.getParameter(MONTH.getName()));
			year = Integer.parseInt(req.getParameter(YEAR.getName()));
			
			try {
				monthEnd = Integer.parseInt(req.getParameter(MONTH.getName() + "End"));
				yearEnd = Integer.parseInt(req.getParameter(YEAR.getName() + "End"));
			}catch (Exception e) {}
			
			domainName = req.getParameter(DOMAIN.getName()) != null && !req.getParameter(DOMAIN.getName()).isEmpty() ? req.getParameter(DOMAIN.getName()) : req.getServerName();
			user = req.getParameter(USER.getName()) != null ? req.getParameter(USER.getName()) : "";
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startDate = calendar.getTime();
		
		Date endDate;
		if(null == yearEnd) {
			endDate = AonDateUtils.getMonthLastDay(startDate);
		} else {
			Calendar calendarEnd = Calendar.getInstance();
			calendarEnd.set(Calendar.YEAR, yearEnd);
			calendarEnd.set(Calendar.MONTH, monthEnd);
			calendarEnd.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
			calendarEnd.set(Calendar.HOUR, 0);
			calendarEnd.set(Calendar.MINUTE, 0);
			calendarEnd.set(Calendar.SECOND, 0);
			endDate = AonDateUtils.getMonthLastDay(calendarEnd.getTime());
		}
		
		resp.setContentType(MimeType.CSV.getName());
		String fileName = "Costes_" + enterpriseName + "_" + (AonStringUtils.isBlank(workplaceName) ? "" : workplaceName) + "_" + formatter.format(startDate) + "." + MimeType.CSV.getExtension();
		resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\";");
		
		try (ServletOutputStream sos = resp.getOutputStream();
				CloseableAONContext aonContext = AONContext.getAONContext(domainName, user) ) {
				
				if (enterpriseId == null || enterpriseId == 0)
					enterpriseId = WorkplaceDAO.get(aonContext, workplaceId, new Options().setSecurity(false)).getEnterprise();
				
				Stream<com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV.EnterprisePayroll> stream =
						EnterprisePayrollCSV.getEnterprisePayrolls(aonContext, startDate, endDate, enterpriseId, workplaceId);
				
				List<IEnterprisePayroll> list = stream
						.filter(p -> types.contains(p.getSalaryType().ordinal()))
						.sorted(Comparator.comparing(IEnterprisePayroll::getEmployee))
						.collect(Collectors.toList());
				
				EnterprisePayrollCSV.write(sos, list);

				sos.flush();
				resp.flushBuffer();
		
	}}
	
	
	
	
	
}