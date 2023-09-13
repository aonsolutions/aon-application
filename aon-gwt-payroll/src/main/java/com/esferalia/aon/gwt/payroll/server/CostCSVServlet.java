package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.DOMAIN;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.ENTERPRISE;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.FILTER;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.MONTH;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.USER;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.WORKPLACE;
import static com.esferalia.aon.gwt.payroll.shared.CostCSVService.Params.YEAR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
@WebServlet(
		name = "Cost-CSV", 
		urlPatterns = { 
				"/aon_gwt_aio/cost_csv/*" ,
				"/aon_gwt_payroll/cost_csv/*" 
		}
)
public class CostCSVServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer enterpriseId;
		Integer workplaceId;
		Integer month;
		Integer year;
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
			
			enterpriseId = req.getParameter(ENTERPRISE.getName()) != null ? Integer.parseInt(req.getParameter(ENTERPRISE.getName())) : null;
			workplaceId = req.getParameter(WORKPLACE.getName()) != null ? Integer.parseInt(req.getParameter(WORKPLACE.getName())) : null;
			month = Integer.parseInt(req.getParameter(MONTH.getName()));
			year = Integer.parseInt(req.getParameter(YEAR.getName()));
			domainName = req.getParameter(DOMAIN.getName()) != null && !req.getParameter(DOMAIN.getName()).isEmpty() ? req.getParameter(DOMAIN.getName()) : req.getServerName();
			user = req.getParameter(USER.getName()) != null ? req.getParameter(USER.getName()) : "";
		}
		
		resp.setContentType(MimeType.CSV.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.CSV.getExtension()+ "\";");
		
		
		try (ServletOutputStream sos = resp.getOutputStream();
				CloseableAONContext aonContext = AONContext.getAONContext(domainName, user) ) {

				if (enterpriseId == null || enterpriseId == 0)
					enterpriseId = AON.getWorkplace(aonContext.getDomainName()
							, aonContext.getDomainId()
							, aonContext.getUser()
							, w -> w.getIdProperty().eq(workplaceId))
							.getEnterprise();
				
				Stream<com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV.EnterprisePayroll> stream =
						EnterprisePayrollCSV.getEnterprisePayrolls(aonContext, month+1, year, enterpriseId, workplaceId);
				
				List<IEnterprisePayroll> list = stream
						.filter(p -> types.contains(p.getSalaryType().ordinal()))
						.sorted(Comparator.comparing(IEnterprisePayroll::getEmployee))
						.collect(Collectors.toList());
				
				EnterprisePayrollCSV.write(sos, list);

				sos.flush();
				resp.flushBuffer();
		
	}}
	
	
	
	
	
}