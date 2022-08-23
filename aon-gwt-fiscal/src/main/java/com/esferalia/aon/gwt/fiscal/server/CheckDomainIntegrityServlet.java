package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.impl.jooq.console.CheckDomainIntegrity;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Check Domain Integrity Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/CheckDomainIntegrityServlet" })
public class CheckDomainIntegrityServlet extends HttpServlet {

	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintStream out = null;
		CloseableAONContext ctx = null;
		try {
			out = new PrintStream(resp.getOutputStream());
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domain = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			ctx = AONContext.getAONContext(domainName, domain, user);
			ConsoleParams params = new ConsoleParams()
				.setDomain(domain)
				.setDomainName(domainName)
				.setDslContext(ctx.getDslContext() )
				.setPrinter(out);
			CheckDomainIntegrity.check(params);
			resp.flushBuffer();
		} catch (Exception e) {
			try {
				if(out != null) {
					out.print(e.getMessage());
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
