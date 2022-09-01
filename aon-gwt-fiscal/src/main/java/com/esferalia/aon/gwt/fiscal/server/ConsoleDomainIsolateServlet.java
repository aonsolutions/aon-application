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
import com.esferalia.aon.occam.impl.jooq.console.ConsoleIsolateDomain;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Domain Isolate Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainIsolateServlet" })
public class ConsoleDomainIsolateServlet extends ConsoleAbstractServlet {

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainIsolateServlet.class.getName());
	
	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		String newDomainName = req.getParameter(IRequestParamsNames.NEW_DOMAIN_NAME);
		ConsoleParams params = new ConsoleParams( );
		CloseableAONContext ctx =  null;
		try {
			ctx = AONContext.getAONContext(domainName,0,"");
			params.setFromConnection(new ConsoleConnectionParams()
				.setAONContext(ctx)
				.setSchema(resolveSchema(ctx))
				.setDomainName(domainName)
				);
			params.setToConnection(new ConsoleConnectionParams()
				.setAONContext(ctx)
				.setSchema(params.getFromConnection().getSchema())
				.setDomainName(newDomainName)
				);
			params.setPrinter(new PrintStream(resp.getOutputStream()));
			LOGGER.log(Level.INFO, "ConsoleDomainIsolateServlet domain \"{0}\" to \"{1}\"", new String[] {
				params.getFromConnection().getDomainName()
				,params.getToConnection().getDomainName()});
			ConsoleIsolateDomain.isolate(params);
			resp.flushBuffer();
		} catch (Exception e) {
			params.getPrinter().println(e.getMessage());
			params.getPrinter().println();
			LOGGER.log(Level.SEVERE, "ConsoleDomainIsolateServlet {0}!",e.getMessage());
		} finally {
			params.getPrinter().println("Request ended.");
			params.getPrinter().println();
			params.getPrinter().flush();
			resp.flushBuffer();
			LOGGER.log(Level.INFO, "ConsoleDomainIsolateServlet finished!");
			if (ctx != null) {
				ctx.close();
			}
		}

	}
	
}
