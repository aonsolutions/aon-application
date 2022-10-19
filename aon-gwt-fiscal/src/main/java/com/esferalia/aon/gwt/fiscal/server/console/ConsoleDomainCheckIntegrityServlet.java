package com.esferalia.aon.gwt.fiscal.server.console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Schema;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDomainCheckIntegrity;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Domain Check Integrity Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainCheckIntegrityServlet" })
public class ConsoleDomainCheckIntegrityServlet extends ConsoleAbstractServlet {

	private static final long serialVersionUID = -5703828624659508582L;
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainCheckIntegrityServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "ConsoleDomainCheckIntegrityServlet start!");
		String domainParamsParam = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
		resp.setContentType(MimeType.JSON.getName());
		ConsoleParams consoleParams = new ConsoleParams()
			.setPrinter(new PrintStream(resp.getOutputStream()));
		DomainParams domainParams = null;
		try {
			domainParams = JsonParser.parseDomainParams(domainParamsParam);
			try (CloseableAONContext ctx = AONContext.getAONContext(domainParams.getSchema())) {
				Schema schema = ctx.getDslContext().meta()
					.getSchemas(domainParams.getSchema())
					.stream()
					.findFirst()
					.orElse(null);
				ConsoleConnectionParams conParams = new ConsoleConnectionParams()
					.setAONContext(ctx)
					.setSchema(schema)
					.setSchemaName(domainParams.getSchema())
					.setDomain(new Domain().setId(domainParams.getId()));
				consoleParams.setFromConnection(conParams);
				ConsoleDomainCheckIntegrity.check(consoleParams);
				resp.flushBuffer();
			} catch (Exception e) {
				e.printStackTrace();
				consoleParams.getPrinter().println(e.getMessage());
				consoleParams.getPrinter().println();
				resp.flushBuffer();
				LOGGER.log(Level.SEVERE, "ConsoleDomainCheckIntegrityServlet {0}!",e.getMessage());
			} finally {
				consoleParams.getPrinter().println("Request ended.");
				consoleParams.getPrinter().println();
				resp.flushBuffer();
				LOGGER.log(Level.INFO, "ConsoleDomainCheckIntegrityServlet finished!");
			}
		} catch (ParseException | java.text.ParseException e1) {
			consoleParams.getPrinter().println("Params parse Problem.");
			consoleParams.getPrinter().println("Request ended.");
			consoleParams.getPrinter().println();
			consoleParams.getPrinter().flush();
			LOGGER.log(Level.INFO, "ConsoleDomainCheckIntegrityServlet finished!");
			resp.flushBuffer();
		}
	}
}
