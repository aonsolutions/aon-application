package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		String enterpriseIdStr = req.getParameter("enterprise");
		String workplaceIdStr = req.getParameter("workplace");
		String yearStr = req.getParameter("year");
		
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
		
		try (OutputStream oos = resp.getOutputStream()){
		AggregatedAnnualSummary.writeExcel(
				oos
				, req.getServerName()
				, enterpriseId
				, workplaceId
				, year);
		oos.flush();
		}
		//http://ayudat.aonsolutions.net:8080/aon-aio/aon_gwt_payroll/AggregatedAnnualSummary/eee?year=2020&enterprise=216767
	}
	
}