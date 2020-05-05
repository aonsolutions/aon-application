package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonConnection;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.ApiImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;

public class AON_SOLUTIONS {
	
	private static IApi getApi() {
		return new ApiImpl();
	}
	
	private static ICommon getCommon() {
		return new CommonImpl();
	}
	
	private static IRegistry getRegistry() {
		return new RegistryImpl();
	}
	
	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}
	
	public static User getUser(Domain domain, String token) {
		Integer length = domain.getParentId() != null ? 2 : 1; 
		Integer[] domains = new Integer[length];
		domains[0] = domain.getId();
		if(domain.getParentId() != null) 
			domains[1] = domain.getParentId();
		AonConnection c = AONContext.getAonConnections(token).stream().filter(ac -> ac.getDomains().contains(domain.getId())
				|| ac.getDomains().contains(domain.getParentId()))
				.findFirst().orElse(new AonConnection());
		Integer[] users = c.getUsers().toArray(new Integer[c.getUsers().size()]);
	
		try (AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "")){		
			return getSecurity().getUser(ctx, f -> f.getDomainProperty().in(domains).and(f.getIdProperty().in(users)));
		}
	}
	
	public static LinkedList<User> getUsersByEmail(String schema, String email) {
		try (AONContext ctx = AONContext.getAONContext(schema)){		
			return getSecurity().getUsersByEmail(ctx, email);
		}
	}
	
	public static String getUserPassword(String schema, Integer user) {
		try (AONContext ctx = AONContext.getAONContext(schema)){
			return getSecurity().getUserPassword(ctx, user);
		}
	}
	
	public static Stream<Domain> getDomainStream(String token) {	
		Stream<Domain> stream = new LinkedList<Domain>().stream();
		for(AonConnection ac : AONContext.getAonConnections(token)) {
			try (AONContext ctx = AONContext.getAONContext(ac)){
				stream = Stream.concat(stream, getCommon().getDomainStream(ctx));
			}
		}
		return stream;
	}

	public static Stream<Company> getCompanyStream(String token) {	
		Stream<Company> stream = new LinkedList<Company>().stream();
		for(AonConnection ac : AONContext.getAonConnections(token)) {
			try (AONContext ctx = AONContext.getAONContext(ac)){
				stream = Stream.concat(stream, getRegistry().getCompanyStream(ctx));
			}
		}
		return stream;
	}
	
	public static Domain getDomain(String token, Integer domainId) {
		try (AONContext ctx = AONContext.getAONContext(token)){
			return getCommon().getDomain(ctx, domainId);
		}
	}
	
	// INVOICE
	
	public static Stream<Invoice> getInvoices(String domainName, Integer domainId, String login, InvoiceFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().getInvoices(ctx, filter);
		} 
	}
	
	public static Invoice insertInvoices(String domainName, Integer domainId, String login, Invoice invoice) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getApi().insertInvoice(ctx, invoice);
		} 
	}
	
	public static void deleteInvoices(String domainName, Integer domainId, String login, LinkedList<Integer> ids) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			for (Integer id : ids) {
				getApi().deleteInvoice(ctx, id);
			}
		} 
	}
	
}
