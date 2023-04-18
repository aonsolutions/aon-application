package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;

@WebServlet(name = "Invoice Stream Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/InvoiceStreamServlet" })
public class InvoiceStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -2697508555670615321L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String accountingReportParams = req.getParameter( IRequestParamsNames.ACCOUNT_REPORT_PARAMS );
			String domainName = req.getParameter( IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int offset = AonNumberUtils.toint( req.getParameter(IRequestParamsNames.OFFSET));
			int limit = AonNumberUtils.toint( req.getParameter(IRequestParamsNames.LIMIT));
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			AccountingReportParams params = JsonParser.parseAccountingParams(accountingReportParams);
			resp.setContentType(MimeType.JSON.getName());
			PrintWriter writer = new PrintWriter (resp.getWriter(), true); 
			writer.print( "[" );
			AON.getInvoiceHeaders(occam, params, offset, limit)
				.map( InvoiceJSON::toMinimalJSON )
				.forEach( js -> writer.print( js.toString() ));
			writer.print( "]" );
			resp.flushBuffer();
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}

	}

}
