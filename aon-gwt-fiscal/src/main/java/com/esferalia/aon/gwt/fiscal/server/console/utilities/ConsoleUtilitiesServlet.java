package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.client.console.ConsoleUtilities;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleUtilities.ConsoleUtilitiesVisitor;
import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.server.console.ConsoleAbstractServlet;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Console Utilities Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleUtilities" })
public class ConsoleUtilitiesServlet extends ConsoleAbstractServlet {

	private static final long serialVersionUID = -5703828624659508582L;
	private static final Logger LOGGER = Logger.getLogger(ConsoleUtilitiesServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "ConsoleUtilitiesAbsServlet start!");
		String consoleUtility = req.getParameter(IRequestParamsNames.CONSOLE_UTILITY);
		if (AonStringUtils.isBlank(consoleUtility))
			throw new IllegalArgumentException("No se ha indicado utilidad");
		ConsoleUtilities cu = ConsoleUtilities.valueOf( consoleUtility );
		String domainParamsParam = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
		resp.setContentType(MimeType.JSON.getName());
		ConsoleParams consoleParams = new ConsoleParams().setPrinter(new PrintStream(resp.getOutputStream()));
		DomainParams domainParams = null;
		try {
			domainParams = JsonParser.parseDomainParams(domainParamsParam);
			run(cu, domainParams, consoleParams);
		} catch (ParseException | java.text.ParseException e1) {
			consoleParams.getPrinter().println("Params parse Problem.");
			consoleParams.getPrinter().println("Request ended.");
			consoleParams.getPrinter().println();
			consoleParams.getPrinter().flush();
			LOGGER.log(Level.INFO, "ConsoleUtilitiesAbsServlet finished!");
		}
		resp.flushBuffer();
	}
	
	private void run(ConsoleUtilities cu, DomainParams domainParams, ConsoleParams consoleParams) throws IOException {
		cu.visit( new ConsoleUtilitiesVisitor<Void>() {

			@Override
			public Void visitListDomains() throws IOException {
				new DomainListConsoleUtility().run( domainParams, consoleParams);
				return null;
			}

			@Override
			public Void visitNordigenFix() throws IOException {
				new NordigenFixConsoleUtility().run( domainParams, consoleParams);
				return null;
			}
		});
	}
	
}
