package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.occam.impl.jooq.console.IsolateDomain;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Console Domain Isolate Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/DomainIsolateServlet" })
public class DomainIsolateServlet extends HttpServlet {

	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintStream out = null;
		CloseableAONContext ctx = null;
		try {
			out = new PrintStream(resp.getOutputStream());
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String newDomainName = req.getParameter(IRequestParamsNames.NEW_DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domain = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			ctx = AONContext.getAONContext(domainName, domain, user);
			ConsoleParams params = new ConsoleParams()
				.setDomainName(domainName)
				.setNewDomainName(newDomainName)
				.setDslContext(ctx.getDslContext() )
				.setPrinter(out);
			IsolateDomain.isolate(params);
			resp.flushBuffer();
		} catch (IOException | SQLException | AonCoreException e) {
//			e.printStackTrace();
			try {
				if(out != null) {
					out.println(e.getMessage());
					out.println();
					resp.flushBuffer();
				}
			} catch (IOException ioe) {
				// Nothing
			}
		} finally {
			if (ctx != null) ctx.close();
		}

	}
	
}
