package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.impl.jooq.ConsoleImpl;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleConnectionParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

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

	public static Stream<ConsoleDomain> getDomains(DomainParams params) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().getDomains(ctx, params);
		}
	}

	public static boolean deleteDomain(DomainParams params, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			ConsoleParams consoleParams = new ConsoleParams(); 
			ConsoleConnectionParams conParams = new ConsoleConnectionParams()
				.setAONContext(ctx)
				.setSchemaName(params.getSchema())
				.setDomain(new Domain().setId(domainId));
			consoleParams.setFromConnection(conParams);
			return getConsole().deleteDomain(consoleParams);
		}
	}

	public static Domain changeActive(DomainParams params, Integer domainId, boolean active) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().changeActive(ctx,domainId, active);
		}
	}

	public static Domain changeExpirationDate(DomainParams params, Integer domainId, Date expireDate) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().changeExpirationDate(ctx,domainId,expireDate);
		}
	}

	public static String remoteAccess(DomainParams params, Integer domainId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(params.getSchema())) {
			return getConsole().remoteAccess(ctx,domainId);
		}
	}

	public static Boolean fix(ConsoleDomainMessage consoleMessage) {
		try (CloseableAONContext ctx = AONContext.getAONContext(consoleMessage.getSchema())) {
			return getConsole().fix(ctx,consoleMessage);
		}
	}

	public static LinkedHashMap<String, Object> viewRow(String schema,String tableName, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(schema)) {
			return getConsole().viewRow(ctx,tableName, id);
		}
	}
	
	
}
