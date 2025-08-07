package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.Schema;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.UnpooledCloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleSchema;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;
import com.esferalia.aon.watson.server.AonRandomStringUtils;

abstract class AbstractConsoleUtility {

	private static final Logger LOGGER = Logger.getLogger(AbstractConsoleUtility.class.getName());

	void run(DomainParams domainParams, ConsoleParams consoleParams) throws IOException {
		try {
			String processId = AonRandomStringUtils.randomAlphabetic(4) + "_" + (new Date()).getTime();
			ConsoleMessageUtils.start(consoleParams.getPrinter());
			if (domainParams.getDbSchema() == null) {
				for (ConsoleSchema s: ConsoleSchema.values()) {
					run( processId, domainParams, consoleParams, s.getSchema() );
				}
			} else {
				run( processId, domainParams, consoleParams, domainParams.getDbSchema());
			}
		} finally {
			ConsoleMessageUtils.end(consoleParams.getPrinter());
		}
	}
	
	private void run(String processId, DomainParams domainParams, ConsoleParams consoleParams, String sch) throws IOException {
		ConsoleMessageUtils.print(consoleParams.getPrinter(), ConsoleMessageUtils.title(processId, "Generando para schema : " + sch));
		try (UnpooledCloseableAONContext ctx = AONContext.getUnpooledAONContext(sch)) {
			Schema schema = ctx.getDslContext().meta()
				.getSchemas(sch)
				.stream()
				.findFirst()
				.orElse(null);
			ConsoleConnectionParams conParams = new ConsoleConnectionParams()
				.setAONContext(ctx)
				.setSchema(schema)
				.setSchemaName(sch)
				.setDomain(new Domain().setId(domainParams.getId()));
			consoleParams.setFromConnection(conParams);
			
			doUtility(processId, consoleParams, domainParams);
			consoleParams.getPrinter().flush();
		} catch (Throwable e) {
			e.printStackTrace();
			ConsoleMessageUtils.print(consoleParams.getPrinter(), ConsoleMessageUtils.error(processId, e.getMessage()));
			consoleParams.getPrinter().println();
			consoleParams.getPrinter().flush();
			LOGGER.log(Level.SEVERE, "ConsoleUtilitiesAbsServlet {0}!",e.getMessage());
		} finally {
			consoleParams.getPrinter().println();
			consoleParams.getPrinter().flush();
			LOGGER.log(Level.INFO, "ConsoleUtilitiesAbsServlet finished!");
		}
	}
	
	protected abstract void doUtility(String processId, ConsoleParams params, DomainParams domainParams);
	
}
