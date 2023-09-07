package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.DOMAIN;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.USER;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import com.esferalia.aon.in.payroll.excel.RemunerationRecord;
import com.esferalia.aon.in.payroll.excel.RemunerationRecord.RemunerationRecordCallback;
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
@WebServlet(name = "RemunerationRecord", 
	urlPatterns = { 
			"/aon_gwt_aio/remuneration_record/*",
			"/aon_gwt_payroll/remuneration_record/*" 
	})
public class RemunerationRecordServlet extends HttpServlet {
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doPost(req, resp);
	}
	
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType(MimeType.MS_EXCEL.getName());
//		resp.setHeader("Content-disposition", "attachment; filename=\"RegistroRetributivo."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
		resp.setContentType("text/html");
//		resp.setContentType("application/json");
		
		String domainName = req.getParameter(DOMAIN.getName()) != null ? req.getParameter(DOMAIN.getName()) : req.getServerName();
		String user = req.getParameter(USER.getName()) != null ? req.getParameter(USER.getName()) : "";
		String enterpriseIdStr = req.getParameter("enterpriseId");
		String yearStr = req.getParameter("year");
		Integer year = yearStr != null && !yearStr.isEmpty() ? Integer.parseInt(yearStr) : null;
		Integer enterpriseId = enterpriseIdStr != null && !enterpriseIdStr.isEmpty() ? Integer.parseInt(enterpriseIdStr) : null;
		
		
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			JSONObject json = new JSONObject();
			JSONArray noSexArray = new JSONArray();
			RemunerationRecord.generateExcel(os, domainName, user, Optional.ofNullable(enterpriseId), year, new RemunerationRecordCallback() {

				@Override
				public void personWithNoSex(String socialSecurityNum, String name) {
					JSONObject noSexObject = new JSONObject();
					noSexObject.put("name", name);
					noSexObject.put("nss", socialSecurityNum);
					noSexArray.put(noSexObject);
				}
				
			});
			
			byte[] excel = os.toByteArray();
			
			String encodedExcel = Base64.getEncoder().encodeToString(excel);
			
			json.put("noSex", noSexArray);
			
			json.put("excel", encodedExcel);
			
			resp.getWriter().write(json.toString());
			resp.getWriter().flush();
			resp.flushBuffer();
		}
	}
	

}
