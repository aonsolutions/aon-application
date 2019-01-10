package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.FlatAccountEntryDetail;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.mutable.MutableBoolean;

@WebServlet(name = "Account Statement Stream Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/AccountStatementStreamServlet" })
public class AccountStatementStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountEntryParams = req.getParameter(IRequestParamsNames.ACCOUNT_ENTRY_PARAMS);
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			int offset = Integer.parseInt(req.getParameter(IRequestParamsNames.OFFSET));
			int limit = Integer.parseInt(req.getParameter(IRequestParamsNames.LIMIT));
			
			AccountingReportParams params = JsonParser.parseAccountingParams(accountEntryParams);
			resp.setContentType(MimeType.JSON.getName());
			PrintWriter out = resp.getWriter();
			out.write('[');
			final MutableBoolean first = new MutableBoolean(true);
			Stream<FlatAccountEntryDetail> stream =  ACCOUNTING.getLedgerStream(domainName, domainId, user, params, offset, limit);
			stream.map( entry -> JsonWriter.writeToJSON(entry))
				.forEach(json -> {
					try {
						if (first.getValue()) {
							first.setValue(false);
						} else {
							out.write(',');							
						}
						json.write(out);
						out.write('\n');
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} );
			;
			out.write(']');
			resp.flushBuffer();
			stream.close();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}
