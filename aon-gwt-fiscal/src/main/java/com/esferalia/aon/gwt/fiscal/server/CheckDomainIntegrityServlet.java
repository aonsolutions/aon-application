package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger LOGGER = Logger.getLogger(CheckDomainIntegrityServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		String user = req.getParameter(IRequestParamsNames.USER);
		int domain = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
		PrintStream out = new PrintStream(resp.getOutputStream());
		
		LOGGER.log(Level.INFO, "CheckDomainIntegrityServlet domain \"{0}\"", new String[] {domainName});
		
		
		try ( CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
			ConsoleParams params = new ConsoleParams()
				.setDomain(domain)
				.setDomainName(domainName)
				.setDslContext(ctx.getDslContext() )
				.setPrinter(out);
			CheckDomainIntegrity.check(params);
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				out.println(e.getMessage());
				out.println();
				resp.flushBuffer();
			} catch (IOException ioe) {
				// Nothing
			}
			LOGGER.log(Level.SEVERE, "CheckDomainIntegrityServlet {0}!",e.getMessage());
		} finally {
			try {
				out.println("Request ended.");
				out.println();
				resp.flushBuffer();
			} catch (IOException ioe) {
				// Nothing
			}
			LOGGER.log(Level.INFO, "DomainIsolateServlet finished!");
		}
	}
	
}
