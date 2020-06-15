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
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.mutable.MutableBoolean;

@WebServlet(name = "Account Stream Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/AccountStreamServlet" })
public class AccountStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -5193110147982936993L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountParams = req.getParameter(IRequestParamsNames.ACCOUNT_PARAMS);
			AccountParams params = JsonParser.parseAccountParams(accountParams);
			resp.setContentType(MimeType.JSON.getName());
			PrintWriter out = resp.getWriter();
			out.write('[');
			final MutableBoolean first = new MutableBoolean(true);
			Stream<Account> stream =  ACCOUNTING.getAccounts(params);
			stream.map( account -> JsonWriter.writeToJSON(account))
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
