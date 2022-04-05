package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.json.VatContextJSON;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "Vat Report Stream", urlPatterns = { "/aon_gwt_fiscal/roms/VatReportStream" })
public class VatReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -6662360372576864159L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String vatParams = req.getParameter("vatParams");
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			AccountingReportParams params = JsonParser.parseAccountingParams(vatParams);
			resp.setContentType(MimeType.JSON.getName());
			final PrintWriter out = resp.getWriter(); 
			out.print('[');
			FISCAL.getVatContext(domainName, domainId, user, params)
				.map(VatContextJSON::toJSON)
				.forEach( json -> {
					out.print(json.toString());
					out.print(',');
				});
			out.print(']');
			out.flush();
			resp.flushBuffer();
		} catch (ParseException | java.text.ParseException e) {
			throw new ServletException(e);
		}

	}
}


