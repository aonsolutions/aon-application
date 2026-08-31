package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.jooq.tools.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Accounting Operation Report Stream", urlPatterns = { "/aon_gwt_fiscal/roms/AccountingOperationReportStream" })
public class AccountingOperationReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = 3846483795164331091L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String operationParams = req.getParameter("operationParams");
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			String user = req.getParameter(IRequestParamsNames.USER);
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			OperationParams params = JsonParser.parseOperationParams(operationParams);
			
			resp.setContentType(MimeType.HTML.getName());
			PrintWriter wr = resp.getWriter();
			
			JSONArray jsonArray = new JSONArray();
			ACCOUNTING.getOperationBreakdown(occam, params)
				.forEach(op -> {
					JSONObject jsonObject = new JSONObject(op);
					jsonObject.put("entryDate", ensureDate(op.getEntryDate()));
					jsonObject.put("taxDate", ensureDate(op.getTaxDate()));
					jsonObject.put("receptionDate", ensureDate(op.getReceptionDate()));
					jsonObject.put("payDate", ensureDate(op.getPayDate()));
					jsonArray.put(jsonObject);
				});
//			System.out.println("jsonArray="+jsonArray);
			
			wr.print(jsonArray);
			wr.flush();			
			resp.flushBuffer();
		} catch (ParseException | java.text.ParseException e) {
			throw new ServletException(e);
		}
	}
	
	// Las fechas se pasan formateadas en un string como dd/MM/yyyy
	private String ensureDate(Date date) {
		return date == null ? null : AonDateUtils.simpleFormat(date); // FORMATEADA COMO dd/MM/yyyy 
	}

}
