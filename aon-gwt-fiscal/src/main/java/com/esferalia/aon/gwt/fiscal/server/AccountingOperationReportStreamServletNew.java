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
import com.esferalia.aon.occam.api.model.fiscal.OperationParamsNew;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Accounting Operation Report Stream New", urlPatterns = { "/aon_gwt_fiscal/roms/AccountingOperationReportStreamNew" })
public class AccountingOperationReportStreamServletNew extends HttpServlet {

	private static final long serialVersionUID = 3846483795164331091L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String operationParams = req.getParameter(IRequestParamsNames.IRPF_PARAMS);
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			String user = req.getParameter(IRequestParamsNames.USER);
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			OperationParamsNew params = JsonParser.parseOperationParamsNew(operationParams);
			System.out.println("AccountingOperationReportStreamServletNew: params="+operationParams);
			
			resp.setContentType(MimeType.HTML.getName());
			PrintWriter wr = resp.getWriter();
			
			JSONArray jsonArray = new JSONArray();
			ACCOUNTING.getOperationBreakdownNew(occam, params)
				.forEach(op -> {
					JSONObject jsonObject = new JSONObject(op);
					// FALTA - LAS FECHAS SE PASAN FORMATEADAS EN UN STRING (dd/MM/yyyy o long), SI NO, EL JSON SE CREA CON UNA FECHA LARGA
					jsonObject.put("entryDate", ensureDate(op.getEntryDate()));
					jsonObject.put("taxDate", ensureDate(op.getTaxDate()));
					jsonObject.put("receptionDate", ensureDate(op.getReceptionDate()));
					jsonObject.put("payDate", ensureDate(op.getPayDate()));
					jsonArray.put(jsonObject);
				});
			System.out.println("jsonArray="+jsonArray);
			wr.print(jsonArray);
			wr.flush();
			
			resp.flushBuffer();
		} catch (ParseException | java.text.ParseException e) {
			throw new ServletException(e);
		}
	}
	
	// FALTA - VER COMO SE PASAN LAS FECHAS, dd/MM/yyyy O COMO LONG (EN CUALQUIER CASO AMBAS SE PASAN EN UN STRING)
	// Para los valores nulos, si pasamos JSONObject.NULL, se pone el valor en el JSON con null, si pasamos simplemente null, el valor se quita del JSON
	private String ensureDate(Date date) {
		return date == null ? null : AonDateUtils.simpleFormat(date); // FORMATEADA COMO dd/MM/yyyy 
//		return date == null ? null : AonNumberUtils.toString(date.getTime()); // COMO LONG EN UN STRING
	}

}
