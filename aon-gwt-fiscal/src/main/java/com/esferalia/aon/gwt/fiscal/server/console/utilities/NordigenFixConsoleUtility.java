package com.esferalia.aon.gwt.fiscal.server.console.utilities;

import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

class NordigenFixConsoleUtility extends AbstractConsoleUtility {

	protected void doUtility(String processId, ConsoleParams params, DomainParams domainParams) {
//		
//		[......]
//				
		ConsoleMessageUtils.print(params.getPrinter(), ConsoleMessageUtils.ok(processId, "Final del proceso."));
	}
}
