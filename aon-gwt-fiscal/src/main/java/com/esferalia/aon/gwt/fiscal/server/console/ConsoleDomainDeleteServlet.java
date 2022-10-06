package com.esferalia.aon.gwt.fiscal.server.console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDeleteDomain;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Domain Delete Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainDeleteServlet" })
public class ConsoleDomainDeleteServlet extends ConsoleAbstractServlet {

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainDeleteServlet.class.getName());
	
	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "ConsoleDomainDeleteServlet start!");
		String domainParamsParam = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
		ConsoleParams consoleParams = new ConsoleParams()
			.setPrinter(new PrintStream(resp.getOutputStream()));
		DomainParams domainParams = null;
		try {
			domainParams = JsonParser.parseDomainParams(domainParamsParam);
			try (CloseableAONContext ctx = AONContext.getAONContext(domainParams.getSchema())) {
				ConsoleConnectionParams conParams = new ConsoleConnectionParams()
					.setAONContext(ctx)
					.setSchemaName(domainParams.getSchema())
					.setDomain(new Domain().setId(domainParams.getId()));
				consoleParams.setFromConnection(conParams);
				ConsoleDeleteDomain.delete(consoleParams);
			} catch (Exception e) {
				e.printStackTrace();
				consoleParams.getPrinter().println(e.getMessage());
				consoleParams.getPrinter().println();
				LOGGER.log(Level.SEVERE, "ConsoleDomainDeleteServlet {0}!",e.getMessage());
			} finally {
				consoleParams.getPrinter().println("Request ended.");
				consoleParams.getPrinter().println();
				consoleParams.getPrinter().flush();
				resp.flushBuffer();
				LOGGER.log(Level.INFO, "ConsoleDomainDeleteServlet finished!");
			}

		} catch (ParseException | java.text.ParseException e1) {
			consoleParams.getPrinter().println("Params parse Problem.");
			consoleParams.getPrinter().println("Request ended.");
			consoleParams.getPrinter().println();
			consoleParams.getPrinter().flush();
			LOGGER.log(Level.INFO, "ConsoleDomainDeleteServlet finished!");
			resp.flushBuffer();
		}
		

	}
	
}
