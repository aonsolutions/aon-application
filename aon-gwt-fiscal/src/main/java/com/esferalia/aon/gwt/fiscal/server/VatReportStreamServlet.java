package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.json.VatContextJSON;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.mutable.MutableBoolean;

@WebServlet(name = "Vat Report Stream", urlPatterns = { "/aon_gwt_fiscal/roms/VatReportStream" })
public class VatReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -6662360372576864159L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String vatParams = req.getParameter(IRequestParamsNames.VAT_PARAMS);
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			AccountingReportParams params = JsonParser.parseAccountingParams(vatParams);
			resp.setContentType(MimeType.JSON.getName());
			final PrintWriter out = resp.getWriter(); 
			Occam occam = new  Occam()
				.setDomain(domainId)
				.setDomainName(domainName)
				.setUser(user);
			out.print('[');
			MutableBoolean first = new MutableBoolean( true );
			FISCAL.getVatContext(occam , params)
				.map(VatContextJSON::toJSON)
				.forEach( json -> {
					if (!first.getValue().booleanValue()) out.print(',');
					out.print(json.toString());
					first.setValue(false);
				});
			out.print(']');
			out.flush();
			resp.flushBuffer();
		} catch (ParseException | java.text.ParseException e) {
			throw new ServletException(e);
		}

	}
}


