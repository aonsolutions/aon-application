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

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDomainIsolate;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Domain Isolate Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainIsolateServlet" })
public class ConsoleDomainIsolateServlet extends ConsoleAbstractServlet {

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainIsolateServlet.class.getName());
	
	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String domainParamsParam = req.getParameter(IRequestParamsNames.DOMAIN_PARAMS);
		String newSchemaName = req.getParameter(IRequestParamsNames.NEW_SCHEMA);
		String newDomainName = req.getParameter(IRequestParamsNames.NEW_DOMAIN_NAME);
		resp.setContentType(MimeType.JSON.getName());
		ConsoleParams params = new ConsoleParams( )
			.setPrinter(new PrintStream(resp.getOutputStream()));
		DomainParams domainParams = null;
		CloseableAONContext fromCtx = null;
		CloseableAONContext toCtx = null;
		try {
			domainParams = JsonParser.parseDomainParams(domainParamsParam);
			
			fromCtx = AONContext.getAONContext(domainParams.getSchema());
			Schema fromSchema = fromCtx.getDslContext().meta()
				.getSchemas(domainParams.getSchema())
				.stream()
				.findFirst()
				.orElse(null);
			ConsoleConnectionParams fromParams = new ConsoleConnectionParams()
				.setAONContext(fromCtx)
				.setSchemaName(domainParams.getSchema())
				.setSchema(fromSchema)
				.setDomain(new Domain()
					.setId(domainParams.getId())
					.setName(domainParams.getName())
					.setDescription(domainParams.getDescription())
				);
			
			toCtx = AONContext.getAONContext(newSchemaName);
			Schema toSchema = toCtx.getDslContext().meta()
				.getSchemas(newSchemaName)
				.stream()
				.findFirst()
				.orElse(null);
			ConsoleConnectionParams toParams = new ConsoleConnectionParams()
				.setAONContext(toCtx)
				.setSchemaName( newSchemaName )
				.setSchema(toSchema)
				.setDomain(new Domain().setName(newDomainName));	

			params
				.setFromConnection(fromParams)
				.setToConnection(toParams)
				.setValidate(domainParams.isValidate())
				.setMustFlatten(domainParams.mustFlatten())
			;
			
			LOGGER.log(Level.INFO, "ConsoleDomainIsolateServlet domain \"{0}\".\"{1}\" to \"{2}\".\"{3}\"", new String[] {
				 params.getFromConnection().getSchemaName()
				,params.getFromConnection().getDomainName()
				,params.getToConnection().getSchemaName()
				,params.getToConnection().getDomainName()});
			ConsoleDomainIsolate.isolate(params);
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			params.getPrinter().println(e.getMessage());
			params.getPrinter().println();
			LOGGER.log(Level.SEVERE, "ConsoleDomainIsolateServlet {0}!",e.getMessage());
		} finally {
			if (fromCtx != null) {
				fromCtx.close();
			}
			if (toCtx != null) { 
				toCtx.close();
			}
			params.getPrinter().println("Request ended.");
			params.getPrinter().println();
			params.getPrinter().flush();
			resp.flushBuffer();
			LOGGER.log(Level.INFO, "ConsoleDomainIsolateServlet finished!");
		}

	}
	
}
