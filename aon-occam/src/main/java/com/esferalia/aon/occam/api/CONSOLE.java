package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.impl.jooq.ConsoleImpl;

public class CONSOLE {

	private CONSOLE() {
		
	}
	
	private static IConsole getConsole() {
		return new ConsoleImpl();
	}

	public static String[] getSchemaNames(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getConsole().getSchemaNames(ctx);
		}
	}

	public static LinkedList<Domain> getDomains(Occam occam, DomainParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().getDomains(ctx, params);
		}
	}

	public static boolean deleteDomain(Occam occam, DomainParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().deleteDomain(ctx, params);
		}
	}
}
