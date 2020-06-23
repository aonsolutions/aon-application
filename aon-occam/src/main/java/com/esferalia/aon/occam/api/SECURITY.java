package com.esferalia.aon.occam.api;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;

public class SECURITY {

	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}

	public static AonToken getAonToken(String token) {
		JSONObject json = SECURITY.decodeJWT(token);
		AonToken aonToken = AonToken.parse(json);
		AONContext ctx = null;
		try {
//			ctx = AONContext.getAONContext(aonToken.getSchema());
			ctx = AONContext.getAONContext(aonToken.getSchemaFirstDomain(), 0, "");
			aonToken.setAuth(getSecurity().unHexUuid(ctx, aonToken.getUuid()));
		} finally {
			if(ctx != null) {
				ctx.close();
			}
		}
		return aonToken;
	}
	
	public static String getUserPassword(String domainName, int domainId, String login, Integer userId) {
		try (AONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().getUserPassword(ctx, userId);
		}
	}
	
	public static JSONObject decodeJWT(String token) {
		DecodedJWT jwt = JWT.decode(token);
		return new JSONObject(jwt.getSubject());
	}

}
