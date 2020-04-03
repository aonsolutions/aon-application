package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonConnection;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.RegistryImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;

public class AON_SOLUTIONS {
	
	private static ICommon getCommon() {
		return new CommonImpl();
	}
	
	private static IRegistry getRegistry() {
		return new RegistryImpl();
	}
	
	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}
	
	public static LinkedList<User> getUsersByEmail(String schema, String email) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(schema);			
			return getSecurity().getUsersByEmail(ctx, email);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static String getUserPassword(String schema, Integer user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(schema);
			return getSecurity().getUserPassword(ctx, user);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	
	public static Stream<Domain> getDomainStream(String token) {	
		Stream<Domain> stream = new LinkedList<Domain>().stream();
		for(AonConnection ac : AONContext.getAonConnections(token)) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(ac);
				stream = Stream.concat(stream, getCommon().getDomainStream(ctx));
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		return stream;
		
	}

	public static Stream<Company> getCompanyStream(String token) {	
		Stream<Company> stream = new LinkedList<Company>().stream();
		for(AonConnection ac : AONContext.getAonConnections(token)) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(ac);
				stream = Stream.concat(stream, getRegistry().getCompanyStream(ctx));
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		return stream;
		
	}
	
	public static Domain getDomain(String token, Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(token);
			return getCommon().getDomain(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
}
