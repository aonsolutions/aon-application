package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.ENTERPRISE;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.WORKPLACE;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.YEAR;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.COMPLETE;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.DOMAIN;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.USER;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.TYPE;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.in.payroll.excel.AggregatedAnnualSummary;
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
@WebServlet(
		name = "Annual-Summary", 
		urlPatterns = { 
				"/aon_gwt_aio/AggregatedAnnualSummary/*" ,
				"/aon_gwt_payroll/AggregatedAnnualSummary/*" 
		}
)



public class AggregatedAnnualSumaryExcelServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(MimeType.MS_EXCEL.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"ResumenAnualAgregado."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
		String enterpriseIdStr = req.getParameter(ENTERPRISE.getName());
		String workplaceIdStr = req.getParameter(WORKPLACE.getName());
		String domainName = req.getParameter(DOMAIN.getName()) != null ? req.getParameter(DOMAIN.getName()) : req.getServerName();
		String user = req.getParameter(USER.getName()) != null ? req.getParameter(USER.getName()) : "";
		String yearStr = req.getParameter(YEAR.getName());
		boolean complete = req.getParameter(COMPLETE.getName()) != null && req.getParameter(COMPLETE.getName()).equalsIgnoreCase("true");
		AggregatedAnnualSummary.SummaryType type = AggregatedAnnualSummary.SummaryType.valueOf(req.getParameter(TYPE.getName()));
		
		Optional<Integer> enterpriseId = Optional.empty();
		Optional<Integer> workplaceId = Optional.empty();
		Integer year = null;
		
		if (enterpriseIdStr != null && !enterpriseIdStr.isEmpty()) {
			try {
				enterpriseId = Optional.ofNullable(Integer.parseInt(enterpriseIdStr));
			} catch (NumberFormatException e) {}
		}
		if (workplaceIdStr != null && !workplaceIdStr.isEmpty()) {
			try {
				workplaceId = Optional.ofNullable(Integer.parseInt(workplaceIdStr));
			} catch (NumberFormatException e) {}
		}
		if (yearStr != null && !yearStr.isEmpty()) {
			try {
				year = Integer.parseInt(yearStr);
			} catch (NumberFormatException e) {}
		}
		
		try (OutputStream os = resp.getOutputStream()){
		AggregatedAnnualSummary.writeExcel(
				os
				, domainName
				, user
				, enterpriseId
				, workplaceId
				, year
				, type
				, complete);
		os.flush();
		}
		//http://ayudat.aonsolutions.net:8080/aon-aio/aon_gwt_payroll/AggregatedAnnualSummary/Resumen_Anual_Agregado_2020?year=2020&enterpriseId=216767
	}
	
}