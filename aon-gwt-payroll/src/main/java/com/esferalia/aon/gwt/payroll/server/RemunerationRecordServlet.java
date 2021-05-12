package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.in.payroll.excel.RemunerationRecord;
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
		resp.setHeader("Content-disposition", "attachment; filename=\"RefistroRetributivo."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
		
		String domain = req.getServerName();
		String enterpriseIdStr = req.getParameter("enterpriseId");
		String yearStr = req.getParameter("year");
		Integer year = yearStr != null && !yearStr.isEmpty() ? Integer.parseInt(yearStr) : null;
		Integer enterpriseId = enterpriseIdStr != null && !enterpriseIdStr.isEmpty() ? Integer.parseInt(enterpriseIdStr) : null; 
		try (OutputStream os = resp.getOutputStream();) {
			RemunerationRecord.generateExcel(os, domain, Optional.ofNullable(enterpriseId), year);
			resp.getOutputStream().flush();
			resp.flushBuffer();
		}
	}
	

}
