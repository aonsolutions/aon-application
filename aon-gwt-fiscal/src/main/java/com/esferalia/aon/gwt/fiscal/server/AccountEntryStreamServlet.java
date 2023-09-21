package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "Account Entry Stream Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/AccountEntryStreamServlet" })
public class AccountEntryStreamServlet extends HttpServlet {

	private static final long serialVersionUID = 3774127041151176262L;

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
			
			AccountEntryParams params = JsonParser.parse(accountEntryParams);
			resp.setContentType(MimeType.JSON.getName());
			PrintWriter out = resp.getWriter();
			final JSONArray entries = new JSONArray();
			ACCOUNTING.getAccountEntriesStream(domainName, domainId, user, params, offset, limit)
				.map( entry -> JsonWriter.writeToJSON(entry))
				.forEach(json -> entries.put(json));
			;
			JsonWriter.write(out,entries);
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}
