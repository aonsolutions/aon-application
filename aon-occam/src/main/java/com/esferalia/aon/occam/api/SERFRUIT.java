package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.SalesFilter;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.SerfruitImpl;

public class SERFRUIT {

	private SERFRUIT() {
		throw new IllegalStateException("Utility class");
	}
	
	private static ISerfruit getSerfruit() {
		return new SerfruitImpl();
	}

	public static Stream<Sales> getSalesStream(Domain domain, User user, SalesFilter filter, Options... options) {
		return getSalesStream(domain.getName(), domain.getId(),  user.getLogin(), filter, options);
	}
	
	public static Stream<Sales> getSalesStream(Domain domain, String login, SalesFilter filter, Options... options) {
		return getSalesStream(domain.getName(), domain.getId(),  login, filter, options);
	}
	
	public static Stream<Sales> getSalesStream(String domainName, Integer domainId, String login, SalesFilter filter, Options... options) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSerfruit().getSalesStream(ctx, filter, options);
		}
	}

}