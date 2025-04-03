package com.esferalia.aon.gwt.fiscal.server.console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.Schema;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.UnpooledCloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDomainCheckScopeIntegrity;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Console Domain Check Scope Integrity Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainCheckScopeIntegrityServlet" })
public class ConsoleDomainCheckScopeIntegrityServlet extends ConsoleAbstractServlet {

	private static final long serialVersionUID = -5703828624659508582L;
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainCheckScopeIntegrityServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "ConsoleDomainCheckScopeIntegrityServlet start!");
		String domainParamsParam = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
		resp.setContentType(MimeType.JSON.getName());
		ConsoleParams consoleParams = new ConsoleParams()
			.setPrinter(new PrintStream(resp.getOutputStream()));
		DomainParams domainParams = null;
		try {
			domainParams = JsonParser.parseDomainParams(domainParamsParam);
			validate(domainParams, consoleParams, resp);
		} catch (ParseException | java.text.ParseException e1) {
			consoleParams.getPrinter().println("Params parse Problem.");
			consoleParams.getPrinter().println("Request ended.");
			consoleParams.getPrinter().println();
			consoleParams.getPrinter().flush();
			LOGGER.log(Level.INFO, "ConsoleDomainCheckScopeIntegrityServlet finished!");
			resp.flushBuffer();
		}
	}

	private void validate(DomainParams domainParams, ConsoleParams consoleParams, HttpServletResponse resp) throws IOException {
		try (UnpooledCloseableAONContext ctx = AONContext.getUnpooledAONContext(domainParams.getDbSchema())) {
			Schema schema = ctx.getDslContext().meta()
				.getSchemas(domainParams.getDbSchema())
				.stream()
				.findFirst()
				.orElse(null);
			ConsoleConnectionParams conParams = new ConsoleConnectionParams()
				.setAONContext(ctx)
				.setSchema(schema)
				.setSchemaName(domainParams.getDbSchema())
				.setDomain(new Domain().setId(domainParams.getId()));
			consoleParams.setFromConnection(conParams);
			ConsoleDomainCheckScopeIntegrity.check(consoleParams);
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			consoleParams.getPrinter().println(e.getMessage());
			consoleParams.getPrinter().println();
			resp.flushBuffer();
			LOGGER.log(Level.SEVERE, "ConsoleDomainCheckScopeIntegrityServlet {0}!",e.getMessage());
		} finally {
			consoleParams.getPrinter().println("Request ended.");
			consoleParams.getPrinter().println();
			resp.flushBuffer();
			LOGGER.log(Level.INFO, "ConsoleDomainCheckScopeIntegrityServlet finished!");
		}
	}
}
