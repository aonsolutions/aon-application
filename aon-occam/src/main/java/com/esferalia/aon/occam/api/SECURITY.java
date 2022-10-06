package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.LinkedList;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AuthDeviceFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;

public class SECURITY {

	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}
	
	public static DomainUserRoles getDomainUserRoles(Domain domain, String login, Integer userId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domain, login)){
			return getSecurity().getDomainUserRoles(ctx, userId);
		}
	}
	
	public static DomainUserRoles getDomainUserRoles(String domainName, Integer domainId, String login, Integer userId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().getDomainUserRoles(ctx, userId);
		}
	}

	public static AonToken getAonToken(String token) {
		JSONObject json = SECURITY.decodeJWT(token);
		AonToken aonToken = AonToken.parse(json);
		aonToken.setAuth(hexStringToByteArray(aonToken.getUuid()));
		return aonToken;
	}
	
	public static byte[] hexStringToByteArray(String hex) {
	    int l = hex.length();
	    byte[] data = new byte[l / 2];
	    for (int i = 0; i < l; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
	                + Character.digit(hex.charAt(i + 1), 16));
	    }
	    return data;
	}
	
	public static String getUserPassword(String domainName, int domainId, String login, Integer userId) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)){
			return getSecurity().getUserPassword(ctx, userId);
		}
	}
	
	public static JSONObject decodeJWT(String token) {
		Algorithm algorithm = Algorithm.HMAC256("aonsecret");
		DecodedJWT jwt = JWT.require(algorithm).build().verify(token);	
		return new JSONObject(jwt.getSubject())
				.put("expired", jwt.getExpiresAt() != null
					&& jwt.getExpiresAt().before(new Date()));
	}
	
	public static AuthDevice saveAuthDevice(Domain domain, String login, AuthDevice ad) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getSecurity().saveAuthDevice(ctx, ad);
		}
	}
	
	public static void deleteAuthDevice(Domain domain, String login, AuthDeviceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			getSecurity().deleteAuthDevice(ctx, filter);
		}
	}
	
	public static AuthDevice getAuthDevice(Domain domain, String login, AuthDeviceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getSecurity().getAuthDevice(ctx, filter);
		}
	}
	
	public static LinkedList<AuthDevice> getAuthDevices(Domain domain, String login, AuthDeviceFilter filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getSecurity().getAuthDevices(ctx, filter);
		}
	}
	
	public static User delete(Domain domain, String login, User user) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login)){
			return getSecurity().delete(ctx, user);
		}
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
		JSONObject object = decodeJWT("asdfjlasdvjawyiwd3iahsdjiaskfopwesuh"
			//"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7J3NjaGVtYSc6JycsICdzY2hlbWFfZmlyc3RfZG9tYWluJzonJywgJ3V1aWQnOicnfSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjA3MjQ3NjU3fQ.aYp2l--oUoLTFUrAmS7mgOLtHl4c62JRxMbF6a4pUQU"
			  //"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7J3NjaGVtYSc6JycsICdzY2hlbWFfZmlyc3RfZG9tYWluJzonJywgJ3V1aWQnOicnfSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjA3MjQ3NjU3fQ.aYp2l--oUoLTFUrAmS7mgOLtHl4c62JRxMbF6a4pUQU";
		);
		System.out.println(object);
	}
}