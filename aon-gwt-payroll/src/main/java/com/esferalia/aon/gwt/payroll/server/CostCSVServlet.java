package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.in.payroll.csv.EnterprisePayrollCSV;
import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
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
		Collection<Integer> types;
		
		//Picking up the parameters
		{
			if (req.getParameterValues("filter") != null)
				types = Arrays.stream(req.getParameterValues("filter"))
				.map(str -> Integer.parseInt(str))
				.collect(Collectors.toUnmodifiableList());
			else
				types = Collections.unmodifiableList(new ArrayList<Integer>());
			
			enterpriseId = req.getParameter("enterpriseId") != null ? Integer.parseInt(req.getParameter("enterpriseId")) : null;
			workplaceId = req.getParameter("workplaceId") != null ? Integer.parseInt(req.getParameter("workplaceId")) : null;
			month = Integer.parseInt(req.getParameter("month"));
			year = Integer.parseInt(req.getParameter("year"));
		}
		
		resp.setContentType(MimeType.CSV.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.CSV.getExtension()+ "\";");
		
		
		try (ServletOutputStream sos = resp.getOutputStream();
				AONContext aonContext = AONContext.getAONContext(req.getServerName(), "") ) {

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