package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Domain Transfer Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainTransferServlet" })
public class ConsoleDomainTransferServlet extends ConsoleAbstractServlet {

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTransferServlet.class.getName());
	
	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ConsoleParams params = new ConsoleParams();
//		ConsoleParams params = getConsoleParams( req ,resp );
		try {
//			params
//				.setSchema(req.getParameter(IRequestParamsNames.SCHEMA))
//				.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME))
//				.setNewSchema(req.getParameter(IRequestParamsNames.NEW_SCHEMA))
//				.setNewDomainName(req.getParameter(IRequestParamsNames.NEW_DOMAIN_NAME))
//				.setFromConnection(resolveConnection(params));
//			LOGGER.log(Level.INFO, "ConsoleDomainTransferServlet domain \"{0}\" to \"{1}\"", new String[] {params.getDomainName(),params.getNewDomainName()});
//			ConsoleTransferDomain.transfer(params);
			resp.flushBuffer();
		} catch (Exception e) {
			params.getPrinter().println(e.getMessage());
			params.getPrinter().println();
			LOGGER.log(Level.SEVERE, "ConsoleDomainTransferServlet {0}!",e.getMessage());
		} finally {
			params.getPrinter().println("Request ended.");
			params.getPrinter().println();
			params.getPrinter().flush();
			resp.flushBuffer();
			LOGGER.log(Level.INFO, "ConsoleDomainIsolateServlet finished!");
		}

	}
	
}
