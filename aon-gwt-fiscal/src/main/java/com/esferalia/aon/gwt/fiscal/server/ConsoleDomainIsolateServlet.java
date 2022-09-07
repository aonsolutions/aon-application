package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Schema;

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
		String schemaName = req.getParameter(IRequestParamsNames.SCHEMA);
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		String newSchemaName = req.getParameter(IRequestParamsNames.NEW_SCHEMA);
		String newDomainName = req.getParameter(IRequestParamsNames.NEW_DOMAIN_NAME);
		String validate = req.getParameter(IRequestParamsNames.VALIDATE);
		String mustFlatten = req.getParameter(IRequestParamsNames.MUST_FLATTEN);
		ConsoleParams params = new ConsoleParams( )
			.setValidate( Boolean.valueOf(validate))
			.setMustFlatten( Boolean.valueOf(mustFlatten));
		try (
			CloseableAONContext fromCtx = AONContext.getAONContext(schemaName);
			CloseableAONContext toCtx = AONContext.getAONContext(newSchemaName) ) {
			
			Schema fromSchema = fromCtx.getDslContext().meta()
				.getSchemas(schemaName)
				.stream()
				.findFirst()
				.orElse(null);
			
			params.setFromConnection(new ConsoleConnectionParams()
				.setAONContext(fromCtx)
				.setSchemaName( schemaName )
				.setSchema( fromSchema )
				.setDomainName(domainName)
				);
			
			Schema toSchema = toCtx.getDslContext().meta()
				.getSchemas(newSchemaName)
				.stream()
				.findFirst()
				.orElse(null);
			params.setToConnection(new ConsoleConnectionParams()
				.setAONContext(toCtx)
				.setSchemaName( newSchemaName )
				.setSchema(toSchema)
				.setDomainName(newDomainName)
				);
			params.setPrinter(new PrintStream(resp.getOutputStream()));
			LOGGER.log(Level.INFO, "ConsoleDomainIsolateServlet domain \"{0}\".\"{1}\" to \"{2}\".\"{3}\"", new String[] {
				 params.getFromConnection().getSchemaName()
				,params.getFromConnection().getDomainName()
				,params.getToConnection().getSchemaName()
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
		}

	}
	
}
