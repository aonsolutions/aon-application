package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDomainCheckIntegrity;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Domain Check Integrity Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainCheckIntegrityServlet" })
public class ConsoleDomainCheckIntegrityServlet extends ConsoleAbstractServlet {

	private static final long serialVersionUID = -5703828624659508582L;
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainCheckIntegrityServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		ConsoleParams params = new ConsoleParams();
		LOGGER.log(Level.INFO, "ConsoleDomainCheckIntegrityServlet domain \"{0}\"",domainName);
		try ( CloseableAONContext ctx = AONContext.getAONContext(domainName,0,"")) {
			params
				.setFromConnection(new ConsoleConnectionParams()
					.setAONContext(ctx)
					.setSchema(resolveSchema(ctx))
					.setDomainName(domainName)
					)
				.setPrinter(new PrintStream(resp.getOutputStream()));
			ConsoleDomainCheckIntegrity.check(params);
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			params.getPrinter().println(e.getMessage());
			params.getPrinter().println();
			resp.flushBuffer();
			LOGGER.log(Level.SEVERE, "ConsoleDomainCheckIntegrityServlet {0}!",e.getMessage());
		} finally {
			params.getPrinter().println("Request ended.");
			params.getPrinter().println();
			resp.flushBuffer();
			LOGGER.log(Level.INFO, "ConsoleDomainCheckIntegrityServlet finished!");
		}
	}
	
}
