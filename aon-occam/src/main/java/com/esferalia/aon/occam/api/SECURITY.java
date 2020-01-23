package com.esferalia.aon.occam.api;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;

public class SECURITY {

	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}

	public static String getUserPassword(String domainName, int domainId, String login, Integer userId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getSecurity().getUserPassword(ctx, userId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static JSONObject decodeJWT(String token) {
		DecodedJWT jwt = JWT.decode(token);
		return new JSONObject(jwt.getSubject());
	}

}
