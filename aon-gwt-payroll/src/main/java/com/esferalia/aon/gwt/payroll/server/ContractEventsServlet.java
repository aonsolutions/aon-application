package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.DOMAIN;
import static com.esferalia.aon.gwt.payroll.shared.AggregatedAnnualSummaryService.Params.USER;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqEvents;
import com.esferalia.aon.in.payroll.excel.EnterpriseContractVariablesExcel;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CONTRACT-EVENTS-SERVLET", urlPatterns = { "/aon_gwt_payroll/contract_events/*"})
public class ContractEventsServlet extends AonApiHttpServlet {
	
	private static final SimpleDateFormat dateFileFormat = new SimpleDateFormat("ddMMyyyy");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final String EXPORT_ROUTE = "/export";
	private static final String IMPORT_ROUTE = "/import";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			switch (request.getPathInfo()) {
				case EXPORT_ROUTE:
					exportContractEventsExcel(request, response);
					break;
				case IMPORT_ROUTE:
					importContractEventsExcel(request, response);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void exportContractEventsExcel(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String domainName = request.getParameter(DOMAIN.getName()) != null ? request.getParameter(DOMAIN.getName()) : request.getServerName();
		String user = request.getParameter(USER.getName()) != null ? request.getParameter(USER.getName()) : "";
		Integer domainId = AonServletUtils.getDomainID(domainName);
		java.sql.Date date = new java.sql.Date(AonDateUtils.getFirstDayOfMonth(new Date()).getTime());
		try {
			date = new java.sql.Date(AonDateUtils.getFirstDayOfMonth(dateFormat.parse(request.getParameter("date"))).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		response.setContentType(MimeType.MS_EXCEL.getName());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Content-disposition", "attachment; filename=\"VariablesEmpresa" + dateFileFormat.format(date) + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
		
		ServletOutputStream output = response.getOutputStream();
		
		Set<String> variables = JooqEvents.exportEmployeeEventsVariablesExcel(domainName, user, date);
		EnterpriseContractVariablesExcel.enterpriseContractVariablesExport(domainName, user, domainId, variables, date, output);
		
		output.flush();
		response.flushBuffer();
	}

	private void importContractEventsExcel(HttpServletRequest request, HttpServletResponse response) {
		// Get FilePart
		try {
			String domainName = request.getParameter(DOMAIN.getName()) != null ? request.getParameter(DOMAIN.getName()) : request.getServerName();
			String user = request.getParameter(USER.getName()) != null ? request.getParameter(USER.getName()) : "";
			Integer domainId = AonServletUtils.getDomainID(domainName);
			
			Map<String, List<String>> messages = new HashMap<>();
			
			Part filePart = request.getPart("uploader");
			if(null != filePart) {
				InputStream is = filePart.getInputStream();
				messages = EnterpriseContractVariablesExcel.enterpriseContractVariablesImport(domainName, user, domainId, is);
			}
			
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);
			
			PrintWriter out = response.getWriter();
			
			JSONObject messagesJson = new JSONObject();
			if(!messages.get("success").isEmpty()) {
				JSONArray successArr = new JSONArray();
				messages.get("success").forEach(message -> successArr.put(message));
				messagesJson.put("success", successArr);
			} 
			
			if(!messages.get("errors").isEmpty()) {
				JSONArray errorArr = new JSONArray();
				messages.get("errors").forEach(message -> errorArr.put(message));
				messagesJson.put("errors", errorArr);
			}
			out.print(messagesJson);
			out.flush();
			
		} catch (IOException | ServletException | SQLException e) {
			throw new AonApiException("No se ha podido analizar el archivo");
		} 
	}
	
}