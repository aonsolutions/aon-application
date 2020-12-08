package com.esferalia.aon.occam.api;

import java.util.Date;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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
		} 
		catch ( Exception e ) {
			//TODO: uuid : login
		}
		finally {
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
	
	public static void main(String[] args) {
		Algorithm algorithm = Algorithm.HMAC256("aonsecret");
		String token =   JWT.create()
				.withIssuer("auth0")
				.withIssuedAt(new Date())
				//.withExpiresAt(AonDateUtils.addDays(new Date(), 1))
				.withSubject("{'schema':'', 'schema_first_domain':'', 'uuid':''}")
				.sign(algorithm);		
		System.out.println(token);
		JSONObject object = decodeJWT(
				"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7J3NjaGVtYSc6JycsICdzY2hlbWFfZmlyc3RfZG9tYWluJzonJywgJ3V1aWQnOicnfSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjA3MjQ3NjU3fQ.aYp2l--oUoLTFUrAmS7mgOLtHl4c62JRxMbF6a4pUQU"
			  //"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7J3NjaGVtYSc6JycsICdzY2hlbWFfZmlyc3RfZG9tYWluJzonJywgJ3V1aWQnOicnfSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjA3MjQ3NjU3fQ.aYp2l--oUoLTFUrAmS7mgOLtHl4c62JRxMbF6a4pUQU";
		);
		System.out.println(object);
	}
}
