package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.json.JSONException;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.mutable.MutableBoolean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
			List<Account> list =  ACCOUNTING.getAccountsList(params);
			list.stream().map( account -> JsonWriter.writeToJSON(account))
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
			list.stream().close();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}
