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
import com.esferalia.aon.occam.impl.jooq.console.ConsoleDeleteDomain;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

@WebServlet(name = "Console Domain Delete Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/ConsoleDomainDeleteServlet" })
public class ConsoleDomainDeleteServlet extends ConsoleAbstractServlet {

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainDeleteServlet.class.getName());
	
	private static final long serialVersionUID = -5703828624659508582L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String schemaName = req.getParameter(IRequestParamsNames.SCHEMA);
		String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
		ConsoleParams params = new ConsoleParams( );
		
		try (CloseableAONContext fromCtx = AONContext.getAONContext(schemaName)) {
			
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
			
			params.setPrinter(new PrintStream(resp.getOutputStream()));
			LOGGER.log(Level.INFO, "ConsoleDomainDeleteServlet domain \"{0}\".\"{1}\"", new String[] {
				 params.getFromConnection().getSchemaName()
				,params.getFromConnection().getDomainName()});
			ConsoleDeleteDomain.delete(params);
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			params.getPrinter().println(e.getMessage());
			params.getPrinter().println();
			LOGGER.log(Level.SEVERE, "ConsoleDomainDeleteServlet {0}!",e.getMessage());
		} finally {
			params.getPrinter().println("Request ended.");
			params.getPrinter().println();
			params.getPrinter().flush();
			resp.flushBuffer();
			LOGGER.log(Level.INFO, "ConsoleDomainDeleteServlet finished!");
		}

	}
	
}
