package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;

public class AON_SOLUTIONS {
	
	private static ICommon getCommon() {
		return new CommonImpl();
	}
	
	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}
	
	public static LinkedList<User> getUsersByEmail(String email) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext();			
			return getSecurity().getUsersByEmail(ctx, email);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static String getUserPassword(Integer user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext();
			return getSecurity().getUserPassword(ctx, user);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	
	public static Stream<Domain> getDomainStream(String token) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(token);
			return getCommon().getDomainStream(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
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
